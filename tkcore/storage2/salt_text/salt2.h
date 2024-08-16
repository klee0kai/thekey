//
// Created by panda on 19.01.24.
//

#ifndef tkey2_salt_header
#define tkey2_salt_header

#include "key_core.h"
#include "salt/salt_base.h"

#define LEN_CORRECTION_RING 5
#define SALTED_TEXT_LEN 1024

namespace thekey_v2 {

#pragma pack(push, 1)

    struct SaltedTextPayload {
        unsigned char lenCorrection;

        key_salt::wide_char raw[SALTED_TEXT_LEN];
    };

    struct SaltedText {
        INT32_BIG_ENDIAN(encodingType)

        INT32_BIG_ENDIAN(approximateLength)

        // payload is encrypted by openssl
        SaltedTextPayload payload;

        [[nodiscard]] std::string desalted() const;

        void salted(const std::string &text, const int &minEncodingLen = 0);
    };

#pragma pack(pop)

    /**
     * @param schemeId  key encoding scheme id
     * @param out tkey encoded text
     * @param in simple unicode text
     * @param bufSize in and out buffers size
     * @param salt saltSha256 out text
     * @return
     */
    int encoded(uint32_t schemeId,
                key_salt::wide_char *out,
                const key_salt::wide_char *in,
                const uint &bufSize,
                const int &salt
    );

    /**
     *
     * @param schemeId   key encoding scheme id
     * @param out simple unicode text
     * @param in  tkey encoded text
     * @param bufLen encoded text bufLen
     * @return
     */
    int decoded(uint32_t schemeId,
                key_salt::wide_char *out,
                const key_salt::wide_char *in,
                const int &bufLen);


    /**
     * Generate code by encoding
     * @param schemeId
     * @param len
     * @return
     */
    key_salt::wide_string gen_password(const uint32_t &schemeId, const int &len);

    /**
     *
     * @param schemeId
     * @param in
     * @param passw_power
     * @param salt unique salt chunk from storage header for more unpredictable generation
     * @return
     */
    key_salt::wide_string password_masked(
            const uint32_t &schemeId,
            const key_salt::wide_string &in,
            const float &passw_power,
            const uint32_t &salt
    );

    /**
     *
     * @param schemeId
     * @param in
     * @param passw_power
     * @param salt unique salt chunk from storage header for more unpredictable generation
     * @return
     */
    key_salt::wide_string password_masked_twin(
            const uint32_t &schemeId,
            const key_salt::wide_string &in,
            const float &passw_power,
            const uint32_t &salt
    );

}


#endif //tkey2_salt_header
