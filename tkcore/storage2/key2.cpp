

#include "key_core.h"
#include "key2.h"
#include "split_password.h"
#include "key_errors.h"
#include "salt/pass_spliter_v1.h"
#include "common.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include <cstring>

#include <openssl/evp.h>
#include <openssl/objects.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>
#include <openssl/aes.h>
#include <openssl/bio.h>
#include <openssl/kdf.h>
#include <openssl/sha.h>
#include <openssl/rand.h>
#include <algorithm>
#include <utility>

using namespace std;
using namespace thekey;
using namespace thekey_v2;
using namespace key_salt;
using namespace thekey_v2;


static char typeOwnerText[FILE_TYPE_OWNER_LEN] = "TheKey key storage. Designed by Andrei Kuzubov / Klee0kai. "
                                                 "Follow original app https://github.com/klee0kai/thekey";


// -------------------- declarations ---------------------------

static std::shared_ptr<StorageHeaderFlat> storageHeader(int fd);

static list<CryptedPasswordFlat> readHist(char *buffer, int len);

static shared_ptr<CryptedNote> readNote(char *buffer, int len);

// -------------------- static ---------------------------------

shared_ptr<StorageInfo> thekey_v2::storageFullInfo(const std::string &file) {
    int fd = open(file.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};
    auto header = storageHeader(fd);
    if (!header)return {};
    auto storage = make_shared<StorageInfo>();
    storage->path = file;
    storage->storageVersion = header->storageVersion();
    storage->name = header->name;
    storage->description = header->description;
    return storage;
}

int thekey_v2::createStorage(const thekey::Storage &storage) {
    int fd = open(storage.file.c_str(), O_RDONLY | O_WRONLY | O_CLOEXEC | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
    if (fd < 0) return KEY_OPEN_FILE_ERROR;
    StorageHeaderFlat header = {};
    memcpy(header.signature, storageSignature_V2, SIGNATURE_LEN);
    header.storageVersion(STORAGE_VER_SECOND);
    memcpy(header.fileTypeOwner, typeOwnerText, FILE_TYPE_OWNER_LEN);
    strncpy(header.name, storage.name.c_str(), STORAGE_NAME_LEN);
    strncpy(header.description, storage.description.c_str(), STORAGE_DESCRIPTION_LEN);
    header.cryptType(Default);
    header.interactionsCount(1000);
    RAND_bytes(header.salt, SALT_LEN);
    auto wroteLen = write(fd, &header, sizeof(header));
    if (wroteLen != sizeof(header)) {
        close(fd);
        return KEY_WRITE_FILE_ERROR;
    }
    close(fd);
    return 0;
}

std::shared_ptr<KeyStorageV2> thekey_v2::storage(const std::string &path, const std::string &passw) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};
    auto header = storageHeader(fd);
    if (!header) {
        close(fd);
        return {};
    }
    auto ctx = cryptContext(
            passw,
            header->interactionsCount(),
            header->salt
    );

    return make_shared<KeyStorageV2>(fd, path, ctx);
}


std::shared_ptr<CryptContext> thekey_v2::cryptContext(
        const std::string &passw,
        const uint &interactionsCount,
        const unsigned char *salt
) {
    auto splitPassw = split(passw);
    auto passwLen = int(splitPassw.passwForPassw.length());
    auto ctx = std::make_shared<CryptContext>();
    memset(&*ctx, 0, sizeof(CryptContext));

    PKCS5_PBKDF2_HMAC((char *) splitPassw.passwForPassw.c_str(), passwLen,
                      salt, SALT_LEN,
                      interactionsCount, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForPassw);
    PKCS5_PBKDF2_HMAC((char *) splitPassw.passwForLogin.c_str(), passwLen,
                      salt, SALT_LEN,
                      interactionsCount, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForLogin);
    PKCS5_PBKDF2_HMAC((char *) splitPassw.passwForHistPassw.c_str(), passwLen,
                      salt, SALT_LEN,
                      interactionsCount, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForHistPassw);
    PKCS5_PBKDF2_HMAC((char *) splitPassw.passwForDescription.c_str(), passwLen,
                      salt, SALT_LEN,
                      interactionsCount, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForDescription);
    splitPassw = {};
    return ctx;
}

// ------------------- public --------------------------
KeyStorageV2::KeyStorageV2(int fd, const std::string &path, const std::shared_ptr<CryptContext> &ctx)
        : fd(fd), storagePath(path), ctx(ctx) {
    tempStoragePath = path.substr(0, path.find_last_of('.')) + "-temp.ckey";
    cachedInfo = {.path = storagePath,};
}

