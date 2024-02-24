//
// Created by panda on 21.01.24.
//

#include "termk2.h"
#include "termotp.h"
#include "key2.h"
#include "utils/term_utils.h"
#include "utils/Interactive.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey_v2;
using namespace thekey;
using namespace term;

#define NOTE_SELECT_SIMPLE 0x1
#define NOTE_SELECT_OTP 0x2

enum SelectedNoteType {
    NoSelect,
    Simple,
    Otp,
};

struct SelectedNote {
    SelectedNoteType type;
    long long notePtr;
};

static SelectedNote ask_select_note(int flags = NOTE_SELECT_SIMPLE | NOTE_SELECT_OTP);

static void printNote(const thekey_v2::DecryptedNote &note);

static void printNote(const thekey_v2::DecryptedOtpNote &note);

shared_ptr<thekey_v2::KeyStorageV2> storageV2 = {};

void thekey_term_v2::login(const std::string &filePath) {
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
                   + "Storage version is " + to_string(storageInfo->storageVersion);

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
        for (const auto &item: storageV2->notes()) {
            auto note = storageV2->note(item);
            cout << "-------------------------------------------" << endl;
            printNote(*note);
        }
        auto otpNotes = storageV2->otpNotes(TK2_GET_NOTE_INFO);
        if (!otpNotes.empty()) {
            cout << " ----------- otp -----------" << endl;
            for (const auto &note: otpNotes) {
                cout << "-------------------------------------------" << endl;
                printNote(note);
            }
        }
        cout << "-------------------------------------------" << endl;
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
                auto hist = storageV2->passwordHistory(item);
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
        auto notePtr = storageV2->createNote();
        int error = storageV2->setNote(notePtr, {
                .site =site,
                .login = login,
                .passw = passw,
                .description = desc,
        }, TK2_SET_NOTE_TRACK_HISTORY);
        if (error) {
            cerr << "error to save note " << errorToString(error) << endl;
            return;
        }
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
        auto schemeType = thekey_v2::find_scheme_type_by_flags(schemeFlags);

        auto len = term::ask_int_from_term("length of password: ");
        auto passw = storageV2->genPassword(schemeType, len);
        cout << "generated password '" << passw << "' " << endl;
    });

    it.cmd({"hist"}, "print gen password history", [&]() {
        if (!storageV2)return;
        for (const auto &item: storageV2->passwordsHistory()) {
            auto hist = storageV2->passwordHistory(item);
            cout << "-------------------------------------------" << endl;
            cout << "passw: " << hist->passw << endl;
            std::tm *changeTm = std::gmtime((time_t *) &hist->genTime);
            cout << "change time : " << asctime(changeTm) << endl;
        }
        cout << "-------------------------------------------" << endl;
    });

    it.loop();
    storageV2.reset();

    cout << "Storage '" << storageInfo->name << "' closed." << endl;
}


void thekey_term_v2::interactiveEditNote(const long long &notePtr) {
    auto note = storageV2->note(notePtr, TK2_GET_NOTE_FULL);

    auto editIt = Interactive();
    cout << "Note " << to_string(notePtr) << " edit mode";
    editIt.helpTitle = "Note " + to_string(notePtr) + " edit mode";

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

    int error = storageV2->setNote(notePtr, *note, TK2_SET_NOTE_TRACK_HISTORY);
    if (error) {
        cerr << "error to save note " << errorToString(error) << endl;
        return;
    } else {
        cout << "note saved " << notePtr << endl;
    }
}

void thekey_term_v2::interactiveEditOtpNote(const long long &notePtr) {
    auto note = storageV2->otpNote(notePtr, TK2_GET_NOTE_INFO);

    auto editIt = Interactive();
    cout << "OTP note " << to_string(notePtr) << " edit mode";
    editIt.helpTitle = "OTP note " + to_string(notePtr) + " edit mode";

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

    editIt.loop();

    int error = storageV2->setOtpNote(*note, 0);
    if (error) {
        cerr << "error to save otp note " << errorToString(error) << endl;
        return;
    } else {
        cout << "otp note saved " << notePtr << endl;
    }
}

static SelectedNote ask_select_note(int flags) {
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


static void printNote(const thekey_v2::DecryptedNote &note) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "gen time : " << asctime(changeTm) << endl;
    cout << "color : " << to_string(note.color) << endl;
    cout << "hist len : " << note.history.size() << endl;
}


static void printNote(const thekey_v2::DecryptedOtpNote &note) {
    cout << "issuer: '" << note.issuer << "'" << endl;
    cout << "name: '" << note.name << "'" << endl;
    if (!note.otpPassw.empty()) cout << "code: '" << note.otpPassw << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.createTime);
    cout << "create time : " << asctime(changeTm) << endl;
    cout << "color : " << to_string(note.color) << endl;
}