//
// Created by panda on 27.01.24.
//

#include <openssl/sha.h>
#include "findk1.h"
#include "../otpauth/tools/base64.h"

using namespace thekey;
using namespace thekey_v1;
using namespace std;

static std::shared_ptr<StorageHeaderShort> storageHeader(int fd);

[[nodiscard]] int thekey_v1::StorageHeaderShort::checkSignature() const {
    return memcmp(signature, &thekey::storageSignature_V1, SIGNATURE_LEN) == 0;
}

string StorageHeaderShort::saltSha256() const {
    char sha256Buf[SHA256_DIGEST_LENGTH + 1];
    memset(sha256Buf, 0, sizeof(sha256Buf));
    ::SHA256(salt, SALT_LEN, (unsigned char *) sha256Buf);
    auto result = std::vector<uint8_t>(SHA256_DIGEST_LENGTH);
    result.assign(sha256Buf, sha256Buf + SHA256_DIGEST_LENGTH);
    return base64::encode(result);
}

shared_ptr<thekey::Storage> thekey_v1::storage(int fd, const string &path) {
    auto header = storageHeader(fd);
    if (!header) return {};
    auto storage = make_shared<Storage>();
    storage->file = path;
    storage->storageVersion = header->storageVersion;
    storage->name = header->name;
    storage->description = header->description;
    storage->saltSha256 = header->saltSha256();
    return storage;
}


static std::shared_ptr<StorageHeaderShort> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageHeaderShort header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V1, SIGNATURE_LEN) != 0
        || header.storageVersion != STORAGE_VER_FIRST) {
        keyError = KEY_STORAGE_FILE_IS_BROKEN;
        return {};
    }
    return make_shared<StorageHeaderShort>(header);
}
