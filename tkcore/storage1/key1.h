//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_STORAGE_V1_H
#define THEKEY_KEY_STORAGE_V1_H

#include "key_core.h"
#include "list"
#include "salt/salt1.h"


// get flags 0x00FF
#define TK1_GET_NOTE_PTR_ONLY 0x00
#define TK1_GET_NOTE_INFO 0x01
#define TK1_GET_NOTE_PASSWORD 0x02
#define TK1_GET_NOTE_HISTORY_FULL 0x04
#define TK1_GET_NOTE_FULL TK1_GET_NOTE_INFO | TK1_GET_NOTE_PASSWORD | TK1_GET_NOTE_HISTORY_FULL

// set flags 0xFF00
#define TK1_SET_NOTE_FORCE 0x100

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
        unsigned int loginLen;
        unsigned int passwLen;
        unsigned int descLen;
        unsigned int noteMaxHist;
    };

    struct DecryptedPassw {
        // note unic id
        long long histPtr;

        // editable
        std::string passw;
        uint64_t genTime;
    };

    struct DecryptedNote {
        // note unic id
        long long notePtr;

        // editable
        std::string site;
        std::string login;
        std::string passw;
        std::string description;

        // not editable
        uint64_t genTime;
        std::vector<DecryptedPassw> history;
    };


    class KeyStorageV1 {

    public:
        KeyStorageV1(int fd, std::string path, std::shared_ptr<CryptContext> passw);

        virtual ~KeyStorageV1();

        virtual int readAll();

        virtual StorageV1Info info();

        virtual int save();

        virtual int save(const std::string &path);

        virtual int saveNewPassw(const std::string &path, const std::string &passw);

        // ---- notes api -----

        virtual std::vector<DecryptedNote> notes(uint flags = TK1_GET_NOTE_PTR_ONLY);

        virtual std::shared_ptr<DecryptedNote> note(long long notePtr, uint flags = TK1_GET_NOTE_PTR_ONLY);

        virtual std::shared_ptr<DecryptedNote> createNote(const DecryptedNote &note = {});

        virtual int setNote(const DecryptedNote &note, uint flags = 0);

        virtual int removeNote(long long notePtr);

        // ---- gen password and history api ----
        virtual std::string genPassw(int len, int genEncoding = ENC_NUM_ONLY);

        virtual std::vector<DecryptedPassw> genPasswHistoryList(const uint &flags = 0);

        virtual std::shared_ptr<DecryptedPassw> genPasswHistory(
                long long histPtr,
                const uint &flags = TK1_GET_NOTE_HISTORY_FULL
        );

    private:


        int fd;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)
        std::shared_ptr<CryptContext> ctx; // decrypt keys

        std::shared_ptr<StorageV1_Header> fheader;
        std::list<CryptedNote> cryptedNotes;
        std::list<CryptedPassw> cryptedGeneratedPassws;

    };

    std::shared_ptr<StorageV1Info> storageV1Info(const std::string &file);

    int createStorage(const thekey::Storage &file);

    std::shared_ptr<KeyStorageV1> storage(std::string path, std::string passw);

}

#endif //THEKEY_KEY_STORAGE_V1_H
