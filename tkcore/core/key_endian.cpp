//
// Created by panda on 14.02.24.
//

#include "key_endian.h"

endian currentEndian() {
    int num = 1;
    if (*(char *) &num == 1) {
        return littleEndian;
    } else {
        return bigEndian;
    }
}