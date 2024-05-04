//
// Created by panda on 21.01.24.
//

#ifndef THEKEY_TERMK2_H
#define THEKEY_TERMK2_H

#include "../def_header.h"
#include "key2.h"

#define NOTE_SELECT_COLOR_GROUP 0x1
#define NOTE_SELECT_SIMPLE 0x2
#define NOTE_SELECT_OTP 0x4

namespace thekey_v2 {

    enum SelectedNoteType {
        NoSelect,
        Group,
        Simple,
        Otp,
    };

    struct SelectedNote {
        SelectedNoteType type;
        long long notePtr;
    };

    extern std::shared_ptr<thekey_v2::KeyStorageV2> storageV2;


    void login(const std::string &filePath);

    void interactiveEditNote(const long long &notePtr);

    void interactiveEditOtpNote(const long long &notePtr);

    SelectedNote ask_select_note(int flags = NOTE_SELECT_SIMPLE | NOTE_SELECT_OTP);

    void printGroup(const thekey_v2::DecryptedColorGroup &group);

    void printNote(const thekey_v2::DecryptedNote &note,
                   const std::vector<thekey_v2::DecryptedColorGroup> &groups = {});

    void printNote(const thekey_v2::DecryptedOtpNote &note,
                   const std::vector<thekey_v2::DecryptedColorGroup> &groups = {});
}

#endif //THEKEY_TERMK2_H
