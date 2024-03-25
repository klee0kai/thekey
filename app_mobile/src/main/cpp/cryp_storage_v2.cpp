//cryp_storage
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "memory"
#include "key_core.h"
#include "key2.h"
#include "brooklyn.h"
#include "salt_text/salt2_schema.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v2;

typedef EngineStorageK2Storage JvmStorage2;
typedef EngineModelStorage JvmStorageInfo;
typedef EngineModelGenPasswParams JvmGenPasswParams;
typedef EngineModelDecryptedPassw JvmDecryptedPassw;

static map<string, shared_ptr<KeyStorageV2>> storages = {};

static KeyStorageV2 *findStorage(const string &path) {
    auto it = storages.find(path);
    if (it == storages.end())return {};
    return &*it->second;
}

JvmStorageInfo JvmStorage2::info() {
    auto storage = findStorage(getStoragePath());
    auto storageInfo = storageFullInfo(getStoragePath());
    if (storageInfo)
        return JvmStorageInfo{
                .path = storageInfo->path,
                .name = storageInfo->name,
                .description = storageInfo->description,
                .version = int(storageInfo->storageVersion),
                .logined = storage != NULL ? 1 : 0
        };

    return {};
}

void JvmStorage2::login(const std::string &passw) {
    storages.erase(getStoragePath());
    auto storageInfo = storageFullInfo(getStoragePath());
    if (!storageInfo) createStorage(Storage{.file = getStoragePath()});

    auto storage = thekey_v2::storage(getStoragePath(), passw);
    if (storage) {
        storage->readAll();
        storage->save();
        storages.insert({getStoragePath(), storage});
    }
}

void JvmStorage2::unlogin() {
    storages.erase(getStoragePath());
}

std::vector<EngineModelDecryptedNote> JvmStorage2::notes(const int &loadInfo) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    auto flags = loadInfo ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storage->notes(flags)) {
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
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};
    auto dnote = storage->note(notePtr, TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    if (!dnote) return {};
    auto result = EngineModelDecryptedNote{
            .ptnote = notePtr,
            .site = dnote->site,
            .login = dnote->login,
            .passw = dnote->passw,
            .desc =  dnote->description,
            .chTime = (int64_t) dnote->genTime,
    };
    return result;

}

int JvmStorage2::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return -1;

    thekey_v2::DecryptedNote dnote = {
            .notePtr = decryptedNote.ptnote,
            .site = decryptedNote.site,
            .login = decryptedNote.login,
            .passw = decryptedNote.passw,
            .description = decryptedNote.desc
    };
    if (!dnote.notePtr) storage->createNote(dnote);
    storage->setNote(dnote);
    return 0;

}

int JvmStorage2::removeNote(const int64_t &notePt) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return -1;

    storage->removeNote(notePt);
    return 0;
}

std::string JvmStorage2::generateNewPassw(const JvmGenPasswParams &params) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return "";

    auto len = params.len;
    auto schemeFlags = SCHEME_NUMBERS;
    if (params.specSymbolsInPassw) {
        schemeFlags |= SCHEME_SPEC_SYMBOLS;
    } else if (params.symbolsInPassw) {
        schemeFlags |= SCHEME_ENGLISH;
    }

    auto schemeId = thekey_v2::findSchemeByFlags(schemeFlags);
    if (!params.oldPassw.empty()) {
        schemeId = thekey_v2::find_scheme_id(key_salt::from(params.oldPassw));
        if (!len) len = params.oldPassw.length();
    }

    return storage->genPassword(schemeId, len);
}

std::string JvmStorage2::lastGeneratedPassw() {
    auto storage = findStorage(getStoragePath());
    if (!storage)return "";
    auto hist = storage->genPasswHistoryList();
    if (!hist.empty()) {
        auto genPassw = storage->genPasswHistory(hist.back().histPtr, TK2_GET_NOTE_HISTORY_FULL);
        if (genPassw) return genPassw->passw;
    }
    auto schemeType = thekey_v2::findSchemeByFlags(SCHEME_NUMBERS);
    return storage->genPassword(schemeType, 4);
}

std::vector<EngineModelDecryptedPassw> JvmStorage2::genHistory() {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    auto hist = storage->genPasswHistoryList();
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

JvmDecryptedPassw JvmStorage2::getGenPassw(const int64_t &ptNote) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    auto passw = storage->genPasswHistory(ptNote);
    if (!passw)return {};

    JvmDecryptedPassw jvmDecryptedPassw = {
            .passwPtr = passw->histPtr,
            .passw = passw->passw,
            .chTime = static_cast<int64_t>(passw->genTime),
    };

    return jvmDecryptedPassw;
}