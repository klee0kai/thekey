#include <jni.h>
#include <string>

#include <android/log.h>

#include "dll_interface/key_manager_ctx.h"
#include "mapping/jmapping.h"

JNIEXPORT jint JNI_OnLoad(JavaVM *pVM, void *reserved) {
    srand(time(NULL));
    jmapping::init(pVM);
    return JNI_VERSION_1_6;
}


extern "C" JNIEXPORT jint JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_createStorage(JNIEnv *env, jobject clazz,
                                                                jobject jStorage) {
    Storage storage = jmapping::jStorage::map(env, jStorage);
    int res = key_manager_ctx::createStorage(storage);
    jmapping::jStorage::release(storage);
    return res;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_changeStorage(JNIEnv *env, jobject clazz,
                                                                jobject jOriginalStorage,
                                                                jobject jStorage) {
    Storage originalStorage = jmapping::jStorage::map(env, jOriginalStorage);
    Storage storage = jmapping::jStorage::map(env, jStorage);
    int res = key_manager_ctx::copyStorage(originalStorage.file, storage, false);
    jmapping::jStorage::release(storage);
    jmapping::jStorage::release(originalStorage);
    return res;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_copyStorage(JNIEnv *env, jobject clazz,
                                                              jobject jOriginalStorage,
                                                              jobject jStorage) {
    Storage originalStorage = jmapping::jStorage::map(env, jOriginalStorage);
    Storage storage = jmapping::jStorage::map(env, jStorage);
    int res = key_manager_ctx::copyStorage(originalStorage.file, storage, true);
    jmapping::jStorage::release(storage);
    jmapping::jStorage::release(originalStorage);
    return res;
}



extern "C" JNIEXPORT jint JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_isLogined(JNIEnv *env, jobject clazz) {
    return key_manager_ctx::isLogined();
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_getLoggedStoragePath(JNIEnv *env, jobject clazz) {
    return env->NewStringUTF(key_manager_ctx::getLoggedStoragePath());
}

extern "C" JNIEXPORT void JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_login(JNIEnv *env, jobject clazz, jstring jpath,
                                                        jstring jpassw) {
    const char *passw = env->GetStringUTFChars(jpassw, NULL);
    const char *path = env->GetStringUTFChars(jpath, NULL);

    key_manager_ctx::login((const unsigned char *) path, (const unsigned char *) passw);

    env->ReleaseStringUTFChars(jpassw, passw);
    env->ReleaseStringUTFChars(jpath, path);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_changeLoggedStorage(JNIEnv *env, jobject clazz,
                                                                      jobject jStorage,
                                                                      jstring jpassw) {
    const char *passw = env->GetStringUTFChars(jpassw, NULL);
    Storage storage = jmapping::jStorage::map(env, jStorage);

    if (passw) {
        //TODO сохранине исходного хранилища для восстановления
        key_manager_ctx::changePassw((const unsigned char *) passw);
        //TODO удаление копии исходного хранилища для восстановления

    }

    if (storage.file) {
        key_manager_ctx::copyStorage(key_manager_ctx::getLoggedStoragePath(), storage, false);
        key_manager_ctx::setLoggedStoragePath(storage.file);
    }


    jmapping::jStorage::release(storage);
    env->ReleaseStringUTFChars(jpassw, passw);
    return 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_unlogin(JNIEnv *env, jobject clazz) {
    key_manager_ctx::unLogin();
}


extern "C" JNIEXPORT jlongArray JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_getNotes(JNIEnv *env, jobject clazz) {
    long long *notes = key_manager_ctx::getNotes();
    int len;
    for (len = 0; notes[len] != NULL; len++);
    jlongArray jNotes = env->NewLongArray(len);
    env->SetLongArrayRegion(jNotes, 0, len, (jlong *) notes);
    delete[]notes;
    return jNotes;
}
extern "C" JNIEXPORT jlongArray JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_getGenPasswds(JNIEnv *env, jobject clazz) {
    long long *passwds = key_manager_ctx::getGenPassds();
    int len;
    for (len = 0; passwds[len] != NULL; len++);
    jlongArray jPasswds = env->NewLongArray(len);
    env->SetLongArrayRegion(jPasswds, 0, len, (jlong *) passwds);
    delete[]passwds;
    return jPasswds;
}



extern "C"
JNIEXPORT jobject JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_getGenPassw(JNIEnv *env, jobject clazz,
                                                              jlong note) {
    if (note == 0)return NULL;
    DecryptedPassw *decryptedPassw = key_manager_ctx::getGenPassw((long long) note);
    jobject jDecryptedPasswd = jmapping::jDecryptedPassw::map(env, decryptedPassw);

    memset(decryptedPassw, 0, sizeof(DecryptedPassw));
    delete decryptedPassw;
    return jDecryptedPasswd;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_getNote(JNIEnv *env, jobject clazz, jlong note,
                                                          jboolean jDecrPassw) {
    if (note == 0)return NULL;
    DecryptedNote *dnote = key_manager_ctx::getNoteItem((long) note, jDecrPassw);
    jobject jDecryptedNote = jmapping::jDecryptedNote::map(env, dnote);

    memset(dnote, 0, sizeof(DecryptedNote));
    delete dnote;
    return jDecryptedNote;
}


extern "C" JNIEXPORT void JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_setNote(JNIEnv *env, jobject clazz, jlong ptnote,
                                                          jobject note) {
    if (ptnote == 0)return;
    DecryptedNote *dnote = jmapping::jDecryptedNote::map(env, note);
    key_manager_ctx::setNote((long) ptnote, dnote);

    memset(dnote, 0, sizeof(DecryptedNote));
    delete dnote;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_createNote(JNIEnv *env, jobject clazz) {
    return (jlong) key_manager_ctx::createNote();
}


extern "C" JNIEXPORT void JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_rmNote(JNIEnv *env, jobject clazz, jlong note) {
    if (note == 0)return;

    key_manager_ctx::rmNote((long) note);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_kee0kai_thekey_engine_CryptStorageEngine_generateNewPassw(JNIEnv *env, jobject clazz,
                                                                   jint len,
                                                                   jint genEncoding) {
    unsigned char *genPassw = key_manager_ctx::genPassw(len, genEncoding);
    jstring jGenPassw = env->NewStringUTF((char *) genPassw);

    memset(genPassw, 0, PASSW_LEN);
    delete[] genPassw;
    return jGenPassw;
}





