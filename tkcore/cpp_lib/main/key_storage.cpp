//
// Created by panda on 13.01.24.
//

#include <cstring>
#include <list>
#include "public/key_storage.h"
#include "public/key_storage_v1.h"
#include "public/key_storage_v2.h"
#include "thekey_core.h"
#include "utils/common.h"

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

using namespace thekey;
using namespace thekey_v1;
using namespace std;

namespace thekey {

#pragma pack(push, 1)

    struct StorageV1HeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V1
        unsigned char storageVersion;

        [[nodiscard]] int checkSignature() const {
            return memcmp(signature, &storageSignature_V1, SIGNATURE_LEN) == 0;
        }

    };

    struct StorageV2HeaderShort {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V2
        INT32_BIG_ENDIAN(storageVersion)

        [[nodiscard]] int checkSignature() const {
            return memcmp(signature, &storageSignature_V2, SIGNATURE_LEN) == 0;
        }

    };

#pragma pack(pop)

    const char *const storageFormat = ".ckey";
    const char storageSignature_V1[SIGNATURE_LEN] = TKEY_SIGNATURE_V1;
    const char storageSignature_V2[SIGNATURE_LEN] = TKEY_SIGNATURE_V2;

}

std::shared_ptr<Storage> thekey::storage(const std::string &path) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};

    char headerRaw[MAX(sizeof(StorageV1HeaderShort), sizeof(StorageV2HeaderShort))];
    size_t readLen = read(fd, headerRaw, sizeof(headerRaw));
    if (readLen != sizeof(headerRaw)) {
        close(fd);
        return {};
    }
    auto *headerV1 = (StorageV1HeaderShort *) headerRaw;
    auto *headerV2 = (StorageV2HeaderShort *) headerRaw;
    unsigned int version = 0;
    if (headerV1->checkSignature()) {
        version = headerV1->storageVersion;
    } else if (headerV2->checkSignature()) {
        version = headerV2->storageVersion();
    }


    switch (version) {
        case 1: {
            auto storage = thekey_v1::storage(fd, path);
            close(fd);
            return storage;
        }
        case 2: {
            auto storage = thekey_v2::storage(fd, path);
            close(fd);
            return storage;
        }
        default:
            close(fd);
            return {};
    }
}

std::list<Storage> thekey::findStorages(const std::string &filePath) {
    auto foundStorages = std::list<Storage>();
    if (!fs::is_directory(filePath)) {
        auto storageInfo = storage(filePath);
        if (storageInfo) {
            foundStorages.push_back(*storageInfo);
        }
    } else {
        for (const auto &entry: fs::directory_iterator(filePath)) {
            auto sublist = findStorages(entry.path().string());
            if (!sublist.empty()) {
                foundStorages.insert(foundStorages.end(), sublist.begin(), sublist.end());
            }
        }
    }
    return foundStorages;
}

void thekey::findStorages(const std::string &filePath, void (*foundStorageCallback)(const Storage &)) {
    if (!fs::is_directory(filePath)) {
        auto storageInfo = storage(filePath);
        if (storageInfo) foundStorageCallback(*storageInfo);
    } else {
        for (const auto &entry: fs::directory_iterator(filePath)) {
            findStorages(entry.path().string(), foundStorageCallback);
        }
    }
}

