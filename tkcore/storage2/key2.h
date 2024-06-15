//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY2_H
#define THEKEY_KEY2_H

#include <mutex>
#include "key_core.h"
#include "list"
#include "format/storage_structure.h"

// get flags 0x00FF
#define TK2_GET_NOTE_PTR_ONLY 0x00
#define TK2_GET_NOTE_INFO 0x01
#define TK2_GET_NOTE_PASSWORD 0x02
#define TK2_GET_NOTE_HISTORY_FULL 0x04
#define TK2_GET_NOTE_INCREMENT_HOTP 0x8
#define TK2_GET_NOTE_FULL TK2_GET_NOTE_INFO| TK2_GET_NOTE_PASSWORD | TK2_GET_NOTE_HISTORY_FULL

// set flags 0xFF00
#define TK2_SET_NOTE_FORCE 0x100
#define TK2_SET_NOTE_INFO 0x200
#define TK2_SET_NOTE_PASSW 0x400
#define TK2_SET_NOTE_TRACK_HISTORY 0x800
#define TK2_SET_NOTE_FULL_HISTORY 0x1000
#define TK2_SET_NOTE_SAVE_TO_FILE 0x2000

namespace thekey_v2 {

    struct CryptContext {
        unsigned char keyForPassw[KEY_LEN + 1];
        unsigned char keyForOtpPassw[KEY_LEN + 1];
        unsigned char keyForLogin[KEY_LEN + 1];
        unsigned char keyForHistPassw[KEY_LEN + 1];
        unsigned char keyForDescription[KEY_LEN + 1];
    };

    struct CryptedPassword {
        long long id;
        CryptedPasswordFlat data;
    };

    struct CryptedNote {
        long long id;
        CryptedNoteFlat note;
        std::list<CryptedPassword> history;
    };

    struct CryptedOtpInfo {
        long long id;
        CryptedOtpInfoFlat data;
    };

    struct StorageInfo {
        std::string path;
        std::string name;
        unsigned int storageVersion;
        std::string description;
        //  --- additional fields ----
        int invalidSectionsContains;
    };

    struct DecryptedColorGroup {
        // color unic id
        long long id;

        // editable
        KeyColor color;
        std::string name;
    };

    struct DecryptedPassw {
        // note unic id
        long long id;

        // editable
        std::string passw;
        uint64_t genTime;
    };

    struct DecryptedNote {
        // note unic id
        long long id;

        // editable
        std::string site;
        std::string login;
        std::string passw;
        std::string description;
        long long colorGroupId;

        // not editable
        uint64_t genTime;
        std::vector<DecryptedPassw> history;
    };

    struct DecryptedOtpNote {
        // note unic id
        long long id;

        // editable
        std::string issuer;
        std::string name;
        std::string secret;
        key_otp::OtpMethod method;
        key_otp::OtpAlgo algo;
        uint32_t digits;
        uint32_t interval;
        uint32_t counter;

        // no have in export
        std::string pin;
        long long colorGroupId;

        // not editable
        std::string otpPassw;
        uint64_t createTime;
    };

    struct DataSnapshot {
        int idCounter;
        int colorGroupIdCounter;
        std::shared_ptr<std::list<CryptedColorGroupFlat>> cryptedColorGroups;
        std::shared_ptr<std::list<CryptedNote>> cryptedNotes;
        std::shared_ptr<std::list<CryptedOtpInfo>> cryptedOtpNotes;
        std::shared_ptr<std::list<CryptedPassword>> cryptedGeneratedPassws;
    };


    class KeyStorageV2 {
    private:

        // ---- context ------
        int storageFileDescriptor;
        int singleDescriptorMode;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)
        std::shared_ptr<CryptContext> ctx;
        std::recursive_mutex editMutex;

        // ---- info ------
        std::shared_ptr<StorageHeaderFlat> fheader;
        StorageInfo cachedInfo;
        // ---- payload ----
        DataSnapshot _dataSnapshot;

    public:
        KeyStorageV2(int fd, const std::string &path, const std::shared_ptr<CryptContext> &ctx);

        virtual ~KeyStorageV2();

        virtual void setSingleDescriptorMode(const int &mode);

        virtual int readAll();

        virtual StorageInfo info();

        virtual int save();

        virtual int save(const std::string &path);

        virtual int save(const int &fd);

