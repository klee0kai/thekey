

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

#define ID_COUNTER_START 10

using namespace std;
using namespace thekey;
using namespace thekey_v2;
using namespace key_salt;
using namespace key_otp;

static char typeOwnerText[FILE_TYPE_OWNER_LEN] = "TheKey key storage. Designed by Andrei Kuzubov / Klee0kai. "
                                                 "Follow original app https://github.com/klee0kai/thekey";


// -------------------- declarations ---------------------------
static std::shared_ptr<StorageHeaderFlat> storageHeader(int fd);

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
    _dataSnapshot = {.idCounter = ID_COUNTER_START};
}

KeyStorageV2::~KeyStorageV2() {
    if (ctx) memset(&*ctx, 0, sizeof(CryptContext));
    if (fd) close(fd);
    ctx.reset();
    fd = 0;
}

int KeyStorageV2::readAll() {
    lock_guard guard(editMutex);

    int idCounter = ID_COUNTER_START;
    int colorGroupIdCounter = ID_COUNTER_START;
    list<CryptedColorGroupFlat> cryptedColorGroups;
    list<CryptedNote> cryptedNotes;
    list<CryptedOtpInfo> cryptedOtpNotes;
    list<CryptedPassword> cryptedGeneratedPassws;

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
                if (len >= sizeof(CryptedNoteFlat)) {
                    auto note = CryptedNote{
                            .id = idCounter++,
                            .note = *(CryptedNoteFlat *) buffer
                    };
                    auto historyFlat = readSimpleList<CryptedPasswordFlat>(
                            buffer + sizeof(CryptedNoteFlat), len - sizeof(CryptedNoteFlat)
                    );
                    for_each(historyFlat, [&](const auto &item) {
                        note.history.push_back(CryptedPassword{
                                .id = idCounter++,
                                .data = item
                        });
                    });
                    cryptedNotes.push_back(note);
                }
                break;
            }
            case GenPasswHistory: {
                const auto &hist = readSimpleList<CryptedPasswordFlat>(buffer, len);
                std::for_each(hist.begin(), hist.end(), [&](const auto &item) {
                    cryptedGeneratedPassws.push_back({.id = idCounter++, .data=item});
                });
                break;
            }
            case OtpNote: {
                const auto &otpNotes = readSimpleList<CryptedOtpInfoFlat>(buffer, len);
                std::for_each(otpNotes.begin(), otpNotes.end(), [&](const auto &item) {
                    cryptedOtpNotes.push_back({.id =idCounter++, .data= item});
                });
                break;
            }
            case ColorGroup: {
                const auto &colorGroups = readSimpleList<CryptedColorGroupFlat>(buffer, len);
                std::for_each(colorGroups.begin(), colorGroups.end(), [&](const auto &item) {
                    cryptedColorGroups.push_back(item);
                    colorGroupIdCounter = MAX(colorGroupIdCounter, item.colorGroupId());
                });
                break;
            }
            default:
                cachedInfo.invalidSectionsContains = 1;
                break;
        }
    }

    snapshot(DataSnapshot{
            .idCounter = idCounter,
            .colorGroupIdCounter = colorGroupIdCounter + 1,
            .cryptedColorGroups = make_shared<list<CryptedColorGroupFlat>>(cryptedColorGroups),
            .cryptedNotes = make_shared<list<CryptedNote>>(cryptedNotes),
            .cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(cryptedOtpNotes),
            .cryptedGeneratedPassws = make_shared<list<CryptedPassword>>(cryptedGeneratedPassws),
    });

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
    auto data = snapshot();
    auto writeLen = write(fd, &*fheader, sizeof(StorageHeaderFlat));
    FileSectionFlat fileSection{};

    if (writeLen != sizeof(StorageHeaderFlat)) goto write_file_error;

    // Color group Section
    fileSection = {};
    fileSection.sectionType(ColorGroup);
    fileSection.sectionLen(data.cryptedColorGroups->size() * sizeof(CryptedColorGroupFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(FileSectionFlat)) goto write_file_error;
    for (const auto &group: *data.cryptedColorGroups) {
        writeLen = write(fd, &group, sizeof(group));
        if (writeLen != sizeof(CryptedColorGroupFlat)) goto write_file_error;
    }

    // Crypted Notes
    for (const auto &note: *data.cryptedNotes) {
        fileSection = {};
        fileSection.sectionType(NoteEntry);
        fileSection.sectionLen(sizeof(CryptedNoteFlat) + note.history.size() * sizeof(CryptedPasswordFlat));
        writeLen = write(fd, &fileSection, sizeof(fileSection));
        if (writeLen != sizeof(FileSectionFlat)) goto write_file_error;

        writeLen = write(fd, &note.note, sizeof(CryptedNoteFlat));
        if (writeLen != sizeof(CryptedNoteFlat)) goto write_file_error;

        for (const auto &hist: note.history) {
            writeLen = write(fd, &hist.data, sizeof(hist.data));
            if (writeLen != sizeof(CryptedPasswordFlat)) goto write_file_error;
        }
    }

    // Gen Passw History Section
    fileSection = {};
    fileSection.sectionType(GenPasswHistory);
    fileSection.sectionLen(data.cryptedGeneratedPassws->size() * sizeof(CryptedPasswordFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(FileSectionFlat)) goto write_file_error;
    for (const auto &hist: *data.cryptedGeneratedPassws) {
        writeLen = write(fd, &hist.data, sizeof(hist.data));
        if (writeLen != sizeof(CryptedPasswordFlat)) goto write_file_error;
    }

    // Otp Notes Section
    fileSection = {};
    fileSection.sectionType(OtpNote);
    fileSection.sectionLen(data.cryptedOtpNotes->size() * sizeof(CryptedOtpInfoFlat));
    writeLen = write(fd, &fileSection, sizeof(fileSection));
    if (writeLen != sizeof(FileSectionFlat)) goto write_file_error;
    for (const auto &otp: *data.cryptedOtpNotes) {
        writeLen = write(fd, &otp.data, sizeof(otp.data));
        if (writeLen != sizeof(CryptedOtpInfoFlat)) goto write_file_error;
    }

    close(fd);
    return 0;

    write_file_error:
    close(fd);
    keyError = KEY_WRITE_FILE_ERROR;
    return KEY_WRITE_FILE_ERROR;
}

int KeyStorageV2::saveNewPassw(
        const std::string &path,
        const std::string &passw,
        const std::function<void(const float &)> &progress
) {
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

    auto allItemsCount = float(colorGroups().size() + notes().size() + otpNotes(0).size());
    int progressCount = 0;

    for (const auto &group: colorGroups(TK2_GET_NOTE_FULL)) {
        destStorage->createColorGroup(group);

        if (progress) progress(MIN(1, progressCount++ / allItemsCount));
    }

    for (const auto &note: notes(TK2_GET_NOTE_FULL)) {
        destStorage->createNote(note);

        if (progress) progress(MIN(1, progressCount++ / allItemsCount));
    }

    for (const auto &srcNote: otpNotes(TK2_GET_NOTE_FULL)) {
        auto destNoteList = destStorage->createOtpNotes(
                exportOtpNote(srcNote.id).toUri(),
                TK2_GET_NOTE_FULL
        );
        if (destNoteList.empty())continue;
        auto destNote = destNoteList.front();

        // not export meta
        destNote.colorGroupId = srcNote.colorGroupId;
        destNote.pin = srcNote.pin;

        destStorage->setOtpNote(destNote);

        if (progress) progress(MIN(1, progressCount++ / allItemsCount));
    }

    destStorage->appendPasswHistory(genPasswHistoryList(TK2_GET_NOTE_FULL));

    return destStorage->save();
}

// ---- color group api ----
std::vector<DecryptedColorGroup> KeyStorageV2::colorGroups(uint flags) {
    auto data = snapshot();
    std::vector<DecryptedColorGroup> groups = {};
    groups.reserve(data.cryptedColorGroups->size());
    int info = flags & TK2_GET_NOTE_INFO;

    for (const auto &cryptedGroup: *data.cryptedColorGroups) {
        DecryptedColorGroup group = {};
        group.id = cryptedGroup.colorGroupId();
        group.color = cryptedGroup.color();

        if (info) {
            group.name = cryptedGroup.name.decrypt(
                    ctx->keyForDescription,
                    fheader->cryptType(),
                    fheader->interactionsCount()
            );
        }

        groups.push_back(group);
    }

    return groups;
}

std::shared_ptr<DecryptedColorGroup> KeyStorageV2::createColorGroup(const thekey_v2::DecryptedColorGroup &group) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedColorGroups = make_shared<list<CryptedColorGroupFlat>>(
            list<CryptedColorGroupFlat>(*data.cryptedColorGroups)
    );

    auto dGroup = make_shared<DecryptedColorGroup>(group);
    dGroup->id = data.colorGroupIdCounter++;
    CryptedColorGroupFlat cryptedColorGroupFlat{};
    cryptedColorGroupFlat.colorGroupId(dGroup->id);
    data.cryptedColorGroups->push_back(cryptedColorGroupFlat);
    snapshot(data);

    setColorGroup(*dGroup);
    return dGroup;
}

