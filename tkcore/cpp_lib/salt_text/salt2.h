//
// Created by panda on 19.01.24.
//

#ifndef tkey2_salt_header
#define tkey2_salt_header

#include "thekey_core.h"
#include "salt_base.h"

#define LEN_CORRECTION_RING 5
#define SALTED_TEXT_LEN 1024

namespace tkey2_salt {

#pragma pack(push, 1)

    struct SaltedTextPayload {
        unsigned char lenCorrection;

        tkey_salt::wide_char raw[SALTED_TEXT_LEN];
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

    /**
     * @param typeEncoding tkey encode type
     * @param out tkey encoded text
     * @param in simple unicode text
     * @param bufSize in and out buffers size
     * @param salt salt out text
     * @return
     */
    int encoded(uint32_t typeEncoding,
                tkey_salt::wide_char *out,
                const tkey_salt::wide_char *in,
                const uint &bufSize,
                const int &salt
    );

    /**
     *
     * @param typeEncoding   tkey encode type
     * @param out simple unicode text
     * @param in  tkey encoded text
     * @param bufLen encoded text bufLen
     * @return
     */
    int decoded(uint32_t typeEncoding,
                tkey_salt::wide_char *out,
                const tkey_salt::wide_char *in,
                const int &bufLen);

}


#endif //tkey2_salt_header
