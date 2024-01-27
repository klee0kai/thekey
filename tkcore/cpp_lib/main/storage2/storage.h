//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_STORAGE_H
#define THEKEY_STORAGE_H

#include "core/key_core.h"
#include "find/key_storage.h"
#include "list"
#include "salt_text/salt_test1.h"
#include "storage_structure.h"

#define TK2_GET_NOTE_PASSWORD 0x1

#define TK2_SET_NOTE_FORCE 0x1
#define TK2_SET_NOTE_TRACK_HISTORY 0x2

namespace thekey_v2 {

    struct CryptContext {
        unsigned char keyForPassw[KEY_LEN + 1];
        unsigned char keyForLogin[KEY_LEN + 1];
        unsigned char keyForHistPassw[KEY_LEN + 1];
        unsigned char keyForDescription[KEY_LEN + 1];
    };

    struct CryptedNote {
        CryptedNoteFlat note;
        std::list<CryptedPasswordFlat> history;
    };

    struct StorageInfo {
        std::string path;
        std::string name;
        unsigned int storageVersion;
        std::string description;
        //  --- additional fields ----
        int invalidSectionsContains;
    };

    struct DecryptedNote {
        std::string site;
        std::string login;
        std::string passw;
        std::string description;
        uint64_t genTime;
        uint32_t color;
        std::vector<long long> history;
    };

    struct DecryptedPassw {
        std::string passw;
        uint64_t genTime;
        uint32_t color;
    };


    class KeyStorageV2 {
    private:

        // ---- context ------
        int fd;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)
        std::shared_ptr<CryptContext> ctx;

        // ---- info ------
        std::shared_ptr<StorageHeaderFlat> fheader;
        StorageInfo cachedInfo;
        // ---- payload ----
        std::list<CryptedNote> cryptedNotes;
        std::list<CryptedPasswordFlat> cryptedGeneratedPassws;

    public:
        KeyStorageV2(int fd, const std::string &path, const std::shared_ptr<CryptContext> &ctx);

        virtual ~KeyStorageV2();

        virtual int readAll();

        virtual StorageInfo info();

        virtual int save();

        virtual int save(const std::string &path);

        virtual int saveToNewPassw(const std::string &path, const std::string &passw);


        // ---- notes api -----
        virtual std::vector<long long> notes();

        /**
         *
         * @param notePtr note unic identifier
         * @param flags TK2_GET_NOTE_PASSWORD
         * @return
         */
        virtual std::shared_ptr<DecryptedNote> note(long long notePtr, uint flags = 0);

        /**
         * @return notePtr note unic identifier
         */
        virtual long long createNote();

        /**
         *
         * @param notePtr dnote unic identifier
         * @param dnote new dnote
         * @param flags TK2_SET_NOTE_FORCE / TK2_SET_NOTE_TRACK_HISTORY / TK2_SET_NOTE_DEEP_COPY
         * @return
         */
        virtual int setNote(long long notePtr, const DecryptedNote &dnote, uint flags = 0);

        virtual int removeNote(long long notePtr);

        // ---- gen password and history api ----
        /**
         * generates a password and immediately saves it to the storage history
         *
         * @param encodingType thekey_v2::find_scheme_type_by_flags result or similar
         * @param len  len of passport
         * @return generated password
         */
        virtual std::string genPassword(uint32_t encodingType, int len);

        /**
         * We get the history of generated passwords
         *
         * @return
         */
        virtual std::vector<long long> passwordsHistory();

        /**
         * get password from history.
         * The identifier can be either from the history of generated passwords or from the history of notes.
         *
         * @param histPtr
         * @return
         */
        virtual std::shared_ptr<DecryptedPassw> passwordHistory(long long histPtr);

    };

    std::shared_ptr<StorageInfo> storageFullInfo(const std::string &file);

    int createStorage(const thekey::Storage &storage);

    std::shared_ptr<KeyStorageV2> storage(const std::string &path, const std::string &passw);

    std::shared_ptr<CryptContext> cryptContext(
            const std::string &passw,
            const uint &interactionsCount,
            const unsigned char *salt
    );

}

#endif //THEKEY_STORAGE_H
