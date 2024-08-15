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
        auto type = findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH);

        // when
        auto passw = from(gen_password(type, 8));

        // then
        ASSERT_TRUE(regex_match(passw, en_regex)) << "passw: '" << passw << "' " << endl;
    }
}

TEST(GenPasswords, PasswMasked) {
    // Given
    auto type = findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH);

    // when
    auto passw = "o13hEeOP";
    auto passwMasked = from(password_masked(type, from(passw), 0.5f));

    // then
    ASSERT_TRUE(passwMasked != passw)
                                << "passw: '" << passw << "' " << endl
                                << "passwMasked: '" << passwMasked << "' " << endl;
    ASSERT_EQ("ihljmmon", passwMasked);
}


TEST(GenPasswords, PasswTwins) {
    for (int i = 0; i < 10; ++i) {
        // Given
        const auto passwPower = 0.6f;
        const auto type = findSchemeByFlags(SCHEME_NUMBERS | SCHEME_ENGLISH);

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

TEST(GenPasswords, TwinIsCorrect) {
    auto passw = from(gen_password(findSchemeByFlags(SCHEME_NUMBERS), 8));
    auto type = find_scheme_id(from(passw), 30);
    auto passwWide = from(passw);
    for (float power = 0.1f; power <= 1.1f; power += 0.1f) {
        auto passwMasked = from(password_masked(type, passwWide, power));

        std::set<std::string> twins;
        for (int i = 0; i < 1e4; ++i) {
            auto twin = from(password_masked_twin(type, passwWide, power));
            auto twinMasked = from(password_masked(type, from(twin), power));
            twins.insert(twins.end(), twin);
            ASSERT_EQ(passwMasked, twinMasked);
        }
    }
}

TEST(GenPasswords, PasswordPower) {
    auto passw = from(gen_password(findSchemeByFlags(SCHEME_NUMBERS), 4));
    auto type = find_scheme_id(from(passw), 30);
    auto combinationCount = 1e4;

    auto power1 = 0.3f;
    std::set<std::string> twins1;
    auto passwMasked1 = from(password_masked(type, from(passw), power1));
    for (int i = 0; i < combinationCount; ++i) {
        auto diffPassw = from(gen_password(type, 4));
        auto diffPasswMasked = from(password_masked(type, from(diffPassw), power1));

        if (passwMasked1 == diffPasswMasked)
            twins1.insert(twins1.end(), diffPassw);
    }

    auto power2 = 0.7f;
    std::set<std::string> twins2;
    auto passwMasked2 = from(password_masked(type, from(passw), power2));
    for (int i = 0; i < combinationCount; ++i) {
        auto diffPassw = from(gen_password(type, 4));
        auto diffPasswMasked = from(password_masked(type, from(diffPassw), power2));

        if (passwMasked2 == diffPasswMasked)
            twins2.insert(twins2.end(), diffPassw);
    }

    ASSERT_TRUE(twins1.size() > twins2.size()) << twins1.size() << " / " << twins2.size();
}

TEST(GenPasswords, PasswPowerTwinsCount) {
    auto passw = from(gen_password(findSchemeByFlags(SCHEME_NUMBERS), 8));
    auto type = find_scheme_id(from(passw), 30);
    auto passwWide = from(passw);
    auto combinationCount = 1e4;

    auto power1 = 0.3f;
    std::set<std::string> twins1;
    for (int i = 0; i < combinationCount; ++i) {
        auto twin = from(password_masked_twin(type, passwWide, power1));
        auto twinMasked = from(password_masked(type, from(twin), power1));
        twins1.insert(twins1.end(), twin);
    }

    auto power2 = 0.7f;
    std::set<std::string> twins2;
    for (int i = 0; i < combinationCount; ++i) {
        auto twin = from(password_masked_twin(type, passwWide, power2));
        auto twinMasked = from(password_masked(type, from(twin), power2));
        twins2.insert(twins2.end(), twin);
    }

    ASSERT_TRUE(twins1.size() > twins2.size()) << twins1.size() << " / " << twins2.size();
}
