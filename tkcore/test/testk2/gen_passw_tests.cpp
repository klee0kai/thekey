//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include <regex>

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

const std::regex en_regex("[a-zA-Z0-9]+");

TEST(GenPasswords, Gen) {
    for (int i = 0; i < 10; ++i) {
        // Given
        auto type = find_scheme_type_by_flags(SCHEME_NUMBERS | SCHEME_ENGLISH);

        // when
        auto passw = from(gen_password(type, 8));

        // then
        ASSERT_TRUE(regex_match(passw, en_regex)) << "passw: '" << passw << "' " << endl;
    }
}

TEST(GenPasswords, PasswMasked) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS | SCHEME_ENGLISH);

    // when
    auto passw = "o13hEeOP";
    auto passwMasked = from(password_masked(type, from(passw), 0.5f));

    // then
    ASSERT_TRUE(passwMasked != passw)
                                << "passw: '" << passw << "' " << endl
                                << "passwMasked: '" << passwMasked << "' " << endl;
    ASSERT_EQ("owyMEJjk", passwMasked);
}


TEST(GenPasswords, PasswTwins) {
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
        stringstream errorMessage;
        errorMessage << "passw: '" << passw << "' " << endl;
        errorMessage << "passwMasked: '" << passwMasked << "' " << endl;
        errorMessage << "passwTwin: '" << passwTwin << "' " << endl;
        errorMessage << "passwTwinMasked: '" << passwTwinMasked << "' " << endl;
        ASSERT_EQ(passwMasked, passwTwinMasked) << errorMessage.str();
        ASSERT_TRUE(regex_match(passw, en_regex)) << errorMessage.str();
        ASSERT_TRUE(regex_match(passwMasked, en_regex)) << errorMessage.str();
        ASSERT_TRUE(regex_match(passwTwin, en_regex)) << errorMessage.str();
        ASSERT_TRUE(regex_match(passwTwinMasked, en_regex)) << errorMessage.str();
    }
}