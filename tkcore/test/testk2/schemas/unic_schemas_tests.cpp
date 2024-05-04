//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "common.h"
#include "salt_text/salt2_schema.h"

using namespace std;
using namespace thekey_v2;
using namespace key_salt;


TEST(unicSchemas, SchemeIdIsUnic) {
    set<uint32_t> ids{};
    for (const auto &item: encodingSchemas) {
        ASSERT_TRUE(ids.find(item.id) == ids.end()) << "scheme id has doubles " << item.id << endl;
        ids.insert(item.id);
    }
}