KeyStorageV2::~KeyStorageV2() {
    if (ctx)memset(&*ctx, 0, sizeof(CryptContext));
    if (fd) close(fd);
    ctx.reset();
    fd = 0;
}

int KeyStorageV2::readAll() {
    cryptedNotes.clear();
    cryptedGeneratedPassws.clear();

    fheader = storageHeader(fd);
    cachedInfo.storageVersion = fheader->storageVersion();
    cachedInfo.name = fheader->name;
    cachedInfo.description = fheader->description;

    while (true) {
        FileSectionFlat section{};
        int len = read(fd, &section, sizeof(section));
        if (!len) break;
        if (len != sizeof(section))return KEY_STORAGE_FILE_IS_BROKEN;
        char buffer[section.sectionLen()];
        len = read(fd, buffer, section.sectionLen());
        if (len != section.sectionLen())return KEY_STORAGE_FILE_IS_BROKEN;

        switch (section.sectionType()) {
            case FileMap: {
                break;
            }
            case NoteEntry: {
                const auto note = readNote(buffer, len);
                if (note) cryptedNotes.push_back(*note);
                break;
            }
            case GenPasswHistory: {
                const auto &hist = readHist(buffer, len);
                std::for_each(hist.begin(), hist.end(), [&](const auto &item) {
                    cryptedGeneratedPassws.push_back(item);
                });
                break;
            }
            default:
                cachedInfo.invalidSectionsContains = 1;
                break;
        }
    }

    return 0;
}

StorageInfo KeyStorageV2::info() {
    return cachedInfo;
}

int KeyStorageV2::save() {
    auto error = save(tempStoragePath);
    if (error)return error;
    error = save(storagePath);
    if (error) return error;
    // everything went fine, you can delete the backup file
    remove(tempStoragePath.c_str());
    return error;
}

