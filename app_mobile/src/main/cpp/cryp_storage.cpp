//
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

static shared_ptr<KeyStorageV1> storageV1 = {};


int EngineCryptStorageEngine::isLogined() {
    return storageV1.get() != NULL;
}

void EngineCryptStorageEngine::login(const std::string &passw) {
    thekey_v1::storage(getStoragePath(), passw);
}

void EngineCryptStorageEngine::unlogin() {
    storageV1.reset();
}

std::vector<EngineModelDecryptedNote> EngineCryptStorageEngine::notes() {
    if (!storageV1)return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    for (const auto &ptnote: storageV1->notes()) {
        auto dnote = storageV1->note(ptnote);
        notes.push_back({
                                .ptnote = ptnote,
                                .site =  dnote->site,
                                .login =  dnote->login,
                                .desc =  dnote->description,
                                .chTime = (int64_t) dnote->genTime,
                        });
    }
    return notes;
}

EngineModelDecryptedNote EngineCryptStorageEngine::note(const int64_t &notePtr) {
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

int EngineCryptStorageEngine::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote) {
    if (!storageV1)return -1;
    auto ptNote = decryptedNote.ptnote;
    if (!ptNote) ptNote = storageV1->createNote();
    DecryptedNote dnote = {
            .site = decryptedNote.site,
            .login = decryptedNote.login,
            .passw = decryptedNote.passw,
            .description = decryptedNote.desc
    };
    storageV1->setNote(ptNote, dnote);
    return 0;

}

int EngineCryptStorageEngine::removeNote(const int64_t &notePt) {
    if (!notePt || !storageV1)return -1;
    storageV1->removeNote(notePt);
}

EngineModelDecryptedPassw EngineCryptStorageEngine::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
EngineCryptStorageEngine::generateNewPassw(const int &len, const int &genPasswEncoding) {
    if (!storageV1)return "";
    return storageV1->genPassw(len, genPasswEncoding);
}