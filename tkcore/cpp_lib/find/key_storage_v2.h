//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_KEY_STORAGE_V2_H
#define THEKEY_KEY_STORAGE_V2_H

#include <cstring>
#include "core/key_core.h"
#include "key_storage.h"

#define FILE_TYPE_OWNER_LEN 256
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512

namespace thekey_v2 {

#pragma pack(push, 1)

    struct StorageHeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V2
        INT32_BIG_ENDIAN(storageVersion)
        char fileTypeOwner[FILE_TYPE_OWNER_LEN]; // typeOwnerText

        char name[STORAGE_NAME_LEN];
        char description[STORAGE_DESCRIPTION_LEN];

        [[nodiscard]] int checkSignature() const {
            return memcmp(signature, &thekey::storageSignature_V2, SIGNATURE_LEN) == 0;
        }
    };

#pragma pack(pop)

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);


}

#endif //THEKEY_KEY_STORAGE_V2_H
