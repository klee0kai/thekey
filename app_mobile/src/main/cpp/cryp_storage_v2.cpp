//cryp_storage
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "memory"
#include "key_core.h"
#include "key1.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v1;

typedef EngineStorageK2Storage JvmStorage2;
typedef EngineModelStorage JvmStorageInfo;

static shared_ptr<KeyStorageV1> storageV1 = {};

JvmStorageInfo JvmStorage2::info() {

    return JvmStorageInfo{

    };
}

void JvmStorage2::login(const std::string &passw) {
    thekey_v1::storage(getStoragePath(), passw);
}

void JvmStorage2::unlogin() {
    storageV1.reset();
}

std::vector<EngineModelDecryptedNote> JvmStorage2::notes() {
    if (!storageV1)return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    for (const auto &dnote: storageV1->notes(TK1_GET_NOTE_INFO)) {
        notes.push_back(
                {
                        .ptnote = dnote.notePtr,
                        .site =  dnote.site,
                        .login =  dnote.login,
                        .desc =  dnote.description,
                        .chTime = (int64_t) dnote.genTime,
                });
    }
    return notes;
}

EngineModelDecryptedNote JvmStorage2::note(const int64_t &notePtr) {
    if (!storageV1)return {};

    auto dnote = storageV1->note(notePtr, 1);
    auto result = EngineModelDecryptedNote{
            .ptnote = notePtr,
            .site =  dnote->site,
            .login =  dnote->login,
            .passw =  dnote->passw,
            .desc =  dnote->description,
            .chTime = (int64_t) dnote->genTime,
    };
    return result;

}

int JvmStorage2::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote) {
    if (!storageV1)return -1;
    thekey_v1::DecryptedNote dnote = {
            .notePtr = decryptedNote.ptnote,
            .site = decryptedNote.site,
            .login = decryptedNote.login,
            .passw = decryptedNote.passw,
            .description = decryptedNote.desc
    };
    if (!dnote.notePtr) storageV1->createNote(dnote);
    storageV1->setNote(dnote);
    return 0;

}

int JvmStorage2::removeNote(const int64_t &notePt) {
    if (!notePt || !storageV1)return -1;
    storageV1->removeNote(notePt);
    return 0;
}

EngineModelDecryptedPassw JvmStorage2::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
JvmStorage2::generateNewPassw(const int &len, const int &genPasswEncoding) {
    if (!storageV1)return "";
    return storageV1->genPassw(len, genPasswEncoding);
}