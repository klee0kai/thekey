//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include "helpers.h"

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


//TEST(EncodingSchemasTests, SchemeSymbols) {
//    auto scheme = schema(0xff02);
//    print_scheme(scheme);
//}

TEST(EncodingSchemasTests, EncodeScheme) {
    // when  scheme a-z + .
    auto scheme = schema(3);

    // then
    ASSERT_EQ(0, scheme->encoded(U'a'));
    ASSERT_EQ(1, scheme->encoded(U'b'));
    ASSERT_EQ(25, scheme->encoded(U'z'));
    ASSERT_EQ(26, scheme->encoded(U'.'));
}


TEST(EncodingSchemasTests, DencodeScheme) {
    // when  scheme a-z + .
    auto scheme = schema(3);

    // then
    ASSERT_EQ(U'a', scheme->decoded(0));
    ASSERT_EQ(U'b', scheme->decoded(1));
    ASSERT_EQ(U'z', scheme->decoded(25));
    ASSERT_EQ(U'.', scheme->decoded(26));
    ASSERT_EQ(U'a', scheme->decoded(27));
}


TEST(EncodingSchemasTests, WideSchemeEncode) {
    auto scheme = schema(0xff02);
//    cout << "scheme  symbols '" << from(scheme->all_symbols()) << "'" << endl;

    // then
    ASSERT_EQ(0, scheme->encoded(U' '));
    ASSERT_EQ(0x410 - 0x20, scheme->encoded(U'А'));
    ASSERT_EQ(0x44F - 0x20, scheme->encoded(U'я'));
    ASSERT_EQ(U' ', scheme->decoded(0));
    ASSERT_EQ(U'А', scheme->decoded(0x410 - 0x20));
    ASSERT_EQ(U'я', scheme->decoded(0x44F - 0x20));

}


TEST(EncodingSchemasTests, EncodeCropText) {
    // GIVEN
    const size_t len = 100L;
    wide_char wideCharArray[len] = {};
    wide_char wideStringEncoded[len] = {};
    wide_char wideStringDecoded[len] = {};
    wide_string wideString = from("dddПППfff");
    memcpy((char *) wideCharArray, (char *) wideString.c_str(), wideString.size() * sizeof(wide_char));
    auto scheme = schema(3);


    // WHEN
    for (int i = 0; i < len; ++i) {
        wideStringEncoded[i] = scheme->encoded(wideCharArray[i]);
        wideStringDecoded[i] = scheme->decoded(wideStringEncoded[i]);
    }

    // THEN
    ASSERT_EQ(U'd', wideStringDecoded[0]);
    ASSERT_EQ(U'a', wideStringDecoded[3]); // cropped
    ASSERT_EQ(U'f', wideStringDecoded[7]);
}


TEST(EncodingSchemasTests, EndNotHaveMark) {
    // GIVEN
    SaltedText saltedText{};

    // WHEN
    saltedText.salted("some.site.com");
    int endIndex = -1;
    for (int i = 0; i < SALTED_TEXT_LEN; ++i) {
        if (saltedText.payload.raw[i] == 0) {
            endIndex = i;
            break;
        }
    }

    // THEN
    ASSERT_EQ(-1, endIndex);
}


TEST(EncodingSchemasTests, SaltDesaltEquals) {
    // GIVEN
    SaltedText saltedText{};

    // WHEN
    saltedText.salted("some.site.com");
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ("some.site.com", str);
}

TEST(EncodingSchemasTests, SaltDesaltTextEquals) {
    // GIVEN
    const string &text = "text";
    SaltedText saltedText{};

    // WHEN
    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}

TEST(EncodingSchemasTests, SaltDesaltEmpty) {
    // GIVEN
    const string &text = "";
    SaltedText saltedText{};

    // WHEN
    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}


TEST(EncodingSchemasTests, SaltDesaltRu) {
    // GIVEN
    const string &text = "приветы ;№ц";
    SaltedText saltedText{};
    // WHEN

    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}


TEST(EncodingSchemasTests, SaltDesaltInListEquals) {
    static string test_texts[] = {
            "text",
            "some.site.com",
            "some.sSumeite.com",
            "some.s12ite.com",
            "12@!1q#",
            "12@ ds",
            "выа ;",
            "приветы ;№ц",
            "приветы ёЁ",
    };


    for (const auto &text: test_texts) {
        // GIVEN
        SaltedText saltedText{};

        // WHEN
        saltedText.salted(text);
        auto str = saltedText.desalted();

        // THEN
        ASSERT_EQ(text, str);
    }
}

