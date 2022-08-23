//
// Created by panda on 2020-02-24.
//

#ifndef THEKEY_JMAPPING_H
#define THEKEY_JMAPPING_H

#include <jni.h>
#include "dll_interface/key_manager_ctx.h"

namespace jmapping {

    int init(JavaVM *pVM);

    namespace jDecryptedNote {

        jobject map(JNIEnv *env, DecryptedNote *note);

        DecryptedNote *map(JNIEnv *env, jobject jDecryptedNote);
    }

    namespace jDecryptedPassw {

        jobject map(JNIEnv *env, DecryptedPassw *dpassw);

        jobjectArray mapArray(JNIEnv *env, DecryptedPassw *dpassws, int len);

        DecryptedPassw *map(JNIEnv *env, jobject jDecryptedPassw);

        DecryptedPassw *mapArray(JNIEnv *env, jobjectArray jDecryptedPassws, int &outLen);
    }

    namespace jStorage {

        jobject map(JNIEnv *env, const Storage *storage);

        Storage map(JNIEnv *env, jobject jStorage);

        void release(Storage &storage);

    }

}

#endif //THEKEY_JMAPPING_H
