//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt_text/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include "helpers.h"

using namespace std;
using namespace thekey_v2;
using namespace thekey_salt;


TEST(Salt2, SchemeSymbols) {
    auto scheme = find_scheme(0xff02);
    print_scheme(scheme);
}

TEST(Salt2, EncodeScheme) {
    // when  scheme a-z + .
    auto scheme = find_scheme(2);

    // then
    ASSERT_EQ(0, scheme->encoded(U'a'));
    ASSERT_EQ(1, scheme->encoded(U'b'));
    ASSERT_EQ(25, scheme->encoded(U'z'));
    ASSERT_EQ(26, scheme->encoded(U'.'));
}


TEST(Salt2, DencodeScheme) {
    // when  scheme a-z + .
    auto scheme = find_scheme(2);

    // then
    ASSERT_EQ(U'a', scheme->decoded(0));
    ASSERT_EQ(U'b', scheme->decoded(1));
    ASSERT_EQ(U'z', scheme->decoded(25));
    ASSERT_EQ(U'.', scheme->decoded(26));
    ASSERT_EQ(U'a', scheme->decoded(27));
}


TEST(Salt2, WideSchemeEncode) {
    // when  scheme a-z + .
    auto scheme = find_scheme(0xff02);
//    cout << "scheme  symbols '" << from(scheme->all_symbols()) << "'" << endl;

    // then
    ASSERT_EQ(0, scheme->encoded(U' '));
    ASSERT_EQ(0x410 - 0x20, scheme->encoded(U'А'));
    ASSERT_EQ(0x44F - 0x20, scheme->encoded(U'я'));
    ASSERT_EQ(U' ', scheme->decoded(0));
    ASSERT_EQ(U'А', scheme->decoded(0x410 - 0x20));
    ASSERT_EQ(U'я', scheme->decoded(0x44F - 0x20));

}


TEST(Salt2, EncodeCropText) {
    // GIVEN
    const size_t len = 100L;
    wide_char wideCharArray[len] = {};
    wide_char wideStringEncoded[len] = {};
    wide_char wideStringDecoded[len] = {};
    wide_string wideString = from("dddПППfff");
    memcpy((char *) wideCharArray, (char *) wideString.c_str(), wideString.size() * sizeof(wide_char));
    auto scheme = find_scheme(1);


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


TEST(Salt2, EndNotHaveMark) {
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


TEST(Salt2, SaltDesaltEquals) {
    // GIVEN
    SaltedText saltedText{};

    // WHEN
    saltedText.salted("some.site.com");
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ("some.site.com", str);
}

TEST(Salt2, SaltDesaltTextEquals) {
    // GIVEN
    const string &text = "text";
    SaltedText saltedText{};

    // WHEN
    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}

TEST(Salt2, SaltDesaltEmpty) {
    // GIVEN
    const string &text = "";
    SaltedText saltedText{};

    // WHEN
    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}


TEST(Salt2, SaltDesaltRu) {
    // GIVEN
    const string &text = "приветы ;№ц";
    SaltedText saltedText{};
    // WHEN

    saltedText.salted(text);
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ(text, str);
}


TEST(Salt2, SaltDesaltInListEquals) {
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
        cout << "text text '" << text << "' " << endl;
        // GIVEN
        SaltedText saltedText{};

        // WHEN
        saltedText.salted(text);
        auto str = saltedText.desalted();

        // THEN
        ASSERT_EQ(text, str);
    }
}

