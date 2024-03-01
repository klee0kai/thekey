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
    auto storageV1 = thekey_v1::storage(inPath, passw);
    if (!storageV1) return keyError;
    error = storageV1->readAll();
    if (error)return error;

    auto info = storageV1->info();

    error = thekey_v2::createStorage(
            {
                    .file = outPath,
                    .name = info.name,
                    .description = info.description
            });
    if (error)return error;
    auto storageV2 = thekey_v2::storage(outPath, passw);
    if (!storageV2) return keyError;
    error = storageV2->readAll();
    if (error)return error;

    auto notesPtrs = storageV1->notes();
    for (const auto &notePtr: notesPtrs) {
        auto noteOrig = storageV1->note(notePtr, 1);
        if (!noteOrig)continue;

        auto newNote = thekey_v2::DecryptedNote{
                .site = noteOrig->site,
                .login = noteOrig->login,
                .passw = noteOrig->passw,
                .description = noteOrig->description
        };
        for (const auto &orHist: storageV1->noteHist(notePtr)) {
            newNote.history.push_back(
                    {
                            .passw = orHist.passw,
                            .genTime = orHist.genTime
                    });
        }

        storageV2->createNote(newNote);
    }

    auto histPtr = storageV1->genPasswHist();
    auto newHistList = vector<thekey_v2::DecryptedPassw>(histPtr.size());
    for (const auto &item: storageV1->genPasswHist()) {
        newHistList.push_back(
                {
                        .passw = item.passw,
                        .genTime = item.genTime
                });
    }
    storageV2->appendPasswHistory(newHistList);

    error = storageV2->save();
    return error;
}