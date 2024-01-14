//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_STORAGE_V1_H
#define THEKEY_KEY_STORAGE_V1_H

#include "thekey_core.h"
#include "key_storage.h"
#include "list"

namespace thekey_v1 {

    struct StorageV1_Header;
    struct CryptedPassw;
    struct SplitPasswords;
    struct CryptContext;
    struct CryptedNote;

    struct StorageV1Info {
        std::string path;
        std::string name;
        unsigned int storageVersion;
        std::string description;
        unsigned int notesCount;
        unsigned int genPasswCount;
        //technical limitations
        unsigned int storageNameLen;
        unsigned int storageDescriptionLen;
        unsigned int siteLen;
        unsigned int passwLen;
        unsigned int descName;
        unsigned int noteMaxHist;
    };

    struct DecryptedNote {
        std::string site;
        std::string login;
        std::string passw;
        std::string description;
        uint64_t genTime;
        int histLen = 0;
    };

    struct DecryptedPassw {
        std::string passw;
        uint64_t genTime;
    };

    class KeyStorageV1 {

    public:
        KeyStorageV1(int fd, std::string path, std::shared_ptr<CryptContext> passw);

        virtual ~KeyStorageV1();

        virtual int readAll();

        virtual StorageV1Info info();

        virtual int save();

        virtual int save(std::string path);

        virtual std::vector<long long> notes();

        virtual std::shared_ptr<DecryptedNote> note(long long notePtr, int decryptPassw);

        virtual std::list<DecryptedPassw> noteHist(long long notePtr);

        virtual long long createNote();

        virtual int setNote(long long notePtr, const DecryptedNote &note, int notCmpOld = 0);

        virtual int removeNote(long long notePtr);

        virtual std::string genPassw(int len, int genEncoding);

        virtual std::string genPasswNote(long long notePtr);

        virtual std::list<DecryptedPassw> genPasswHist();

    private:


        int fd;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)
        std::shared_ptr<CryptContext> ctx; // decrypt keys

        std::shared_ptr<StorageV1_Header> fheader;
        std::list<CryptedNote> cryptedNotes;
        std::list<CryptedPassw> cryptedGeneratedPassws;

    };

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);

    std::shared_ptr< StorageV1Info> storageV1Info(const std::string &file);

    int createStorage(const thekey::Storage &file);

    std::shared_ptr<KeyStorageV1> storage(std::string path, std::string passw);

}

#endif //THEKEY_KEY_STORAGE_V1_H
