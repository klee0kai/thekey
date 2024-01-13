//
// Created by panda on 2020-01-26.
//

#ifndef THEKEY_KEY_MANAGER_CTX_H
#define THEKEY_KEY_MANAGER_CTX_H

#include <string>
#include <fcntl.h>
#include <unistd.h>
#include <vector>
#include <ctime>

#define FILE_VER_FIRST 0x01
#define SIGNATURE_LEN 7
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512
extern char storageSignature[SIGNATURE_LEN];
extern size_t FileVer1_HEADER_LEN;
extern size_t CryptedPassw_LEN;
extern size_t CryptedNote_LEN;

#define FILE_PATH_LEN 255

#define SITE_LEN 256
#define LOGIN_LEN 256
#define PASSW_LEN 48
#define DESC_LEN 2048

// TODO на уровне java сделать ограничение текста корректным по неиспользуемым байтам при солении
// получать эти данные при инициализации
#define NOTE_PASSW_HIST_LEN 20

#define SALT_LEN 2048

struct CryptContext { // набор ключей для работы с текущим хранилищем - шифрование, дешифрование
    unsigned char *keyForLogin = NULL; // логин
    unsigned char *keyForPassw = NULL; // пароль
    unsigned char *keyForNoteHistPassw = NULL; // история паролей в логине
    unsigned char *keyForDescr = NULL; // описание записи
    unsigned char *keyForGenPassw = NULL; // для сгенерированных паролей
};

struct Storage {
    const char *file = NULL;
    const char *name = NULL;// имя хранилища (видно всем)
    const char *description = NULL;// описание хранилища (видно всем)

    void release() {
        if (file != NULL)delete[] file;
        if (name != NULL)delete[] name;
        if (description != NULL)delete[] description;
    }
};

struct DecryptedPassw {
    unsigned char passw[PASSW_LEN];
    uint64_t genTime;
};

struct DecryptedNote { // <----> CryptedNote
    unsigned char site[SITE_LEN];
    unsigned char login[LOGIN_LEN];
    unsigned char passw[PASSW_LEN];
    unsigned char description[DESC_LEN];
    DecryptedPassw hist[NOTE_PASSW_HIST_LEN];

    uint64_t genTime;
    int histLen = 0;
};

/**
 *  !! СТРУКТУРА ФАЙЛА !!
 *  0:                      <FileVer1_Header>
 *                          genPasswCount * <CryptedPassw>
 *                          genPasswCount * <CryptedNote>
 */

#pragma pack(push, 1)
struct FileVer1_Header {
    char signature[SIGNATURE_LEN];// сигнатура файла ckey для подтверждени, должна быть равна {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00};
    char ver;// версия хранилища (видно всем)
    char name[STORAGE_NAME_LEN];// имя хранилища (видно всем)
    char description[STORAGE_DESCRIPTION_LEN];// описание хранилища (видно всем)
    unsigned int notesCount; // TODO htons(), htonl(), ntohs(), ntohl()
    unsigned int genPasswCount;// TODO htons(), htonl(), ntohs(), ntohl()
    char salt[SALT_LEN];// используемая соль при шифровании
};

struct CryptedPassw {
    unsigned char passw[PASSW_LEN];
    uint64_t genTime;
};

struct CryptedNote {
    unsigned char site[SITE_LEN];
    unsigned char login[LOGIN_LEN];
    unsigned char passw[PASSW_LEN];
    unsigned char description[DESC_LEN];
    CryptedPassw hist[NOTE_PASSW_HIST_LEN]; // шифруется как обычный genHist

    uint64_t genTime; // не шифруется
    int histLen = 0; // не шифруется
};
#pragma pack(pop)


extern CryptContext storCryptContext;


// note - заполненная карточка  паролями и описаниями
// gen passw - сгенерированный, незаполненный пароль
namespace key_manager_ctx {

    int createStorage(const Storage storage);

    int copyStorage(const char *source, const Storage storage, bool saveOriginal);

    int isLogined();

    int login(const unsigned char *file, const unsigned char *passw);

    int changePassw(const unsigned char *passw);

    const char *getLoggedStoragePath();

    void setLoggedStoragePath(const char *path);

    void unLogin();

    long long *getNotes();

    long long *getGenPassds();

    DecryptedPassw *getGenPassw(long long id, CryptContext cryptContext = storCryptContext);

    int setGenPassw(long long id, DecryptedPassw *decryptedPassw, CryptContext cryptContext = storCryptContext);

    DecryptedNote *getNoteItem(long long note, bool decryptPassw, CryptContext cryptContext = storCryptContext);

    int setNote(long long note, const DecryptedNote *dnote, CryptContext cryptContext = storCryptContext,
                int bNoCmprOld = 0);

    long long createNote();

    void rmNote(long note);

    unsigned char *genPassw(int len, int genEncoding, CryptContext cryptContext = storCryptContext);

    int saveChanges();

}


#endif //THEKEY_KEY_MANAGER_CTX_H
