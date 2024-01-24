//
// Created by panda on 22.01.24.
//

#include <cstring>
#include "public/storage2/storage_structure.h"
#include "thekey_core.h"

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
using namespace thekey_v2;
using namespace tkey2_salt;

static unsigned char iv[] = "1234567887654321";

std::string CryptedTextFlat::decrypt(
        const thekey_v2::EncryptType &encryptType,
        const unsigned char *key
) const {
    SaltedText decrypted = {};

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    EVP_DecryptInit(ctx, EVP_aes_256_cbc(), key, iv);
    int outlen = 0;
    if (!EVP_DecryptUpdate(ctx,
                           (unsigned char *) &decrypted.payload, &outlen,
                           (unsigned char *) &raw.payload, sizeof(raw.payload))) {
        EVP_CIPHER_CTX_free(ctx);
        return "";
    }
    EVP_CIPHER_CTX_free(ctx);

    return decrypted.desalted();
}

void CryptedTextFlat::encrypt(
        const std::string &text,
        const thekey_v2::EncryptType &encryptType,
        const unsigned char *key
) {
    SaltedText decrypted = {};
    decrypted.salted(text);

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    EVP_DecryptInit(ctx, EVP_aes_256_cbc(), key, iv);
    int outlen = 0;
    if (!EVP_DecryptUpdate(ctx,
                           (unsigned char *) &raw.payload, &outlen,
                           (unsigned char *) &decrypted.payload, sizeof(raw.payload))) {
        EVP_CIPHER_CTX_free(ctx);
        return;
    }
    EVP_CIPHER_CTX_free(ctx);


}




