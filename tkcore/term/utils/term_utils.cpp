//
// Created by panda on 07.06.2020.
//

#include "term_utils.h"
#include <termios.h>

using namespace std;

void term::flush_await() {
    sync();
    usleep(100000);
}

string term::ask_from_term(const string &message) {
    flush_await();
    if (!message.empty())cout << message;

    std::string response;
    cin >> response;
    return response;
}

int term::ask_int_from_term(const string &message) {
    flush_await();

    if (!message.empty())cout << message;

    int response;
    cin >> response;
    return response;
}

std::string term::ask_password_from_term(const string &message) {
    flush_await();

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

