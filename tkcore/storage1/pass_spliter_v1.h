//
// Created by panda on 2020-01-26.
//

#ifndef THEKEY_PASS_SPLITER_V1_H
#define THEKEY_PASS_SPLITER_V1_H

namespace thekey_v1 {

    void splitPasswForPasswords(unsigned char *outPassw, const unsigned char *passw);

    void splitPasswForLogin(unsigned char *outPassw, const unsigned char *passw);

    void splitPasswForDescription(unsigned char *outPassw, const unsigned char *passw);

    void splitPasswForNoteHistPassw(unsigned char *outPassw, const unsigned char *passw);

    void splitPasswForGenPassw(unsigned char *outPassw, const unsigned char *passw);

}

#endif //THEKEY_PASS_SPLITER_V1_H
