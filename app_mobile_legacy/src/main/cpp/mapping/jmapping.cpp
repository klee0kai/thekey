//
// Created by panda on 2020-02-24.
//

#include "jmapping.h"

static struct jDecriptedNoteClass_info {
    jfieldID site = NULL;
    jfieldID login = NULL;
    jfieldID passw = NULL;
    jfieldID desc = NULL;
    jfieldID chtime = NULL;
    jfieldID hist = NULL;

    jmethodID initMethod = NULL;
} *jDecriptedNoteClassInfo = NULL;

static struct jDecriptedPasswClass_info {
    jfieldID passw = NULL;
    jfieldID chtime = NULL;

    jmethodID initMethod = NULL;
} *jDecriptedPasswClassInfo = NULL;

static struct jStorageClass_info {
    jfieldID path = NULL;
    jfieldID name = NULL;
    jfieldID description = NULL;


    jmethodID initMethod = NULL;
} *jStorageClassInfo;

int jmapping::init(JavaVM *pVM) {
    JNIEnv *env = NULL;
    pVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    {
        // create jDecriptedNoteClassInfo
        jDecriptedNoteClassInfo = new jDecriptedNoteClass_info{};
        jclass cls = env->FindClass("com/kee0kai/thekey/engine/model/DecryptedNote");
        jDecriptedNoteClassInfo->site = env->GetFieldID(cls, "site", "Ljava/lang/String;");
        jDecriptedNoteClassInfo->login = env->GetFieldID(cls, "login", "Ljava/lang/String;");
        jDecriptedNoteClassInfo->passw = env->GetFieldID(cls, "passw", "Ljava/lang/String;");
        jDecriptedNoteClassInfo->desc = env->GetFieldID(cls, "desc", "Ljava/lang/String;");
        jDecriptedNoteClassInfo->chtime = env->GetFieldID(cls, "chTime", "J");
        jDecriptedNoteClassInfo->hist = env->GetFieldID(cls, "hist",
                                                        "[Lcom/kee0kai/thekey/engine/model/DecryptedPassw;");
        jDecriptedNoteClassInfo->initMethod = env->GetMethodID(cls, "<init>",
                                                               "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J[Lcom/kee0kai/thekey/engine/model/DecryptedPassw;)V");
    }

    {
        // create jDecriptedPasswClassInfo
        jDecriptedPasswClassInfo = new jDecriptedPasswClass_info{};
        jclass cls = env->FindClass("com/kee0kai/thekey/engine/model/DecryptedPassw");
        jDecriptedPasswClassInfo->passw = env->GetFieldID(cls, "passw", "Ljava/lang/String;");
        jDecriptedPasswClassInfo->chtime = env->GetFieldID(cls, "chTime", "J");
        jDecriptedPasswClassInfo->initMethod = env->GetMethodID(cls, "<init>",
                                                                "(Ljava/lang/String;J)V");

    }

    {
        //create JStorageClassInfo
        jStorageClassInfo = new jStorageClass_info{};
        jclass cls = env->FindClass("com/kee0kai/thekey/model/Storage");
        jStorageClassInfo->path = env->GetFieldID(cls, "path", "Ljava/lang/String;");
        jStorageClassInfo->name = env->GetFieldID(cls, "name", "Ljava/lang/String;");
        jStorageClassInfo->description = env->GetFieldID(cls, "description", "Ljava/lang/String;");
        jStorageClassInfo->initMethod = env->GetMethodID(cls, "<init>",
                                                         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    }

    return 0;
}

jobject jmapping::jDecryptedNote::map(JNIEnv *env, DecryptedNote *note) {
    jstring jSite = note->site != NULL ? env->NewStringUTF((const char *) note->site) : NULL;
    jstring jLogin = note->login != NULL ? env->NewStringUTF((const char *) note->login) : NULL;
    jstring jPassw = note->passw != NULL ? env->NewStringUTF((const char *) note->passw) : NULL;
    jstring jDesc = note->description ? env->NewStringUTF((const char *) note->description) : NULL;
    jobjectArray jhist = jmapping::jDecryptedPassw::mapArray(env, note->hist, note->histLen);

    jclass cls = env->FindClass("com/kee0kai/thekey/engine/model/DecryptedNote");

    jobject jDecrNote = env->NewObject(cls, jDecriptedNoteClassInfo->initMethod, jSite, jLogin,
                                       jPassw, jDesc, note->genTime, jhist);
    return jDecrNote;
}

//don't mapp hist and chtime
DecryptedNote *jmapping::jDecryptedNote::map(JNIEnv *env, jobject jDecryptedNote) {
    jstring jSite = (jstring) env->GetObjectField(jDecryptedNote, jDecriptedNoteClassInfo->site);
    jstring jLogin = (jstring) env->GetObjectField(jDecryptedNote, jDecriptedNoteClassInfo->login);
    jstring jPassw = (jstring) env->GetObjectField(jDecryptedNote, jDecriptedNoteClassInfo->passw);
    jstring jDesc = (jstring) env->GetObjectField(jDecryptedNote, jDecriptedNoteClassInfo->desc);
    const char *site = jSite != NULL ? env->GetStringUTFChars(jSite, NULL) : NULL;
    const char *login = jLogin != NULL ? env->GetStringUTFChars(jLogin, NULL) : NULL;
    const char *passw = jPassw != NULL ? env->GetStringUTFChars(jPassw, NULL) : NULL;
    const char *desc = jDesc != NULL ? env->GetStringUTFChars(jDesc, NULL) : NULL;

    DecryptedNote *note = new DecryptedNote{};
    if (site != NULL) strcpy((char *) note->site, site);
    if (login != NULL) strcpy((char *) note->login, login);
    if (passw != NULL) strcpy((char *) note->passw, passw);
    if (desc != NULL) strcpy((char *) note->description, desc);

    if (site != NULL) env->ReleaseStringUTFChars(jSite, site);
    if (login != NULL) env->ReleaseStringUTFChars(jLogin, login);
    if (passw != NULL) env->ReleaseStringUTFChars(jPassw, passw);
    if (desc != NULL) env->ReleaseStringUTFChars(jDesc, desc);
    return note;

}


jobject jmapping::jDecryptedPassw::map(JNIEnv *env, DecryptedPassw *dpassw) {
    jstring jPassw = dpassw->passw != NULL ? env->NewStringUTF((const char *) dpassw->passw) : NULL;
    jclass cls = env->FindClass("com/kee0kai/thekey/engine/model/DecryptedPassw");

    jobject jDecrPassw = env->NewObject(cls, jDecriptedPasswClassInfo->initMethod, jPassw,
                                        dpassw->genTime);
    return jDecrPassw;
}

DecryptedPassw *jmapping::jDecryptedPassw::map(JNIEnv *env, jobject jDecryptedPassw) {
    jstring jPassw = (jstring) env->GetObjectField(jDecryptedPassw,
                                                   jDecriptedPasswClassInfo->passw);
    long chTime = (long) env->GetLongField(jDecryptedPassw, jDecriptedPasswClassInfo->chtime);
    const char *passw = jPassw != NULL ? env->GetStringUTFChars(jPassw, NULL) : NULL;

    DecryptedPassw *dPassw = new DecryptedPassw{};
    if (passw != NULL) strcpy((char *) dPassw->passw, passw);
    dPassw->genTime = chTime;

    if (passw != NULL) env->ReleaseStringUTFChars(jPassw, passw);
    return dPassw;
}

jobjectArray jmapping::jDecryptedPassw::mapArray(JNIEnv *env, DecryptedPassw *dpassws, int len) {
    jclass cls = env->FindClass("com/kee0kai/thekey/engine/model/DecryptedPassw");
    jobject initObj = env->NewObject(cls, jDecriptedPasswClassInfo->initMethod, NULL, 0);

    jobjectArray jDecPassws = env->NewObjectArray(len, cls, initObj);
    for (int i = 0; i < len; i++) {
        jobject jpassw = jmapping::jDecryptedPassw::map(env, dpassws + i);
        env->SetObjectArrayElement(jDecPassws, i, jpassw);
    }

    return jDecPassws;
}

DecryptedPassw *
jmapping::jDecryptedPassw::mapArray(JNIEnv *env, jobjectArray jDecryptedPassws, int &outLen) {
    outLen = env->GetArrayLength(jDecryptedPassws);
    DecryptedPassw *passwds = new DecryptedPassw[outLen];
    for (int i = 0; i < outLen; i++) {
        DecryptedPassw *decryptedPassw = jmapping::jDecryptedPassw::map(env,
                                                                        env->GetObjectArrayElement(
                                                                                jDecryptedPassws,
                                                                                i));
        memcpy(passwds + i, decryptedPassw, sizeof(DecryptedPassw));
        memset(decryptedPassw, 0, sizeof(DecryptedPassw));
        delete decryptedPassw;
    }
    return passwds;
}


Storage jmapping::jStorage::map(JNIEnv *env, jobject jStorage) {
    jclass cls = env->FindClass("com/kee0kai/thekey/model/Storage");
    if (jStorage == NULL)
        return {};

    jstring jPath = (jstring) env->GetObjectField(jStorage, jStorageClassInfo->path);
    jstring jName = (jstring) env->GetObjectField(jStorage, jStorageClassInfo->name);
    jstring jDescr = (jstring) env->GetObjectField(jStorage, jStorageClassInfo->description);
    char *path = NULL, *name = NULL, *descr = NULL;
    if (jPath != NULL) {
        const char *_path = env->GetStringUTFChars(jPath, NULL);
        path = new char[strlen(_path) + 1];
        strcpy(path, _path);
        env->ReleaseStringUTFChars(jPath, _path);
    }
    if (jName != NULL) {
        const char *_name = env->GetStringUTFChars(jName, NULL);
        name = new char[strlen(_name) + 1];
        strcpy(name, _name);
        env->ReleaseStringUTFChars(jName, _name);
    }
    if (jDescr != NULL) {
        const char *_descr = env->GetStringUTFChars(jDescr, NULL);
        descr = new char[strlen(_descr) + 1];
        strcpy(descr, _descr);
        env->ReleaseStringUTFChars(jDescr, _descr);
    }

    struct Storage storage = {};
    storage.file = path;
    storage.name = name;
    storage.description = descr;
    return storage;
}

void jmapping::jStorage::release(Storage &storage) {
    if (storage.file != NULL) {
        delete storage.file;
        storage.file = NULL;
    }
    if (storage.name != NULL) {
        delete storage.name;
        storage.name = NULL;
    }
    if (storage.description != NULL) {
        delete storage.description;
        storage.description = NULL;
    }

}

jobject jmapping::jStorage::map(JNIEnv *env, const Storage *storage) {
    jclass cls = env->FindClass("com/kee0kai/thekey/model/Storage");
    jstring jPath = env->NewStringUTF(storage->file);
    jstring jName = env->NewStringUTF(storage->name);
    jstring jDescr = env->NewStringUTF(storage->description);

    jobject initObj = env->NewObject(cls, jStorageClassInfo->initMethod, jPath, jName, jDescr);
    return initObj;
}
