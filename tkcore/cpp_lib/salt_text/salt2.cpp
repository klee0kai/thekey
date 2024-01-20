//
// Created by panda on 19.01.24.
//

#include "salt2.h"
#include "thekey_core.h"
#include "salt2_schema.h"
#include <cstdarg>
#include <cstring>

using namespace std;
using namespace tkey2_salt;
using namespace tkey_salt;


void tkey2_salt::SaltedText::salted(const std::string &text) {
    auto wideString = from(text);
    auto wideStringRaw = wideString.c_str();
    auto type = find_scheme_type(wideString);
    wide_char source[SALTED_TEXT_LEN];
    randmem(source, SALTED_TEXT_LEN);
    randmem(payload.raw, SALTED_TEXT_LEN);
    int wide_len = 0;
    for (; wide_len < SALTED_TEXT_LEN && wideStringRaw[wide_len]; ++wide_len) {
        source[wide_len] = wideStringRaw[wide_len];
    }
    encoded(type, payload.raw, source, SALTED_TEXT_LEN, 1);

    encodingType(type);
    approximateLength(wide_len / LEN_CORRECTION_RING);
    unsigned char lenCorrection = wide_len - approximateLength() * LEN_CORRECTION_RING;
    payload.lenCorrection = SALT_IN_RING(lenCorrection, LEN_CORRECTION_RING);
}

std::string tkey2_salt::SaltedText::desalted() const {
    auto type = encodingType();
    wide_char out[SALTED_TEXT_LEN + 1];
    memset(out, 0, size(out));
    decoded(type, out, payload.raw, SALTED_TEXT_LEN);
    uint32_t lenCorrection = DESALT_IN_RING(payload.lenCorrection, LEN_CORRECTION_RING);
    uint32_t len = approximateLength() * LEN_CORRECTION_RING + lenCorrection;
    out[len] = 0;
    return from(wide_string(out));
}

int tkey2_salt::encoded(
        uint32_t typeEncoding,
        tkey_salt::wide_char *out,
        const tkey_salt::wide_char *in,
        const uint &bufSize,
        const int &salt) {
    auto scheme = find_scheme(typeEncoding);
    if (!scheme)return -1;
    auto scheme_len = scheme->len();
    randmem(out, bufSize);
    int len = 0;
    for (; len < bufSize && in[len]; len++) {
        out[len] = scheme->encoded(in[len]);
        if (salt) {
            out[len] = SALT_IN_RING(out[len], scheme_len);
        }
    }
    return len;
}

int tkey2_salt::decoded(
        uint32_t typeEncoding,
        tkey_salt::wide_char *out,
        const tkey_salt::wide_char *in,
        const int &bufLen) {
    memset(out, 0, bufLen);
    auto scheme = find_scheme(typeEncoding);
    if (!scheme)return -1;
    for (int i = 0; i < bufLen; ++i) {
        auto desalted = in[i] % scheme->len();
        out[i] = scheme->decoded(desalted);
    }
    return bufLen;
}