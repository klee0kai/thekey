//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY2_H
#define THEKEY_KEY2_H

#include "key_core.h"
#include "list"
#include "format/storage_structure.h"

// get flags 0x00FF
#define TK2_GET_NOTE_PTR_ONLY 0x00
#define TK2_GET_NOTE_INFO 0x01
#define TK2_GET_NOTE_PASSWORD 0x02
#define TK2_GET_NOTE_FULL TK2_GET_NOTE_INFO|TK2_GET_NOTE_PASSWORD

// set flags 0xFF00
#define TK2_SET_NOTE_FORCE 0x100
#define TK2_SET_NOTE_TRACK_HISTORY 0x200

namespace thekey_v2 {

    struct CryptContext {
        unsigned char keyForPassw[KEY_LEN + 1];
        unsigned char keyForOtpPassw[KEY_LEN + 1];
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
        // note unic id
        long long notePtr;

        std::string site;
        std::string login;
        std::string passw;
        std::string description;
        KeyColor color;

        // not editable
        uint64_t genTime;
        std::vector<long long> history;
    };

    struct DecryptedOtpNote {
        // note unic id
        long long notePtr;

        // editable
        std::string issuer;
        std::string name;
        KeyColor color;

        // not editable
        std::string otpPassw;
        key_otp::OtpMethod method;
        uint32_t interval;
        uint64_t createTime;
    };

    struct DecryptedPassw {
        std::string passw;
        uint64_t genTime;
        KeyColor color;
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
        std::list<CryptedOtpInfoFlat> cryptedOtpNotes;
        std::list<CryptedPasswordFlat> cryptedGeneratedPassws;

    public:
        KeyStorageV2(int fd, const std::string &path, const std::shared_ptr<CryptContext> &ctx);

        virtual ~KeyStorageV2();

        virtual int readAll();

        virtual StorageInfo info();

        virtual int save();

        virtual int save(const std::string &path);

        // ---- notes api -----
        /**
         * read all notes
         *
         * @param flags read detail flags TK2_GET_NOTE_*
         * @return
         */
        virtual std::vector<DecryptedNote> notes(uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         *
         * @param notePtr note unic identifier
         * @param flags TK2_GET_NOTE_PASSWORD
         * @return
         */
        virtual std::shared_ptr<DecryptedNote> note(long long notePtr, uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         * @return notePtr note unic identifier
         */
        virtual std::shared_ptr<DecryptedNote> createNote(const DecryptedNote &note = {});

        /**
         *
         * @param dnote new dnote
         * @param flags TK2_SET_NOTE_FORCE / TK2_SET_NOTE_TRACK_HISTORY
         * @return
         */
        virtual int setNote(const DecryptedNote &dnote, uint flags = 0);

        virtual int removeNote(long long notePtr);

        // ---- otp api -----
        /**
         * Create OTP note from uri
         *
         * @param uri otp uri. For example otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
         * @param flags decrypt otp flags
         * @return
         */
        virtual std::list<DecryptedOtpNote> createOtpNotes(const std::string &uri, uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         * Edit OTP dnote OTP dnote from uri
         *
         * @param dnote
         * @param flags
         * @return
         */
        virtual int setOtpNote(const DecryptedOtpNote &dnote, uint flags = 0);

        /**
         * Get all available otp notes
         *
         * @param flags
         * @return
         */
        virtual std::vector<DecryptedOtpNote> otpNotes(uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         * Get Otp note by ptr
         *
         * @param notePtr otp note ptr
         * @param flags
         * @param now now time for tests
         * @return
         */
        virtual std::shared_ptr<DecryptedOtpNote> otpNote(
                long long notePtr,
                uint flags = TK2_GET_NOTE_PTR_ONLY,
                time_t now = time(NULL)
        );

        /**
         * export Otp note to uri
         *
         * @param notePtr  otp note ptr
         * @return uri like this otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
         */
        virtual key_otp::OtpInfo exportOtpNote(long long notePtr);

        /**
         * Remove otp note by ptr
         *
         * @param notePtr otp note ptr
         * @return
         */
        virtual int removeOtpNote(long long notePtr);

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

#endif //THEKEY_KEY2_H
