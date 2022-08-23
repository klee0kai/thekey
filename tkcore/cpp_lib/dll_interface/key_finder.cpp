//
// Created by panda on 07.06.2020.
//

#include <ftw.h>
#include <cstring>
#include "key_finder.h"
#include "sys/stat.h"

static const char *const storageFormat = ".ckey";
static const size_t storageFormatLen = strlen(storageFormat);

static void (*fnStoreFoundGlobal)(Storage) = NULL;

static int dirTree(const char *checkPath, const struct stat *sbuf, int type, struct FTW *ftwb);

static int checkStorageSignature(const char *checkPath);


int key_finder::findStorages(const char *srcDir, void (*fnStoreFound)(Storage)) {
    fnStoreFoundGlobal = fnStoreFound;
    nftw(srcDir, dirTree, 60, 0);
    return 0;

}

static int checkStorageSignature(const char *checkPath) {
    int stFd = open(checkPath, O_RDONLY | O_CLOEXEC);
    if (stFd == -1) return 0;


    char buf[FileVer1_HEADER_LEN];
    int readed = read(stFd, buf, FileVer1_HEADER_LEN);
    if (readed >= FileVer1_HEADER_LEN) {
        FileVer1_Header *h = (FileVer1_Header *) &buf;
        if (memcmp(h->signature, storageSignature, SIGNATURE_LEN) == 0 && h->ver == 1
            && h->description[STORAGE_DESCRIPTION_LEN - 1] == 0 && h->name[STORAGE_NAME_LEN - 1] == 0) {
            Storage storage = {};
            storage.file = checkPath;
            storage.name = h->name;
            storage.description = h->description;

            fnStoreFoundGlobal(storage);
        }
    }


    close(stFd);
    return 0;

}

static int dirTree(const char *checkPath, const struct stat *sbuf, int type, struct FTW *ftwb) {

    if (!S_ISREG(sbuf->st_mode))return 0;
    int checkPathLen = strlen(checkPath);
    if (checkPathLen <= storageFormatLen)return 0;


    if (strcmp(checkPath + checkPathLen - storageFormatLen, storageFormat) == 0) {
        checkStorageSignature(checkPath);
    }
    return 0;
}


