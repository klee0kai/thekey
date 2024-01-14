//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_STORAGE_H
#define THEKEY_KEY_STORAGE_H

#include "thekey_core.h"

#define SIGNATURE_LEN 7
#define TKEY_SIGNATURE {'t', 'k', 'e', 'y', 0x2, 0x47, 0x00}

namespace thekey {

    extern const char *const storageFormat;
    extern const char storageSignature[SIGNATURE_LEN];

    struct Storage {
        std::string file;
        unsigned int storageVersion;
        std::string name;
        std::string description;
    };

    std::list<Storage> findStorages(const std::string &filePath);

    std::shared_ptr<Storage> storage(const std::string &path);

}

#endif //THEKEY_KEY_STORAGE_H
