//
// Created by panda on 13.01.24.
//

#ifndef THEKEY_KEY_ERRORS_H
#define THEKEY_KEY_ERRORS_H

#define KEY_OK 0
#define KEY_UNKNOWN_ERROR -1
#define KEY_OPEN_FILE_ERROR -2
#define KEY_WRITE_FILE_ERROR -3
#define KEY_STORAGE_FILE_IS_BROKEN -4
#define KEY_NOTE_NOT_FOUND -5
#define KEY_STORAGE_VERSION_NOT_SUPPORT -6
#define KEY_CRYPT_ERROR -7
#define KEY_HIST_NOT_FOUND -8
#define KEY_NO_DEFAULT_STRATEGY -9


namespace thekey {

    extern int keyError;

    std::string errorToString(const int &keyError);

}

#endif //THEKEY_KEY_ERRORS_H
