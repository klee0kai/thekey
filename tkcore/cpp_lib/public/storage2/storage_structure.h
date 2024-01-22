//
// Created by panda on 19.01.24.
//

#ifndef THEKEY_STORAGE_STRUCTURE_H
#define THEKEY_STORAGE_STRUCTURE_H

#include "thekey_core.h"

#define SIGNATURE_LEN 7

#define STORAGE_VER_SECOND 0x02
#define FILE_TYPE_OWNER_LEN 256
#define STORAGE_NAME_LEN 128
#define STORAGE_DESCRIPTION_LEN 512
#define SALT_LEN 2048
#define KEY_LEN 2048

#define ENCRYPTED_LEN 2048

namespace thekey_v2 {

    enum EncryptTypes {
        Default,
        AES256,
    };

    enum FileSectionTypes {
        /**
         *  short map of Sections.
         */
        FileMap,

        /**
         * Decrypted note with log/ passw /description.
         * After it, the history of password changes
         */
        NoteEntry,

        /**
         *  Generated password history
         */
        GenPasswHistory,
    };


/**
 *  !! File Structure !!
 */
#pragma pack(push, 1)

    struct StorageHeaderFlat {
        char signature[SIGNATURE_LEN]; // TKEY_SIGNATURE_V2;
        INT32_BIG_ENDIAN(storageVersion) // STORAGE_VER_SECOND
        char fileTypeOwner[FILE_TYPE_OWNER_LEN]; // typeOwnerText

        char name[STORAGE_NAME_LEN];
        char description[STORAGE_DESCRIPTION_LEN];

        unsigned char salt[SALT_LEN]; // crypt/decrypt salt
        INT32_BIG_ENDIAN(keyInteractionsCount)// key crypt/decrypt interaction count
        INT32_BIG_ENDIAN(interactionsCount)// crypt/decrypt interaction count
        INT32_BIG_ENDIAN_ENUM(encryptionType, EncryptTypes) // crypt type
    };

    struct FileSectionFlat {
        INT32_BIG_ENDIAN_ENUM(sectionType, FileSectionTypes)

        INT32_BIG_ENDIAN(sectionLen)
    };


    struct CryptedTextFlat {
        INT32_BIG_ENDIAN(approximateLength)

        unsigned char lengthCorrection;

        unsigned char raw[ENCRYPTED_LEN];

        [[nodiscard]] std::string decryptLen() const;

        void encryptLen(const std::string &text);

        [[nodiscard]] std::string decrypt() const;

        void encrypt(const std::string &text);
    };

    struct CryptedPasswordFlat {
        INT64_BIG_ENDIAN(genTime)

        INT32_BIG_ENDIAN(color)

        CryptedTextFlat password;
    };

    struct CryptedNoteFlat {
        INT64_BIG_ENDIAN(genTime)

        INT32_BIG_ENDIAN(color)

        CryptedTextFlat site;
        CryptedTextFlat login;
        CryptedTextFlat password;
        CryptedTextFlat description;
    };

#pragma pack(pop)

}

#endif //THEKEY_STORAGE_STRUCTURE_H
