//
// Created by panda on 01.03.2020.
//

#ifndef THEKEY_COMMON_H
#define THEKEY_COMMON_H

#include <vector>
#include <string>

template<typename T>
struct Result {
    int error;
    T result;
};

int memcmpr(void *mem, char mch, int len);

int ends_with(std::string const &value, std::string const &ending);

std::vector<uint8_t> to_vector(std::string const &value);

std::vector<uint8_t> sha256(std::vector<uint8_t> const &value);

#endif //THEKEY_COMMON_H
