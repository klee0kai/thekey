//
// Created by panda on 24.02.24.
//


#include "termk2.h"
#include "termk2.h"
#include "../otp/termotp.h"
#include "key2.h"
#include <algorithm>
#include <iterator>
#include <list>

using namespace std;
using namespace thekey;
using namespace thekey_v2;


void thekey_v2::printGroup(const thekey_v2::DecryptedColorGroup &group) {
    cout << "group : " << group.id << " " << to_string(group.color) << " - " << group.name << endl;
}

void thekey_v2::printNote(const thekey_v2::DecryptedNote &note,
                          const std::vector<thekey_v2::DecryptedColorGroup> &groups) {
    cout << "site: '" << note.site << "'" << endl;
    cout << "login: '" << note.login << "'" << endl;
    if (!note.passw.empty()) cout << "passw: '" << note.passw << "'" << endl;
    cout << "desc: '" << note.description << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.genTime);
    cout << "gen time : " << asctime(changeTm) << endl;
    cout << "hist len : " << note.history.size() << endl;

    auto colorGroupIt = std::find_if(groups.begin(), groups.end(), [&](const DecryptedColorGroup &it) {
        return it.id == note.colorGroupId;
    });
    if (colorGroupIt != groups.end()) {
        printGroup(*colorGroupIt);
    }
}


void thekey_v2::printNote(const thekey_v2::DecryptedOtpNote &note,
                          const std::vector<thekey_v2::DecryptedColorGroup> &groups) {


    cout << "issuer: '" << note.issuer << "'" << endl;
    cout << "name: '" << note.name << "'" << endl;
    if (!note.otpPassw.empty()) cout << "code: '" << note.otpPassw << "'" << endl;
    std::tm *changeTm = std::gmtime((time_t *) &note.createTime);
    cout << "create time : " << asctime(changeTm) << endl;

    auto colorGroupIt = std::find_if(groups.begin(), groups.end(), [&](const DecryptedColorGroup &it) {
        return it.id == note.colorGroupId;
    });
    if (colorGroupIt != groups.end()) {
        printGroup(*colorGroupIt);
    }
}