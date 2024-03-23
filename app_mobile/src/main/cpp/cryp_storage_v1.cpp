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
#include "key_find.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v1;

typedef EngineStorageK1Storage JvmStorage1;
typedef EngineModelStorage JvmStorageInfo;

static map<string, KeyStorageV1> storages = {};

static KeyStorageV1 *findStorage(const string &path) {
    auto it = storages.find(path);
    if (it == storages.end())return {};
    return &it->second;
}

JvmStorageInfo JvmStorage1::info() {
    auto storageV1 = findStorage(getStoragePath());
    auto storageInfo = thekey::storage(getStoragePath());
    if (storageInfo)
        return JvmStorageInfo{
                .path = storageInfo->file,
                .name = storageInfo->name,
                .description = storageInfo->description,
                .version = int(storageInfo->storageVersion),
                .isLogined = storageV1 != NULL ? 1 : 0
        };

    return {};
}

void JvmStorage1::login(const std::string &passw) {
    auto storage = thekey_v1::storage(getStoragePath(), passw);
    if (storage) storages.insert({getStoragePath(), *storage});
}

void JvmStorage1::unlogin() {
    storages.erase(getStoragePath());
}

std::vector<EngineModelDecryptedNote> JvmStorage1::notes() {
    auto storageV1 = findStorage(getStoragePath());
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

EngineModelDecryptedNote JvmStorage1::note(const int64_t &notePtr) {
    auto storageV1 = findStorage(getStoragePath());
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

int JvmStorage1::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote) {
    auto storageV1 = findStorage(getStoragePath());
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

int JvmStorage1::removeNote(const int64_t &notePt) {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return -1;

    storageV1->removeNote(notePt);
    return 0;
}

EngineModelDecryptedPassw JvmStorage1::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
JvmStorage1::generateNewPassw(const int &len, const int &genPasswEncoding) {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return "";
    return storageV1->genPassw(len, genPasswEncoding);
}