//
// Created by panda on 06.06.2020.
//

#include "cmd_processing.h"
#include <iomanip>
#include <sys/ioctl.h>
#include <sys/fcntl.h>
#include <curses.h>
#include "public/key_finder.h"
#include "term_utils.h"

#define COLUMN_WIDTH 40

using namespace std;

static char ident = '\t';

static void storageFound(Storage storage) {

    cout << endl;
    cout << "-------------------------------------" << endl;
    cout << "path: " << storage.file << endl;
    cout << "name: " << storage.name << endl;
    cout << "desc: " << storage.description << endl;


}

void cmd_pr::findStorages(const char *srcDir) {

    if (srcDir == NULL)
        srcDir = "/";
    cout << "Available crypted storages on device: " << endl;
    key_finder::findStorages(srcDir, storageFound);
    cout << "-------------------------------------" << endl;
}

void cmd_pr::printHelp() {

    cout << "TheKey - (ver. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Options:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-h or --help";
    cout << "help" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-f or --find or --list";
    cout << "list available storages on device" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "-l [path] or --login [path]";
    cout << "login to crypted storage" << endl;
}


static void printHelpStTerm() {
    cout << "TheKey - (ver. " << TERM_VERSION << ") cryp/encrypt your secure passwords storages" << endl;
    cout << endl;
    cout << ident << "Commands:" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "help or h";
    cout << "help for commands" << endl;

    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "list or l";
    cout << "list notes" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "hist";
    cout << "show history of gen passwds" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "info or i";
    cout << "info about storage" << endl;


    cout << ident;
    cout.width(COLUMN_WIDTH);
    cout << std::left << "exit or quit or q";
    cout << "exit from program" << endl;
}


static void listNotes() {
    long long *notes = key_manager_ctx::getNotes();
    for (int i = 0; notes[i]; i++) {
        DecryptedNote *decryptedNote = key_manager_ctx::getNoteItem(notes[i], 1);
        cout << "-------------------------------------------" << endl;
        cout << "site: " << decryptedNote->site << endl;
        cout << "login: " << decryptedNote->login << endl;
        cout << "pass: " << decryptedNote->passw << endl;
        cout << "desc: " << decryptedNote->description << endl;
        if (decryptedNote->histLen > 0) {
            cout << "passw hist" << endl;
            for (int j = 0; j < decryptedNote->histLen; j++) {
                cout << ident << decryptedNote->hist->passw << endl;
            }
        }
        memset(decryptedNote, 0, sizeof(DecryptedNote));
        delete decryptedNote;
    }

    cout << "-------------------------------------------" << endl;

    delete[]notes;
}

static void showHistory() {
    long long *genPasswds = key_manager_ctx::getGenPassds();
    for (int i = 0; genPasswds[i]; i++) {
        DecryptedPassw *decryptedPassw = key_manager_ctx::getGenPassw(genPasswds[i]);
        cout << "-------------------------------------------" << endl;
        cout << "passw: " << decryptedPassw->passw << endl;
        memset(decryptedPassw, 0, sizeof(DecryptedPassw));
        delete decryptedPassw;
    }

    cout << "-------------------------------------------" << endl;

    delete[]genPasswds;
}

static void showInfo() {
    const char *filePath = key_manager_ctx::getLoggedStoragePath();
    int fd = open(filePath, O_RDONLY, 0);
    if (fd == -1) {
        cerr << "file not found " << filePath << endl;
        return;
    }

    FileVer1_Header fileVer1Header;
    read(fd, &fileVer1Header, FileVer1_HEADER_LEN);
    close(fd);

    cout << "storage: " << filePath << endl;
    cout << "ver: " << (int) fileVer1Header.ver << endl;
    cout << "name: " << fileVer1Header.name << endl;
    cout << "desc: " << fileVer1Header.description << endl;
    cout << "notesCount: " << fileVer1Header.notesCount << endl;
    cout << "histCount: " << fileVer1Header.genPasswCount << endl;
    cout << endl;
}

static int processCmdsStTerm(int argc, char **argv) {
    if (argc <= 0 || strlen(argv[0])==0)
        //ignore, continue
        return 0;

    if (strcmp(argv[0], "q") == 0 || strcmp(argv[0], "exit") == 0 || strcmp(argv[0], "quit") == 0)
        return 1;

    if (strcmp(argv[0], "help") == 0 || strcmp(argv[0], "h") == 0) {
        printHelpStTerm();
        return 0;
    }

    if (strcmp(argv[0], "list") == 0 || strcmp(argv[0], "l") == 0) {
        listNotes();
        return 0;
    }

    if (strcmp(argv[0], "hist") == 0) {
        showHistory();
        return 0;
    }


    if (strcmp(argv[0], "info") == 0 || strcmp(argv[0], "i") == 0) {
        showInfo();
        return 0;
    }

    cout << "unknown command  " << argv[0] << endl;

    return 0;
}

void cmd_pr::login(const char *filePath) {
    int fd = open(filePath, O_RDONLY, 0);
    if (fd == -1) {
        cerr << "file not found " << filePath << endl;
        return;
    }

    cout << "TheKey (ver. " << TERM_VERSION << ") designed by Panda" << endl;
    for (int tryPasswInput = 0; tryPasswInput < 3; tryPasswInput++) {
        cout << "Input passw:";
        char passw[PASSW_LEN];
        memset(passw, 0, PASSW_LEN);
        term_utils::get_password(passw);
        cout << endl;;
        int res = key_manager_ctx::login((const unsigned char *) filePath, (const unsigned char *) passw);
        memset(passw, 0, PASSW_LEN);

        if (res || !key_manager_ctx::isLogined()) {
            cerr << "error login to " << filePath << endl;
            cout << endl;
            continue;
        }
        break;
    }

    FileVer1_Header fileVer1Header;
    read(fd, &fileVer1Header, FileVer1_HEADER_LEN);
    close(fd);


    cout << "Welcome to " << fileVer1Header.name << endl;
    cout << "Input help for look the manual" << endl;


    char cmdBuf[100];
    int exitFlag = 0;
    while (!exitFlag) {
        cout << ">";
        memset(cmdBuf,0,0);
        cin.getline(cmdBuf, 100, '\n');

        size_t argc = term_utils::argsCount(cmdBuf);
        char **argv = new char *[argc];
        term_utils::splitArgs(cmdBuf, argv, argc);

        exitFlag = processCmdsStTerm(argc, argv);

        delete[]argv;

    }

}







