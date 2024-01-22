//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_STORAGE_H
#define THEKEY_STORAGE_H

#include "thekey_core.h"
#include "public/key_storage.h"
#include "list"
#include "salt_text/salt_test1.h"
#include "public/storage2/storage_structure.h"

namespace thekey_v2 {

    struct CryptContext;
    struct CryptedNote;

    struct StorageInfo {
        std::string path;
        std::string name;
        unsigned int storageVersion;
        std::string description;
        //  --- additional fields ----
        int invalidSectionsContains;
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

    };

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);

    std::shared_ptr<StorageInfo> storageFullInfo(const std::string &file);

    int createStorage(const thekey::Storage &storage);

    std::shared_ptr<KeyStorageV2> storage(const std::string &path, const std::string &passw);

}

#endif //THEKEY_STORAGE_H
