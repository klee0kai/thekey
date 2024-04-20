//
// Created by panda on 24.02.24.
//

#include "termk2.h"
#include "termk2.h"
#include "../otp/termotp.h"
#include "key2.h"
#include "../utils/term_utils.h"
#include "../utils/Interactive.h"

using namespace std;
using namespace thekey;
using namespace thekey_v2;
using namespace term;

SelectedNote thekey_v2::ask_select_note(int flags) {
    auto index = 0;
    auto selectColorGroup = (flags & NOTE_SELECT_COLOR_GROUP) != 0;
    auto selectSimpleNotes = (flags & NOTE_SELECT_SIMPLE) != 0;
    auto selectOtpNotes = (flags & NOTE_SELECT_OTP) != 0;

    auto groups = selectColorGroup ? storageV2->colorGroups(TK2_GET_NOTE_INFO) : vector<DecryptedColorGroup>{};
    auto notes = selectSimpleNotes ? storageV2->notes(TK2_GET_NOTE_INFO) : vector<DecryptedNote>{};
    auto otpNotes = selectOtpNotes ? storageV2->otpNotes(TK2_GET_NOTE_INFO) : vector<DecryptedOtpNote>{};

    for (const auto &group: groups) {
        cout << ++index << ") " << to_string(group.color) << " - " << group.name << endl;
    }
    for (const auto &note: notes) {
        cout << ++index << ") '" << note.site << "' / '" << note.login << "' " << endl;
    }
    for (const auto &note: otpNotes) {
        cout << ++index << ") '" << note.issuer << "' / '" << note.name << "' " << endl;
    }
    auto noteIndex = term::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > groups.size() + notes.size() + otpNotes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return {};
    }
    noteIndex--;
    if (noteIndex < groups.size()) {
        return {
                .type = Group,
                .notePtr = notes[noteIndex].id
        };
    } else if (noteIndex < notes.size()) {
        return {
                .type = Simple,
                .notePtr =  notes[noteIndex].id
        };
    } else {
        noteIndex -= notes.size();
        return {
                .type = Otp,
                .notePtr = otpNotes[noteIndex].id
        };
    }
}