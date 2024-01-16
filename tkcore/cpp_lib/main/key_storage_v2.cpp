

#include "thekey_core.h"
#include "public/key_storage_v2.h"
#include "public/key_errors.h"
#include "utils/pass_spliter_v1.h"
#include "utils/common.h"
#include "salt_text/s_text.h"
#include <cstring>

#include <openssl/evp.h>
#include <openssl/objects.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>
#include <openssl/aes.h>
#include <openssl/bio.h>
#include <openssl/kdf.h>
#include <openssl/sha.h>
#include <openssl/rand.h>
#include <algorithm>

using namespace std;
using namespace thekey;
using namespace thekey_v2;

#define FILE_TYPE_OWNER_LEN 256

static char typeOwnerText[FILE_TYPE_OWNER_LEN] = "TheKey key storage. Designed by Andrei Kuzubov / Klee0kai. "
                                                 "Follow original app https://github.com/klee0kai/thekey";

#define STORAGE_VER_SECOND 0x02
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512
#define SALT_LEN 2048
#define KEY_LEN 2048

#define SITE_LEN 256
#define LOGIN_LEN 256
#define PASSW_LEN 48
#define DESC_LEN 2048

/**
 *  !! File Structure !!
 */
#pragma pack(push, 1)
struct thekey_v2::StorageV2_Header {
    char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V2;
    INT32_BIG_ENDIAN(storageVersion) // STORAGE_VER_SECOND
    char fileTypeOwner[FILE_TYPE_OWNER_LEN]; // typeOwnerText

    char name[STORAGE_NAME_LEN];
    char description[STORAGE_DESCRIPTION_LEN];

    unsigned char salt[SALT_LEN]; // crypt/decrypt salt
    INT32_BIG_ENDIAN(interactionsCount)// crypt/decrypt interaction count
    INT32_BIG_ENDIAN_ENUM(encryptionType, EncryptTypes) // crypt type
};

#pragma pack(pop)

static std::shared_ptr<StorageV2_Header> storageHeader(int fd);


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

int thekey_v2::createStorage(const thekey::Storage &storage) {
    int fd = open(storage.file.c_str(), O_RDONLY | O_WRONLY | O_CLOEXEC | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
    if (fd < 0) return KEY_OPEN_FILE_ERROR;
    StorageV2_Header header = {};
    memcpy(header.signature, storageSignature_V2, SIGNATURE_LEN);
    header.storageVersion(STORAGE_VER_SECOND);
    memcpy(header.fileTypeOwner, typeOwnerText, FILE_TYPE_OWNER_LEN);
    strncpy(header.name, storage.name.c_str(), STORAGE_NAME_LEN);
    strncpy(header.description, storage.description.c_str(), STORAGE_DESCRIPTION_LEN);
    header.encryptionType(Default);
    header.interactionsCount(1000);
    RAND_bytes(header.salt, SALT_LEN);
    auto wroteLen = write(fd, &header, sizeof(header));
    if (wroteLen != sizeof(header)) {
        close(fd);
        return KEY_WRITE_FILE_ERROR;
    }
    close(fd);
    return 0;
}

// -------------------- private ------------------------------
static std::shared_ptr<StorageV2_Header> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageV2_Header header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V2, SIGNATURE_LEN) != 0
        || header.storageVersion() != STORAGE_VER_SECOND)
        return {};
    return make_shared<StorageV2_Header>(header);
}
