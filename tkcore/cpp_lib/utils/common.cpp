//
// Created by panda on 01.03.2020.
//

#include "common.h"
#include "string"

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