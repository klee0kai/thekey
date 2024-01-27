//
// Created by panda on 25.01.24.
//

#include <functional>
#include "interactive.h"
#include "term_utils.h"

#define COLUMN_WIDTH 40

using namespace std;

static char ident = '\t';


struct CmdHandler {
    std::vector<std::string> variants;
    std::string cmdDesc;
    function<void()> invoke;
};

void interactive::cmd(
        const std::vector<std::string> &cmds,
        const std::string &cmdDesc,
        const function<void()> &invoke
) {
    allCmds.push_back(CmdHandler{
            .variants = cmds,
            .cmdDesc = cmdDesc,
            .invoke = invoke
    });
}


void interactive::loop() {
    cmd({"h", "help"}, "print help", [this]() {
        cout << welcomeText << endl;

        cout << endl;
        cout << ident << "Options:" << endl;

        for (const auto &cmd: allCmds) {
            cout << ident;
            cout.width(COLUMN_WIDTH);
            cout << std::left;
            int i = 0;
            for (const auto &variant: cmd.variants) {
                if (i++ > 0) cout << " or ";
                cout << variant;
            }
            cout << cmd.cmdDesc << endl;
        }
    });

    cmd({"q", "exit", "quit"}, "exit from interactive subprogram", []() {});

    cout << welcomeText << endl;

    while (true) {
        auto input = term::ask_from_term("> ");

        if (input == "q" || input == "exit" || input == "quit") {
            break;
        }

        int found = 0;
        for (const auto &cmdHandler: allCmds) {
            if (found)break;
            for (const auto &cmd: cmdHandler.variants) {
                if (cmd == input) {
                    found = 1;
                    cmdHandler.invoke();
                    break;
                }
            }
        }

        if (!found) {
            cerr << "cmd not found " << input << " use 'help' to see all variants" << endl;
        }
    }

    cout << byeText << endl;
}