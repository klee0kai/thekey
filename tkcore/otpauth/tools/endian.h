//
// Created by panda on 11.02.24.
//

#ifndef THEKEY_ENDIAN_H
#define THEKEY_ENDIAN_H

enum endian {
    littleEndian,
    bigEndian
};

endian currentEndian() {
    int num = 1;
    if (*(char *) &num == 1) {
        return littleEndian;
    } else {
        return bigEndian;
    }

}

template<typename T>
T swap(const T &value, const endian &from, const endian &to) {
    if (from == to) {
        return value;
    } else {
        auto *origin = (const uint8_t *) &value;
        uint8_t byte[sizeof(T)];
        for (int i = 0; i < sizeof(value); ++i) {
            byte[sizeof(T) - i - 1] = origin[i];
        }
        return *(T *) byte;
    }
}

template<typename T>
T swap(const T &origin, const endian &to) {
    return swap(origin, currentEndian(), to);
}

#endif //THEKEY_ENDIAN_H
