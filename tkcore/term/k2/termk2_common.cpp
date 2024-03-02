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


void thekey_v2::printNote(const thekey_v2::DecryptedNote &note) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "gen time : " << asctime(changeTm) << endl;
    cout << "color : " << to_string(note.color) << endl;
    cout << "hist len : " << note.history.size() << endl;
}


void thekey_v2::printNote(const thekey_v2::DecryptedOtpNote &note) {
    cout << "issuer: '" << note.issuer << "'" << endl;
    cout << "name: '" << note.name << "'" << endl;
    if (!note.otpPassw.empty()) cout << "code: '" << note.otpPassw << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.createTime);
    cout << "create time : " << asctime(changeTm) << endl;
    cout << "color : " << to_string(note.color) << endl;
}