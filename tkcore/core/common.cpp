//
// Created by panda on 01.03.2020.
//

#include "common.h"
#include "string"
#include <openssl/sha.h>
#include <cstring>

using namespace std;

int memcmpr(void *mem, char mch, int len) {
    int i = 0;
    char *memchr = (char *) mem;
    for (; memchr[i] == mch && i < len - 1; i++);
    return memchr[i] - mch;
}


int ends_with(std::string const &value, std::string const &ending) {
    if (ending.size() > value.size()) return false;
    return std::equal(ending.rbegin(), ending.rend(), value.rbegin());
}

std::vector<uint8_t> to_vector(std::string const &value) {
    std::vector<uint8_t> v;
    std::copy(value.begin(), value.end(), std::back_inserter(v));
    return v;
}

std::vector<uint8_t> sha256(std::vector<uint8_t> const &value) {
    char sha256Buf[SHA256_DIGEST_LENGTH + 1];
    memset(sha256Buf, 0, sizeof(sha256Buf));
    ::SHA256(value.data(), value.size(), (unsigned char *) sha256Buf);
    auto result = std::vector<uint8_t>(SHA256_DIGEST_LENGTH);
    result.assign(sha256Buf, sha256Buf + SHA256_DIGEST_LENGTH);
    return result;
}