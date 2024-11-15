//
// Created by panda on 19.01.24.
//

#include "salt2.h"
#include "key_core.h"
#include "salt2_schema.h"
#include <cstring>
#include <cmath>
#include <iostream>

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

static auto hashFunc = hash<uint32_t>();

static unsigned int gen_offset(const vector<uint32_t> &args);

void thekey_v2::SaltedText::salted(const std::string &text, const int &minEncodingLen) {
    auto wideString = from(text);
    auto wideStringRaw = wideString.c_str();
    auto type = find_scheme_id(wideString, minEncodingLen);
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

std::string thekey_v2::SaltedText::desalted() const {
    auto type = encodingType();
    wide_char out[SALTED_TEXT_LEN + 1];
    memset(out, 0, sizeof(out));
    decoded(type, out, payload.raw, SALTED_TEXT_LEN);
    uint32_t lenCorrection = DESALT_IN_RING(payload.lenCorrection, LEN_CORRECTION_RING);
    uint32_t len = approximateLength() * LEN_CORRECTION_RING + lenCorrection;
    out[len] = 0;
    return from(wide_string(out));
}

int thekey_v2::encoded(
        uint32_t schemeId,
        key_salt::wide_char *out,
        const key_salt::wide_char *in,
        const uint &bufSize,
        const int &salt) {
    auto scheme = schema(schemeId);
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

int thekey_v2::decoded(
        uint32_t schemeId,
        key_salt::wide_char *out,
        const key_salt::wide_char *in,
        const int &bufLen) {
    memset(out, 0, bufLen);
    auto scheme = schema(schemeId);
    if (!scheme)return -1;
    for (int i = 0; i < bufLen; ++i) {
        out[i] = scheme->decoded(in[i]);
    }
    return bufLen;
}

wide_string thekey_v2::gen_password(
        const uint32_t &schemeId,
        const int &len
) {
    auto scheme = schema(schemeId);
    if (!scheme)return {};
    wide_char randPassw[len + 1];
    randmem(randPassw, len);
    for (int i = 0; i < len; ++i) {
        randPassw[i] = scheme->decoded(randPassw[i]);
    }
    randPassw[len] = 0;
    return randPassw;
}

wide_string thekey_v2::password_masked(
        const uint32_t &schemeId,
        const wide_string &in,
        const float &passw_power,
        const uint32_t &salt
) {
    auto scheme = schema(schemeId);
    if (!scheme)return {};
    auto ring = MAX(uint(round(scheme->len() * pow(passw_power, 5))), 1);

    wide_string outPassw{};
    outPassw.reserve(in.length());
    for (int i = 0; i < in.length(); ++i) {
        auto maskOffset = int(gen_offset({
                                                 salt,
                                                 uint32_t(i),
                                                 (uint32_t) scheme->len(),
                                                 (uint32_t) passw_power,
                                                 (uint32_t) in.length()
                                         }));
        wide_char c = in.at(i);
        c = scheme->encoded(c, maskOffset);
        c = DESALT_IN_RING(c, ring);
        outPassw += scheme->decoded(c, -maskOffset);
    }
    return outPassw;
}

key_salt::wide_string thekey_v2::password_masked_twin(
        const uint32_t &schemeId,
        const key_salt::wide_string &in,
        const float &passw_power,
        const uint32_t &salt
) {
    auto scheme = schema(schemeId);
    if (!scheme) return {};
    auto schemeLen = scheme->len();
    auto ring = MAX(uint(round(scheme->len() * pow(passw_power, 5))), 1);

    wide_string outPassw{};
    outPassw.reserve(in.length());
    for (int i = 0; i < in.length(); ++i) {
        auto maskOffset = int(gen_offset({
                                                 salt,
                                                 uint32_t(i),
                                                 (uint32_t) scheme->len(),
                                                 (uint32_t) passw_power,
                                                 (uint32_t) in.length()
                                         }));
        wide_char c = in.at(i);
        c = scheme->encoded(c, maskOffset);
        c = DESALT_IN_RING(c, ring);
        auto steps = (schemeLen - c) / ring;
        c = steps > 0 ? c + rand(steps) * ring : c;
        outPassw += scheme->decoded(c, -maskOffset);
    }
    return outPassw;
}


// -------------------- private ---------------------
static unsigned int gen_offset(const vector<uint32_t> &args) {
    unsigned int maskOffset = 0;
    for (const auto &item: args) {
        maskOffset ^= hashFunc(item);
    }
    return maskOffset % 200;
}