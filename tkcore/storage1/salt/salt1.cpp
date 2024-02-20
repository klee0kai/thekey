//
//  salt_text.cpp
//  Encoding
//
//  Created by panda on 23.02.2020.
//  Copyright © 2020 panda. All rights reserved.
//

#include "salt1.h"
#include "salt/salt_base.h"
#include <string.h>
#include <cstring>
#include <limits.h>
#include <openssl/rand.h>

using namespace key_salt;
using namespace key_salt;

#define SALT_IN_RING(x, max, ring) ( (x) + (ring) * rand((max) / (ring) ))
#define TYPE_MAX(typeLen) ((1L << ( (typeLen) * 8L)) -1L)

#define TEXT_DECODE_RESERVE_LEN 6 // extra unused bytes in the text

/**
 *  кол-во вариантов в наборе для кодировки
 */
#define ENC_PASSW_NUM_ONLY_SYM_SET 10
#define ENC_PASSW_NUM_EN_SYM_SET 62  // 10 + 26 + 26
#define ENC_PASSW_NUM_EN_SPEC_SYM_SET 94 // 128 - 33 - 1 remove control characters
#define ENC_PASSW_NUM_EN_SPEC_SPACE_SYM_SET 95 // 128 - 33 - 1 remove control characters


size_t SaltTextHeader_LEN = sizeof(key_salt::SaltTextHeader);


static int acsii_to_num(unsigned char *out, const unsigned char *source, bool salt);

static int num_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len);

static int ascii_to_num_en(unsigned char *out, const unsigned char *source, bool salt);

static int num_en_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len);

static int ascii_to_num_en_spec_symbols(unsigned char *out, const unsigned char *source, bool salt);

static int num_en_spec_symbols_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len);

static int ascii_to_num_en_spec_symbols_space(unsigned char *out, const unsigned char *source, bool salt);

static int num_en_spec_symbols_space_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len);

void saltHeader(SaltTextHeader *header, size_t lenRing);

void desaltgHeader(SaltTextHeader *header, size_t lenRing);

int key_salt::salt_text(unsigned char *out, const unsigned char *source, unsigned int buflen) {
    int sourcelen = strlen((const char *) source);
    size_t lenRing = buflen - SaltTextHeader_LEN - TEXT_DECODE_RESERVE_LEN;
    if (sourcelen > lenRing)
        return -1;
    SaltTextHeader *salted = (SaltTextHeader *) out;
    randmem(out, buflen);

    int olen = 0;
    if ((olen = acsii_to_num(salted->saltText, source, true)) > 0) {
        salted->coding = ENC_NUM_ONLY;
        salted->len = (size_t) olen;
        saltHeader(salted, lenRing);
        return buflen;
    }
    if ((olen = ascii_to_num_en(salted->saltText, source, true)) > 0) {
        salted->coding = ENC_EN_NUM;
        salted->len = (size_t) olen;
        saltHeader(salted, lenRing);
        return buflen;
    }
    if ((olen = ascii_to_num_en_spec_symbols(salted->saltText, source, true)) > 0) {
        salted->coding = ENC_EN_NUM_SPEC_SYMBOLS;
        salted->len = (size_t) olen;
        saltHeader(salted, lenRing);
        return buflen;
    }
    if ((olen = ascii_to_num_en_spec_symbols_space(salted->saltText, source, true)) > 0) {
        salted->coding = ENC_EN_NUM_SPEC_SYMBOLS_SPACE;
        salted->len = (size_t) olen;
        saltHeader(salted, lenRing);
        return buflen;
    }
    memcpy(salted->saltText, source, (size_t) sourcelen);
    salted->coding = ENC_EN_NON;
    salted->len = (size_t) sourcelen;
    saltHeader(salted, lenRing);
    return buflen;
}

int key_salt::desalt_text(unsigned char *out, const unsigned char *source, unsigned int buflen) {
    memcpy(out, source, buflen);

    SaltTextHeader salted = {};
    SaltTextHeader *sourceSaltedHeader = (SaltTextHeader *) source;
    memcpy(&salted, source, SaltTextHeader_LEN);
    size_t lenRing = buflen - SaltTextHeader_LEN - TEXT_DECODE_RESERVE_LEN;
    desaltgHeader(&salted, lenRing);
    memset(out, 0, buflen);


    switch (salted.coding) {
        case ENC_NUM_ONLY: {
            return num_to_acsii(out, sourceSaltedHeader->saltText, salted.len);
        }
        case ENC_EN_NUM: {
            return num_en_to_acsii(out, sourceSaltedHeader->saltText, salted.len);
        }
        case ENC_EN_NUM_SPEC_SYMBOLS: {
            return num_en_spec_symbols_to_acsii(out, sourceSaltedHeader->saltText, salted.len);
        }
        case ENC_EN_NUM_SPEC_SYMBOLS_SPACE: {
            return num_en_spec_symbols_space_to_acsii(out, sourceSaltedHeader->saltText, salted.len);
        }
        default://ENC_EN_NON
        {
            memcpy(out, sourceSaltedHeader->saltText, salted.len);
            return salted.len;
        }
    }
}

