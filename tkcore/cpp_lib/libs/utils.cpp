//
// Created by panda on 01.03.2020.
//

#include "utils.h"

int memcmpr(void *mem, char mch, int len) {
    int i = 0;
    char *memchr = (char *) mem;
    for (; memchr[i] == mch && i < len - 1; i++);
    return memchr [i] - mch;
}
