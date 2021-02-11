package uwu.smsgamer.smsserverutils.commands;

import uwu.smsgamer.smsserverutils.commands.commands.*;

public class CommandManager {
    public static void setupCommands() {
        new EvaluateCommand();
        new SendMsgCommand();
        new SmsServerUtilsCommand();
    }
}
