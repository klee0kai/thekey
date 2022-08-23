//
// Created by panda on 2020-01-26.
//

#include "key_manager_ctx.h"
#include "libs/pass_spliter.h"
#include "salt_text/s_text.h"
#include "libs/utils.h"


#include "libs/def_header.h"


#define KEY_LEN 2048
#define ITERATION 1000


char storageSignature[SIGNATURE_LEN] = {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00};
size_t FileVer1_HEADER_LEN = sizeof(FileVer1_Header);
size_t CryptedPassw_LEN = sizeof(CryptedPassw);
size_t CryptedNote_LEN = sizeof(CryptedNote);


static std::vector<CryptedNote *> cryptedNotes;
static std::vector<CryptedPassw *> genPassds;

static char *filePath;
static char *tempFilePath;// предзапись изменений
CryptContext storCryptContext;


static FileVer1_Header fheader = {}; // TODO делать динамическим

static unsigned char iv[] = "1234567887654321";


static int saveToFile(char *path);

static int encode(unsigned char *outText, const unsigned char *inText, unsigned int buflen,
                  const unsigned char *passw);

static int decode(unsigned char *outText, const unsigned char *inText, unsigned int buflen,
                  const unsigned char *passw);

int key_manager_ctx::createStorage(const Storage storage) {
    int fd = open(storage.file, O_RDONLY | O_WRONLY | O_CLOEXEC | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
    if (fd < 0) return -1;
    FileVer1_Header fheader = {.ver = FILE_VER_FIRST};
    memcpy(fheader.signature, storageSignature, SIGNATURE_LEN);
    if (storage.name != NULL)strncpy(fheader.name, storage.name, STORAGE_NAME_LEN);
    if (storage.description != NULL)strncpy(fheader.description, storage.description, STORAGE_DESCRIPTION_LEN);
    RAND_bytes((unsigned char *) fheader.salt, SALT_LEN);
    write(fd, &fheader, sizeof(FileVer1_Header));
    close(fd);
    return 0;
}

int key_manager_ctx::copyStorage(const char *source, const Storage storage, bool saveOriginal) {
    int isSameFile = strcmp(source, storage.file) == 0;

    if (!saveOriginal) {
        // change header's fields
        int fd = open(source, O_CLOEXEC | O_RDWR);
        FileVer1_Header fheader;

        lseek(fd, 0, SEEK_SET);

        int readed = read(fd, &fheader, FileVer1_HEADER_LEN);

        if (readed != FileVer1_HEADER_LEN) {
            close(fd);
            return -1;
        }

        lseek(fd, 0, SEEK_SET);
        if (storage.name != NULL)strncpy(fheader.name, storage.name, STORAGE_NAME_LEN);
        if (storage.description != NULL)strncpy(fheader.description, storage.description, STORAGE_DESCRIPTION_LEN);
        write(fd, &fheader, FileVer1_HEADER_LEN);
        close(fd);

        //move file
        if (!isSameFile && rename(source, storage.file))
            return -1;

        return 0;
    }
    if (isSameFile)
        return -1;


    // copy/move new file
    int fdOriginal = open(source, O_RDONLY | O_CLOEXEC);
    if (fdOriginal < 0) return -1;

    int fd = open(storage.file, O_RDONLY | O_WRONLY | O_CLOEXEC | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
    if (fd < 0) {
        close(fdOriginal);
        return -1;
    }

    //copy header
    FileVer1_Header fheader;

    int readed = read(fdOriginal, &fheader, FileVer1_HEADER_LEN);
    int writed = 0;

    if (readed != FileVer1_HEADER_LEN) {
        close(fdOriginal);
        close(fd);
        unlink(storage.file);
        return -1;
    }
    if (storage.name != NULL)strncpy(fheader.name, storage.name, STORAGE_NAME_LEN);
    if (storage.description != NULL)strncpy(fheader.description, storage.description, STORAGE_DESCRIPTION_LEN);

    writed = write(fd, &fheader, FileVer1_HEADER_LEN);
    if (writed != readed) {
        close(fdOriginal);
        close(fd);
        unlink(storage.file);
        return -1;
    }

    //copy storage
    char buffer[BUFSIZ];
    while (readed = read(fdOriginal, buffer, BUFSIZ), readed > 0) {
        writed = write(fd, buffer, readed);
        if (writed != readed) {
            close(fdOriginal);
            close(fd);
            unlink(storage.file);
            return -1;
        }
    }

    close(fd);
    close(fdOriginal);
    if (!saveOriginal)unlink(source);

    return 0;
}

int key_manager_ctx::isLogined() {
    return storCryptContext.keyForPassw != NULL && storCryptContext.keyForLogin != NULL &&
           storCryptContext.keyForDescr != NULL;
}

const char *key_manager_ctx::getLoggedStoragePath() {
    return filePath;
}

void key_manager_ctx::setLoggedStoragePath(const char *file) {
    if (filePath == NULL) return;
    strcpy(filePath, (const char *) file);
    strcpy(tempFilePath, (const char *) file);
    int pointIndex;
    for (pointIndex = strlen(filePath) - 1;
         pointIndex > 0 && file[pointIndex] != '.' && file[pointIndex] != '/'; pointIndex--);
    if (filePath[pointIndex] == '/')pointIndex = strlen(filePath) - 1;
    strcpy(tempFilePath + pointIndex, "-temp.t_key");
}

int key_manager_ctx::login(const unsigned char *file, const unsigned char *passw) {
    filePath = new char[FILE_PATH_LEN];
    tempFilePath = new char[FILE_PATH_LEN];
    strcpy(filePath, (const char *) file);
    strcpy(tempFilePath, (const char *) file);
    int pointIndex;
    for (pointIndex = strlen(filePath) - 1;
         pointIndex > 0 && file[pointIndex] != '.' && file[pointIndex] != '/'; pointIndex--);
    if (filePath[pointIndex] == '/')pointIndex = strlen(filePath) - 1;
    strcpy(tempFilePath + pointIndex, "-temp.t_key");


    int fd = open((char *) file, O_CLOEXEC | O_RDONLY);
    if (fd < 0) {
        Storage storage = {};
        storage.file = (char *) file;
        createStorage(storage);
        fd = open((char *) file, O_CLOEXEC | O_RDONLY);
    }

    if (fd < 0 | read(fd, &fheader, sizeof(fheader)) != sizeof(fheader)) {
        unLogin();
        return -1;
    }

    if (memcmp(fheader.signature, storageSignature, SIGNATURE_LEN) != 0
        || fheader.ver != 1
           && fheader.description[STORAGE_DESCRIPTION_LEN - 1] != 0 ||
        fheader.name[STORAGE_NAME_LEN - 1] != 0) {
        unLogin();
        return -1;
    }


    {
        //read notes
        size_t blockSize = sizeof(CryptedNote);
        char *buff = new char[blockSize];
        for (int i = 0; i < fheader.notesCount && read(fd, buff, blockSize) == blockSize; i++) {
            CryptedNote *note = new CryptedNote{};
            memcpy(note, buff, blockSize);
            cryptedNotes.push_back(note);
        }
        memset(buff, 0, blockSize);
        delete[]buff;
    }

    {
        //read  gen passwds
        size_t blockSize = sizeof(CryptedPassw);
        char *buff = new char[blockSize];
        for (int i = 0; i < fheader.genPasswCount && read(fd, buff, blockSize) == blockSize; i++) {
            CryptedPassw *genP = new CryptedPassw{};
            memcpy(genP, buff, blockSize);
            genPassds.push_back(genP);
        }
        memset(buff, 0, blockSize);
        delete[]buff;
    }
    close(fd);


    unsigned char *passwForLogin = new unsigned char[PASSW_LEN];
    unsigned char *passwForPassw = new unsigned char[PASSW_LEN];
    unsigned char *passwForDescription = new unsigned char[PASSW_LEN];
    unsigned char *passwForNoteHistPassw = new unsigned char[PASSW_LEN];
    unsigned char *passwForGenPassw = new unsigned char[PASSW_LEN];
    memset(passwForPassw, 0, PASSW_LEN);
    memset(passwForLogin, 0, PASSW_LEN);
    memset(passwForNoteHistPassw, 0, PASSW_LEN);
    memset(passwForDescription, 0, PASSW_LEN);
    memset(passwForGenPassw, 0, PASSW_LEN);
    splitPasswForPasswords(passwForPassw, passw);
    splitPasswForLogin(passwForLogin, passw);
    splitPasswForNoteHistPassw(passwForNoteHistPassw, passw);
    splitPasswForDescription(passwForDescription, passw);
    splitPasswForGenPassw(passwForGenPassw, passw);


    storCryptContext.keyForLogin = new unsigned char[KEY_LEN];
    storCryptContext.keyForPassw = new unsigned char[KEY_LEN];
    storCryptContext.keyForNoteHistPassw = new unsigned char[KEY_LEN];
    storCryptContext.keyForDescr = new unsigned char[KEY_LEN];
    storCryptContext.keyForGenPassw = new unsigned char[KEY_LEN];
    memset(storCryptContext.keyForLogin, 0, KEY_LEN);
    memset(storCryptContext.keyForPassw, 0, KEY_LEN);
    memset(storCryptContext.keyForNoteHistPassw, 0, KEY_LEN);
    memset(storCryptContext.keyForDescr, 0, KEY_LEN);
    memset(storCryptContext.keyForGenPassw, 0, KEY_LEN);

    PKCS5_PBKDF2_HMAC((char *) passwForLogin, PASSW_LEN,
                      (unsigned char *) fheader.salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, storCryptContext.keyForLogin);
    PKCS5_PBKDF2_HMAC((char *) passwForPassw, PASSW_LEN,
                      (unsigned char *) fheader.salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, storCryptContext.keyForPassw);
    PKCS5_PBKDF2_HMAC((char *) passwForNoteHistPassw, PASSW_LEN,
                      (unsigned char *) fheader.salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, storCryptContext.keyForNoteHistPassw);
    PKCS5_PBKDF2_HMAC((char *) passwForDescription, PASSW_LEN,
                      (unsigned char *) fheader.salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, storCryptContext.keyForDescr);
    PKCS5_PBKDF2_HMAC((char *) passwForGenPassw, PASSW_LEN,
                      (unsigned char *) fheader.salt, SALT_LEN,
                      ITERATION, EVP_sha512_256(),
                      KEY_LEN, storCryptContext.keyForGenPassw);


    memset(passwForLogin, 0, PASSW_LEN);
    memset(passwForPassw, 0, PASSW_LEN);
    memset(passwForNoteHistPassw, 0, PASSW_LEN);
    memset(passwForDescription, 0, PASSW_LEN);
    memset(passwForGenPassw, 0, PASSW_LEN);
    delete[] passwForLogin;
    delete[] passwForPassw;
    delete[] passwForNoteHistPassw;
    delete[] passwForDescription;
    delete[] passwForGenPassw;

    return 0;
}

int key_manager_ctx::changePassw(const unsigned char *passw) {


    CryptContext newStoreCryptContext;
    { // подготовка новых ключей шифрования в newStoreCryptContext

        unsigned char *passwForLogin = new unsigned char[PASSW_LEN];
        unsigned char *passwForPassw = new unsigned char[PASSW_LEN];
        unsigned char *passwForDescription = new unsigned char[PASSW_LEN];
        unsigned char *passwForNoteHistPassw = new unsigned char[PASSW_LEN];
        unsigned char *passwForGenPassw = new unsigned char[PASSW_LEN];
        memset(passwForPassw, 0, PASSW_LEN);
        memset(passwForLogin, 0, PASSW_LEN);
        memset(passwForNoteHistPassw, 0, PASSW_LEN);
        memset(passwForDescription, 0, PASSW_LEN);
        memset(passwForGenPassw, 0, PASSW_LEN);
        splitPasswForPasswords(passwForPassw, passw);
        splitPasswForLogin(passwForLogin, passw);
        splitPasswForNoteHistPassw(passwForNoteHistPassw, passw);
        splitPasswForDescription(passwForDescription, passw);
        splitPasswForGenPassw(passwForGenPassw, passw);


        newStoreCryptContext.keyForLogin = new unsigned char[KEY_LEN];
        newStoreCryptContext.keyForPassw = new unsigned char[KEY_LEN];
        newStoreCryptContext.keyForNoteHistPassw = new unsigned char[KEY_LEN];
        newStoreCryptContext.keyForDescr = new unsigned char[KEY_LEN];
        newStoreCryptContext.keyForGenPassw = new unsigned char[KEY_LEN];
        memset(newStoreCryptContext.keyForLogin, 0, KEY_LEN);
        memset(newStoreCryptContext.keyForPassw, 0, KEY_LEN);
        memset(newStoreCryptContext.keyForNoteHistPassw, 0, KEY_LEN);
        memset(newStoreCryptContext.keyForDescr, 0, KEY_LEN);
        memset(newStoreCryptContext.keyForGenPassw, 0, KEY_LEN);

        PKCS5_PBKDF2_HMAC((char *) passwForLogin, PASSW_LEN,
                          (unsigned char *) fheader.salt, SALT_LEN,
                          ITERATION, EVP_sha512_256(),
                          KEY_LEN, newStoreCryptContext.keyForLogin);
        PKCS5_PBKDF2_HMAC((char *) passwForPassw, PASSW_LEN,
                          (unsigned char *) fheader.salt, SALT_LEN,
                          ITERATION, EVP_sha512_256(),
                          KEY_LEN, newStoreCryptContext.keyForPassw);
        PKCS5_PBKDF2_HMAC((char *) passwForNoteHistPassw, PASSW_LEN,
                          (unsigned char *) fheader.salt, SALT_LEN,
                          ITERATION, EVP_sha512_256(),
                          KEY_LEN, newStoreCryptContext.keyForNoteHistPassw);
        PKCS5_PBKDF2_HMAC((char *) passwForDescription, PASSW_LEN,
                          (unsigned char *) fheader.salt, SALT_LEN,
                          ITERATION, EVP_sha512_256(),
                          KEY_LEN, newStoreCryptContext.keyForDescr);
        PKCS5_PBKDF2_HMAC((char *) passwForGenPassw, PASSW_LEN,
                          (unsigned char *) fheader.salt, SALT_LEN,
                          ITERATION, EVP_sha512_256(),
                          KEY_LEN, newStoreCryptContext.keyForGenPassw);


        memset(passwForLogin, 0, PASSW_LEN);
        memset(passwForPassw, 0, PASSW_LEN);
        memset(passwForNoteHistPassw, 0, PASSW_LEN);
        memset(passwForDescription, 0, PASSW_LEN);
        memset(passwForGenPassw, 0, PASSW_LEN);
        delete[] passwForLogin;
        delete[] passwForPassw;
        delete[] passwForNoteHistPassw;
        delete[] passwForDescription;
        delete[] passwForGenPassw;
    }

    { // пересохранение записей по новым ключам шифрования
        long long *notes = key_manager_ctx::getNotes();
        for (int i = 0; notes[i] != NULL; i++) {
            struct DecryptedNote *noteItem = getNoteItem(notes[i], true);
            setNote(notes[i], noteItem, newStoreCryptContext, 1);
            memset(noteItem, 0, sizeof(DecryptedNote));
            delete noteItem;

        }
    }


    { // пересохранение истории сгенерированных паролей по новом ключам шифрования
        long long *passwds = key_manager_ctx::getGenPassds();
        for (int i = 0; passwds[i] != NULL; i++) {
            DecryptedPassw *decryptedPassw = getGenPassw(passwds[i]);
            setGenPassw(passwds[i], decryptedPassw, newStoreCryptContext);
            memset(decryptedPassw, 0, sizeof(DecryptedPassw));
            delete decryptedPassw;
        }
    }

    { // сохранение новых ключей шифрования как основных
        if (storCryptContext.keyForLogin != NULL) {
            memset(storCryptContext.keyForLogin, 0, KEY_LEN);
            delete[] storCryptContext.keyForLogin;
            storCryptContext.keyForLogin = NULL;
        }

        if (storCryptContext.keyForPassw != NULL) {
            memset(storCryptContext.keyForPassw, 0, KEY_LEN);
            delete[] storCryptContext.keyForPassw;
            storCryptContext.keyForPassw = NULL;
        }

        if (storCryptContext.keyForNoteHistPassw != NULL) {
            memset(storCryptContext.keyForNoteHistPassw, 0, KEY_LEN);
            delete[] storCryptContext.keyForNoteHistPassw;
            storCryptContext.keyForNoteHistPassw = NULL;
        }

        if (storCryptContext.keyForDescr != NULL) {
            memset(storCryptContext.keyForDescr, 0, KEY_LEN);
            delete[] storCryptContext.keyForDescr;
            storCryptContext.keyForDescr = NULL;
        }

        if (storCryptContext.keyForGenPassw != NULL) {
            memset(storCryptContext.keyForGenPassw, 0, KEY_LEN);
            delete[] storCryptContext.keyForGenPassw;
            storCryptContext.keyForGenPassw = NULL;
        }


        storCryptContext = newStoreCryptContext;
    }

    return 0;
}

void key_manager_ctx::unLogin() {
    if (storCryptContext.keyForLogin != NULL) {
        memset(storCryptContext.keyForLogin, 0, KEY_LEN);
        delete[] storCryptContext.keyForLogin;
        storCryptContext.keyForLogin = NULL;
    }

    if (storCryptContext.keyForPassw != NULL) {
        memset(storCryptContext.keyForPassw, 0, KEY_LEN);
        delete[] storCryptContext.keyForPassw;
        storCryptContext.keyForPassw = NULL;
    }

    if (storCryptContext.keyForNoteHistPassw != NULL) {
        memset(storCryptContext.keyForNoteHistPassw, 0, KEY_LEN);
        delete[] storCryptContext.keyForNoteHistPassw;
        storCryptContext.keyForNoteHistPassw = NULL;
    }

    if (storCryptContext.keyForDescr != NULL) {
        memset(storCryptContext.keyForDescr, 0, KEY_LEN);
        delete[] storCryptContext.keyForDescr;
        storCryptContext.keyForDescr = NULL;
    }

    if (storCryptContext.keyForGenPassw != NULL) {
        memset(storCryptContext.keyForGenPassw, 0, KEY_LEN);
        delete[] storCryptContext.keyForGenPassw;
        storCryptContext.keyForGenPassw = NULL;
    }

    if (filePath != NULL) {
        memset(filePath, 0, strlen(filePath));
        delete[] filePath;
        filePath = NULL;
    }

    if (tempFilePath != NULL) {
        memset(tempFilePath, 0, strlen(tempFilePath));

        delete[]tempFilePath;
        tempFilePath = NULL;
    }


    for (auto iter = cryptedNotes.begin(); iter != cryptedNotes.end(); iter++) {
        if (*iter != NULL) {
            memset(*iter, 0, sizeof(CryptedNote));
            delete *iter;
        }
    }
    cryptedNotes.clear();

    for (auto iter = genPassds.begin(); iter != genPassds.end(); iter++) {
        if (*iter != NULL) {
            memset(*iter, 0, sizeof(CryptedPassw));
            delete *iter;
        }
    }
    genPassds.clear();


    fheader = {};

}

long long *key_manager_ctx::getNotes() {
    int len = cryptedNotes.size();
    long long *notes = new long long[len + 1];
    memset(notes, 0, sizeof(long long) * len);

    int i = 0;
    for (auto iter = cryptedNotes.begin(); iter != cryptedNotes.end(); iter++, i++) {
        notes[i] = ((long) *iter);
    }
    notes[len] = 0;
    return notes;
}


long long *key_manager_ctx::getGenPassds() {
    int len = genPassds.size();
    long long *passw = new long long[len + 1];
    memset(passw, 0, sizeof(long) * len);

    int i = 0;
    for (auto iter = genPassds.begin(); iter != genPassds.end(); iter++, i++) {
        passw[i] = ((long) *iter);
    }
    passw[len] = 0;
    return passw;
}


DecryptedNote *key_manager_ctx::getNoteItem(long long ptNote, bool decryptPassw, CryptContext cryptContext) {
    struct CryptedNote *cryptedNote = (CryptedNote *) ptNote;
    struct DecryptedNote *decryptedNote = new DecryptedNote{};

    //non encoded fields
    decryptedNote->genTime = cryptedNote->genTime;
    decryptedNote->histLen = cryptedNote->histLen % NOTE_PASSW_HIST_LEN;


    {
        //decode site
        memset(decryptedNote->site, 0, SITE_LEN);
        if (memcmpr((void *) cryptedNote->site, 0, SITE_LEN) != NULL)
            decode(decryptedNote->site, cryptedNote->site, SITE_LEN, cryptContext.keyForLogin);
    }
    {
        //decode login
        memset(decryptedNote->login, 0, LOGIN_LEN);
        if (memcmpr((void *) cryptedNote->login, 0, LOGIN_LEN) != NULL)
            decode(decryptedNote->login, cryptedNote->login, LOGIN_LEN, cryptContext.keyForLogin);
    }
    if (decryptPassw) {
        //decode passw
        memset(decryptedNote->passw, 0, PASSW_LEN);
        if (memcmpr((void *) cryptedNote->passw, 0, PASSW_LEN) != NULL)
            decode(decryptedNote->passw, cryptedNote->passw, PASSW_LEN, cryptContext.keyForPassw);
    }
    {
        //decode description
        memset(decryptedNote->description, 0, DESC_LEN);
        if (memcmpr((void *) cryptedNote->description, 0, DESC_LEN) != NULL)
            decode(decryptedNote->description, cryptedNote->description, DESC_LEN, cryptContext.keyForDescr);
    }

    if (decryptPassw) {
        //decode hist
        memset(decryptedNote->hist, 0, NOTE_PASSW_HIST_LEN * sizeof(DecryptedPassw));
        for (int i = 0; i < decryptedNote->histLen; i++) {
            decode(decryptedNote->hist[i].passw, cryptedNote->hist[i].passw, PASSW_LEN,
                   cryptContext.keyForNoteHistPassw);
            decryptedNote->hist[i].genTime = cryptedNote->hist[i].genTime;
        }
    }

    return decryptedNote;
}


DecryptedPassw *key_manager_ctx::getGenPassw(long long ptGenPassw, CryptContext cryptContext) {
    struct CryptedPassw *cryptedNote = (CryptedPassw *) ptGenPassw;
    struct DecryptedPassw *decryptedPassw = new DecryptedPassw();
    memset(decryptedPassw->passw, 0, PASSW_LEN);
    decode(decryptedPassw->passw, cryptedNote->passw, PASSW_LEN, cryptContext.keyForGenPassw);
    decryptedPassw->genTime = cryptedNote->genTime;
    return decryptedPassw;
}


int key_manager_ctx::setGenPassw(long long ptGenPassw, DecryptedPassw *decryptedPassw, CryptContext cryptContext) {
    struct CryptedPassw *cryptedNote = (CryptedPassw *) ptGenPassw;
    memset(cryptedNote->passw, 0, PASSW_LEN);
    encode(cryptedNote->passw, decryptedPassw->passw, PASSW_LEN, cryptContext.keyForGenPassw);
    cryptedNote->genTime = decryptedPassw->genTime;
    saveChanges();
    return 0;
}


int key_manager_ctx::setNote(long long note, const DecryptedNote *dnote, CryptContext cryptContext, int bNoCmprOld) {
    struct CryptedNote *cryptedNote = (CryptedNote *) note;

    struct DecryptedNote *dOldNote = key_manager_ctx::getNoteItem(note, true);


    if (bNoCmprOld || strcmp((char *) dOldNote->site, (char *) dnote->site)) {
        //encrypt login
        memset(cryptedNote->site, 0, SITE_LEN);
        encode(cryptedNote->site, dnote->site, SITE_LEN, cryptContext.keyForLogin);
    }

    if (bNoCmprOld || strcmp((char *) dOldNote->login, (char *) dnote->login)) {
        //encrypt login
        memset(cryptedNote->login, 0, LOGIN_LEN);
        encode(cryptedNote->login, dnote->login, LOGIN_LEN, cryptContext.keyForLogin);
    }

    if (bNoCmprOld || strcmp((char *) dOldNote->passw, (char *) dnote->passw)) {
        //encrypt passw
        memset(cryptedNote->passw, 0, PASSW_LEN);
        encode(cryptedNote->passw, dnote->passw, PASSW_LEN, cryptContext.keyForPassw);
        cryptedNote->genTime = (uint64_t) time(NULL);

        if (strlen((const char *) dOldNote->passw) > 0) {
            //encrypt passwHist
            cryptedNote->histLen = MIN(dOldNote->histLen + 1, NOTE_PASSW_HIST_LEN);
            memmove(dOldNote->hist + 1, dOldNote->hist, (NOTE_PASSW_HIST_LEN - 1) * sizeof(DecryptedPassw));

            *dOldNote->hist = {.genTime= (uint64_t) time(NULL)};
            strncpy((char *) dOldNote->hist->passw, (const char *) dOldNote->passw, PASSW_LEN);

            for (int i = 0; i < cryptedNote->histLen; i++) {
                encode(cryptedNote->hist[i].passw, (unsigned char *) dOldNote->hist[i].passw, PASSW_LEN,
                       cryptContext.keyForNoteHistPassw);
                cryptedNote->hist[i].genTime = dOldNote->hist[i].genTime;
            }

        }
    }

    if (bNoCmprOld || strcmp((char *) dOldNote->description, (char *) dnote->description)) {
        //encrypt description
        memset(cryptedNote->description, 0, DESC_LEN);
        encode(cryptedNote->description, dnote->description, DESC_LEN, cryptContext.keyForDescr);
    }

    memset(dOldNote, 0, sizeof(DecryptedNote));
    delete dOldNote;

    saveChanges();
    return 0;
}


long long key_manager_ctx::createNote() {
    struct CryptedNote *cryptedNote = new CryptedNote{};
    cryptedNotes.push_back(cryptedNote);
    fheader.notesCount = cryptedNotes.size();
    return (long long) cryptedNote;
}

void key_manager_ctx::rmNote(long note) {
    struct CryptedNote *cryptedNote = (CryptedNote *) note;
    for (auto iter = cryptedNotes.begin(); iter != cryptedNotes.end(); iter++) {
        if (*iter == cryptedNote) {
            cryptedNotes.erase(iter);
            fheader.notesCount = cryptedNotes.size();


            saveChanges();
            return;
        }
    }
}


unsigned char *key_manager_ctx::genPassw(int len, int genEncoding, CryptContext cryptContext) {
    unsigned char *passw = new unsigned char[PASSW_LEN];
    memset(passw, 0, PASSW_LEN);

    //gen passw
    s_text::genpassw(passw, len, genEncoding);

    {
        //save passw
        unsigned char *encodedPassw = new unsigned char[PASSW_LEN];
        memset(encodedPassw, 0, PASSW_LEN);
        encode(encodedPassw, passw, PASSW_LEN, cryptContext.keyForGenPassw);
        struct CryptedPassw *gen = new CryptedPassw{};
        gen->genTime = (uint64_t) time(NULL);
        memcpy(gen->passw, encodedPassw, PASSW_LEN);
        genPassds.push_back(gen);
        fheader.genPasswCount = genPassds.size();
        delete[]encodedPassw;
    }

    saveChanges();
    return passw;

}


int key_manager_ctx::saveChanges() {
    saveToFile(tempFilePath);

    saveToFile(filePath);

    // все прошло штатно, можно удалять запасной файл
    remove(tempFilePath);

    return 0;
}

static int saveToFile(char *path) {
    int fd = open(path, O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, S_IRUSR | S_IWUSR);
    if (fd < 0) return -1;
    fheader.notesCount = cryptedNotes.size();
    fheader.genPasswCount = genPassds.size();

    write(fd, &fheader, sizeof(FileVer1_Header));

    for (auto iter = cryptedNotes.begin(); iter != cryptedNotes.end(); iter++) {
        struct CryptedNote *cryptedNote = (CryptedNote *) *iter;
        write(fd, cryptedNote, sizeof(CryptedNote));
    }
    for (auto iter = genPassds.begin(); iter != genPassds.end(); iter++) {
        struct CryptedPassw *passw = (CryptedPassw *) *iter;
        write(fd, passw, sizeof(CryptedPassw));
    }
    close(fd);

    return 0;
}


static int
encode(unsigned char *outText, const unsigned char *originalText, unsigned int buflen, const unsigned char *key) {
    unsigned char *saltedText = new unsigned char[buflen];
    RAND_bytes(saltedText, buflen);
    s_text::salt_text(saltedText, originalText, buflen);

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

    s_text::desalt_text(outText, saltedText, buflen);

    memset(saltedText, 0, buflen);
    delete[]saltedText;
    return buflen;


}

