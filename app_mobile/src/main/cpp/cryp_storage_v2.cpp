//cryp_storage
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "memory"
#include "key_core.h"
#include "key2.h"
#include "key_find.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v2;

typedef EngineStorageK2Storage JvmStorage2;
typedef EngineModelStorage JvmStorageInfo;

static map<string, KeyStorageV2> storages = {};

static KeyStorageV2 *findStorage(const string &path) {
    auto it = storages.find(path);
    if (it == storages.end())return {};
    return &it->second;
}

JvmStorageInfo JvmStorage2::info() {
    auto storageV2 = findStorage(getStoragePath());
    auto storageInfo = thekey::storage(getStoragePath());
    if (storageInfo)
        return JvmStorageInfo{
                .path = storageInfo->file,
                .name = storageInfo->name,
                .description = storageInfo->description,
                .version = int(storageInfo->storageVersion),
                .isLogined = storageV2 != NULL ? 1 : 0
        };

    return {};
}

void JvmStorage2::login(const std::string &passw) {
    auto storage = thekey_v2::storage(getStoragePath(), passw);
    if (storage) storages.insert({getStoragePath(), *storage});
}

void JvmStorage2::unlogin() {
    storages.erase(getStoragePath());
}

std::vector<EngineModelDecryptedNote> JvmStorage2::notes() {
    auto storageV2 = findStorage(getStoragePath());
    if (!storageV2)return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    for (const auto &dnote: storageV2->notes(TK2_GET_NOTE_INFO)) {
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
    auto storageV2 = findStorage(getStoragePath());
    if (!storageV2)return {};

    auto dnote = storageV2->note(notePtr, 1);
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
    auto storageV2 = findStorage(getStoragePath());
    if (!storageV2)return {};

    thekey_v2::DecryptedNote dnote = {
            .notePtr = decryptedNote.ptnote,
            .site = decryptedNote.site,
            .login = decryptedNote.login,
            .passw = decryptedNote.passw,
            .description = decryptedNote.desc
    };
    if (!dnote.notePtr) storageV2->createNote(dnote);
    storageV2->setNote(dnote);
    return 0;

}

int JvmStorage2::removeNote(const int64_t &notePt) {
    auto storageV2 = findStorage(getStoragePath());
    if (!notePt || !storageV2)return -1;
    storageV2->removeNote(notePt);
    return 0;
}

EngineModelDecryptedPassw JvmStorage2::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
JvmStorage2::generateNewPassw(const int &len, const int &genPasswEncoding) {
    auto storageV2 = findStorage(getStoragePath());
    if (!storageV2)return "";
//    return storageV2->genPassw(len, genPasswEncoding);
    return "";
}