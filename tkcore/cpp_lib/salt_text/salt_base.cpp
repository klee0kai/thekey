//
// Created by panda on 20.01.24.
//

#include "salt_base.h"
#include <openssl/rand.h>

using namespace std;
using namespace thekey_salt;

static wstring_convert<codecvt_utf8<wide_char>, wide_char> converter;

long thekey_salt::rand(ulong max) {
    return random() % max;
}


void thekey_salt::randmem(unsigned char *mem, uint len) {
    RAND_bytes(mem, len);
}

void thekey_salt::memsalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = (unsigned char) SALT_IN_RING(mem[i], ring);
    }
}

void thekey_salt::memdesalt(unsigned char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        unsigned char a = mem[i];
        a %= ring;
        mem[i] = a;
    }
}

// ---- wide char ------
void thekey_salt::randmem(thekey_salt::wide_char *mem, uint len) {
    RAND_bytes((unsigned char *) mem, sizeof(wide_char) * len);
}

void thekey_salt::memsalt(thekey_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = SALT_IN_RING(mem[i], ring);
    }
}

void thekey_salt::memdesalt(thekey_salt::wide_char *mem, uint len, uint ring) {
    for (int i = 0; i < len; i++) {
        mem[i] = mem[i] % ring;
    }
}


wide_string thekey_salt::from(const string &string) {
    return converter.from_bytes(string);
}

std::string thekey_salt::from(const wide_string &wideString) {
    return converter.to_bytes(wideString);
}

std::string thekey_salt::from(const thekey_salt::wide_char &wideChar) {
    return converter.to_bytes(wideChar);
}
