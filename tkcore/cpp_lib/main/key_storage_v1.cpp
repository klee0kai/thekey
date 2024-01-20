//
// Created by panda on 13.01.24.
//

#include "thekey_core.h"
#include "public/key_storage_v1.h"
#include "public/key_errors.h"
#include "utils/pass_spliter_v1.h"
#include "utils/common.h"
#include "salt_text/salt_test1.h"
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

using namespace std;
using namespace thekey;
using namespace thekey_v1;


static unsigned char iv[] = "1234567887654321";

#define STORAGE_VER_FIRST 0x01
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512
#define SALT_LEN 2048
#define KEY_LEN 2048
#define ITERATION 1000

#define SITE_LEN 256
#define LOGIN_LEN 256
#define PASSW_LEN 48
#define DESC_LEN 2048

// получать эти данные при инициализации
#define NOTE_PASSW_HIST_LEN 20


/**
 *  !! File Structure !!
 *  0:                      <StorageV1_Header>
 *                          genPasswCount * <CryptedPassw>
 *                          genPasswCount * <CryptedNote>
 */
#pragma pack(push, 1)
/**
 * File header not crypter.
 * Available for every one
 */
struct thekey_v1::StorageV1_Header {
    char signature[SIGNATURE_LEN];// {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00};
    unsigned char storageVersion;
    char name[STORAGE_NAME_LEN];
    char description[STORAGE_DESCRIPTION_LEN];
    unsigned int notesCount; // TODO htons(), htonl(), ntohs(), ntohl()
    unsigned int genPasswCount;// TODO htons(), htonl(), ntohs(), ntohl()
    unsigned char salt[SALT_LEN];// используемая соль при шифровании
};

struct thekey_v1::CryptedPassw {
    unsigned char passw[PASSW_LEN];
    uint64_t genTime;
};

struct thekey_v1::CryptedNote {
    unsigned char site[SITE_LEN];
    unsigned char login[LOGIN_LEN];
    unsigned char passw[PASSW_LEN];
    unsigned char description[DESC_LEN];
    CryptedPassw hist[NOTE_PASSW_HIST_LEN]; // шифруется как обычный genHist

    uint64_t genTime; // не шифруется
    int histLen = 0; // не шифруется
};
#pragma pack(pop)

struct thekey_v1::SplitPasswords {
    unsigned char passwForLogin[PASSW_LEN];
    unsigned char passwForPassw[PASSW_LEN];
    unsigned char passwForDescription[PASSW_LEN];
    unsigned char passwForNoteHistPassw[PASSW_LEN];
    unsigned char passwForGenPassw[PASSW_LEN];
};

struct thekey_v1::CryptContext {
    unsigned char keyForLogin[KEY_LEN];
    unsigned char keyForPassw[KEY_LEN];
    unsigned char keyForDescription[KEY_LEN];
    unsigned char keyForNoteHistPassw[KEY_LEN];
    unsigned char keyForGenPassw[KEY_LEN];
};

static std::shared_ptr<StorageV1_Header> storageHeader(int fd);

static int encode(unsigned char *outText,
                  const unsigned char *originalText,
                  unsigned int buflen, const unsigned char *key);

static int decode(
        unsigned char *outText, const unsigned char *inText,
        unsigned int buflen,
        const unsigned char *key);


std::shared_ptr<thekey::Storage> thekey_v1::storage(int fd, const string &path) {
    auto header = storageHeader(fd);
    if (!header)return {};
    auto storage = make_shared<Storage>();
    storage->file = path;
    storage->storageVersion = header->storageVersion;
    storage->name = header->name;
    storage->description = header->description;
    return storage;
}

