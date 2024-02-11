//
// Created by panda on 07.06.2020.
//

#include "term_utils.h"
#include <termios.h>
#include <stdio.h>
#include <unistd.h>
#include <iostream>

using namespace std;

void term::flush_await() {
    sync();
    usleep(100000);
}

int term::checkInput() {
    int flags = fcntl(STDIN_FILENO, F_GETFL, 0);
    fcntl(STDIN_FILENO, F_SETFL, flags | O_NONBLOCK);
    char buf[100];
    int len = read(STDIN_FILENO, buf, sizeof(buf));
    fcntl(STDIN_FILENO, F_SETFL, flags & !O_NONBLOCK);
    return len > 0;
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

