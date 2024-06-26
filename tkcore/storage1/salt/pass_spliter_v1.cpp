//
// Created by panda on 2020-01-26.
//

#include "pass_spliter_v1.h"

#define PASSW_MASK 0xFF // sampling from the original password to create a password for decrypting passwords
#define LOGIN_MASK 0xA6 // sampling from the original password to create a password for decrypting logins
#define DESC_MASK 0x68 //sampling from the original password to create a password for decrypting descriptions
#define NOTE_HIST_MASK 0x65 //sampling from the original password to create a password for decrypting history
#define GEN_PASSW_MASK 0x65 // sampling from the original password to create a password for decrypting generated passwords

namespace key_salt {

    void splitPasswForPasswords(unsigned char *outPassw, const unsigned char *passw) {
        for (int i = 0; passw[i] != 0; i++) {
            outPassw[i] = (unsigned char) ((passw[i] & PASSW_MASK) ^ 0x95);
        }
    }

    void splitPasswForLogin(unsigned char *outPassw, const unsigned char *passw) {
        for (int i = 0; passw[i] != 0; i++) {
            outPassw[i] = (unsigned char) ((passw[i] & LOGIN_MASK) ^ 0x7A);
        }
    }

    void splitPasswForDescription(unsigned char *outPassw, const unsigned char *passw) {
        for (int i = 0; passw[i] != 0; i++) {
            outPassw[i] = (unsigned char) ((passw[i] & DESC_MASK) ^ 0x16);
        }
    }

    void splitPasswForNoteHistPassw(unsigned char *outPassw, const unsigned char *passw) {
        for (int i = 0; passw[i] != 0; i++) {
            outPassw[i] = (unsigned char) ((passw[i] & NOTE_HIST_MASK) ^ 0x10);
        }
    }


    void splitPasswForGenPassw(unsigned char *outPassw, const unsigned char *passw) {
        for (int i = 0; passw[i] != 0; i++) {
            outPassw[i] = (unsigned char) ((passw[i] & GEN_PASSW_MASK) ^ 0x14);
        }
    }

}
