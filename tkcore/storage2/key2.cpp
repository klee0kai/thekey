

#include "key_core.h"
#include "key2.h"
#include "split_password.h"
#include "key_errors.h"
#include "salt/pass_spliter_v1.h"
#include "common.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include <cstring>
#include "otp.h"
#include "tools/base32.h"

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
using namespace key_otp;

static char typeOwnerText[FILE_TYPE_OWNER_LEN] = "TheKey key storage. Designed by Andrei Kuzubov / Klee0kai. "
                                                 "Follow original app https://github.com/klee0kai/thekey";


// -------------------- declarations ---------------------------
static std::shared_ptr<StorageHeaderFlat> storageHeader(int fd);

static shared_ptr<CryptedNote> readNote(char *buffer, int len);

template<typename T>
list<T> readSimpleList(char *buffer, int len) {
    list<T> passwords = {};
    if (len >= sizeof(T)) {
        for (int offset = 0; offset <= len - sizeof(T); offset += sizeof(T)) {
            auto *flat = (T *) (buffer + offset);
            passwords.push_back(*flat);
        }
    }
    return passwords;
}

template<typename T>
typename list<T>::iterator findByPtr(list<T> &sList, const long long &ptr) {
    return std::find_if(sList.begin(), sList.end(), [&](const T &it) { return (long long) &it == ptr; });;
}

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
    if (fd < 0) {
        keyError = KEY_OPEN_FILE_ERROR;
        return KEY_OPEN_FILE_ERROR;
    }
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
        keyError = KEY_WRITE_FILE_ERROR;
        return KEY_WRITE_FILE_ERROR;
    }
    close(fd);
    return 0;
}