int KeyStorageV2::save(const std::string &path) {
    int fd = open(path.c_str(), O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, S_IRUSR | S_IWUSR);
    if (fd < 0) return -1;
    auto writeLen = write(fd, &*fheader, sizeof(StorageHeaderFlat));
    FileSectionFlat fileSection{};


    if (writeLen != sizeof(StorageHeaderFlat)) goto write_file_error;

    for (const auto &note: cryptedNotes) {
        fileSection = {};
        fileSection.sectionType(NoteEntry);
        fileSection.sectionLen(sizeof(CryptedNoteFlat) + note.history.size() * sizeof(CryptedPasswordFlat));
        writeLen = write(fd, &fileSection, sizeof(fileSection));
        if (writeLen != sizeof(fileSection)) goto write_file_error;

        writeLen = write(fd, &note.note, sizeof(CryptedNoteFlat));
        if (writeLen != sizeof(CryptedNoteFlat)) goto write_file_error;

        for (const auto &hist: note.history) {
            writeLen = write(fd, &hist, sizeof(hist));
            if (writeLen != sizeof(hist)) goto write_file_error;
        }
    }

    fileSection = {};
    fileSection.sectionType(GenPasswHistory);
    fileSection.sectionLen(cryptedGeneratedPassws.size() * sizeof(CryptedPasswordFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(fileSection)) goto write_file_error;
    for (const auto &hist: cryptedGeneratedPassws) {
        writeLen = write(fd, &hist, sizeof(hist));
        if (writeLen != sizeof(hist)) goto write_file_error;
    }

    close(fd);
    return 0;

    write_file_error:
    close(fd);
    return KEY_WRITE_FILE_ERROR;

}

// ---- notes api ----

std::vector<long long> KeyStorageV2::notes() {
    std::vector<long long> notes = {};
    for (const auto &item: cryptedNotes) {
        notes.push_back((long long) &item);
    }
    return notes;
}


std::shared_ptr<DecryptedNote> KeyStorageV2::note(long long notePtr, uint flags) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return {};
    }

    auto decryptedNote = std::make_shared<DecryptedNote>();
    decryptedNote->genTime = cryptedNote->note.genTime();
    decryptedNote->color = cryptedNote->note.color();
    std::vector<long long> notes = {};
    for (const auto &item: cryptedNote->history) {
        decryptedNote->history.push_back((long long) &item);
    }

    decryptedNote->site = cryptedNote->note.site.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    decryptedNote->login = cryptedNote->note.login.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    decryptedNote->description = cryptedNote->note.description.decrypt(
            ctx->keyForDescription,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    if (flags & TK2_GET_NOTE_PASSWORD) {
        decryptedNote->passw = cryptedNote->note.password.decrypt(
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    return decryptedNote;
}

long long KeyStorageV2::createNote() {
    cryptedNotes.push_back({});
    const CryptedNote &it = cryptedNotes.back();
    return (long long) &it;
}

int KeyStorageV2::setNote(long long notePtr,
                          const thekey_v2::DecryptedNote &dnote,
                          uint flags) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return KEY_NOTE_NOT_FOUND;
    }

    auto notCmpOld = (flags & TK2_SET_NOTE_FORCE);
    auto trackHist = (flags & TK2_SET_NOTE_TRACK_HISTORY);
    auto old = note(notePtr, TK2_GET_NOTE_PASSWORD);

    cryptedNote->note.color(dnote.color);

    if (notCmpOld || old->site != dnote.site) {
        cryptedNote->note.site.encrypt(
                dnote.site,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }
    if (notCmpOld || old->login != dnote.login) {
        cryptedNote->note.login.encrypt(
                dnote.login,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->description != dnote.description) {
        cryptedNote->note.description.encrypt(
                dnote.description,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->passw != dnote.passw) {
        cryptedNote->note.password.encrypt(
                dnote.passw,
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
        cryptedNote->note.genTime(time(NULL));

        if (trackHist && !old->passw.empty() && old->passw != dnote.passw) {
            CryptedPasswordFlat hist{};
            hist.password.encrypt(
                    old->passw,
                    ctx->keyForHistPassw,
                    fheader->cryptType(),
                    fheader->interactionsCount()
            );
            hist.genTime(old->genTime);
            cryptedNote->history.push_back(hist);
        }
    }

    auto error = save();
    return error;
}

int KeyStorageV2::removeNote(long long notePtr) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return KEY_NOTE_NOT_FOUND;
    }
    cryptedNotes.erase(cryptedNote);

    auto error = save();
    return error;
}

// ---- gen passw and hist api ----
std::string KeyStorageV2::genPassword(uint32_t encodingType, int len) {
    auto passw = from(thekey_v2::gen_password(encodingType, len));

    CryptedPasswordFlat cryptedPasswordFlat{};
    cryptedPasswordFlat.genTime(time(NULL));
    cryptedPasswordFlat.password.encrypt(
            passw,
            ctx->keyForHistPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    cryptedGeneratedPassws.push_back(cryptedPasswordFlat);

    save();
    return passw;
}

std::vector<long long> KeyStorageV2::passwordsHistory() {
    std::vector<long long> generatedPasswordHistory = {};
    for (const auto &item: cryptedGeneratedPassws) {
        generatedPasswordHistory.push_back((long long) &item);
    }
    return generatedPasswordHistory;
}

std::shared_ptr<DecryptedPassw> KeyStorageV2::passwordHistory(long long histPtr) {
    shared_ptr<CryptedPasswordFlat> histPassw = {};

    for (const auto &item: cryptedGeneratedPassws) {
        if ((long long) &item == histPtr) {
            histPassw = make_shared<CryptedPasswordFlat>(item);
            break;
        }
    }
    for (const auto &note: cryptedNotes) {
        if (!histPassw)
            for (const auto &item: note.history) {
                if ((long long) &item == histPtr) {
                    histPassw = make_shared<CryptedPasswordFlat>(item);
                    break;
                }
            }
    }
    if (!histPassw) {
        return {};
    }
    DecryptedPassw dPassw{};
    dPassw.genTime = histPassw->genTime();
    dPassw.color = histPassw->color();
    dPassw.passw = histPassw->password.decrypt(
            ctx->keyForHistPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    return make_shared<DecryptedPassw>(dPassw);
}

// -------------------- private ------------------------------
static std::shared_ptr<StorageHeaderFlat> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageHeaderFlat header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V2, SIGNATURE_LEN) != 0
        || header.storageVersion() != STORAGE_VER_SECOND)
        return {};
    return make_shared<StorageHeaderFlat>(header);
}


static list<CryptedPasswordFlat> readHist(char *buffer, int len) {
    list<CryptedPasswordFlat> passwords = {};
    if (len >= sizeof(CryptedPasswordFlat)) {
        for (int offset = 0; offset <= len - sizeof(CryptedPasswordFlat); offset += sizeof(CryptedPasswordFlat)) {
            auto *flat = (CryptedPasswordFlat *) (buffer + offset);
            passwords.push_back(*flat);
        }
    }
    return passwords;
}

static shared_ptr<CryptedNote> readNote(char *buffer, int len) {
    if (len < sizeof(CryptedNoteFlat))return {};
    auto note = CryptedNote{
            .note = *(CryptedNoteFlat *) buffer,
            .history = readHist(buffer + sizeof(CryptedNoteFlat), len - sizeof(CryptedNoteFlat))
    };
    return make_shared<CryptedNote>(note);
}