std::shared_ptr<StorageV1Info> thekey_v1::storageV1Info(const std::string &file) {
    int fd = open(file.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};
    auto fheader = storageHeader(fd);
    if (!fheader)return {};
    auto info = StorageV1Info{
            .path = file,
            .name = fheader->name,
            .storageVersion = fheader->storageVersion,
            .description = fheader->description,
            .notesCount = fheader->notesCount,
            .genPasswCount = fheader->genPasswCount,
            //technical limitations
            .storageNameLen = STORAGE_NAME_LEN,
            .storageDescriptionLen = STORAGE_DESCRIPTION_LEN,
            .siteLen = SITE_LEN,
            .loginLen = LOGIN_LEN,
            .passwLen = PASSW_LEN,
            .descLen = DESC_LEN,
            .noteMaxHist = NOTE_PASSW_HIST_LEN,
    };
    return make_shared<StorageV1Info>(info);
}

std::shared_ptr<KeyStorageV1> thekey_v1::storage(std::string path, std::string passw) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};
    auto header = storageHeader(fd);
    if (!header) {
        close(fd);
        return {};
    }

    auto splitPassw = std::make_shared<SplitPasswords>();
    memset(&*splitPassw, 0, sizeof(SplitPasswords));
    splitPasswForLogin(splitPassw->passwForLogin, (const unsigned char *) passw.c_str());
    splitPasswForPasswords(splitPassw->passwForPassw, (const unsigned char *) passw.c_str());
    splitPasswForNoteHistPassw(splitPassw->passwForNoteHistPassw, (const unsigned char *) passw.c_str());
    splitPasswForDescription(splitPassw->passwForDescription, (const unsigned char *) passw.c_str());
    splitPasswForGenPassw(splitPassw->passwForGenPassw, (const unsigned char *) passw.c_str());

    auto ctx = std::make_shared<CryptContext>();
    memset(&*ctx, 0, sizeof(CryptContext));
    PKCS5_PBKDF2_HMAC((char *) splitPassw->passwForLogin, PASSW_LEN,
                      header->salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForLogin);
    PKCS5_PBKDF2_HMAC((char *) splitPassw->passwForPassw, PASSW_LEN,
                      header->salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForPassw);
    PKCS5_PBKDF2_HMAC((char *) splitPassw->passwForNoteHistPassw, PASSW_LEN,
                      (unsigned char *) header->salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForNoteHistPassw);
    PKCS5_PBKDF2_HMAC((char *) splitPassw->passwForDescription, PASSW_LEN,
                      (unsigned char *) header->salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForDescription);
    PKCS5_PBKDF2_HMAC((char *) splitPassw->passwForGenPassw, PASSW_LEN,
                      (unsigned char *) header->salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, ctx->keyForGenPassw);
    memset(&*splitPassw, 0, sizeof(SplitPasswords));

    return make_shared<KeyStorageV1>(fd, path, ctx);
}