int KeyStorageV2::setColorGroup(const thekey_v2::DecryptedColorGroup &dGroup) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedColorGroups = make_shared<list<CryptedColorGroupFlat>>(
            list<CryptedColorGroupFlat>(*data.cryptedColorGroups)
    );

    auto cryptedGroup = findPtrBy(*data.cryptedColorGroups, [&](const auto &note) {
        return note.colorGroupId() == dGroup.id;
    });
    if (!cryptedGroup) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }

    cryptedGroup->color(dGroup.color);
    cryptedGroup->name.encrypt(
            dGroup.name,
            ctx->keyForDescription,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    snapshot(data);
    auto error = save();
    return error;
}

int KeyStorageV2::removeColorGroup(long long colorGroupId) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedColorGroups = make_shared<list<CryptedColorGroupFlat>>(
            list<CryptedColorGroupFlat>(*data.cryptedColorGroups)
    );
    data.cryptedNotes = make_shared<list<CryptedNote>>(
            list<CryptedNote>(*data.cryptedNotes)
    );
    data.cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(
            list<CryptedOtpInfo>(*data.cryptedOtpNotes)
    );

    auto cryptedGroup = findItBy(*data.cryptedColorGroups, [&](const auto &item) {
        return item.colorGroupId() == colorGroupId;
    });
    if (cryptedGroup == data.cryptedColorGroups->end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }
    data.cryptedColorGroups->erase(cryptedGroup);

    for_each(data.cryptedNotes->begin(), data.cryptedNotes->end(), [&](CryptedNote &it) {
        if (it.note.colorGroupId() == colorGroupId) {
            it.note.colorGroupId(0);
        }
    });

    for_each(data.cryptedOtpNotes->begin(), data.cryptedOtpNotes->end(), [&](CryptedOtpInfo &it) {
        if (it.data.colorGroupId() == colorGroupId) {
            it.data.colorGroupId(0);
        }
    });


    snapshot(data);
    auto error = save();
    return error;
}


