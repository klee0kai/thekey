//
// Created by panda on 20.01.24.
//

#include "salt_base.h"
#include <openssl/rand.h>

using namespace std;
using namespace key_salt;

static wstring_convert<codecvt_utf8<wide_char>, wide_char> converter;

long key_salt::rand(unsigned long max) {
    return random() % max;
}


void key_salt::randmem(unsigned char *mem, uint len) {
    RAND_bytes(mem, len);
}

void key_salt::memsalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = (unsigned char) SALT_IN_RING(mem[i], ring);
    }
}

void key_salt::memdesalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        unsigned char a = mem[i];
        a %= ring;
        mem[i] = a;
    }
}

// ---- wide char ------
void key_salt::randmem(key_salt::wide_char *mem, uint len) {
    RAND_bytes((unsigned char *) mem, sizeof(wide_char) * len);
}

void key_salt::memsalt(key_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = SALT_IN_RING(mem[i], ring);
    }
}

void key_salt::memdesalt(key_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = mem[i] % ring;
    }
}


wide_string key_salt::from(const string &string) {
    return converter.from_bytes(string);
}

std::string key_salt::from(const wide_string &wideString) {
    return converter.to_bytes(wideString);
}

std::string key_salt::from(const key_salt::wide_char &wideChar) {
    return converter.to_bytes(wideChar);
}