int thekey_v1::createStorage(const thekey::Storage &storage) {
    int fd = open(storage.file.c_str(), O_RDONLY | O_WRONLY | O_CLOEXEC | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
    if (fd < 0) return KEY_OPEN_FILE_ERROR;
    StorageV1_Header header = {};
    memcpy(header.signature, storageSignature_V1, SIGNATURE_LEN);
    header.storageVersion = STORAGE_VER_FIRST;
    strncpy(header.name, storage.name.c_str(), STORAGE_NAME_LEN);
    strncpy(header.description, storage.description.c_str(), STORAGE_DESCRIPTION_LEN);
    RAND_bytes(header.salt, SALT_LEN);
    auto wroteLen = write(fd, &header, sizeof(header));
    if (wroteLen != sizeof(header)) {
        close(fd);
        return KEY_WRITE_FILE_ERROR;
    }
    close(fd);
    return 0;
}

KeyStorageV1::KeyStorageV1(int fd, string path, shared_ptr<CryptContext> ctx) :
        fd(fd), storagePath(path), ctx(ctx) {
    tempStoragePath = path.substr(0, path.find_last_of('.')) + "-temp.ckey";
}

KeyStorageV1::~KeyStorageV1() {
    memset(&*ctx, 0, sizeof(CryptContext));
    if (fd) close(fd);
    fd = 0;
}

int KeyStorageV1::readAll() {
    fheader = storageHeader(fd);
    cryptedNotes.clear();
    cryptedGeneratedPassws.clear();
    //read notes
    for (int i = 0; i < fheader->notesCount; i++) {
        auto note = CryptedNote{};
        if (read(fd, &note, sizeof(CryptedNote)) != sizeof(CryptedNote)) {
            return KEY_STORAGE_FILE_IS_BROKEN;
        }
        cryptedNotes.push_back(note);
    }

    //read gen passws
    for (int i = 0; i < fheader->genPasswCount; i++) {
        auto genPassw = CryptedPassw{};
        if (read(fd, &genPassw, sizeof(CryptedPassw)) != sizeof(CryptedPassw)) {
            return KEY_STORAGE_FILE_IS_BROKEN;
        }
        cryptedGeneratedPassws.push_back(genPassw);
    }
    return 0;
}

StorageV1Info KeyStorageV1::info() {
    return StorageV1Info{
            .path = storagePath,
            .name = fheader->name,
            .storageVersion = fheader->storageVersion,
            .description = fheader->description,
            .notesCount = fheader->notesCount,
            .genPasswCount = fheader->genPasswCount,
            //technical limitations
            .storageNameLen = STORAGE_NAME_LEN,
            .storageDescriptionLen = STORAGE_DESCRIPTION_LEN,
            .siteLen = SITE_LEN,
            .loginLen = LOGIN_LEN,
            .passwLen = PASSW_LEN,
            .descLen = DESC_LEN,
            .noteMaxHist = NOTE_PASSW_HIST_LEN,
    };
}

int KeyStorageV1::save() {
    auto error = save(tempStoragePath);
    if (error)return error;
    error = save(storagePath);
    if (error) return error;
    // everything went fine, you can delete the backup file
    remove(tempStoragePath.c_str());
    return error;
}

int KeyStorageV1::save(const std::string &path) {
    int fd = open(path.c_str(), O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, S_IRUSR | S_IWUSR);
    if (fd < 0) return -1;
    fheader->notesCount = cryptedNotes.size();
    fheader->genPasswCount = cryptedGeneratedPassws.size();
    auto writeLen = write(fd, &*fheader, sizeof(StorageV1_Header));
    if (writeLen != sizeof(StorageV1_Header)) goto write_file_error;

    for (const auto &item: cryptedNotes) {
        writeLen = write(fd, &item, sizeof(CryptedNote));
        if (writeLen != sizeof(CryptedNote)) goto write_file_error;
    }

    for (const auto &item: cryptedGeneratedPassws) {
        writeLen = write(fd, &item, sizeof(CryptedPassw));
        if (writeLen != sizeof(CryptedPassw)) goto write_file_error;
    }

    close(fd);
    return 0;

    write_file_error:
    close(fd);
    return KEY_WRITE_FILE_ERROR;
}

int KeyStorageV1::saveToNewPassw(const std::string &path, const std::string &passw) {
    auto storageInfo = info();
    auto error = createStorage({.file = path, .storageVersion = storageInfo.storageVersion,
                                       .name = storageInfo.name, .description = storageInfo.description});
    if (error)return error;
    auto newStorage = storage(path, passw);
    newStorage->readAll();
    auto newCryptCtx = newStorage->ctx;

    for (const auto &crypNoteOriginal: cryptedNotes) {
        CryptedNote crypNoteNew = {};
        crypNoteNew.genTime = crypNoteOriginal.genTime;
        crypNoteNew.histLen = crypNoteOriginal.histLen % NOTE_PASSW_HIST_LEN;

        unsigned char siteDecrypted[SITE_LEN];
        memset(siteDecrypted, 0, SITE_LEN);
        if (memcmpr((void *) crypNoteOriginal.site, 0, SITE_LEN) != NULL) {
            decode(siteDecrypted, crypNoteOriginal.site, SITE_LEN, ctx->keyForLogin);
            encode(crypNoteNew.site, siteDecrypted, SITE_LEN, newCryptCtx->keyForLogin);
        }

        unsigned char loginDecrypted[LOGIN_LEN];
        memset(loginDecrypted, 0, LOGIN_LEN);
        if (memcmpr((void *) crypNoteOriginal.login, 0, LOGIN_LEN) != NULL) {
            decode(loginDecrypted, crypNoteOriginal.login, LOGIN_LEN, ctx->keyForLogin);
            encode(crypNoteNew.login, loginDecrypted, LOGIN_LEN, newCryptCtx->keyForLogin);
        }

        unsigned char passwDecrypted[PASSW_LEN];
        memset(passwDecrypted, 0, PASSW_LEN);
        if (memcmpr((void *) crypNoteOriginal.passw, 0, PASSW_LEN) != NULL) {
            decode(passwDecrypted, crypNoteOriginal.passw, PASSW_LEN, ctx->keyForPassw);
            encode(crypNoteNew.passw, passwDecrypted, PASSW_LEN, newCryptCtx->keyForPassw);
        }

        unsigned char descriptionDecrypted[DESC_LEN];
        memset(descriptionDecrypted, 0, DESC_LEN);
        if (memcmpr((void *) crypNoteOriginal.description, 0, DESC_LEN) != NULL) {
            decode(descriptionDecrypted, crypNoteOriginal.description, DESC_LEN, ctx->keyForDescription);
            encode(crypNoteNew.description, descriptionDecrypted, DESC_LEN, newCryptCtx->keyForDescription);
        }

        for (int i = 0; i < crypNoteOriginal.histLen; ++i) {
            memset(passwDecrypted, 0, PASSW_LEN);
            decode(passwDecrypted, crypNoteOriginal.hist[i].passw, PASSW_LEN, ctx->keyForNoteHistPassw);
            encode(crypNoteNew.hist[i].passw, passwDecrypted, PASSW_LEN, newCryptCtx->keyForNoteHistPassw);
            crypNoteNew.hist[i].genTime = crypNoteOriginal.hist[i].genTime;
        }

        newStorage->cryptedNotes.push_back(crypNoteNew);
    }

    for (const auto &cryptedHistOriginal: cryptedGeneratedPassws) {
        CryptedPassw cryptedHistNew = {};

        unsigned char passwDecrypted[PASSW_LEN];
        memset(passwDecrypted, 0, PASSW_LEN);
        decode(passwDecrypted, cryptedHistOriginal.passw, PASSW_LEN, ctx->keyForGenPassw);
        encode(cryptedHistNew.passw, passwDecrypted, PASSW_LEN, newCryptCtx->keyForGenPassw);
        cryptedHistNew.genTime = cryptedHistOriginal.genTime;

        newStorage->cryptedGeneratedPassws.push_back(cryptedHistNew);
    }

    error = newStorage->save();
    return error;
}

std::vector<long long> KeyStorageV1::notes() {
    std::vector<long long> notes = {};
    for (const auto &item: cryptedNotes) {
        notes.push_back((long long) &item);
    }
    return notes;
}

std::shared_ptr<DecryptedNote> KeyStorageV1::note(long long notePtr, int decryptPassw) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return {};
    }
    auto decryptedNote = std::make_shared<DecryptedNote>();
    decryptedNote->genTime = cryptedNote->genTime;
    decryptedNote->histLen = cryptedNote->histLen % NOTE_PASSW_HIST_LEN;

    unsigned char siteBuffer[SITE_LEN];
    memset(siteBuffer, 0, SITE_LEN);
    if (memcmpr((void *) cryptedNote->site, 0, SITE_LEN) != NULL)
        decode(siteBuffer, cryptedNote->site, SITE_LEN, ctx->keyForLogin);
    decryptedNote->site = (char *) siteBuffer;

    unsigned char loginBuffer[LOGIN_LEN];
    memset(loginBuffer, 0, LOGIN_LEN);
    if (memcmpr((void *) cryptedNote->login, 0, LOGIN_LEN) != NULL)
        decode(loginBuffer, cryptedNote->login, LOGIN_LEN, ctx->keyForLogin);
    decryptedNote->login = (char *) loginBuffer;

    if (decryptPassw) {
        unsigned char passwBuffer[PASSW_LEN];
        memset(passwBuffer, 0, PASSW_LEN);
        if (memcmpr((void *) cryptedNote->passw, 0, PASSW_LEN) != NULL)
            decode(passwBuffer, cryptedNote->passw, PASSW_LEN, ctx->keyForPassw);
        decryptedNote->passw = (char *) passwBuffer;
        memset(passwBuffer, 0, PASSW_LEN);
    }

    unsigned char descriptionBuffer[DESC_LEN];
    memset(descriptionBuffer, 0, DESC_LEN);
    if (memcmpr((void *) cryptedNote->description, 0, DESC_LEN) != NULL)
        decode(descriptionBuffer, cryptedNote->description, DESC_LEN, ctx->keyForDescription);
    decryptedNote->description = (char *) descriptionBuffer;

    return decryptedNote;
}