// ---- notes api ----

std::vector<DecryptedNote> KeyStorageV2::notes(uint flags) {
    auto data = snapshot();
    std::vector<DecryptedNote> notes = {};
    notes.reserve(data.cryptedNotes->size());
    for (const auto &it: *data.cryptedNotes) {
        auto itFull = note(it.id, flags);
        if (itFull) notes.push_back(*itFull);
    }
    return notes;
}


std::shared_ptr<DecryptedNote> KeyStorageV2::note(long long id, uint flags) {
    auto data = snapshot();
    auto cryptedNote = findPtrBy(*data.cryptedNotes, [&](const CryptedNote &note) { return note.id == id; });
    if (!cryptedNote) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    auto decryptedNote = std::make_shared<DecryptedNote>();
    decryptedNote->id = id;
    decryptedNote->genTime = cryptedNote->note.genTime();
    decryptedNote->colorGroupId = cryptedNote->note.colorGroupId();

    for (const auto &it: cryptedNote->history) {
        auto itFull = genPasswHistory(it.id, flags);
        if (itFull) decryptedNote->history.push_back(*itFull);
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

shared_ptr<DecryptedNote> KeyStorageV2::createNote(const DecryptedNote &note, uint flags) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedNotes = make_shared<list<CryptedNote>>(list<CryptedNote>(*data.cryptedNotes));

    auto createdId = data.idCounter++;
    data.cryptedNotes->push_back({.id=createdId});
    auto dNote = make_shared<DecryptedNote>(note);
    dNote->id = createdId;
    snapshot(data);

    setNote(*dNote, flags | TK2_SET_NOTE_FORCE | TK2_SET_NOTE_FULL_HISTORY);
    return dNote;
}

int KeyStorageV2::setNote(const thekey_v2::DecryptedNote &dnote, uint flags) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedNotes = make_shared<list<CryptedNote>>(list<CryptedNote>(*data.cryptedNotes));

    auto cryptedNote = findPtrBy(*data.cryptedNotes, [&](const auto &note) { return note.id == dnote.id; });
    if (!cryptedNote) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }

    auto notCmpOld = (flags & TK2_SET_NOTE_FORCE);
    auto setNoteInfo = (flags & TK2_SET_NOTE_INFO);
    auto setNotePassw = (flags & TK2_SET_NOTE_PASSW);
    auto trackHist = (flags & TK2_SET_NOTE_TRACK_HISTORY);
    auto setFullHistory = (flags & TK2_SET_NOTE_FULL_HISTORY);
    auto saveToFile = (flags & TK2_SET_NOTE_SAVE_TO_FILE);
    auto old = note(dnote.id, TK2_GET_NOTE_FULL);

    cryptedNote->note.colorGroupId(dnote.colorGroupId);

    if (setNoteInfo && (notCmpOld || old->site != dnote.site)) {
        cryptedNote->note.site.encrypt(
                dnote.site,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }
    if (setNoteInfo && (notCmpOld || old->login != dnote.login)) {
        cryptedNote->note.login.encrypt(
                dnote.login,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (setNoteInfo && (notCmpOld || old->description != dnote.description)) {
        cryptedNote->note.description.encrypt(
                dnote.description,
                ctx->keyForDescription,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (setNotePassw && (notCmpOld || old->passw != dnote.passw)) {
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
            cryptedNote->history.push_front({.id = data.idCounter++, .data = hist});
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

            cryptedNote->history.push_back({.id= data.idCounter++, .data = hist});
        }
    }

    snapshot(data);
    auto error = saveToFile ? save() : 0;
    return error;
}

int KeyStorageV2::removeNote(long long id) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedNotes = make_shared<list<CryptedNote>>(list<CryptedNote>(*data.cryptedNotes));

    auto cryptedNote = findItBy(*data.cryptedNotes, [&](const auto &item) { return item.id == id; });
    if (cryptedNote == data.cryptedNotes->end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }
    data.cryptedNotes->erase(cryptedNote);

    snapshot(data);
    auto error = save();
    return error;
}

// ---- otp note api ----
std::list<DecryptedOtpNote> KeyStorageV2::createOtpNotes(const std::string &uri, uint flags) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(list<CryptedOtpInfo>(*data.cryptedOtpNotes));

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

        auto createdId = data.idCounter++;
        data.cryptedOtpNotes->push_back({.id=createdId, .data=cryped});
        addedOtpPtrsList.push_back(createdId);
    }
    snapshot(data);


    list<DecryptedOtpNote> addedOtpNotes{};
    for (const auto &otpPtr: addedOtpPtrsList) {
        const auto &otp = otpNote(otpPtr, flags);
        if (otp) addedOtpNotes.push_back(*otp);
    }

    save();
    return addedOtpNotes;
}

