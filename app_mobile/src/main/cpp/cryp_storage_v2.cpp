//cryp_storage
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "memory"
#include "key_core.h"
#include "key2.h"
#include "split_password.h"
#include "brooklyn.h"
#include "salt_text/salt2_schema.h"
#include "tools/base32.h"

using namespace brooklyn;
using namespace std;
using namespace thekey;
using namespace thekey_v2;

typedef brooklyn::EngineStorageK2Storage JvmStorage2;
typedef brooklyn::EngineModelStorage JvmStorageInfo;
typedef brooklyn::EngineModelCreateStorageConfig JvmCreateConfig;
typedef brooklyn::EngineModelGenPasswParams JvmGenPasswParams;
typedef brooklyn::EngineModelDecryptedPassw JvmDecryptedPassw;
typedef brooklyn::EngineModelDecryptedNote JvmDecryptedNote;
typedef brooklyn::EngineModelDecryptedOtpNote JvmDecryptedOtpNote;
typedef brooklyn::EngineModelDecryptedColorGroup JvmColorGroup;
typedef brooklyn::EngineModelTwinsCollection JvmTwinsCollection;

static map<string, shared_ptr<KeyStorageV2>> storages = {};

static KeyStorageV2 *findStorage(const string &engineId) {
    auto it = storages.find(engineId);
    if (it == storages.end())return {};
    return &*it->second;
}

JvmStorageInfo JvmStorage2::info() {
    auto storage = findStorage(getEngineIdentifier());
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

void JvmStorage2::login(const std::string &passw, const JvmCreateConfig &createConfig) {
    auto fileDescriptor = getFileDescriptor();
    storages.erase(getEngineIdentifier());
    shared_ptr<KeyStorageV2> storage = {};
    if (fileDescriptor) {
        storage = thekey_v2::storage(*fileDescriptor, getStoragePath(), passw);
        if (storage) storage->setSingleDescriptorMode(1);
    } else {
        auto storageInfo = storageFullInfo(getStoragePath());
        if (!storageInfo)
            createStorage(
                    Storage{.file = getStoragePath()},
                    createConfig.keyInteractionsCount,
                    createConfig.interactionsCount
            );
        storage = thekey_v2::storage(getStoragePath(), passw);
    }
    if (storage) {
        storage->readAll();
        storages.insert({getEngineIdentifier(), storage});
    }
}

void JvmStorage2::unlogin() {
    storages.erase(getEngineIdentifier());
}

void JvmStorage2::logoutAll() {
    storages.clear();
}

std::vector<JvmColorGroup> JvmStorage2::colorGroups(const int &info) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage) return {};
    auto notes = std::vector<JvmColorGroup>();
    auto flags = info ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &group: storage->colorGroups(flags)) {
        notes.push_back(
                JvmColorGroup{
                        .id = group.id,
                        .name =  group.name,
                        .color =  group.color,
                }
        );
    }
    return notes;
}

std::shared_ptr<JvmColorGroup> JvmStorage2::saveColorGroup(const JvmColorGroup &group) {
    auto storage = findStorage(getEngineIdentifier());
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
    storage->save();

    auto jniColorGroup = make_shared<JvmColorGroup>(
            JvmColorGroup{
                    .id = dGroup->id,
                    .name = dGroup->name,
                    .color = dGroup->color,
            }
    );

    return jniColorGroup;
}

int JvmStorage2::setNotesGroup(const std::vector<int64_t> &notePtrs, const int64_t &groupId) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage) return -1;

    auto flags = TK2_GET_NOTE_PTR_ONLY;
    for (const auto &id: notePtrs) {
        auto note = storage->note(id, flags);
        if (!note)continue;
        note->colorGroupId = groupId;
        storage->setNote(*note, flags);
    }

    return storage->save();
}

int JvmStorage2::removeColorGroup(const int64_t &colorGroupId) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage) return -1;

    storage->removeColorGroup(colorGroupId);
    return storage->save();
}

std::vector<JvmDecryptedNote> JvmStorage2::notes(const int &loadInfo) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage) return {};
    auto notes = std::vector<JvmDecryptedNote>();
    auto flags = loadInfo ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storage->notes(flags)) {
        auto history = std::vector<JvmDecryptedPassw>();
        for (const auto &dhist: dnote.history) {
            history.push_back(
                    {
                            .passwPtr= dhist.id,
                            .passw= dhist.passw,
                            .chTime =  (int64_t) dhist.genTime
                    });
        }

        notes.push_back(
                {
                        .ptnote = dnote.id,
                        .site =  dnote.site,
                        .login =  dnote.login,
                        .desc =  dnote.description,
                        .chTimeSec = (int64_t) dnote.genTime,
                        .colorGroupId = dnote.colorGroupId,
                        .hist = history,
                });
    }
    return notes;
}

JvmDecryptedNote JvmStorage2::note(const int64_t &notePtr) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};
    auto dnote = storage->note(notePtr,
                               TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD |
                               TK2_GET_NOTE_HISTORY_FULL);
    if (!dnote) return {};
    auto history = std::vector<JvmDecryptedPassw>();
    for (const auto &dhist: dnote->history) {
        history.push_back(
                {
                        .passwPtr= dhist.id,
                        .passw= dhist.passw,
                        .chTime =  (int64_t) dhist.genTime
                });
    }
    auto result = JvmDecryptedNote{
            .ptnote = notePtr,
            .site = dnote->site,
            .login = dnote->login,
            .passw = dnote->passw,
            .desc =  dnote->description,
            .chTimeSec = (int64_t) dnote->genTime,
            .colorGroupId = dnote->colorGroupId,
            .hist = history,
    };
    return result;

}

