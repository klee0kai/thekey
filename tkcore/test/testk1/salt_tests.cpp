//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt/salt1.h"
#include "common.h"

using namespace std;
using namespace key_salt;

extern void saltHeader(SaltTextHeader *header, size_t lenRing);

extern void desaltgHeader(SaltTextHeader *header, size_t lenRing);

TEST(SaltTests, SalteDesalt) {
    for (int i = PASSW_MIN_LEN; i <= PASSW_MAX_LEN; i++) {
        SaltTextHeader saltTextHeader;
        SaltTextHeader origTextHeader;
        saltTextHeader.coding = rand() % 5;
        saltTextHeader.lenCoding = ENC_LEN_PASSW;
        saltTextHeader.len = i;
        int ring = rand();
        origTextHeader = saltTextHeader;
        saltHeader(&saltTextHeader, MAX(ring, PASSW_MAX_LEN));
        desaltgHeader(&saltTextHeader, MAX(ring, PASSW_MAX_LEN));

        int condition = memcmp(&origTextHeader, &saltTextHeader, sizeof(SaltTextHeader));
        ASSERT_FALSE(condition);
        if (condition) {
            cerr << "saltHeader error for ENC_LEN_PASSW "
                 << "saltHeader err for " << (unsigned int) origTextHeader.coding
                 << " " << (unsigned int) origTextHeader.len
                 << " " << (unsigned int) origTextHeader.lenCoding
                 << " " << (size_t) MAX(ring, PASSW_MAX_LEN) << endl;
        }

    }

    unsigned char fullMemSalt[sizeof(SaltTextHeader)];
    memset(fullMemSalt, 0, sizeof(SaltTextHeader));
    int fmSaltIndex = 0;
    for (; fmSaltIndex < 1000; fmSaltIndex++) {
        SaltTextHeader saltTextHeader;
        SaltTextHeader origTextHeader;
        saltTextHeader.coding = rand() % 5;
        saltTextHeader.len = rand();
        saltTextHeader.lenCoding = ((saltTextHeader.len <= PASSW_MAX_LEN && saltTextHeader.len >= PASSW_MIN_LEN)
                                    ? ENC_LEN_PASSW
                                    : ENC_LEN_TEXT);
        int ring = rand();
        origTextHeader = saltTextHeader;
        saltHeader(&saltTextHeader, MAX(ring, origTextHeader.len + 1));
        for (int j = 0; j < sizeof(SaltTextHeader); j++) {
            fullMemSalt[j] |= ((unsigned char *) &saltTextHeader)[j];
        }

        desaltgHeader(&saltTextHeader, MAX(ring, origTextHeader.len + 1));

        int condition = memcmp(&origTextHeader, &saltTextHeader, sizeof(SaltTextHeader));
        ASSERT_FALSE(condition);
        if (condition) {
            cerr << "saltHeader err for " << (unsigned int) origTextHeader.coding
                 << " " << (unsigned int) origTextHeader.len
                 << " " << (unsigned int) origTextHeader.lenCoding
                 << " " << (size_t) MAX(ring, origTextHeader.len + 1)
                 << " " << (origTextHeader.len < MAX(ring, origTextHeader.len + 1)) << endl;
        }


        if (memcmpr(fullMemSalt, -1, sizeof(SaltTextHeader)) == 0) {
            break;
        }
    }

    //  saltHeader salt not work
    ASSERT_FALSE(fmSaltIndex >= 1000);
}