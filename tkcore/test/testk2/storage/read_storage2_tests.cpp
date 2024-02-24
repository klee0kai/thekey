//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>

#ifdef __ANDROID__
namespace fs = std::__fs::filesystem;
#else
namespace fs = std::filesystem;
#endif

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

static string storageV2CollectionFolder =
        fs::current_path()
                .parent_path()
                .parent_path()
                .parent_path()
                .parent_path()
        / "somedata/storage_v2";

TEST(ReadStorageV2, CheckFolder) {
    cout << "storageV2CollectionFolder = " << storageV2CollectionFolder << endl;
    ASSERT_FALSE(fs::is_empty(storageV2CollectionFolder))
                                << "storageV2CollectionFolder is empty "
                                << storageV2CollectionFolder << endl;
}

TEST(ReadStorageV2, ReadTs4CorrectPassw) {
    // Given
    cout << "cwd " << storageV2CollectionFolder << endl;
//    unsigned char salt[SALT_LEN];
//    memset(salt, '3', SALT_LEN);
//
//    auto ctx = cryptContext("test", 1000, salt);
//    CryptedTextFlat crypted{};
//
//    When
//    crypted.encrypt("some_text", ctx->keyForLogin);
//    auto decrypted = crypted.decrypt(ctx->keyForLogin);
//
//    Then
//    ASSERT_EQ("some_text", decrypted);
}
