//
// Created by panda on 25.01.24.
//

#include <functional>
#include "Interactive.h"
#include "term_utils.h"

#define COLUMN_WIDTH 40

using namespace std;

static char ident = '\t';


void Interactive::cmd(
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

void Interactive::loop() {
    cmd({"h", "help"}, "print help", [this]() {
        cout << helpTitle << endl;

        cout << endl;
        cout << ident << "Options:" << endl;

        for (const auto &cmd: allCmds) {
            cout << ident;
            cout.width(COLUMN_WIDTH);
            cout << std::left << cmd.variantsHelp() << cmd.cmdDesc << endl;
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


std::string CmdHandler::variantsHelp() const {
    string str{};
    int i = 0;
    for (const auto &variant: variants) {
        if (i++ > 0) str += " or ";
        str += variant;
    }
    return str;
}