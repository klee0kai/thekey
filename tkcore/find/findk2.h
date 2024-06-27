//
// Created by panda on 27.01.24.
//

#ifndef THEKEY_FINDK2_H
#define THEKEY_FINDK2_H

#include <cstring>
#include "key_core.h"
#include "key_find.h"

#define FILE_TYPE_OWNER_LEN 256
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512
#define SALT_LEN 2048

namespace thekey_v2 {

#pragma pack(push, 1)

    struct StorageHeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V2
        INT32_BIG_ENDIAN(storageVersion)

        char fileTypeOwner[FILE_TYPE_OWNER_LEN]; // typeOwnerText

        char name[STORAGE_NAME_LEN];
        char description[STORAGE_DESCRIPTION_LEN];
        unsigned char salt[SALT_LEN]; // crypt/decrypt saltSha256

        [[nodiscard]] int checkSignature() const;

        [[nodiscard]] std::string saltSha256() const;

    };

#pragma pack(pop)

    std::shared_ptr<thekey::Storage> storage(int fd, const std::string &file);


}

#endif //THEKEY_FINDK2_H
