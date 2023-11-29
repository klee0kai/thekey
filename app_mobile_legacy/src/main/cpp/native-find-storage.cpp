#include <jni.h>
#include <string>
#include <ftw.h>
#include "mapping/jmapping.h"

#include <android/log.h>

#include "dll_interface/key_finder.h"


static JNIEnv *envGlb = NULL;
static jmethodID jmethodIdGlb = NULL;
static jobject jStorageFilesEngineGlb;

static void storeFound(Storage storage) {
    jobject jStorage = jmapping::jStorage::map(envGlb, &storage);
    envGlb->CallVoidMethod(jStorageFilesEngineGlb, jmethodIdGlb, jStorage);
    envGlb->DeleteLocalRef(jStorage);
}


extern "C" JNIEXPORT void JNICALL
Java_com_kee0kai_thekey_engine_FindStorageEngine_findStorage(JNIEnv *env, jobject thiz,
                                                             jstring jSourceDir) {
    jclass jclass1 = env->GetObjectClass(thiz);
    jmethodID jmethodId = env->GetMethodID(jclass1, "onStorageFounded",
                                                 "(Lcom/kee0kai/thekey/model/Storage;)V");
    if (jmethodId == NULL)return;
    ::jmethodIdGlb = jmethodId;
    ::envGlb = env;
    ::jStorageFilesEngineGlb = thiz;

    const char *sourceDir = env->GetStringUTFChars(jSourceDir, NULL);
    key_finder::findStorages(sourceDir, storeFound);
    env->ReleaseStringUTFChars(jSourceDir, sourceDir);

    ::envGlb = NULL;
    ::jmethodIdGlb = NULL;
    ::jStorageFilesEngineGlb = NULL;
}