int JvmStorage2::saveNote(const JvmDecryptedNote &decryptedNote, const int &setAll) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage) return -1;

    auto flags = 0;
    if (setAll) {
        flags |= TK2_SET_NOTE_INFO | TK2_SET_NOTE_PASSW | TK2_SET_NOTE_TRACK_HISTORY;
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
    storage->save();
    return 0;
}

int JvmStorage2::removeNote(const int64_t &notePt) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return -1;

    storage->removeNote(notePt);
    storage->save();
    return 0;
}

std::string JvmStorage2::generateNewPassw(const JvmGenPasswParams &params) {
    auto storage = findStorage(getEngineIdentifier());
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

    auto result = storage->genPassword(schemeId, len);
    storage->save();
    return result;
}

std::string JvmStorage2::lastGeneratedPassw() {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return "";
    auto hist = storage->genPasswHistoryList();
    if (!hist.empty()) {
        auto genPassw = storage->genPasswHistory(hist.back().id, TK2_GET_NOTE_HISTORY_FULL);
        if (genPassw) return genPassw->passw;
    }
    auto schemeType = thekey_v2::findSchemeByFlags(SCHEME_NUMBERS);
    auto result = storage->genPassword(schemeType, 4);
    storage->save();
    return result;
}

std::vector<JvmDecryptedPassw> JvmStorage2::genHistory(const int &info) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};

    auto flags = info ? TK2_GET_NOTE_HISTORY_FULL : 0;
    auto hist = storage->genPasswHistoryList(flags);
    auto jvmHist = std::vector<JvmDecryptedPassw>();
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
    auto storage = findStorage(getEngineIdentifier());
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

int JvmStorage2::removeHist(const int64_t &histPt) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};
    storage->removePasswHistory(histPt);
    return storage->save();
}

int JvmStorage2::removeOldHist(const int64_t &oldestTimeSec) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};
    auto history = storage->genPasswHistoryList();
    for (const auto &hist: history) {
        if (hist.genTime < oldestTimeSec) {
            storage->removePasswHistory(hist.id);
        }
    }
    storage->save();
    return 0;
}

std::vector<JvmDecryptedOtpNote> JvmStorage2::otpNotes(const int &info) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};
    auto otpNotes = std::vector<JvmDecryptedOtpNote>();
    auto flags = info ? TK2_GET_NOTE_INFO : TK2_GET_NOTE_PTR_ONLY;
    for (const auto &dnote: storage->otpNotes(flags)) {
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

JvmDecryptedOtpNote JvmStorage2::otpNote(
        const int64_t &notePtr,
        const int &increment
) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};
    auto flags = TK2_GET_NOTE_INFO | TK2_GET_NOTE_PASSWORD;
    if (increment) {
        flags |= TK2_GET_NOTE_INCREMENT_HOTP;
    }
    auto dnotePtr = storage->otpNote(notePtr, flags);
    if (!dnotePtr)return {};
    auto dnote = *dnotePtr;
    return JvmDecryptedOtpNote{
            .ptnote = dnote.id,
            .issuer =  dnote.issuer,
            .name =  dnote.name,
            .secret = dnote.secret,
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

int JvmStorage2::saveOtpNote(const JvmDecryptedOtpNote &jOtp, const int &setAll) {
    auto storage = findStorage(getEngineIdentifier());
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
            .colorGroupId = jOtp.colorGroupId,
            .otpPassw = jOtp.otpPassw,
    };

    auto flags = TK2_SET_NOTE_INFO;
    if (setAll) {
        flags |= TK2_SET_NOTE_PASSW;
    }
    if (dnote.id == 0) {
        storage->createOtpNote(dnote, flags);
    } else {
        storage->setOtpNote(dnote, flags);
    }

    return storage->save();
}

int JvmStorage2::removeOtpNote(const int64_t &notePt) {
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};

    storage->removeOtpNote(notePt);
    return storage->save();
}

shared_ptr<JvmDecryptedOtpNote> JvmStorage2::otpNoteFromUrl(const std::string &url) {
    auto otpList = key_otp::parseOtpUri(url);
    if (otpList.empty())return {};
    auto otp = otpList.front();
    return make_shared<JvmDecryptedOtpNote>(
            JvmDecryptedOtpNote{
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
    auto storage = findStorage(getEngineIdentifier());
    if (!storage)return {};

    auto flags = TK2_GET_NOTE_PTR_ONLY;
    for (const auto &id: notePtrs) {
        auto note = storage->otpNote(id, flags);
        if (!note)continue;
        note->colorGroupId = groupId;
        storage->setOtpNote(*note, flags);
    }

    return storage->save();
}

std::shared_ptr<JvmTwinsCollection> JvmStorage2::findTwins(const std::string &passw) {

    const auto &info = storageFullInfo(getStoragePath());
    if (!info) return {};
    const auto &twins = thekey_v2::twins(passw, info->saltMini);
    auto jTwins = make_shared<JvmTwinsCollection>(JvmTwinsCollection{});
    jTwins->otpTwins.insert(jTwins->otpTwins.end(),
                            twins.passwForOtpTwins.begin(),
                            twins.passwForOtpTwins.end());
    jTwins->loginTwins.insert(jTwins->loginTwins.end(),
                              twins.passwForLoginTwins.begin(),
                              twins.passwForLoginTwins.end());
    jTwins->histTwins.insert(jTwins->histTwins.end(),
                             twins.passwForHistPasswTwins.begin(),
                             twins.passwForHistPasswTwins.end());
    jTwins->descTwins.insert(jTwins->descTwins.end(),
                             twins.passwForDescriptionTwins.begin(),
                             twins.passwForDescriptionTwins.end());

    return jTwins;

}