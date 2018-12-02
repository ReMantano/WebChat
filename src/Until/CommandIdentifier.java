package Until;

import java.util.Arrays;
import java.util.List;

public class CommandIdentifier {


    public Command checkCommand(String command){
        if (command != null && command.charAt(0) == '\\') {
            String temp = command.substring(1);
            List<String> commandList = Arrays.asList(temp.split(" "));
            String commandName = commandList.get(0);
            return findCommand(commandName.toUpperCase());
        }else
            return Command.NULL;
    }

    private Command findCommand(String command){
        try {
            return Command.valueOf(command);
        }catch (IllegalArgumentException e){
            return Command.UNKNOWN;
        }

    }


}