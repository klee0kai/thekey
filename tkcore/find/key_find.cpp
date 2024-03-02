//
// Created by panda on 13.01.24.
//

#include <list>

#include "key_find.h"
#include "findk1.h"
#include "findk2.h"
#include "key_core.h"

namespace fs = std::filesystem;

using namespace std;
using namespace thekey;
using namespace thekey_v1;
using namespace thekey_v2;

std::shared_ptr<Storage> thekey::storage(const std::string &path) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};

    char headerRaw[MAX(sizeof(thekey_v1::StorageHeaderShort), sizeof(thekey_v2::StorageHeaderShort))];
    size_t readLen = read(fd, headerRaw, sizeof(headerRaw));
    auto *headerV1 = readLen >= sizeof(thekey_v1::StorageHeaderShort)
                     ? (thekey_v1::StorageHeaderShort *) headerRaw : NULL;
    auto *headerV2 = readLen >= sizeof(thekey_v2::StorageHeaderShort)
                     ? (thekey_v2::StorageHeaderShort *) headerRaw : NULL;
    unsigned int version = 0;
    if (headerV1 && headerV1->checkSignature()) {
        version = headerV1->storageVersion;
    } else if (headerV2 && headerV2->checkSignature()) {
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
            keyError = KEY_STORAGE_VERSION_NOT_SUPPORT;
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

void thekey::findStorages(
        const std::string &filePath,
        const std::function<void(const Storage &)> &foundStorageCallback
) {
    if (!fs::is_directory(filePath)) {
        auto storageInfo = storage(filePath);
        if (storageInfo && foundStorageCallback) foundStorageCallback(*storageInfo);
    } else {
        for (const auto &entry: fs::directory_iterator(filePath)) {
            findStorages(entry.path().string(), foundStorageCallback);
        }
    }
}

