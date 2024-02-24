//
// Created by panda on 27.01.24.
//

#include "findk2.h"

using namespace thekey;
using namespace thekey_v2;
using namespace std;

static std::shared_ptr<thekey_v2::StorageHeaderShort> storageHeader(int fd);

int thekey_v2::StorageHeaderShort::checkSignature() const {
    return memcmp(signature, &thekey::storageSignature_V2, SIGNATURE_LEN) == 0;
}

shared_ptr<thekey::Storage> thekey_v2::storage(int fd, const std::string &file) {
    auto header = storageHeader(fd);
    if (!header)return {};
    auto storage = make_shared<Storage>();
    storage->file = file;
    storage->storageVersion = header->storageVersion();
    storage->name = header->name;
    storage->description = header->description;
    return storage;
}

static std::shared_ptr<thekey_v2::StorageHeaderShort> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    thekey_v2::StorageHeaderShort header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V2, SIGNATURE_LEN) != 0
        || header.storageVersion() != STORAGE_VER_SECOND) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    return make_shared<thekey_v2::StorageHeaderShort>(header);
}