std::list<DecryptedPassw> KeyStorageV1::noteHist(long long notePtr) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return {};
    }
    auto hist = std::list<DecryptedPassw>{};

    for (int i = 0; i < cryptedNote->histLen; ++i) {
        unsigned char passwBuffer[PASSW_LEN];
        memset(passwBuffer, 0, PASSW_LEN);
        decode(passwBuffer, cryptedNote->hist[i].passw, PASSW_LEN, ctx->keyForNoteHistPassw);
        hist.push_back({.passw =(char *) passwBuffer, .genTime = cryptedNote->genTime});
    }

    return hist;
}

long long KeyStorageV1::createNote() {
    cryptedNotes.push_back({});
    const CryptedNote &it = cryptedNotes.back();
    fheader->notesCount = cryptedNotes.size();
    return (long long) &it;
}

int KeyStorageV1::setNote(
        long long notePtr,
        const thekey_v1::DecryptedNote &dnote,
        int notCmpOld
) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return KEY_NOTE_NOT_FOUND;
    }
    auto old = note(notePtr, 1);

    if (notCmpOld || old->site != dnote.site) {
        memset(cryptedNote->site, 0, SITE_LEN);
        encode(cryptedNote->site, (unsigned char *) dnote.site.c_str(), SITE_LEN, ctx->keyForLogin);
    }

    if (notCmpOld || old->login != dnote.login) {
        memset(cryptedNote->login, 0, LOGIN_LEN);
        encode(cryptedNote->login, (unsigned char *) dnote.login.c_str(), LOGIN_LEN, ctx->keyForLogin);
    }

    if (notCmpOld || old->passw != dnote.passw) {
        memset(cryptedNote->passw, 0, PASSW_LEN);
        encode(cryptedNote->passw, (unsigned char *) dnote.passw.c_str(), PASSW_LEN, ctx->keyForPassw);
        cryptedNote->genTime = time(NULL);

        if (!old->passw.empty()) {
            unsigned char cryptedPasswBuffer[PASSW_LEN];
            //encrypt passwHist
            cryptedNote->histLen = MIN(old->histLen + 1, NOTE_PASSW_HIST_LEN);
            memmove(cryptedNote->hist + 1, cryptedNote->hist, (NOTE_PASSW_HIST_LEN - 1) * sizeof(DecryptedPassw));

            memset(cryptedNote->hist[0].passw, 0, PASSW_LEN);
            encode(cryptedNote->hist[0].passw, (unsigned char *) old->passw.c_str(),
                   PASSW_LEN, ctx->keyForNoteHistPassw);
            cryptedNote->hist[0].genTime = old->genTime;
        }
    }

    if (notCmpOld || old->description != dnote.description) {
        memset(cryptedNote->description, 0, DESC_LEN);
        encode(cryptedNote->description, (unsigned char *) dnote.description.c_str(), DESC_LEN, ctx->keyForDescription);
    }

    auto error = save();
    return error;
}

