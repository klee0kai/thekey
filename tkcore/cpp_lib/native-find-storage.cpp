#include <jni.h>
#include <string>
#include <ftw.h>
#include "mapping/jmapping.h"

#include <android/log.h>

#include "dll_interface/key_finder.h"


static JNIEnv *envGlb = NULL;
static jmethodID jmethodIdGlb = NULL;
static jclass jStorageFilesEngineGlb;

static void storeFound(Storage storage) {
    jobject jStorage = jmapping::jStorage::map(envGlb, &storage);
    envGlb->CallStaticVoidMethod(jStorageFilesEngineGlb, jmethodIdGlb, jStorage);
    envGlb->DeleteLocalRef(jStorage);
}


extern "C" JNIEXPORT void JNICALL
Java_com_kuzubov_thekey_tojni_FindStorageEngine_findStorage(JNIEnv *env, jclass jclass1, jstring jSourceDir) {
    jmethodID jmethodId = env->GetStaticMethodID(jclass1, "onStorageFounded", "(Lcom/kuzubov/thekey/model/Storage;)V");
    if (jmethodId == NULL)return;
    ::jmethodIdGlb = jmethodId;
    ::envGlb = env;
    ::jStorageFilesEngineGlb = jclass1;

    const char *sourceDir = env->GetStringUTFChars(jSourceDir, NULL);
    key_finder::findStorages(sourceDir, storeFound);
    env->ReleaseStringUTFChars(jSourceDir, sourceDir);

    ::envGlb = NULL;
    ::jmethodIdGlb = NULL;
    ::jStorageFilesEngineGlb = NULL;
}
