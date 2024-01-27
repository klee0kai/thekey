//
// Created by panda on 21.01.24.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt_text/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include "helpers.h"
#include <regex>

using namespace std;
using namespace thekey_v2;
using namespace thekey_salt;

TEST(Salt2Schemas, SimpleDecoded) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);
    ASSERT_TRUE(scheme);
    print_scheme(scheme);

    //then
    ASSERT_EQ(U'2', scheme->decoded(2));
}

TEST(Salt2Schemas, SimpleEncode) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(2, scheme->encoded(U'2'));
}


TEST(Salt2Schemas, EncodeOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(4, scheme->encoded(U'2', 2));
}

TEST(Salt2Schemas, EncodeBigOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(4, scheme->encoded(U'2', 22));
}

TEST(Salt2Schemas, EncodeMinusOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(1, scheme->encoded(U'2', -1));
}

TEST(Salt2Schemas, EncodeMinusBigOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(1, scheme->encoded(U'2', -21));
}


TEST(Salt2Schemas, DecodeOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(U'4', scheme->decoded(2, 2));
}


TEST(Salt2Schemas, DecodeBigOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(U'4', scheme->decoded(2, 22));
}


TEST(Salt2Schemas, DecodeMinusOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(U'1', scheme->decoded(2, -1));
}


TEST(Salt2Schemas, DecodeMinusBigOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);

    //then
    ASSERT_EQ(U'1', scheme->decoded(2, -21));
}


TEST(Salt2Schemas, EncodingDecoded) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);
    ASSERT_TRUE(scheme);
    print_scheme(scheme);

    //when
    auto c = scheme->encoded(u'1');
    c = scheme->decoded(c);

    //then
    ASSERT_EQ(U'1', c);
}


TEST(Salt2Schemas, ShortOffset) {
    // Given
    auto type = find_scheme_type_by_flags(SCHEME_NUMBERS);
    auto scheme = find_scheme(type);
    ASSERT_TRUE(scheme);
    wide_char original = u'6';
    int offset = int(thekey_salt::rand(100));

    //when
    auto c_offseted = scheme->encoded(original, offset);
    c_offseted = scheme->decoded(c_offseted);

    auto c_back_offseted = scheme->encoded(c_offseted);
    c_back_offseted = scheme->decoded(c_back_offseted, -offset);

    //then
    ASSERT_EQ(U'6', c_back_offseted);
}