int KeyStorageV1::removeNote(long long notePtr) {
    auto cryptedNote = std::find_if(cryptedNotes.begin(), cryptedNotes.end(),
                                    [notePtr](const CryptedNote &note) {
                                        return (long long) &note == notePtr;
                                    });
    if (cryptedNote == cryptedNotes.end()) {
        return KEY_NOTE_NOT_FOUND;
    }
    cryptedNotes.erase(cryptedNote);

    fheader->notesCount = cryptedNotes.size();
    auto error = save();
    return error;
}

std::string KeyStorageV1::genPassw(int len, int genEncoding) {
    unsigned char passw[PASSW_LEN];
    memset(passw, 0, PASSW_LEN);

    //gen passw
    tkey1_salt_text::genpassw(passw, len, genEncoding);

    //save passw
    unsigned char encodedPassw[PASSW_LEN];
    memset(encodedPassw, 0, PASSW_LEN);
    encode(encodedPassw, passw, PASSW_LEN, ctx->keyForGenPassw);
    CryptedPassw gen = {};
    gen.genTime = time(NULL);
    memcpy(gen.passw, encodedPassw, PASSW_LEN);

    cryptedGeneratedPassws.push_back(gen);
    fheader->genPasswCount = cryptedGeneratedPassws.size();
    auto error = save();
    return (char *) passw;
}

