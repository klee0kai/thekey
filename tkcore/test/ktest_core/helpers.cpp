//
// Created by panda on 21.01.24.
//

#include "helpers.h"

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

void thekey_v2::print_scheme(const thekey_v2::EncodingScheme *scheme) {
    if (!scheme)return;

    cout << "scheme " << hex << scheme->id << dec << endl;
    static int i;
    i = 0;
    scheme->all_symbols([](const wide_char &c) {
        cout << hex << "sym " << i++ << " | '" << from(c) << "' " << endl;
    });
    cout << endl;
}
