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
        const string &passw,
        const std::function<void(const float &)> &progress
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

    return migrateK1toK2(*srcStorage, *dstStorage, progress);
}

int thekey_v1::migrateK1toK2(
        thekey_v1::KeyStorageV1 &source,
        const std::string &outPath,
        const std::string &passw,
        const std::function<void(const float &)> &progress
) {
    int error = 0;
    auto info = source.info();

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

    return migrateK1toK2(source, *dstStorage, progress);
}

int thekey_v1::migrateK1toK2(
        thekey_v1::KeyStorageV1 &source,
        thekey_v2::KeyStorageV2 &dest,
        const std::function<void(const float &)> &progress
) {
    const auto &liteNotes = source.notes();
    const auto &liteGenHist = source.genPasswHistoryList();

    auto allItemsCount = float(liteNotes.size() + liteGenHist.size());
    int progressCount = 0;

    for (const auto &orNotePtr: liteNotes) {
        auto noteOrig = *source.note(orNotePtr.notePtr, TK2_GET_NOTE_FULL);
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
        progress(MIN(1, ++progressCount / allItemsCount));
    }

    for (const auto &liteHist: liteGenHist) {
        auto orHist = *source.genPasswHistory(liteHist.histPtr, TK1_GET_NOTE_FULL);
        auto newHistList = vector<thekey_v2::DecryptedPassw>();
        newHistList.push_back(
                {
                        .passw = orHist.passw,
                        .genTime = orHist.genTime
                });

        dest.appendPasswHistory(newHistList);
        progress(MIN(1, ++progressCount / allItemsCount));
    }

    auto error = dest.save();
    return error;
}