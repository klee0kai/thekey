//
// Created by panda on 01.03.24.
//

#include "k1tok2.h"
#include "key1.h"
#include "key2.h"

using namespace std;
using namespace thekey;

int thekey_v1::migrateK1toK2(
        const string &inPath,
        const string &outPath,
        const string &passw
) {
    int error = 0;
    auto srcStorage = thekey_v1::storage(inPath, passw);
    if (!srcStorage) return keyError;
    error = srcStorage->readAll();
    if (error)return error;

    auto info = srcStorage->info();

    error = thekey_v2::createStorage(
            {
                    .file = outPath,
                    .name = info.name,
                    .description = info.description
            });
    if (error)return error;
    auto dstStorage = thekey_v2::storage(outPath, passw);
    if (!dstStorage) return keyError;
    error = dstStorage->readAll();
    if (error)return error;

    return migrateK1toK2(*srcStorage, *dstStorage);
}

int thekey_v1::migrateK1toK2(thekey_v1::KeyStorageV1 &source, thekey_v2::KeyStorageV2 &dest) {
    for (const auto &noteOrig: source.notes(TK1_GET_NOTE_FULL)) {
        auto newNote = thekey_v2::DecryptedNote{
                .site = noteOrig.site,
                .login = noteOrig.login,
                .passw = noteOrig.passw,
                .description = noteOrig.description
        };
        for (const auto &orHist: noteOrig.history) {
            newNote.history.push_back(
                    {
                            .passw = orHist.passw,
                            .genTime = orHist.genTime
                    });
        }

        dest.createNote(newNote);
    }

    auto histPtr = source.genPasswHist();
    auto newHistList = vector<thekey_v2::DecryptedPassw>();
    for (const auto &item: source.genPasswHist()) {
        newHistList.push_back(
                {
                        .passw = item.passw,
                        .genTime = item.genTime
                });
    }
    dest.appendPasswHistory(newHistList);

    auto error = dest.save();
    return error;
}