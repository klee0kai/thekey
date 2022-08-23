//
//  salt_text.hpp
//  Encoding
//
//  Created by panda on 23.02.2020.
//  Copyright © 2020 panda. All rights reserved.
//

#ifndef salt_text_hpp
#define salt_text_hpp

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <cstdlib>
#include <string>
#include <math.h>
#include "libs/def_header.h"


#define PASSW_MIN_LEN 4L
#define PASSW_MAX_LEN 16L

/**
 * все кодировки пароля не имеют символа окончания строки 0.
 * SaltTextHeader.coding
 */
#define ENC_NUM_ONLY 0
#define ENC_EN_NUM 1
#define ENC_EN_NUM_SPEC_SYMBOLS 2
#define ENC_EN_NUM_SPEC_SYMBOLS_SPACE 3 // с пробелом
#define ENC_EN_NON 4  // не перекодировалось, только смещение

/**
 * кодирока длины
 */
#define ENC_LEN_PASSW 0 // кодирока длины [4..16]
#define ENC_LEN_TEXT 1

extern size_t SaltTextHeader_LEN;

#pragma pack(push, 1)
struct SaltTextHeader {
    unsigned char coding; // в кольце 5 TODO сделать отдельное соление по кодирокам
    unsigned char lenCoding; // в кольце 2 кодировка длины TODO сделать отдельное соление по кодирокам
    uint32_t len; //  в кольце ожидаемого текста или по кодировке длины
    unsigned char saltText[]; // засоленный текст
};
#pragma pack(pop)


namespace s_text {

    /**
    * солит пароль с изменением кодировки
    * @param out  - длина равна source и out буферу
    * @param source
    * @return длина после кодировки
    */
    int salt_text(unsigned char *out, const unsigned char *source, unsigned int buflen);

    /**
    * солит пароль с изменением кодировки
    * @param out  - длина равна source и out буферу
    * @param source
    * @return длина после кодировки
    */
    int desalt_text(unsigned char *out, const unsigned char *source, unsigned int buflen);


    /**
    * генерить пароль
    */
    int genpassw(unsigned char *out, unsigned int len, unsigned int encoding);

}

#endif /* salt_text_hpp */