std::shared_ptr<DecryptedOtpNote> KeyStorageV2::createOtpNote(const thekey_v2::DecryptedOtpNote &dnote, uint flags) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(list<CryptedOtpInfo>(*data.cryptedOtpNotes));

    auto createdId = data.idCounter++;
    data.cryptedOtpNotes->push_back({.id=createdId});
    auto dNote = make_shared<DecryptedOtpNote>(dnote);
    dNote->id = createdId;
    snapshot(data);

    setOtpNote(*dNote, flags | TK2_SET_NOTE_FORCE | TK2_SET_NOTE_FULL_HISTORY | TK2_SET_NOTE_PASSW);
    return dNote;
}

int KeyStorageV2::setOtpNote(const thekey_v2::DecryptedOtpNote &dnote, uint flags) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(list<CryptedOtpInfo>(*data.cryptedOtpNotes));

    auto cryptedNote = findItBy(*data.cryptedOtpNotes, [&](const auto &item) { return item.id == dnote.id; });
    if (cryptedNote == data.cryptedOtpNotes->end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }

    auto notCmpOld = (flags & TK2_SET_NOTE_FORCE);
    auto setPasswFlag = (flags & TK2_SET_NOTE_PASSW);
    auto old = otpNote(dnote.id, TK2_GET_NOTE_FULL);

    cryptedNote->data.createTime(time(NULL));
    cryptedNote->data.method(dnote.method);
    cryptedNote->data.algorithm(dnote.algo);
    cryptedNote->data.digits(dnote.digits);
    cryptedNote->data.interval(dnote.interval);
    cryptedNote->data.counter(dnote.counter);
    cryptedNote->data.colorGroupId(dnote.colorGroupId);

    if (notCmpOld || old->issuer != dnote.issuer) {
        cryptedNote->data.issuer.encrypt(
                dnote.issuer,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->name != dnote.name) {
        cryptedNote->data.name.encrypt(
                dnote.name,
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (notCmpOld || old->pin != dnote.pin) {
        cryptedNote->data.pin.encrypt(
                dnote.pin,
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if (setPasswFlag && (notCmpOld || old->secret != dnote.secret)) {
        cryptedNote->data.secret.encrypt(
                base32::decodeRaw(dnote.secret),
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    snapshot(data);
    auto error = save();
    return error;
}

std::vector<DecryptedOtpNote> KeyStorageV2::otpNotes(uint flags) {
    auto data = snapshot();
    std::vector<DecryptedOtpNote> notes = {};
    notes.reserve(data.cryptedOtpNotes->size());
    for (const auto &item: *data.cryptedOtpNotes) {
        const auto &otp = otpNote(item.id, flags);
        if (otp) notes.push_back(*otp);
    }

    return notes;
}

std::shared_ptr<DecryptedOtpNote> KeyStorageV2::otpNote(long long id, uint flags, time_t now) {
    auto data = snapshot();
    auto cryptedNote = findPtrBy(*data.cryptedOtpNotes, [&](const auto &item) { return item.id == id; });
    if (!cryptedNote) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    auto decryptedNote = std::make_shared<DecryptedOtpNote>();
    decryptedNote->id = id;
    decryptedNote->createTime = cryptedNote->data.createTime();
    decryptedNote->colorGroupId = cryptedNote->data.colorGroupId();
    decryptedNote->method = cryptedNote->data.method();
    decryptedNote->algo = cryptedNote->data.algorithm();
    decryptedNote->digits = cryptedNote->data.digits();
    decryptedNote->interval = cryptedNote->data.interval();
    decryptedNote->counter = cryptedNote->data.counter();

    if ((flags & TK2_GET_NOTE_INFO) != 0) {
        decryptedNote->issuer = cryptedNote->data.issuer.decrypt(
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        decryptedNote->name = cryptedNote->data.name.decrypt(
                ctx->keyForLogin,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }

    if ((flags & TK2_GET_NOTE_PASSWORD) != 0) {
        decryptedNote->secret = base32::encode(cryptedNote->data.secret.decrypt(
                ctx->keyForOtpPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        ), true);

        decryptedNote->pin = cryptedNote->data.pin.decrypt(
                ctx->keyForPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );

        auto otpInfo = exportOtpNote(id);
        decryptedNote->otpPassw = key_otp::generate(otpInfo, now);

        if ((flags & TK2_GET_NOTE_INCREMENT_HOTP) != 0 && cryptedNote->data.method() == HOTP) {
            cryptedNote->data.counter(cryptedNote->data.counter() + 1);
            save();
        }
    }

    return decryptedNote;
}

OtpInfo KeyStorageV2::exportOtpNote(long long id) {
    auto data = snapshot();
    auto cryptedNote = findPtrBy(*data.cryptedOtpNotes, [&](const auto &item) { return item.id == id; });
    if (!cryptedNote) {
        keyError = KEY_NOTE_NOT_FOUND;
        return {};
    }

    OtpInfo otp{
            .scheme = cryptedNote->data.scheme(),
            .method = cryptedNote->data.method(),
            .algorithm = cryptedNote->data.algorithm(),

            .digits = cryptedNote->data.digits(),
            .interval = cryptedNote->data.interval(),
            .counter = cryptedNote->data.counter()
    };

    otp.issuer = cryptedNote->data.issuer.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    otp.name = cryptedNote->data.name.decrypt(
            ctx->keyForLogin,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    otp.secret = cryptedNote->data.secret.decrypt(
            ctx->keyForOtpPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );

    otp.pin = cryptedNote->data.pin.decrypt(
            ctx->keyForPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    return otp;
}

int KeyStorageV2::removeOtpNote(long long id) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedOtpNotes = make_shared<list<CryptedOtpInfo>>(list<CryptedOtpInfo>(*data.cryptedOtpNotes));

    auto cryptedNote = findItBy(*data.cryptedOtpNotes, [&](const auto &item) { return item.id == id; });
    if (cryptedNote == data.cryptedOtpNotes->end()) {
        keyError = KEY_NOTE_NOT_FOUND;
        return KEY_NOTE_NOT_FOUND;
    }
    data.cryptedOtpNotes->erase(cryptedNote);

    snapshot(data);
    auto error = save();
    return error;
}

// ---- gen passw and hist api ----
std::string KeyStorageV2::genPassword(uint32_t schemeId, int len) {
    if (!len) return "";
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedGeneratedPassws = make_shared<list<CryptedPassword>>(
            list<CryptedPassword>(*data.cryptedGeneratedPassws));

    auto passw = from(thekey_v2::gen_password(schemeId, len));

    CryptedPasswordFlat cryptedPasswordFlat{};
    cryptedPasswordFlat.genTime(time(NULL));
    cryptedPasswordFlat.password.encrypt(
            passw,
            ctx->keyForHistPassw,
            fheader->cryptType(),
            fheader->interactionsCount()
    );
    data.cryptedGeneratedPassws->push_back({.id = data.idCounter++, .data=cryptedPasswordFlat});

    snapshot(data);
    save();
    return passw;
}

std::vector<DecryptedPassw> KeyStorageV2::genPasswHistoryList(const uint &flags) {
    auto data = snapshot();
    std::vector<DecryptedPassw> generatedPasswordHistory = {};
    generatedPasswordHistory.reserve(data.cryptedGeneratedPassws->size());
    for (const auto &it: *data.cryptedGeneratedPassws) {
        auto itFull = genPasswHistory(it.id, flags);
        if (itFull) generatedPasswordHistory.push_back(*itFull);
    }
    return generatedPasswordHistory;
}

std::shared_ptr<DecryptedPassw> KeyStorageV2::genPasswHistory(long long id, const uint &flags) {
    auto data = snapshot();

    shared_ptr<CryptedPassword> histPassw = {};
    for (const auto &item: *data.cryptedGeneratedPassws) {
        if (item.id == id) {
            histPassw = make_shared<CryptedPassword>(item);
            break;
        }
    }
    for (const auto &note: *data.cryptedNotes) {
        if (!histPassw)
            for (const auto &item: note.history) {
                if (item.id == id) {
                    histPassw = make_shared<CryptedPassword>(item);
                    break;
                }
            }
    }
    if (!histPassw) {
        keyError = KEY_HIST_NOT_FOUND;
        return {};
    }
    DecryptedPassw dPassw{};
    dPassw.id = id;
    dPassw.genTime = histPassw->data.genTime();

    if (flags & TK2_GET_NOTE_HISTORY_FULL) {
        dPassw.passw = histPassw->data.password.decrypt(
                ctx->keyForHistPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
    }
    return make_shared<DecryptedPassw>(dPassw);
}

int KeyStorageV2::appendPasswHistory(const std::vector<DecryptedPassw> &hist) {
    lock_guard guard(editMutex);
    auto data = snapshot();
    data.cryptedGeneratedPassws = make_shared<list<CryptedPassword>>(
            list<CryptedPassword>(*data.cryptedGeneratedPassws));

    for (const auto &histItem: hist) {
        CryptedPasswordFlat cryptedPasswordFlat{};
        cryptedPasswordFlat.genTime(histItem.genTime);
        cryptedPasswordFlat.password.encrypt(
                histItem.passw,
                ctx->keyForHistPassw,
                fheader->cryptType(),
                fheader->interactionsCount()
        );
        data.cryptedGeneratedPassws->push_back({.id =data.idCounter++, .data=cryptedPasswordFlat});
    }

    snapshot(data);
    auto error = save();
    return error;
}

// -------------------- private ------------------------------
DataSnapshot KeyStorageV2::snapshot() {
    lock_guard guard(editMutex);
    return _dataSnapshot;
}

void KeyStorageV2::snapshot(const thekey_v2::DataSnapshot &data) {
    lock_guard guard(editMutex);
    _dataSnapshot = data;
}


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




