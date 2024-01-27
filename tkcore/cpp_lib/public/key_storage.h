//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_STORAGE_H
#define THEKEY_KEY_STORAGE_H

#include "core/key_core.h"

namespace thekey {

    extern const char *const storageFormat;
    extern const char storageSignature_V1[SIGNATURE_LEN];
    extern const char storageSignature_V2[SIGNATURE_LEN];

    struct Storage {
        std::string file;
        unsigned int storageVersion;
        std::string name;
        std::string description;
    };

    std::list<Storage> findStorages(const std::string &filePath);

    void findStorages(const std::string &filePath, void (*foundStorageCallback)(const Storage &));

    std::shared_ptr<Storage> storage(const std::string &path);

}

#endif //THEKEY_KEY_STORAGE_H
