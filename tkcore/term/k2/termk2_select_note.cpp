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
    auto selectSimpleNotes = (flags & NOTE_SELECT_SIMPLE) != 0;
    auto selectOtpNotes = (flags & NOTE_SELECT_OTP) != 0;

    auto notes = selectSimpleNotes ? storageV2->notes(TK2_GET_NOTE_INFO) : vector<DecryptedNote>{};
    auto otpNotes = selectOtpNotes ? storageV2->otpNotes(TK2_GET_NOTE_INFO) : vector<DecryptedOtpNote>{};
    for (const auto &note: notes) {
        cout << ++index << ") '" << note.site << "' / '" << note.login << "' " << endl;
    }
    for (const auto &note: otpNotes) {
        cout << ++index << ") '" << note.issuer << "' / '" << note.name << "' " << endl;
    }
    auto noteIndex = term::ask_int_from_term("Select note. Write index: ");
    if (noteIndex < 1 || noteIndex > notes.size() + otpNotes.size()) {
        cerr << "incorrect index " << noteIndex << endl;
        return {};
    }
    noteIndex--;
    if (noteIndex < notes.size()) {
        return {
                .type = Simple,
                .notePtr =  notes[noteIndex].notePtr
        };
    } else {
        noteIndex -= notes.size();
        return {
                .type = Otp,
                .notePtr = otpNotes[noteIndex].notePtr
        };
    }
}