std::shared_ptr<KeyStorageV2> thekey_v2::storage(const std::string &path, const std::string &passw) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) {
        keyError = KEY_OPEN_FILE_ERROR;
        return {};
    }
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
    PKCS5_PBKDF2_HMAC((char *) splitPassw.passwForOtp.c_str(), passwLen,
                      salt, SALT_LEN,
                      interactionsCount, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForOtpPassw);
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
        if (len != sizeof(section)) {
            keyError = KEY_STORAGE_FILE_IS_BROKEN;
            return KEY_STORAGE_FILE_IS_BROKEN;
        }
        char buffer[section.sectionLen()];
        len = read(fd, buffer, section.sectionLen());
        if (len != section.sectionLen()) {
            keyError = KEY_STORAGE_FILE_IS_BROKEN;
            return KEY_STORAGE_FILE_IS_BROKEN;
        }

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
                const auto &hist = readSimpleList<CryptedPasswordFlat>(buffer, len);
                std::for_each(hist.begin(), hist.end(), [&](const auto &item) {
                    cryptedGeneratedPassws.push_back(item);
                });
                break;
            }
            case OtpNote: {
                const auto &otpNotes = readSimpleList<CryptedOtpInfoFlat>(buffer, len);
                std::for_each(otpNotes.begin(), otpNotes.end(), [&](const auto &item) {
                    cryptedOtpNotes.push_back(item);
                });
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
    if (fd < 0) {
        keyError = KEY_OPEN_FILE_ERROR;
        return KEY_OPEN_FILE_ERROR;
    }
    auto writeLen = write(fd, &*fheader, sizeof(StorageHeaderFlat));
    FileSectionFlat fileSection{};

    if (writeLen != sizeof(StorageHeaderFlat)) goto write_file_error;

    // Crypted Notes
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

    // Gen Passw History Section
    fileSection = {};
    fileSection.sectionType(GenPasswHistory);
    fileSection.sectionLen(cryptedGeneratedPassws.size() * sizeof(CryptedPasswordFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(fileSection)) goto write_file_error;
    for (const auto &hist: cryptedGeneratedPassws) {
        writeLen = write(fd, &hist, sizeof(hist));
        if (writeLen != sizeof(hist)) goto write_file_error;
    }

    // Otp Notes Section
    fileSection = {};
    fileSection.sectionType(OtpNote);
    fileSection.sectionLen(cryptedOtpNotes.size() * sizeof(CryptedOtpInfoFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(fileSection)) goto write_file_error;
    for (const auto &otp: cryptedOtpNotes) {
        writeLen = write(fd, &otp, sizeof(otp));
        if (writeLen != sizeof(otp)) goto write_file_error;
    }

    close(fd);
    return 0;

    write_file_error:
    close(fd);
    keyError = KEY_WRITE_FILE_ERROR;
    return KEY_WRITE_FILE_ERROR;
}

int KeyStorageV2::saveNewPassw(const std::string &path, const std::string &passw) {

    auto storageInfo = info();
    auto error = createStorage(
            {
                    .file = path,
                    .storageVersion = storageInfo.storageVersion,
                    .name = storageInfo.name,
                    .description = storageInfo.description
            });
    if (error)return error;
    auto destStorage = storage(path, passw);
    destStorage->readAll();
    auto newCryptCtx = destStorage->ctx;

    for (const auto &note: notes(TK2_GET_NOTE_FULL)) {
        destStorage->createNote(note);
    }

    for (const auto &srcNote: otpNotes(TK2_GET_NOTE_FULL)) {
        auto destNoteList = destStorage->createOtpNotes(
                exportOtpNote(srcNote.notePtr).toUri(),
                TK2_GET_NOTE_FULL
        );
        if (destNoteList.empty())continue;
        auto destNote = destNoteList.front();

        // not export meta
        destNote.color = srcNote.color;
        destNote.pin = srcNote.pin;

        destStorage->setOtpNote(destNote);
    }

    destStorage->appendPasswHistory(genPasswHistoryList(TK2_GET_NOTE_FULL));

    return destStorage->save();
}

// ---- notes api ----

std::vector<DecryptedNote> KeyStorageV2::notes(uint flags) {
    std::vector<DecryptedNote> notes = {};
    notes.reserve(cryptedNotes.size());
    for (const auto &item: cryptedNotes) {
        notes.push_back(*note((long long) &item, flags));
    }
    return notes;
}


std::shared_ptr<DecryptedNote> KeyStorageV2::note(long long notePtr, uint flags) {
    auto cryptedNote = findByPtr(cryptedNotes, notePtr);
    if (cryptedNote == cryptedNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    auto decryptedNote = std::make_shared<DecryptedNote>();
    decryptedNote->notePtr = notePtr;
    decryptedNote->genTime = cryptedNote->note.genTime();
    decryptedNote->color = cryptedNote->note.color();

    for (const auto &item: cryptedNote->history) {
        decryptedNote->history.push_back(*genPasswHistory((long long) &item, flags));
    }

    if ((flags & TK2_GET_NOTE_INFO) != 0) {
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
    }

    if (flags & TK2_GET_NOTE_PASSWORD) {
        decryptedNote->passw = cryptedNote->note.password.decrypt(
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    return decryptedNote;
}

shared_ptr<DecryptedNote> KeyStorageV2::createNote(const DecryptedNote &note) {
    cryptedNotes.push_back({});
    const CryptedNote &it = cryptedNotes.back();
    auto dNote = make_shared<DecryptedNote>(note);
    dNote->notePtr = (long long) &it;
    setNote(*dNote, TK2_SET_NOTE_FORCE | TK2_SET_NOTE_FULL_HISTORY);
    return dNote;
}

int KeyStorageV2::setNote(const thekey_v2::DecryptedNote &dnote,
                          uint flags) {
    auto cryptedNote = findByPtr(cryptedNotes, dnote.notePtr);
    if (cryptedNote == cryptedNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }

    auto notCmpOld = (flags & TK2_SET_NOTE_FORCE);
    auto trackHist = (flags & TK2_SET_NOTE_TRACK_HISTORY);
    auto setFullHistory = (flags & TK2_SET_NOTE_FULL_HISTORY);
    auto old = note(dnote.notePtr, TK2_GET_NOTE_FULL);

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
                ctx->keyForDescription,
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
            cryptedNote->history.push_front(hist);
        }
    }

    if (setFullHistory) {
        cryptedNote->history.clear();
        for (const auto &item: dnote.history) {
            CryptedPasswordFlat hist{};
            hist.password.encrypt(
                    item.passw,
                    ctx->keyForHistPassw,
                    fheader->cryptType(),
                    fheader->interactionsCount()
            );
            hist.genTime(item.genTime);
            hist.color(item.color);

            cryptedNote->history.push_back(hist);
        }

    }


    auto error = save();
    return error;
}

int KeyStorageV2::removeNote(long long notePtr) {
    auto cryptedNote = findByPtr(cryptedNotes, notePtr);
    if (cryptedNote == cryptedNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }
    cryptedNotes.erase(cryptedNote);

    auto error = save();
    return error;
}

// ---- otp note api ----
std::list<DecryptedOtpNote> KeyStorageV2::createOtpNotes(const std::string &uri, uint flags) {
    list<long long> addedOtpPtrsList{};
    const auto &otpList = key_otp::parseOtpUri(uri);
    for (const auto &otp: otpList) {
        CryptedOtpInfoFlat cryped{};
        cryped.createTime(time(NULL));
        cryped.scheme(otp.scheme);
        cryped.method(otp.method);
        cryped.algorithm(otp.algorithm);
        cryped.digits(otp.digits);
        cryped.interval(otp.interval);
        cryped.counter(otp.counter);

        cryped.issuer.encrypt(
                otp.issuer,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        cryped.name.encrypt(
                otp.name,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        cryped.secret.encrypt(
                otp.secret,
                ctx->keyForOtpPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        cryptedOtpNotes.push_back(cryped);
        addedOtpPtrsList.push_back((long long) &cryptedOtpNotes.back());
    }

    list<DecryptedOtpNote> addedOtpNotes{};
    for (const auto &otpPtr: addedOtpPtrsList) {
        const auto &otp = otpNote(otpPtr, flags);
        if (otp) addedOtpNotes.push_back(*otp);
    }

    save();
    return addedOtpNotes;
}

int KeyStorageV2::setOtpNote(const thekey_v2::DecryptedOtpNote &dnote, uint flags) {
    auto cryptedNote = findByPtr(cryptedOtpNotes, dnote.notePtr);
    if (cryptedNote == cryptedOtpNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }

    auto notCmpOld = (flags & TK2_SET_NOTE_FORCE);
    auto old = otpNote(dnote.notePtr, TK2_GET_NOTE_FULL);

    cryptedNote->color(dnote.color);

    if (notCmpOld || old->issuer != dnote.issuer) {
        cryptedNote->issuer.encrypt(
                dnote.issuer,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->name != dnote.name) {
        cryptedNote->name.encrypt(
                dnote.name,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->pin != dnote.pin) {
        cryptedNote->pin.encrypt(
                dnote.pin,
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }


    auto error = save();
    return error;
}

std::vector<DecryptedOtpNote> KeyStorageV2::otpNotes(uint flags) {
    std::vector<DecryptedOtpNote> notes = {};
    notes.reserve(cryptedOtpNotes.size());
    for (const auto &item: cryptedOtpNotes) {
        auto ptr = (long long) &item;
        const auto &otp = otpNote(ptr, flags);
        if (otp) notes.push_back(*otp);
    }

    return notes;
}

std::shared_ptr<DecryptedOtpNote> KeyStorageV2::otpNote(long long notePtr, uint flags, time_t now) {
    auto cryptedNote = findByPtr(cryptedOtpNotes, notePtr);
    if (cryptedNote == cryptedOtpNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    auto decryptedNote = std::make_shared<DecryptedOtpNote>();
    decryptedNote->notePtr = notePtr;
    decryptedNote->createTime = cryptedNote->createTime();
    decryptedNote->color = cryptedNote->color();
    decryptedNote->method = cryptedNote->method();
    decryptedNote->interval = cryptedNote->interval();

    if ((flags & TK2_GET_NOTE_INFO) != 0) {
        decryptedNote->issuer = cryptedNote->issuer.decrypt(
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        decryptedNote->name = cryptedNote->name.decrypt(
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if ((flags & TK2_GET_NOTE_PASSWORD) != 0) {
        decryptedNote->pin = cryptedNote->pin.decrypt(
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        auto otpInfo = exportOtpNote(notePtr);
        decryptedNote->otpPassw = key_otp::generate(otpInfo, now);

        if (cryptedNote->method() == HOTP) {
            cryptedNote->counter(cryptedNote->counter() + 1);
            save();
        }
    }

    return decryptedNote;
}

OtpInfo KeyStorageV2::exportOtpNote(long long notePtr) {
    auto cryptedNote = findByPtr(cryptedOtpNotes, notePtr);
    if (cryptedNote == cryptedOtpNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    OtpInfo otp{
            .scheme = cryptedNote->scheme(),
            .method = cryptedNote->method(),
            .algorithm = cryptedNote->algorithm(),

            .digits = cryptedNote->digits(),
            .interval = cryptedNote->interval(),
            .counter = cryptedNote->counter()
    };

    otp.issuer = cryptedNote->issuer.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    otp.name = cryptedNote->name.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    otp.secret = cryptedNote->secret.decrypt(
            ctx->keyForOtpPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    otp.pin = cryptedNote->pin.decrypt(
            ctx->keyForPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    return otp;
}

int KeyStorageV2::removeOtpNote(long long notePtr) {
    auto cryptedNote = findByPtr(cryptedOtpNotes, notePtr);
    if (cryptedNote == cryptedOtpNotes.end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }
    cryptedOtpNotes.erase(cryptedNote);

    auto error = save();
    return error;
}

// ---- gen passw and hist api ----
std::string KeyStorageV2::genPassword(uint32_t schemeId, int len) {
    auto passw = from(thekey_v2::gen_password(schemeId, len));

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

std::vector<DecryptedPassw> KeyStorageV2::genPasswHistoryList(const uint &flags) {
    std::vector<DecryptedPassw> generatedPasswordHistory = {};
    generatedPasswordHistory.reserve(cryptedGeneratedPassws.size());
    for (const auto &item: cryptedGeneratedPassws) {
        generatedPasswordHistory.push_back(*genPasswHistory((long long) &item, flags));
    }
    return generatedPasswordHistory;
}

std::shared_ptr<DecryptedPassw> KeyStorageV2::genPasswHistory(long long histPtr, const uint &flags) {
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
        keyError = KEY_HIST_NOT_FOUND;
        return {};
    }
    DecryptedPassw dPassw{};
    dPassw.histPtr = histPtr;
    dPassw.genTime = histPassw->genTime();
    dPassw.color = histPassw->color();

    if (flags & TK2_GET_NOTE_HISTORY_FULL) {
        dPassw.passw = histPassw->password.decrypt(
                ctx->keyForHistPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }
    return make_shared<DecryptedPassw>(dPassw);
}

int KeyStorageV2::appendPasswHistory(const std::vector<DecryptedPassw> &hist) {
    for (const auto &histItem: hist) {
        CryptedPasswordFlat cryptedPasswordFlat{};
        cryptedPasswordFlat.genTime(histItem.genTime);
        cryptedPasswordFlat.password.encrypt(
                histItem.passw,
                ctx->keyForHistPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
        cryptedGeneratedPassws.push_back(cryptedPasswordFlat);
    }

    auto error = save();
    return error;
}

// -------------------- private ------------------------------
static std::shared_ptr<StorageHeaderFlat> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageHeaderFlat header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V2, SIGNATURE_LEN) != 0
        || header.storageVersion() != STORAGE_VER_SECOND) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    return make_shared<StorageHeaderFlat>(header);
}


static shared_ptr<CryptedNote> readNote(char *buffer, int len) {
    if (len < sizeof(CryptedNoteFlat))return {};
    auto note = CryptedNote{
            .note = *(CryptedNoteFlat *) buffer,
            .history = readSimpleList<CryptedPasswordFlat>(
                    buffer + sizeof(CryptedNoteFlat), len - sizeof(CryptedNoteFlat)
            )
    };
    return make_shared<CryptedNote>(note);
}


