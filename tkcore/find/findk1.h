//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_FINDK1_H
#define THEKEY_FINDK1_H

#include <cstring>
#include "key_core.h"
#include "key_find.h"

#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512

namespace thekey_v1 {

#pragma pack(push, 1)

    struct StorageHeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V1
        unsigned char storageVersion;
        char name[STORAGE_NAME_LEN];
        char description[STORAGE_DESCRIPTION_LEN];

        [[nodiscard]] int checkSignature() const;

    };

#pragma pack(pop)

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);


}

#endif //THEKEY_FINDK1_H
