//
// Created by panda on 21.01.24.
//

#include "termk2.h"
#include "../otp/termotp.h"
#include "key2.h"
#include "../utils/term_utils.h"
#include "../utils/Interactive.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey;
using namespace thekey_v2;
using namespace term;

shared_ptr<thekey_v2::KeyStorageV2> thekey_v2::storageV2 = {};

void thekey_v2::login(const std::string &filePath) {
    keyError = 0;
    auto storageInfo = thekey_v2::storageFullInfo(filePath);
    if (!storageInfo) {
        cerr << "can't open file " << filePath << " " << errorToString(keyError) << endl;
        return;
    }
    auto passw = ask_password_from_term("Input password: ");
    storageV2 = storage(filePath, passw);
    if (!storageV2) {
        cerr << "error login to " << filePath << " " << errorToString(keyError) << endl;
        return;
    }

    cout << "Reading storage..." << endl;
    storageV2->readAll();
    cout << string("Welcome to storage '") << storageInfo->name << "'" << endl;

    auto it = Interactive();
    it.helpTitle = "Storage '" + storageInfo->name + "' interactive mode. "
                   + "Storage version is " + std::to_string(storageInfo->storageVersion);

    it.cmd({"info"}, "print storage info", [&]() {
        if (!storageV2)return;
        auto info = storageV2->info();
        cout << "storage: " << info.path << endl;
        cout << "storageVersion: " << info.storageVersion << endl;
        cout << "name: " << info.name << endl;
        cout << "desc: " << info.description << endl;

        cout << endl;
    });

    it.cmd({"l", "list"}, "list storage notes", [&]() {
        if (!storageV2)return;
        for (const auto &note: storageV2->notes(TK2_GET_NOTE_INFO)) {
            cout << "-------------------------------------------" << endl;
            printNote(note);
        }
        auto otpNotes = storageV2->otpNotes(TK2_GET_NOTE_INFO);
        if (!otpNotes.empty()) {
            cout << "------------------- otp -------------------" << endl;
            for (const auto &note: otpNotes) {
                printNote(note);
                cout << "-------------------------------------------" << endl;
            }
        } else {
            cout << "-------------------------------------------" << endl;
        }
    });

    it.cmd({"p", "passw"}, "print note password or OTP codes", [&]() {
        if (!storageV2)return;
        const auto &select = ask_select_note();
        switch (select.type) {
            case Simple: {
                auto noteFull = storageV2->note(select.notePtr, TK2_GET_NOTE_FULL);
                printNote(*noteFull);
                break;
            }
            case Otp: {
                auto otpInfo = storageV2->exportOtpNote(select.notePtr);
                if (!otpInfo.interval) {
                    cerr << "error: interval is 0" << endl;
                }
                thekey_otp::interactiveOtpCode(otpInfo);
            }
                break;
            case NoSelect:
                break;
        }
    });

    it.cmd({"noteHist"}, "note passwords history", [&]() {
        if (!storageV2)return;
        const auto &select = ask_select_note(NOTE_SELECT_SIMPLE);

        if (select.type == Simple) {
            auto note = storageV2->note(select.notePtr, 0);
            for (const auto &item: note->history) {
                auto hist = storageV2->genPasswHistory(item.id);
                cout << "-------------------------------------------" << endl;
                cout << "passw: " << hist->passw << endl;
                std::tm *changeTm = std::gmtime((time_t *) &hist->genTime);
                cout << "change time : " << asctime(changeTm) << endl;
            }
            cout << endl;
        }
    });

    it.cmd({"create"}, "create new note", [&]() {
        if (!storageV2)return;
        auto site = ask_from_term("site : ");
        auto login = ask_from_term("login : ");
        auto passw = ask_password_from_term("password : ");
        auto desc = ask_from_term("description : ");
        auto notePtr = storageV2->createNote(
                {
                        .site =site,
                        .login = login,
                        .passw = passw,
                        .description = desc,
                }
        );
        cout << "note saved " << notePtr << endl;
    });

    it.cmd({"createOtp"}, "create new otp note", [&]() {
        if (!storageV2)return;
        auto uri = ask_from_term("input uri (otpauth or otpauth-migration schemas): ");
        const auto &otpNotes = storageV2->createOtpNotes(uri);
        cout << "added " << otpNotes.size() << " otp notes " << endl;
    });

    it.cmd({"edit"}, "edit note", [&]() {
        if (!storageV2)return;

        const auto &selectOtp = ask_select_note();

        switch (selectOtp.type) {
            case Simple:
                interactiveEditNote(selectOtp.notePtr);
                break;
            case Otp:
                interactiveEditOtpNote(selectOtp.notePtr);
                break;
            case NoSelect:
                break;
        }
    });

    it.cmd({"export"}, "Export OTP note", [&]() {
        if (!storageV2)return;
        const auto &selectOtp = ask_select_note(NOTE_SELECT_OTP);
        if (selectOtp.type == Otp) {
            cout << "otp note uri: " << storageV2->exportOtpNote(selectOtp.notePtr).toUri() << endl;
        }
    });

    it.cmd({"remove"}, "remove note", [&]() {
        if (!storageV2)return;
        const auto &selectNote = ask_select_note();

        if (selectNote.type == Simple) {
            storageV2->removeNote(selectNote.notePtr);
            cout << "note removed" << endl;
        } else if (selectNote.type == Otp) {
            storageV2->removeOtpNote(selectNote.notePtr);
            cout << "otp note removed" << endl;
        }
    });

    it.cmd({"gen"}, "generate new password", [&]() {
        if (!storageV2)return;

        cout << "select password encSelect: " << endl;
        cout << "0) numbers only " << endl;
        cout << "1) english symbols and numbers " << endl;
        cout << "2) english symbols, numbers, spec symbols " << endl;
        cout << "3) english symbols, numbers, spec symbols, space " << endl;
        auto schemeFlags = 0;
        auto encSelect = term::ask_int_from_term();
        switch (encSelect) {
            case 0:
                schemeFlags = SCHEME_NUMBERS;
                break;
            case 1:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS;
                break;
            case 2:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS;
                break;
            case 3:
                schemeFlags = SCHEME_ENGLISH | SCHEME_NUMBERS | SCHEME_SPEC_SYMBOLS | SCHEME_SPACE_SYMBOL;
                break;
        }
        auto schemeType = thekey_v2::findSchemeByFlags(schemeFlags);

        auto len = term::ask_int_from_term("length of password: ");
        auto passw = storageV2->genPassword(schemeType, len);
        cout << "generated password '" << passw << "' " << endl;
    });

    it.cmd({"hist"}, "print gen password history", [&]() {
        if (!storageV2)return;
        for (const auto &item: storageV2->genPasswHistoryList()) {
            auto hist = storageV2->genPasswHistory(item.id);
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << hist->passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &hist->genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << "-------------------------------------------" << endl;
    });

    it.cmd({"changePassw"}, "change storage master password", [&]() {
        if (!storageV2)return;

        auto newPath = term::ask_from_term("write new path to save storage: ");
        auto passw = term::ask_password_from_term("write new storage master passw: ");
        if (!ends_with(newPath, ".ckey")) newPath += ".ckey";

        cout << "changing password for storage..." << flush;
        int error = storageV2->saveNewPassw(newPath, passw, [](const float &) { cout << "." << flush; });
        cout << endl;
        if (error) {
            cerr << "error to change storage password : " << errorToString(error) << endl;
            return;
        }
        cout << "storage password has been changed to new file : " << newPath << endl;
    });

    it.loop();
    storageV2.reset();

    cout << "Storage '" << storageInfo->name << "' closed." << endl;
}
