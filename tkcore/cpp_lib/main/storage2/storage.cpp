

#include "thekey_core.h"
#include "public/storage2/storage.h"
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
#include <utility>

using namespace std;
using namespace thekey;
using namespace thekey_v2;


static char typeOwnerText[FILE_TYPE_OWNER_LEN] = "TheKey key storage. Designed by Andrei Kuzubov / Klee0kai. "
                                                 "Follow original app https://github.com/klee0kai/thekey";

struct thekey_v2::SplitPasswords {
    std::string passwForLogin;
    std::string passwForPassw;
    std::string passwForDescription;
    std::string passwForHistPassw;
};

struct thekey_v2::CryptContext {
    unsigned char keyForLogin[KEY_LEN];
    unsigned char keyForPassw[KEY_LEN];
    unsigned char keyForDescription[KEY_LEN];
    unsigned char passwForHistPassw[KEY_LEN];
};

// -------------------- declarations ---------------------------

static std::shared_ptr<StorageFullHeader> storageHeader(int fd);

// -------------------- static ---------------------------------
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
    StorageFullHeader header = {};
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

// ------------------- public --------------------------
KeyStorageV2::KeyStorageV2(int fd, const std::string &path, const std::shared_ptr<CryptContext> &ctx)
        : fd(fd), storagePath(path), ctx(ctx) {
    tempStoragePath = path.substr(0, path.find_last_of('.')) + "-temp.ckey";
}

KeyStorageV2::~KeyStorageV2() {
    if (ctx)memset(&*ctx, 0, sizeof(CryptContext));
    if (fd) close(fd);
    ctx.reset();
    fd = 0;
}



// -------------------- private ------------------------------
static std::shared_ptr<StorageFullHeader> storageHeader(int fd) {
    lseek(fd, 0, SEEK_SET);
    StorageFullHeader header = {};
    size_t readLen = read(fd, &header, sizeof(header));
    if (readLen != sizeof(header)) {
        return {};
    }
    if (memcmp(&header.signature, &thekey::storageSignature_V2, SIGNATURE_LEN) != 0
        || header.storageVersion() != STORAGE_VER_SECOND)
        return {};
    return make_shared<StorageFullHeader>(header);
}
