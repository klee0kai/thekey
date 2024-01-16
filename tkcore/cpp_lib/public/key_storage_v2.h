//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_STORAGE_V2_H
#define THEKEY_KEY_STORAGE_V2_H

#include "thekey_core.h"
#include "key_storage.h"
#include "list"
#include "salt_text/s_text.h"

namespace thekey_v2 {

    enum EncryptTypes {
        Default,
        AES256,
    };

    struct StorageV2_Header;
    struct CryptContext;

    class KeyStorageV2 {

    public:
        KeyStorageV2(int fd, std::string path, std::shared_ptr<CryptContext> passw);

        virtual ~KeyStorageV2();

    private:


        int fd;
        std::string storagePath;
        std::string tempStoragePath; // predict file write. (protect broken bits)

        std::shared_ptr<StorageV2_Header> fheader;

    };

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);

    int createStorage(const thekey::Storage &storage);

    std::shared_ptr<KeyStorageV2> storage(std::string path, std::string passw);

}

#endif //THEKEY_KEY_STORAGE_V2_H
