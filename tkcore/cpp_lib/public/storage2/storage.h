//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_STORAGE_H
#define THEKEY_STORAGE_H

#include "thekey_core.h"
#include "public/key_storage.h"
#include "list"
#include "salt_text/s_text.h"
#include "public/storage2/storage_structure.h"

namespace thekey_v2 {

    struct SplitPasswords;
    struct CryptContext;

    class KeyStorageV2 {

    public:
        KeyStorageV2(int fd, const std::string &path,const std::shared_ptr<CryptContext>& ctx);

        virtual ~KeyStorageV2();

        virtual int readAll();

        virtual StorageFullHeader info();

    private:


        int fd;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)
        std::shared_ptr<CryptContext> ctx;

        std::shared_ptr<StorageFullHeader> fheader;

    };

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);

    int createStorage(const thekey::Storage &storage);

    std::shared_ptr<KeyStorageV2> storage(std::string path, std::string passw);

}

#endif //THEKEY_STORAGE_H
