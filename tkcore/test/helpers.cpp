//
// Created by panda on 21.01.24.
//

#include "helpers.h"

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

void tkey2_salt::print_scheme(const tkey2_salt::EncodingScheme *scheme) {
    if (!scheme)return;

    cout << "scheme " << hex << scheme->type << dec << endl;
    static int i;
    i = 0;
    scheme->all_symbols([](const wide_char &c) {
        cout << hex << "sym " << i++ << " | '" << from(c) << "' " << endl;
    });
    cout << endl;
}
