//
// Created by panda on 28.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "key_find.h"
#include "key2.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;

typedef EngineModelStorage JvmStorage;
typedef EngineEditstorageEditStorageEngine JvmEditStorageEngine;
typedef brooklyn::EngineModelDecryptedNote JvmDecryptedNote;
typedef brooklyn::EngineModelDecryptedOtpNote JvmDecryptedOtpNote;


std::shared_ptr<JvmStorage> JvmEditStorageEngine::findStorageInfo(const std::string &path) {
    auto storage = shared_ptr<JvmStorage>{

    };
    return storage;
}


int JvmEditStorageEngine::createStorage(const JvmStorage &storage) {
    auto error = thekey_v2::createStorage(thekey::Storage{
            .file = storage.path,
            .name = storage.name,
            .description = storage.description,
    });
    return error;
}

int JvmEditStorageEngine::editStorage(const JvmStorage &storage) {
    return 0;
}

int JvmEditStorageEngine::move(const std::string &from, const std::string &to) {
    rename(from.c_str(), to.c_str());
    return 0;
}


void JvmEditStorageEngine::changePassw(
        const std::string &path,
        const std::string &currentPassw,
        const std::string &newPassw
) {
    auto storage = thekey_v2::storage(path, currentPassw);
    if (!storage) return ;
    storage->readAll();
    storage->saveNewPassw(path,newPassw);
}

std::vector<EngineModelDecryptedNote>
JvmEditStorageEngine::notes(
        const std::string &path,
        const std::string &passw
) {
    auto storage = thekey_v2::storage(path, passw);
    if (!storage) return {};
    storage->readAll();

    auto notes = std::vector<JvmDecryptedNote>();
    for (const auto &dnote: storage->notes(TK2_GET_NOTE_INFO)) {
        notes.push_back(
                {
                        .ptnote = dnote.id,
                        .site =  dnote.site,
                        .login =  dnote.login,
                        .desc =  dnote.description,
                        .chTime = (int64_t) dnote.genTime,
                        .colorGroupId = dnote.colorGroupId,
                });
    }

    return notes;
}

std::vector<EngineModelDecryptedOtpNote>
JvmEditStorageEngine::otpNotes(
        const std::string &path,
        const std::string &passw
) {
    auto storage = thekey_v2::storage(path, passw);
    if (!storage) return {};
    storage->readAll();
    auto otpNotes = std::vector<JvmDecryptedOtpNote>();
    for (const auto &dnote: storage->otpNotes(TK2_GET_NOTE_INFO)) {
        otpNotes.push_back(
                JvmDecryptedOtpNote{
                        .ptnote = dnote.id,
                        .issuer =  dnote.issuer,
                        .name =  dnote.name,
                        .otpMethodRaw = dnote.method,
                        .otpAlgoRaw = dnote.algo,
                        .digits = int(dnote.digits),
                        .interval = int(dnote.interval),
                        .counter = int(dnote.counter),
                        .crTime = (int64_t) dnote.createTime,
                        .colorGroupId = dnote.colorGroupId,
                });
    }

    return otpNotes;
}