std::list<DecryptedPassw> KeyStorageV1::genPasswHist() {
    auto hist = list<DecryptedPassw>();
    for (const auto &item: cryptedGeneratedPassws) {
        unsigned char passwBuffer[PASSW_LEN];
        memset(passwBuffer, 0, PASSW_LEN);
        decode(passwBuffer, item.passw, PASSW_LEN, ctx->keyForGenPassw);
        hist.push_back({.passw = (char *) passwBuffer, .genTime = item.genTime});
        memset(passwBuffer, 0, PASSW_LEN);
    }
    return hist;
}

static std::shared_ptr<StorageV1_Header> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageV1_Header header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V1, SIGNATURE_LEN) != 0
        || header.storageVersion != STORAGE_VER_FIRST)
        return {};
    return make_shared<thekey_v1::StorageV1_Header>(header);
}


static int encode(unsigned char *outText,
                  const unsigned char *originalText,
                  unsigned int buflen, const unsigned char *key) {
    unsigned char *saltedText = new unsigned char[buflen];
    RAND_bytes(saltedText, buflen);
    tkey1_salt_text::salt_text(saltedText, originalText, buflen);

    memset(outText, 0, buflen);
    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    EVP_EncryptInit(ctx, EVP_aes_256_cbc(), key, iv);
    int outlen = 0;
    if (!EVP_EncryptUpdate(ctx, outText + 1, &outlen, saltedText + 1, buflen - 1)) {
        EVP_CIPHER_CTX_free(ctx);
        memset(saltedText, 0, buflen);
        delete[] saltedText;
        return -1;
    }
    outText[0] = saltedText[0];

    EVP_CIPHER_CTX_free(ctx);

    memset(saltedText, 0, buflen);
    delete[] saltedText;
    return buflen;
}

static int decode(unsigned char *outText, const unsigned char *inText, unsigned int buflen,
                  const unsigned char *key) {

    unsigned char *saltedText = new unsigned char[buflen];

    memset(outText, 0, buflen);
    memset(saltedText, 0, buflen);

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    EVP_DecryptInit(ctx, EVP_aes_256_cbc(), key, iv);
    int outlen = 0;
    if (!EVP_DecryptUpdate(ctx, saltedText + 1, &outlen, inText + 1, buflen - 1)) {
        EVP_CIPHER_CTX_free(ctx);
        memset(saltedText, 0, buflen);
        delete[]saltedText;
        return -1;
    }
    saltedText[0] = inText[0];


    EVP_CIPHER_CTX_free(ctx);

    tkey1_salt_text::desalt_text(outText, saltedText, buflen);

    memset(saltedText, 0, buflen);
    delete[]saltedText;
    return buflen;


}