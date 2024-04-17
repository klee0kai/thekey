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
#include "tools/base32.h"

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

std::vector<EngineModelDecryptedColorGroup> JvmStorage2::colorGroups(const int &info) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return {};
    auto notes = std::vector<EngineModelDecryptedColorGroup>();
    auto flags = info ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &group: storage->colorGroups(flags)) {
        notes.push_back(
                EngineModelDecryptedColorGroup{
                        .id = group.id,
                        .name =  group.name,
                        .color =  group.color,
                }
        );
    }
    return notes;
}

std::shared_ptr<EngineModelDecryptedColorGroup> JvmStorage2::saveColorGroup(const brooklyn::EngineModelDecryptedColorGroup &group) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return {};

    auto dGroup = make_shared<DecryptedColorGroup>(DecryptedColorGroup{
            .id = group.id,
            .color = KeyColor(group.color),
            .name = group.name
    });
    if (!dGroup->id) {
        dGroup = storage->createColorGroup(*dGroup);
    } else {
        storage->setColorGroup(*dGroup);
    }

    auto jniColorGroup = make_shared<EngineModelDecryptedColorGroup>(
            EngineModelDecryptedColorGroup{
                    .id = dGroup->id,
                    .name = dGroup->name,
                    .color = dGroup->color,
            }
    );

    return jniColorGroup;
}

int JvmStorage2::setNotesGroup(const std::vector<int64_t> &notePtrs, const int64_t &groupId) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return -1;

    auto flags = TK2_GET_NOTE_PTR_ONLY;
    for (const auto &id: notePtrs) {
        auto note = storage->note(id, flags);
        if (!note)continue;
        note->colorGroupId = groupId;
        storage->setNote(*note, flags);
    }

    storage->save();
    return 0;
}

int JvmStorage2::removeColorGroup(const int64_t &colorGroupId) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return -1;

    storage->removeColorGroup(colorGroupId);
    return 0;
}

std::vector<EngineModelDecryptedNote> JvmStorage2::notes(const int &loadInfo) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return {};
    auto notes = std::vector<EngineModelDecryptedNote>();
    auto flags = loadInfo ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storage->notes(flags)) {
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
            .colorGroupId = dnote->colorGroupId,
    };
    return result;

}

int JvmStorage2::saveNote(const brooklyn::EngineModelDecryptedNote &decryptedNote, const int &setAll) {
    auto storage = findStorage(getStoragePath());
    if (!storage) return -1;

    auto flags = 0;
    if (setAll) {
        flags |= TK2_SET_NOTE_INFO | TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY | TK2_SET_NOTE_SAVE_TO_FILE;
    }

    thekey_v2::DecryptedNote dnote = {
            .id = decryptedNote.ptnote,
            .site = decryptedNote.site,
            .login = decryptedNote.login,
            .passw = decryptedNote.passw,
            .description = decryptedNote.desc,
            .colorGroupId = decryptedNote.colorGroupId,
    };
    if (!dnote.id) {
        storage->createNote(dnote, flags);
    } else {
        storage->setNote(dnote, flags);
    }
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
        auto genPassw = storage->genPasswHistory(hist.back().id, TK2_GET_NOTE_HISTORY_FULL);
        if (genPassw) return genPassw->passw;
    }
    auto schemeType = thekey_v2::findSchemeByFlags(SCHEME_NUMBERS);
    return storage->genPassword(schemeType, 4);
}

std::vector<EngineModelDecryptedPassw> JvmStorage2::genHistory(const int &info) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    auto flags = info ? TK2_GET_NOTE_HISTORY_FULL : 0;
    auto hist = storage->genPasswHistoryList(flags);
    auto jvmHist = std::vector<EngineModelDecryptedPassw>();
    jvmHist.reserve(hist.size());
    for (const auto &item: hist) {
        jvmHist.push_back(JvmDecryptedPassw{
                .passwPtr = item.id,
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
            .passwPtr = passw->id,
            .passw = passw->passw,
            .chTime = static_cast<int64_t>(passw->genTime),
    };

    return jvmDecryptedPassw;
}

std::vector<EngineModelDecryptedOtpNote> JvmStorage2::otpNotes(const int &info) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};
    auto otpNotes = std::vector<EngineModelDecryptedOtpNote>();
    auto flags = info ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storage->otpNotes(flags)) {
        otpNotes.push_back(
                EngineModelDecryptedOtpNote{
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

EngineModelDecryptedOtpNote JvmStorage2::otpNote(const int64_t &notePtr) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};
    auto dnotePtr = storage->otpNote(notePtr, TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD);
    if (!dnotePtr)return {};
    auto dnote = *dnotePtr;
    return EngineModelDecryptedOtpNote{
            .ptnote = dnote.id,
            .issuer =  dnote.issuer,
            .name =  dnote.name,
            .secret = dnote.secret,
            .pin = dnote.pin,
            .otpPassw = dnote.otpPassw,
            .otpMethodRaw = dnote.method,
            .otpAlgoRaw = dnote.algo,
            .digits = int(dnote.digits),
            .interval = int(dnote.interval),
            .counter = int(dnote.counter),
            .crTime = (int64_t) dnote.createTime,
            .colorGroupId = dnote.colorGroupId,
    };


}

int JvmStorage2::saveOtpNote(const brooklyn::EngineModelDecryptedOtpNote &jOtp, const int &setAll) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    auto dnote = DecryptedOtpNote{
            .id = jOtp.ptnote,
            .issuer = jOtp.issuer,
            .name = jOtp.name,
            .secret = jOtp.secret,
            .method =  key_otp::OtpMethod(jOtp.otpMethodRaw),
            .algo =  key_otp::OtpAlgo(jOtp.otpAlgoRaw),
            .digits = uint32_t(jOtp.digits),
            .interval = uint32_t(jOtp.interval),
            .counter = uint32_t(jOtp.counter),
            .pin = jOtp.pin,
            .colorGroupId = jOtp.colorGroupId,
    };

    if (dnote.id == 0) {
        storage->createOtpNote(dnote, 0);
    } else {
        storage->setOtpNote(dnote, 0);
    }
    return -1;
}

int JvmStorage2::removeOtpNote(const int64_t &notePt) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    storage->removeOtpNote(notePt);
    return 0;
}

shared_ptr<EngineModelDecryptedOtpNote> JvmStorage2::otpNoteFromUrl(const std::string &url) {
    auto otpList = key_otp::parseOtpUri(url);
    if (otpList.empty())return {};
    auto otp = otpList.front();
    return make_shared<EngineModelDecryptedOtpNote>(
            EngineModelDecryptedOtpNote{
                    .issuer = otp.issuer,
                    .name = otp.name,
                    .url = otp.toUri(),
                    .secret = base32::encode(otp.secret, true),
                    .otpMethodRaw = otp.method,
                    .otpAlgoRaw = otp.algorithm,
                    .digits =int(otp.digits),
                    .interval=int(otp.interval),
                    .counter = int(otp.counter),
            });
}

int JvmStorage2::setOtpNotesGroup(const std::vector<int64_t> &notePtrs, const int64_t &groupId) {
    auto storage = findStorage(getStoragePath());
    if (!storage)return {};

    auto flags = TK2_GET_NOTE_PTR_ONLY;
    for (const auto &id: notePtrs) {
        auto note = storage->otpNote(id, flags);
        if (!note)continue;
        note->colorGroupId = groupId;
        storage->setOtpNote(*note, flags);
    }

    storage->save();
    return 0;
}