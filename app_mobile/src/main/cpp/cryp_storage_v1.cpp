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

static shared_ptr<KeyStorageV1> storageV1 = {};

int EngineStorageK1Storage::isLogined() {
    return storageV1.get() != NULL;
}

void EngineStorageK1Storage::login(const std::string &passw) {
    thekey_v1::storage(getStoragePath(), passw);
}

void EngineStorageK1Storage::unlogin() {
    storageV1.reset();
}

std::vector<EngineModelDecryptedNote> EngineStorageK1Storage::notes() {
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

EngineModelDecryptedNote EngineStorageK1Storage::note(const int64_t &notePtr) {
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

int EngineStorageK1Storage::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote) {
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

int EngineStorageK1Storage::removeNote(const int64_t &notePt) {
    if (!notePt || !storageV1)return -1;
    storageV1->removeNote(notePt);
    return 0;
}

EngineModelDecryptedPassw EngineStorageK1Storage::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
EngineStorageK1Storage::generateNewPassw(const int &len, const int &genPasswEncoding) {
    if (!storageV1)return "";
    return storageV1->genPassw(len, genPasswEncoding);
}