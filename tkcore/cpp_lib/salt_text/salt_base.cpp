//
// Created by panda on 20.01.24.
//

#include "salt_base.h"
#include <openssl/rand.h>

using namespace std;
using namespace tkey_salt;

static wstring_convert<codecvt_utf8<wide_char>, wide_char> converter;

long tkey_salt::rand(ulong max) {
    return random() % max;
}


void tkey_salt::randmem(unsigned char *mem, uint len) {
    RAND_bytes(mem, len);
}

void tkey_salt::memsalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = (unsigned char) SALT_IN_RING(mem[i], ring);
    }
}

void tkey_salt::memdesalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        unsigned char a = mem[i];
        a %= ring;
        mem[i] = a;
    }
}

// ---- wide char ------
void tkey_salt::randmem(tkey_salt::wide_char *mem, uint len) {
    RAND_bytes((unsigned char *) mem, sizeof(wide_char) * len);
}

void tkey_salt::memsalt(tkey_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = SALT_IN_RING(mem[i], ring);
    }
}

void tkey_salt::memdesalt(tkey_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = mem[i] % ring;
    }
}


wide_string tkey_salt::from(const string &string) {
    return converter.from_bytes(string);
}

std::string tkey_salt::from(const wide_string &wideString) {
    return converter.to_bytes(wideString);
}

std::string tkey_salt::from(const tkey_salt::wide_char &wideChar) {
    return converter.to_bytes(wideChar);
}
