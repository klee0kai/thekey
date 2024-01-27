//
// Created by panda on 25.01.24.
//

#ifndef THEKEY_INTERACTIVE_H
#define THEKEY_INTERACTIVE_H

#include "../def_header.h"

struct CmdHandler {
    std::vector<std::string> variants;
    std::string cmdDesc;
    std::function<void()> invoke;

    [[nodiscard]] std::string variantsHelp() const;
};

class Interactive {

public:
    Interactive() = default;

    virtual ~Interactive() = default;

    std::string helpTitle;

    void cmd(
            const std::vector<std::string> &cmds,
            const std::string &cmdDesc,
            const std::function<void()> &invoke
    );

    void loop();

private:
    std::vector<CmdHandler> allCmds;
};

#endif //THEKEY_INTERACTIVE_H
