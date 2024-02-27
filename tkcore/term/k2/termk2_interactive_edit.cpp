//
// Created by panda on 24.02.24.
//


#include "termk2.h"
#include "../termotp.h"
#include "key2.h"
#include "../utils/term_utils.h"
#include "../utils/Interactive.h"

using namespace std;
using namespace thekey_v2;
using namespace thekey;
using namespace term;

void thekey_v2::interactiveEditNote(const long long &notePtr) {
    auto note = storageV2->note(notePtr, TK2_GET_NOTE_FULL);

    auto editIt = Interactive();
    cout << "Note " << std::to_string(notePtr) << " edit mode";
    editIt.helpTitle = "Note " + std::to_string(notePtr) + " edit mode";

    editIt.cmd({"p", "print"}, "print note", [&]() {
        cout << "current note is: " << endl;
        printNote(*note);
    });

    editIt.cmd({"s", "site"}, "edit site", [&]() {
        note->site = term::ask_from_term("site : ");
    });

    editIt.cmd({"l", "login"}, "edit login", [&]() {
        note->login = term::ask_from_term("login : ");
    });

    editIt.cmd({"passw"}, "edit password", [&]() {
        note->passw = term::ask_password_from_term("password : ");
    });

    editIt.cmd({"d", "desc"}, "edit description", [&]() {
        note->description = term::ask_from_term("description : ");
    });

    editIt.cmd({"color"}, "edit color", [&]() {
        for (int i = 0; i < KEY_COLOR_LEN; ++i) {
            cout << i << ") " << to_string(KeyColor(i)) << endl;
        }
        note->color = KeyColor(term::ask_int_from_term("color : "));
    });

    editIt.loop();

    int error = storageV2->setNote(*note, TK2_SET_NOTE_TRACK_HISTORY);
    if (error) {
        cerr << "error to save note " << errorToString(error) << endl;
        return;
    } else {
        cout << "note saved " << notePtr << endl;
    }
}

void thekey_v2::interactiveEditOtpNote(const long long &notePtr) {
    auto note = storageV2->otpNote(notePtr, TK2_GET_NOTE_FULL);

    auto editIt = Interactive();
    cout << "OTP note " << std::to_string(notePtr) << " edit mode";
    editIt.helpTitle = "OTP note " + std::to_string(notePtr) + " edit mode";

    editIt.cmd({"p", "print"}, "print note", [&]() {
        cout << "current note is: " << endl;
        printNote(*note);
    });

    editIt.cmd({"name"}, "edit name", [&]() {
        note->name = term::ask_from_term("name : ");
    });

    editIt.cmd({"issuer"}, "edit issuer", [&]() {
        note->issuer = term::ask_from_term("issuer : ");
    });

    editIt.cmd({"color"}, "edit color", [&]() {
        for (int i = 0; i < KEY_COLOR_LEN; ++i) {
            cout << i << ") " << to_string(KeyColor(i)) << endl;
        }
        note->color = KeyColor(term::ask_int_from_term("color : "));
    });

    if (note->method == key_otp::YAOTP) {
        editIt.cmd({"resetPin"}, "Reset Pin", [&]() {
            note->pin = "";
        });

        editIt.cmd({"pin"}, "Edit pin", [&]() {
            note->pin = term::ask_from_term("pin : ");
        });
    }

    editIt.loop();

    int error = storageV2->setOtpNote(*note, 0);
    if (error) cerr << "error to save otp note " << errorToString(error) << endl;
    else cout << "otp note saved " << notePtr << endl;

}
