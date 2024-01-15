//
// Created by panda on 13.01.24.
//

#include <cstring>
#include <list>
#include "public/key_storage.h"
#include "public/key_storage_v1.h"
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
    struct StorageFileHeader {
        char signature[SIGNATURE_LEN];
        unsigned char storageVersion;
    };
#pragma pack(pop)

    const char *const storageFormat = ".ckey";
    const char storageSignature[SIGNATURE_LEN] = TKEY_SIGNATURE;

}

std::shared_ptr<Storage> thekey::storage(const std::string &path) {
    int fd = open(path.c_str(), O_RDONLY | O_CLOEXEC);
    if (fd == -1) return {};

    StorageFileHeader header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        close(fd);
        return {};
    }
    if (memcmp(&header.signature, &storageSignature, SIGNATURE_LEN) != 0) {
        close(fd);
        return {};
    }
    auto version = header.storageVersion;

    switch (version) {
        case 1: {
            auto storage = thekey_v1::storage(fd, path);
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
