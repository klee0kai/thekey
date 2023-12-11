//
// Created by panda on 11.12.2023.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include "brooklyn.h"
#include "dll_interface/key_finder.h"

using namespace brooklyn;


int EngineCryptStorageEngine::isLogined() {
    return key_manager_ctx::isLogined()
           && getStoragePath() == key_manager_ctx::getLoggedStoragePath();
}

void EngineCryptStorageEngine::login(const std::string &passw) {
    key_manager_ctx::login(
            (const unsigned char *) getStoragePath().c_str(),
            (const unsigned char *) passw.c_str()
    );
}

void EngineCryptStorageEngine::unlogin() {
    key_manager_ctx::unLogin();
}

std::vector<EngineModelDecryptedNote> EngineCryptStorageEngine::notes() {
    long long *btNotes = key_manager_ctx::getNotes();
    int len;
    for (len = 0; btNotes[len]; len++);
    auto notes = std::vector<EngineModelDecryptedNote>(len);
    for (len = 0; btNotes[len]; len++) {
        DecryptedNote *dnote = key_manager_ctx::getNoteItem((long) btNotes[len], 0);
        notes.push_back(
                EngineModelDecryptedNote{
                        .site = (char *) dnote->site,
                        .login = (char *) dnote->login,
                        .desc = (char *) dnote->description,
                        .chTime = (int64_t) dnote->genTime,
                }
        );
    }
    return notes;
}

EngineModelDecryptedPassw EngineCryptStorageEngine::getGenPassw(const int64_t &ptNote) {
    EngineModelDecryptedPassw passw = {};
    return passw;
}


std::string
EngineCryptStorageEngine::generateNewPassw(const int &len, const int &genPasswEncoding) {
    unsigned char *genPassw = key_manager_ctx::genPassw(len, genPasswEncoding);
    std::string passwStr = (char *) genPassw;
    delete[] genPassw;
    return passwStr;
}