        virtual int saveNewPassw(
                const std::string &path,
                const std::string &passw,
                const std::function<void(const float &)> &progress = {}
        );

        // ---- group api -----
        /**
         * get all color groups in storage
         * @return
         */
        virtual std::vector<DecryptedColorGroup> colorGroups(uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         * create new color group
         * @param group
         * @return
         */
        virtual std::shared_ptr<DecryptedColorGroup> createColorGroup(const thekey_v2::DecryptedColorGroup &group = {});

        /**
         * set color dGroup
         * @param dGroup
         * @return
         */
        virtual int setColorGroup(const thekey_v2::DecryptedColorGroup &dGroup);

        /**
         * remove color group
         * @param colorGroupId
         * @return
         */
        virtual int removeColorGroup(long long colorGroupId);


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
         * @param id note unic identifier
         * @param flags TK2_GET_NOTE_PASSWORD
         * @return
         */
        virtual std::shared_ptr<DecryptedNote> note(long long id, uint flags = TK2_GET_NOTE_PTR_ONLY);

        /**
         * @return created note. Has id
         */
        virtual std::shared_ptr<DecryptedNote> createNote(const DecryptedNote &note = {}, uint flags = 0);

        /**
         *
         * @param dnote new dnote
         * @param flags TK2_SET_NOTE_FORCE / TK2_SET_NOTE_TRACK_HISTORY
         * @return
         */
        virtual int
        setNote(const DecryptedNote &dnote, uint flags = TK2_SET_NOTE_TRACK_HISTORY | TK2_SET_NOTE_SAVE_TO_FILE);

        virtual int removeNote(long long id);

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
         * create new OTP
         * @param dnote
         * @param flags
         * @return
         */
        virtual std::shared_ptr<DecryptedOtpNote> createOtpNote(const DecryptedOtpNote &dnote = {}, uint flags = 0);

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
         * @param id otp note identifier
         * @param flags
         * @param now now time for tests
         * @return
         */
        virtual std::shared_ptr<DecryptedOtpNote> otpNote(
                long long id,
                uint flags = TK2_GET_NOTE_PTR_ONLY,
                time_t now = time(NULL)
        );

        /**
         * export Otp note to uri
         *
         * @param id  otp note identifier
         * @return uri like this otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example
         */
        virtual key_otp::OtpInfo exportOtpNote(long long id);

        /**
         * Remove otp note by id
         *
         * @param id otp note identifier
         * @return
         */
        virtual int removeOtpNote(long long id);

        // ---- gen password and history api ----
        /**
         * generates a password and immediately saves it to the storage history
         *
         * @param schemeId thekey_v2::findSchemeByFlags result or similar
         * @param len  len of passport
         * @return generated password
         */
        virtual std::string genPassword(uint32_t schemeId, int len);

        /**
         * We get the history of generated passwords
         *
         * @param flags TK2_GET_NOTE_HISTORY_FULL
         * @return
         */
        virtual std::vector<DecryptedPassw> genPasswHistoryList(const uint &flags = 0);

        /**
         * get password from history.
         * The identifier can be either from the history of generated passwords or from the history of notes.
         *
         * @param id history note identifier
         * @param flags TK2_GET_NOTE_HISTORY_FULL
         * @return
         */
        virtual std::shared_ptr<DecryptedPassw> genPasswHistory(
                long long id,
                const uint &flags = TK2_GET_NOTE_HISTORY_FULL
        );

        /**
         * Append generated password history
         *
         * @param hist
         * @return
         */
        virtual int appendPasswHistory(const std::vector<DecryptedPassw> &hist);

    private:

        /**
         * thread securely receiving a snapshot of data
         * @return
         */
        virtual DataSnapshot snapshot();

        /**
         *thread securely set a snapshot of data
         * @param data
         */
        virtual void snapshot(const DataSnapshot &data);


    };

    std::shared_ptr<StorageInfo> storageFullInfo(const std::string &file);

    int createStorage(const thekey::Storage &storage);

    std::shared_ptr<KeyStorageV2> storage(const std::string &path, const std::string &passw);

    std::shared_ptr<KeyStorageV2> storage(const int &fd, const std::string &path, const std::string &passw);

    std::shared_ptr<CryptContext> cryptContext(
            const std::string &passw,
            const uint &interactionsCount,
            const unsigned char *salt
    );

}

#endif //THEKEY_KEY2_H
