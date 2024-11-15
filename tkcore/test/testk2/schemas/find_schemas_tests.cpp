//
// Created by panda on 21.01.24.
//

#include <gtest/gtest.h>
#include "salt/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include "helpers.h"

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

TEST(FindSchemasTests, SimpleDecoded) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);
    ASSERT_TRUE(scheme);

    //then
    ASSERT_EQ(U'2', scheme->decoded(2));
}

TEST(FindSchemasTests, SimpleEncode) {
    // Given
    auto type = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(type);

    //then
    ASSERT_EQ(2, scheme->encoded(U'2'));
}


TEST(FindSchemasTests, EncodeOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(4, scheme->encoded(U'2', 2));
}

TEST(FindSchemasTests, EncodeBigOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(4, scheme->encoded(U'2', 22));
}

TEST(FindSchemasTests, EncodeMinusOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(1, scheme->encoded(U'2', -1));
}

TEST(FindSchemasTests, EncodeMinusBigOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(1, scheme->encoded(U'2', -21));
}


TEST(FindSchemasTests, DecodeOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(U'4', scheme->decoded(2, 2));
}


TEST(FindSchemasTests, DecodeBigOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(U'4', scheme->decoded(2, 22));
}


TEST(FindSchemasTests, DecodeMinusOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(U'1', scheme->decoded(2, -1));
}


TEST(FindSchemasTests, DecodeMinusBigOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);

    //then
    ASSERT_EQ(U'1', scheme->decoded(2, -21));
}


TEST(FindSchemasTests, EncodingDecoded) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);
    ASSERT_TRUE(scheme);

    //when
    auto c = scheme->encoded(u'1');
    c = scheme->decoded(c);

    //then
    ASSERT_EQ(U'1', c);
}


TEST(FindSchemasTests, ShortOffset) {
    // Given
    auto id = findSchemeByFlags(SCHEME_NUMBERS);
    auto scheme = schema(id);
    ASSERT_TRUE(scheme);
    wide_char original = u'6';
    int offset = int(key_salt::rand(100));

    //when
    auto c_offseted = scheme->encoded(original, offset);
    c_offseted = scheme->decoded(c_offseted);

    auto c_back_offseted = scheme->encoded(c_offseted);
    c_back_offseted = scheme->decoded(c_back_offseted, -offset);

    //then
    ASSERT_EQ(U'6', c_back_offseted);
}

