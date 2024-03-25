//cryp_storage
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "memory"
#include "key_core.h"
#include "key1.h"
#include "key_find.h"
#include "brooklyn.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v1;

typedef EngineStorageK1Storage JvmStorage1;
typedef EngineModelStorage JvmStorageInfo;
typedef EngineModelGenPasswParams JvmGenPasswParams;
typedef EngineModelDecryptedPassw JvmDecryptedPassw;

static map<string, shared_ptr<KeyStorageV1>> storages = {};

static KeyStorageV1 *findStorage(const string &path) {
    auto it = storages.find(path);
    if (it == storages.end())return {};
    return &*it->second;
}

JvmStorageInfo JvmStorage1::info() {
    auto storageV1 = findStorage(getStoragePath());
    auto storageInfo = storageV1Info(getStoragePath());
    if (storageInfo)
        return JvmStorageInfo{
                .path = storageInfo->path,
                .name = storageInfo->name,
                .description = storageInfo->description,
                .version = int(storageInfo->storageVersion),
                .logined = storageV1 != NULL ? 1 : 0
        };

    return {};
}

void JvmStorage1::login(const std::string &passw) {
    storages.erase(getStoragePath());
    auto storageInfo = storageV1Info(getStoragePath());
    if (!storageInfo) createStorage(Storage{.file = getStoragePath()});

    auto storage = thekey_v1::storage(getStoragePath(), passw);
    if (storage) {
        storage->readAll();
        storage->save();
        storages.insert({getStoragePath(), storage});
    }
}

void JvmStorage1::unlogin() {
    storages.erase(getStoragePath());
}

std::vector<EngineModelDecryptedNote> JvmStorage1::notes(const int &loadInfo) {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    auto flags = loadInfo ? TK1_GET_NOTE_INFO : TK1_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storageV1->notes(flags)) {
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
    auto dnote = storageV1->note(notePtr, TK1_GET_NOTE_INFO | TK1_GET_NOTE_PASSWORD);
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

std::string JvmStorage1::generateNewPassw(const JvmGenPasswParams &params) {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return "";
    int genPasswEncoding = ENC_NUM_ONLY;
    if (params.specSymbolsInPassw) {
        genPasswEncoding = ENC_EN_NUM_SPEC_SYMBOLS;
    } else if (params.symbolsInPassw) {
        genPasswEncoding = ENC_EN_NUM;
    }
    return storageV1->genPassw(params.len, genPasswEncoding);
}

std::string JvmStorage1::lastGeneratedPassw() {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return "";
    auto hist = storageV1->genPasswHistoryList();
    if (!hist.empty()) {
        auto genPassw = storageV1->genPasswHistory(hist.back().histPtr, TK1_GET_NOTE_HISTORY_FULL);
        if (genPassw) return genPassw->passw;
    }
    return storageV1->genPassw(4, ENC_NUM_ONLY);
}

std::vector<EngineModelDecryptedPassw> JvmStorage1::genHistory() {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return {};

    auto hist = storageV1->genPasswHistoryList();
    auto jvmHist = std::vector<EngineModelDecryptedPassw>();
    jvmHist.reserve(hist.size());
    for (const auto &item: hist) {
        jvmHist.push_back(JvmDecryptedPassw{
                .passwPtr = item.histPtr,
                .passw = item.passw,
                .chTime = static_cast<int64_t>(item.genTime),
        });
    }

    return jvmHist;
}

JvmDecryptedPassw JvmStorage1::getGenPassw(const int64_t &ptNote) {
    auto storageV1 = findStorage(getStoragePath());
    if (!storageV1)return {};

    auto passw = storageV1->genPasswHistory(ptNote);
    if (!passw)return {};

    JvmDecryptedPassw jvmDecryptedPassw = {
            .passwPtr = passw->histPtr,
            .passw = passw->passw,
            .chTime = static_cast<int64_t>(passw->genTime),
    };

    return jvmDecryptedPassw;
}