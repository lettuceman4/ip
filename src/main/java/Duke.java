import java.util.Scanner;

public class Duke {
    public static void main(String[] args) {
        String separationLine = "     _____________________________________________________\n";
        String indentation = "      ";
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(separationLine + logo);

        System.out.print(indentation + "Ah another dude trying to be productive. Sure. \n");
        System.out.print(indentation + "Let's see how long it takes. \n");
        System.out.print(indentation + "So, tell me, what do you want, sweetie? \n" + separationLine);

        InputOutputHandler handler = new InputOutputHandler();

        handler.handleInput();
    }
}
