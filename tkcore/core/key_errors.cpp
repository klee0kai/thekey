//
// Created by panda on 03.02.24.
//

#include <string>
#include "key_errors.h"

using namespace std;

int thekey::keyError = KEY_OK;


string thekey::errorToString(const int &keyError) {
    switch (keyError) {
        case KEY_OK:
            return "ok";

        case KEY_OPEN_FILE_ERROR:
            return "open file error";
        case KEY_WRITE_FILE_ERROR:
            return "write file error";
        case KEY_STORAGE_FILE_IS_BROKEN:
            return "storage is broken";
        case KEY_NOTE_NOT_FOUND:
            return "note not found";
        case KEY_STORAGE_VERSION_NOT_SUPPORT:
            return "storage version not supported";
        case KEY_CRYPT_ERROR:
            return "cryp error";

        case KEY_HIST_NOT_FOUND:
            return "hist error";

        default:
        case KEY_UNKNOWN_ERROR:
            return "unknown error";
    }


}