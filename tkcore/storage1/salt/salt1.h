//
//  salt_text.hpp
//  Encoding
//
//  Created by panda on 23.02.2020.
//  Copyright © 2020 panda. All rights reserved.
//

#ifndef salt1_text_hpp
#define salt1_text_hpp

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cstdlib>
#include <string>
#include <math.h>
#include "key_core.h"


#define PASSW_MIN_LEN 4L
#define PASSW_MAX_LEN 16L

/**
 * all password encodings do not have a 0 line ending character.
 * SaltTextHeader.coding
 */
#define ENC_NUM_ONLY 0
#define ENC_EN_NUM 1
#define ENC_EN_NUM_SPEC_SYMBOLS 2
#define ENC_EN_NUM_SPEC_SYMBOLS_SPACE 3
#define ENC_EN_NON 4  //not recoded, only offset

/**
 * length code
 */
#define ENC_LEN_PASSW 0 // length code [4..16]
#define ENC_LEN_TEXT 1


namespace key_salt {

#pragma pack(push, 1)
    struct SaltTextHeader {
        unsigned char coding; // in ring 5
        unsigned char lenCoding; // in ring 2 length code
        uint32_t len; // in a ring of expected text or by length encoding
        unsigned char saltText[]; // salted text
    };
#pragma pack(pop)

    /**
    * salts the password by changing the encoding
    * @param out  - length equal to source and out buffer
    * @param source
    * @return length after encoding
    */
    int salt_text(unsigned char *out, const unsigned char *source, unsigned int buflen);

    /**
    * salts the password by changing the encoding
    * @param out  - length equal to source and out buffer
    * @param source
    * @return length after encoding
    */
    int desalt_text(unsigned char *out, const unsigned char *source, unsigned int buflen);


    /**
    * generate password
    */
    int genpassw(unsigned char *out, unsigned int len, unsigned int encoding);

}

#endif /* salt1_text_hpp */

