//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "thekey.h"
#include "thekey_core.h"
#include "salt_text/salt2.h"
#include "salt_text/salt_base.h"
#include "utils/common.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace tkey2_salt;
using namespace tkey_salt;


TEST(Salt2Test, SaltDesaltEquals) {
    // GIVEN
    SaltedText saltedText{};
    saltedText.salted("some.site.com");


    // WHEN
    auto str = saltedText.desalted();

    // THEN
    ASSERT_EQ("some.site.com", str);
}


TEST(Salt2Test, EncodeScheme) {
    // when  scheme a-z + .
    auto scheme = find_scheme(2);

    // then
    ASSERT_EQ(0, scheme->encoded(U'a'));
    ASSERT_EQ(1, scheme->encoded(U'b'));
    ASSERT_EQ(25, scheme->encoded(U'z'));
    ASSERT_EQ(26, scheme->encoded(U'.'));
}


TEST(Salt2Test, DencodeScheme) {
    // when  scheme a-z + .
    auto scheme = find_scheme(2);

    // then
    ASSERT_EQ(U'a', scheme->decoded(0));
    ASSERT_EQ(U'b', scheme->decoded(1));
    ASSERT_EQ(U'z', scheme->decoded(25));
    ASSERT_EQ(U'.', scheme->decoded(26));
    ASSERT_EQ(U'a', scheme->decoded(27));
}