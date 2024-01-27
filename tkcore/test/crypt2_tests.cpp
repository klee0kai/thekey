//
// Created by panda on 24.01.24.
//


#include <gtest/gtest.h>
#include "key2.h"
#include <regex>
#include <memory>

using namespace std;
using namespace thekey_v2;
using namespace thekey_v2;
using namespace thekey_salt;

TEST(GenPassw2, CryptDecrypt) {
    // Given
    unsigned char salt[SALT_LEN];
    memset(salt, '3', SALT_LEN);

    auto ctx = cryptContext("test", 1000, salt);
    CryptedTextFlat crypted{};

    // When
    crypted.encrypt("some_text", ctx->keyForLogin);
    auto decrypted = crypted.decrypt(ctx->keyForLogin);

    // Then
    ASSERT_EQ("some_text", decrypted);
}


TEST(GenPassw2, CryptDecryptLong) {
    // Given
    unsigned char salt[SALT_LEN];
    memset(salt, '3', SALT_LEN);


    auto ctx = cryptContext("test", 1000, salt);
    CryptedTextFlat crypted{};

    // When
    crypted.encrypt("some long text text with numbers 123",
                    ctx->keyForLogin,
                    Default,
                    1000000
    );
    auto decrypted = crypted.decrypt(
            ctx->keyForLogin,
            Default,
            1000000
    );

    // Then
    ASSERT_EQ("some long text text with numbers 123", decrypted);
}