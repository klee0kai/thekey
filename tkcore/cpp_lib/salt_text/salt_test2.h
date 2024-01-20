//
// Created by panda on 19.01.24.
//

#ifndef tkey2_salt_header
#define tkey2_salt_header

#include "thekey_core.h"
#include "salt_base.h"

#define LEN_CORRECTION_RING 5
#define SALTED_TEXT_LEN 4096

#define UCHAR_SALT(name, ring) \
    unsigned char raw##name;        \
     [[nodiscard]] unsigned char name() const { return DESALT_IN_RING(raw##name, ring ); } \
     void name(unsigned char name) { raw##name = SALT_IN_RING(name, ring ); }


namespace tkey2_salt {
#pragma pack(push, 1)

    struct SaltedTextPayload {
        UCHAR_SALT(lenCorrection, LEN_CORRECTION_RING)

        unsigned char raw[SALTED_TEXT_LEN];
    };

    struct SaltedText {
        INT32_BIG_ENDIAN(encodingType)

        INT32_BIG_ENDIAN(approximateLength)

        // payload is encrypted by openssl
        SaltedTextPayload payload;

        [[nodiscard]] std::string desalted() const;

        void salted(const std::string &text);
    };

#pragma pack(pop)

    uint32_t findEncodingType(const std::string &str);

    uint32_t findEncodingTypeByFlags(const uint32_t &flags);

    uint32_t encodingLen(uint32_t type);

    std::string encodingSymbols(uint32_t type);

    /**
     * @param typeEncoding tkey encode type
     * @param out_chars tkey encoded text
     * @param in_chars simple unicode text
     * @param bufSize in_chars and out_chars buffers size
     * @param salt salt out_chars text
     * @return
     */
    int encoded(uint32_t typeEncoding,
                unsigned char *out_chars,
                const unsigned char *in_chars,
                const uint &bufSize,
                const int &salt
    );

    /**
     *
     * @param typeEncoding   tkey encode type
     * @param out simple unicode text
     * @param in  tkey encoded text
     * @param len encoded text len
     * @return
     */
    int decoded(uint32_t typeEncoding,
                unsigned char *out,
                const unsigned char *in,
                const int &len);

}


#endif //tkey2_salt_header
