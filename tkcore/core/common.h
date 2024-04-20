//
// Created by panda on 01.03.2020.
//

#ifndef THEKEY_COMMON_H
#define THEKEY_COMMON_H

#include <vector>
#include <string>
#include <list>
#include <algorithm>

template<typename T>
struct Result {
    int error;
    T result;
};

int memcmpr(void *mem, char mch, int len);

int ends_with(std::string const &value, std::string const &ending);

std::vector<uint8_t> to_vector(std::string const &value);

std::vector<uint8_t> sha256(std::vector<uint8_t> const &value);

template<typename T, typename Predicate>
typename std::list<T>::iterator findItBy(std::list<T> &sList, const Predicate ptr) {
    return std::find_if(sList.begin(), sList.end(), ptr);
}

template<typename T, typename Predicate>
T *findPtrBy(std::list<T> &sList, const Predicate ptr) {
    auto it = std::find_if(sList.begin(), sList.end(), ptr);
    if (it == sList.end()) return NULL;
    return &*it;
}

template<typename T, typename Predicate>
void for_each(std::list<T> &sList, const Predicate ptr) {
    std::for_each(sList.begin(), sList.end(), ptr);
}

#endif //THEKEY_COMMON_H