int key_salt::genpassw(unsigned char *out, unsigned int len, unsigned int encoding) {
    unsigned char *source = new unsigned char[len];
    randmem(source, len);
    switch (encoding) {
        case ENC_NUM_ONLY: {
            int i = num_to_acsii(out, source, (unsigned int) len);
            delete[] source;
            return i;
        }
        case ENC_EN_NUM: {
            int i = num_en_to_acsii(out, source, (unsigned int) len);
            delete[] source;
            return i;
        }
        case ENC_EN_NUM_SPEC_SYMBOLS: {
            int i = num_en_spec_symbols_to_acsii(out, source, (unsigned int) len);
            delete[] source;
            return i;
        }
        default://ENC_EN_NON
        {
            memcpy(out, source, len);
            delete[] source;

            return len;
        }
    }
}


static int ascii_to_num_en(unsigned char *out, const unsigned char *source, bool salt) {
    unsigned int len;
    for (len = 0; source[len] != 0; len++) {
        out[len] = source[len];
        if (out[len] >= 48) out[len] -= 48;
        else return -1;
        if (out[len] >= 17)out[len] -= 7;
        else if (out[len] > 9)return -1;
        if (out[len] >= 42)out[len] -= 6;
        else if (out[len] > 35) return -1;
        if (out[len] >= 62) return -1;
    }
    if (salt) memsalt(out, len, ENC_PASSW_NUM_EN_SYM_SET);
    return len;
}

static int num_en_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len) {
    memcpy(out, source, len);
    memdesalt(out, len, ENC_PASSW_NUM_EN_SYM_SET);
    for (int i = 0; i < len; i++) {
        out[i] = out[i] + '0';
        if (out[i] > '9')out[i] += 7;
        if (out[i] > 'Z')out[i] += 6;
    }
    return len;
}

static int ascii_to_num_en_spec_symbols(unsigned char *out, const unsigned char *source, bool salt) {
    unsigned int len;
    for (len = 0; source[len] != 0; len++) {
        out[len] = source[len];
        unsigned char c = '~';
        bool a = 0xc2 > '~';
        if (out[len] > '~')return -1;
        if (out[len] >= '!') out[len] -= '!';
        else return -1;
    }
    if (salt) memsalt(out, len, ENC_PASSW_NUM_EN_SPEC_SYM_SET);
    return len;
}

static int num_en_spec_symbols_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len) {
    memcpy(out, source, len);
    memdesalt(out, len, ENC_PASSW_NUM_EN_SPEC_SYM_SET);
    for (int i = 0; i < len; i++) {
        out[i] = out[i] + '!';
    }
    return len;
}

static int ascii_to_num_en_spec_symbols_space(unsigned char *out, const unsigned char *source, bool salt) {
    unsigned int len;
    for (len = 0; source[len] != 0; len++) {
        out[len] = source[len];
        unsigned char c = '~';
        bool a = 0xc2 > '~';
        if (out[len] > '~')return -1;
        if (out[len] >= ' ') out[len] -= ' ';
        else return -1;
    }
    if (salt) memsalt(out, len, ENC_PASSW_NUM_EN_SPEC_SPACE_SYM_SET);
    return len;
}

static int num_en_spec_symbols_space_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len) {
    memcpy(out, source, len);
    memdesalt(out, len, ENC_PASSW_NUM_EN_SPEC_SPACE_SYM_SET);
    for (int i = 0; i < len; i++) {
        out[i] = out[i] + ' ';
    }
    return len;
}

static int acsii_to_num(unsigned char *out, const unsigned char *source, bool salt) {
    unsigned int len;
    for (len = 0; source[len] != 0; len++) {
        if (source[len] < '0' || source[len] > '9')
            return -1;
        out[len] = source[len] - '0';
    }
    if (salt) memsalt(out, len, ENC_PASSW_NUM_ONLY_SYM_SET);
    return len;
}

static int num_to_acsii(unsigned char *out, const unsigned char *source, unsigned int len) {
    memcpy(out, source, len);
    memdesalt(out, len, ENC_PASSW_NUM_ONLY_SYM_SET);
    for (int i = 0; i < len; i++) {
        out[i] = out[i] + '0';
    }
    return len;
}

void saltHeader(SaltTextHeader *header, size_t lenRing) {
    long charMax = TYPE_MAX(sizeof(char));
    long uint32Max = TYPE_MAX(sizeof(uint32_t));

    header->lenCoding = (unsigned char) ((header->len <= PASSW_MAX_LEN && header->len >= PASSW_MIN_LEN)
                                         ? ENC_LEN_PASSW : ENC_LEN_TEXT);

    header->coding = (unsigned char) SALT_IN_RING(header->coding, charMax, 5L);
    if (header->lenCoding == ENC_LEN_PASSW)
        header->len = (size_t) SALT_IN_RING(header->len - PASSW_MIN_LEN, uint32Max, PASSW_MAX_LEN - PASSW_MIN_LEN + 1);
    else
        header->len = (size_t) SALT_IN_RING(header->len, uint32Max, lenRing);

    header->lenCoding = (unsigned char) SALT_IN_RING(header->lenCoding, charMax, 2L);
}

void desaltgHeader(SaltTextHeader *header, size_t lenRing) {
    header->lenCoding %= 2;
    header->coding %= 5;
    if (header->lenCoding == ENC_LEN_PASSW)
        header->len = header->len % (PASSW_MAX_LEN - PASSW_MIN_LEN + 1) + PASSW_MIN_LEN;
    else header->len %= lenRing;
}
