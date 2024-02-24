//
// Created by panda on 24.02.24.
//

#include "termk2.h"
#include "termk2.h"
#include "../termotp.h"
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

    auto notes = selectSimpleNotes ? storageV2->notes() : vector<long long>{};
    auto otpNotes = selectOtpNotes ? storageV2->otpNotes(TK2_GET_NOTE_INFO) : vector<DecryptedOtpNote>{};
    for (const auto &item: notes) {
        auto note = storageV2->note(item, 0);
        cout << ++index << ") '" << note->site << "' / '" << note->login << "' " << endl;
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
        auto notePtr = notes[noteIndex];
        return {
                .type = Simple,
                .notePtr = notePtr
        };
    } else {
        noteIndex -= notes.size();
        auto notePtr = otpNotes[noteIndex].notePtr;
        return {
                .type = Otp,
                .notePtr = notePtr
        };
    }
}