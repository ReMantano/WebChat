package Until;

import org.junit.Assert;
import org.junit.Test;

public class testCommandIdentifier {

    @Test
    public void testIdentifier(){

        Assert.assertEquals(checkCommandTest("\\exit"),Command.EXIT);
        Assert.assertEquals(checkCommandTest("\\leave"),Command.LEAVE);
        Assert.assertEquals(checkCommandTest("\\register"),Command.REGISTER);
        Assert.assertEquals(checkCommandTest("\\unknown"),Command.UNKNOWN);
        Assert.assertEquals(checkCommandTest("Hello"),Command.NULL);


    }

    private Command checkCommandTest(String command){
        CommandIdentifier commandIdentifier = new CommandIdentifier();

        return commandIdentifier.checkCommand(command);
    }

}
