//
// Created by klee0kai on 08.01.2022.
//

#include <gtest/gtest.h>
#include "key_core.h"
#include "salt_text/salt2.h"
#include "salt/salt_base.h"
#include "common.h"
#include "salt_text/salt2_schema.h"
#include "key2.h"
#include "split_password.h"
#include <regex>

using namespace std;
using namespace thekey_v2;
using namespace key_salt;

const std::regex en_regex("[a-zA-Z0-9]+");

TEST(TwinsTests, TwinsUncoverCorrectData) {
    // GIVEN
    auto storageFile = "TwinsUncoverCorrectData.ckey";
    unlink(storageFile);
    auto error = thekey_v2::createStorage({
                                                  .file = storageFile,
                                                  .name = "test_storage_v2_name",
                                                  .description ="some_storage_description"
                                          });
    ASSERT_FALSE(error);
    auto storage = thekey_v2::storage(storageFile, "1234");
    ASSERT_TRUE(storage);
    error = storage->readAll();
    ASSERT_FALSE(error);

    auto orangeGroup = storage->createColorGroup({.color = ORANGE, .name = "orange simple"});
    auto violetGroup = storage->createColorGroup({.color = VIOLET, .name = "violet"});
    auto pinkGroup = storage->createColorGroup({.color = PINK, .name = "pink_group"});

    auto now = time(NULL);
    storage->createNote(
            {
                    .site = "somesite.com",
                    .login = "some_user_login",
                    .passw = "simpplepassw",
                    .description = "somesite_desc",
                    .colorGroupId = orangeGroup->id,

            }
    );

    storage->createNote(
            {
                    .site = "testget.cv",
                    .login = "person@email.su",
                    .passw = "12@21QW",
                    .description = "desc",
                    .colorGroupId = violetGroup->id,
            });
    storage->save();
    storage.reset();

    // WHEN
    auto info = thekey_v2::storageFullInfo(storageFile);
    auto twins = thekey_v2::twins("1234", info->saltMini);

    // THEN
    for (const auto &item: twins.passwForDescriptionTwins) {
        storage = thekey_v2::storage(storageFile, item);
        ASSERT_TRUE(storage);
        error = storage->readAll();
        ASSERT_FALSE(error);

        const auto &notes = storage->notes(TK2_GET_NOTE_FULL);
        auto note = notes.begin();
        ASSERT_EQ(2, notes.size());

        ASSERT_EQ("somesite_desc", note->description);
        ASSERT_NE("some_user_login", note->login);
        ASSERT_NE("somesite.com", note->site);
        ASSERT_NE("simpplepassw", note->passw);

        note++;
        ASSERT_EQ("desc", note->description);
        ASSERT_NE("person@email.su", note->login);
        ASSERT_NE("testget.cv", note->site);
        ASSERT_NE("12@21QW", note->passw);

        storage.reset();
    }

    for (const auto &item: twins.passwForLoginTwins) {
        storage = thekey_v2::storage(storageFile, item);
        ASSERT_TRUE(storage);
        error = storage->readAll();
        ASSERT_FALSE(error);

        const auto &notes = storage->notes(TK2_GET_NOTE_FULL);
        auto note = notes.begin();
        ASSERT_EQ(2, notes.size());

        ASSERT_EQ("some_user_login", note->login);
        ASSERT_NE("simpplepassw", note->passw);

        note++;
        ASSERT_EQ("person@email.su", note->login);
        ASSERT_NE("12@21QW", note->passw);

        storage.reset();
    }

}
