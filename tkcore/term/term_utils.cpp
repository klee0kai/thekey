//
// Created by panda on 07.06.2020.
//

#include "term_utils.h"
#include <termios.h>


void term_utils::get_password(char *password) {
    static struct termios old_terminal;
    static struct termios new_terminal;

    //get settings of the actual terminal
    tcgetattr(STDIN_FILENO, &old_terminal);

    // do not echo the characters
    new_terminal = old_terminal;
    new_terminal.c_lflag &= ~(ECHO);

    // set this as the new terminal options
    tcsetattr(STDIN_FILENO, TCSANOW, &new_terminal);

    // get the password
    // the user can add chars and delete if he puts it wrong
    // the input process is done when he hits the enter
    // the \n is stored, we replace it with \0
    if (fgets(password, BUFSIZ, stdin) == NULL)
        password[0] = '\0';
    else
        password[strlen(password) - 1] = '\0';

    // go back to the old settings
    tcsetattr(STDIN_FILENO, TCSANOW, &old_terminal);
}




size_t term_utils::argsCount(const char *sourceText) {
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


void term_utils::splitArgs(char *sourceText, char **&argsOut, size_t &len) {
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


void term_utils::clear_opt() {
    optind = 0;
    opterr = 0;
    optopt = 0;
}
