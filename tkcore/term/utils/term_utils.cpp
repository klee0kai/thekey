//
// Created by panda on 07.06.2020.
//

#include "term_utils.h"
#include <termios.h>

using namespace std;

string term::ask_from_term(string message) {
    if (!message.empty())cout << message;

    std::string response;
    cin >> response;
    return response;
}

int term::ask_int_from_term(string message) {
    if (!message.empty())cout << message;

    int response;
    cin >> response;
    return response;
}

std::string term::ask_password_from_term(std::string message) {
    if (!message.empty())cout << message;

    ::termios old_terminal;
    ::termios new_terminal;

    //get settings of the actual terminal
    tcgetattr(STDIN_FILENO, &old_terminal);

    // do not echo the characters
    new_terminal = old_terminal;
    new_terminal.c_lflag &= ~(ECHO);

    tcsetattr(STDIN_FILENO, TCSANOW, &new_terminal);

    std::string password;
    cin >> password;

    // go back to the old settings
    tcsetattr(STDIN_FILENO, TCSANOW, &old_terminal);

    cout << endl;
    return password;
}


size_t term::argsCount(const char *sourceText) {
    size_t count = 0;
    if (sourceText[0] != ' ' && sourceText[0] != '\n')
        count++;
    for (int i = 0; sourceText[i] != 0; i++) {
        if (sourceText[i] == ' ' || sourceText[i] == '\n') {
            while (sourceText[i] == ' ' || sourceText[i] == '\n')
                i++;
            if (sourceText[i] != 0)
                count++;
        }
    }
    return count;
}


void term::splitArgs(char *sourceText, char **&argsOut, size_t &len) {
    len = argsCount(sourceText);
    size_t argIndex = 0;

    if (sourceText[0] != ' ' && sourceText[0] != '\n')
        argsOut[argIndex++] = sourceText;
    for (int i = 0; sourceText[i] != 0; i++) {
        if (sourceText[i] == ' ' || sourceText[i] == '\n') {
            while (sourceText[i] == ' ' || sourceText[i] == '\n')
                sourceText[i++] = 0;
            if (sourceText[i] != 0)
                argsOut[argIndex++] = sourceText + i;
        }
    }
}


void term::clear_opt() {
    optind = 0;
    opterr = 0;
    optopt = 0;
}
