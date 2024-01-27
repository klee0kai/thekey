//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_KEY_STORAGE_V1_H
#define THEKEY_KEY_STORAGE_V1_H

#include <cstring>
#include "core/key_core.h"
#include "find.h"

#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512

namespace thekey_v1 {

#pragma pack(push, 1)

    struct StorageHeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V1
        unsigned char storageVersion;
        char name[STORAGE_NAME_LEN];
        char description[STORAGE_DESCRIPTION_LEN];

        [[nodiscard]] int checkSignature() const {
            return memcmp(signature, &thekey::storageSignature_V1, SIGNATURE_LEN) == 0;
        }

    };

#pragma pack(pop)

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);


}

#endif //THEKEY_KEY_STORAGE_V1_H
