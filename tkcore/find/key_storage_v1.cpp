//
// Created by panda on 27.01.24.
//

#include "key_storage_v1.h"

using namespace thekey;
using namespace thekey_v1;
using namespace std;

static std::shared_ptr<StorageHeaderShort> storageHeader(int fd);

shared_ptr<thekey::Storage> thekey_v1::storage(int fd, const string &path) {
    auto header = storageHeader(fd);
    if (!header)return {};
    auto storage = make_shared<Storage>();
    storage->file = path;
    storage->storageVersion = header->storageVersion;
    storage->name = header->name;
    storage->description = header->description;
    return storage;
}


static std::shared_ptr<StorageHeaderShort> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageHeaderShort header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V1, SIGNATURE_LEN) != 0
        || header.storageVersion != STORAGE_VER_FIRST)
        return {};
    return make_shared<StorageHeaderShort>(header);
}
