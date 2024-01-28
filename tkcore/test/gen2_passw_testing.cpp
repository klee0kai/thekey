//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt_text/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include <regex>

using namespace std;
using namespace thekey_v2;
using namespace thekey_salt;

const std::regex en_regex("[a-zA-Z0-9]+");

TEST(GenPassw2, Gen) {
    for (int i = 0; i < 10; ++i) {
        // Given
        auto type = find_scheme_type_by_flags(SCHEME_NUMBERS | SCHEME_ENGLISH);

        // when
        auto passw = from(gen_password(type, 8));

        // then
        cout << "passw: '" << passw << "' " << endl;
        ASSERT_TRUE(regex_match(passw, en_regex));
        cout << "--------------------------------" << endl;
    }
}

TEST(GenPassw2, PasswMasked) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS | SCHEME_ENGLISH);

    // when
    auto passw = "o13hEeOP";
    auto passwMasked = from(password_masked(type, from(passw), 0.5f));

    // then
    cout << "passw: '" << passw << "' " << endl;
    cout << "passwMasked: '" << passwMasked << "' " << endl;
    ASSERT_TRUE(passwMasked != passw);
    ASSERT_EQ("owyMEJjk", passwMasked);
    cout << "--------------------------------" << endl;
}


TEST(GenPassw2, PasswTwins) {
    for (int i = 0; i < 10; ++i) {
        // Given
        const auto passwPower = 0.6f;
        const auto type = find_scheme_type_by_flags(SCHEME_NUMBERS | SCHEME_ENGLISH);

        // when

        auto passw = from(gen_password(type, 8));
        auto passwMasked = from(password_masked(type, from(passw), passwPower));
        auto passwTwin = from(password_masked_twin(type, from(passw), passwPower));
        auto passwTwinMasked = from(password_masked(type, from(passwMasked), passwPower));


        // then
        cout << "passw: '" << passw << "' " << endl;
        cout << "passwMasked: '" << passwMasked << "' " << endl;
        cout << "passwTwin: '" << passwTwin << "' " << endl;
        cout << "passwTwinMasked: '" << passwTwinMasked << "' " << endl;
        ASSERT_EQ(passwMasked, passwTwinMasked);
        ASSERT_TRUE(regex_match(passw, en_regex));
        ASSERT_TRUE(regex_match(passwMasked, en_regex));
        ASSERT_TRUE(regex_match(passwTwin, en_regex));
        ASSERT_TRUE(regex_match(passwTwinMasked, en_regex));
        cout << "--------------------------------" << endl;
    }
}