import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class IOHandler {
    String separationLine = "     _____________________________________________________\n";
    String indentation = "      ";
    Scanner sc = new Scanner(System.in);
    String reply = sc.nextLine();

    String[] replyArr;

    String topPartOfBotReplyMessage = separationLine + indentation;
    String botPartOfBotReplyMessage = "\n" + separationLine.substring(0, separationLine.length() - 1);

    TaskManager taskManager = new TaskManager(new ArrayList<>());
    TaskSaveAndLoadManager taskSaveAndLoadManager = new TaskSaveAndLoadManager();

    private DateAndTime handleTime(String timeFormat) {
        if (!timeFormat.contains(" ")) {
            LocalDate localDate = LocalDate.parse(timeFormat);
            return new DateAndTime(localDate);
        } else {
            String[] split = timeFormat.split(" ");
            LocalTime localTime = LocalTime.parse(split[0].trim());
            LocalDate localDate = LocalDate.parse(split[1].trim());
            return new DateAndTime(localDate, localTime);
        }
    }

    private String[] splitReply() {
        // index 4 is excluded
        if (reply.length() >= 4) {
            if (reply.substring(0, 4).equals("done")) {
                replyArr = reply.split(" ");
            } else if (reply.contains("/")){
                replyArr = reply.split("/");
            } else {
                replyArr = reply.split(" ");
            }
        } else {
            // printMessage(new UnexpectedInputException().toString());
            replyArr = null;
        }
        return replyArr;
    }

    private void printMessage(String message) {
        System.out.println(topPartOfBotReplyMessage + message + botPartOfBotReplyMessage);
    }

    private void handleUserInput()
    {
        String fullReply;
        if (reply.equals("list")) {
            String botReply = "Checking the whole list doesn't make you finish anything faster... \n";
            String resultList = indentation + taskManager.convertTaskListToString(taskManager.getTaskList());
            fullReply = botReply + resultList;
        } else if (replyArr != null && replyArr[0].equals("done")) {
            int taskIndex = Integer.parseInt(replyArr[1]) - 1;
            taskManager.markTaskAsDone(taskIndex);
            String botReply = "Wah finally. Wondering how long more I need to wait... \n";
            String taskDone = indentation + taskManager.getTask(taskIndex).toString();
            fullReply = botReply + taskDone;
        } else if (replyArr != null && replyArr[0].equals("delete")) {
            int taskIndex = Integer.parseInt(replyArr[1]) - 1;
            Task cacheTask = taskManager.getTask(taskIndex);
            taskManager.removeTask(taskIndex);
            String botReply = "Good good... Okay removed! Looks more apt for a lazy ass like you. \n";
            String taskDone = indentation + cacheTask.toString();
            fullReply = botReply + taskDone;
        } else if (replyArr != null && replyArr[0].equals("find")) {
            ArrayList<Task> foundTasks = taskManager.findTaskThatHasKeyword(replyArr[1]);
            String botReply = "";
            if (foundTasks.size() == 0) {
                botReply = "Sorry can't find any tasks with such keyword";
            } else {
                botReply = "Found 'em. But at what cost.. \n.";
            }
            String resultList = indentation + taskManager.convertTaskListToString(foundTasks);
            fullReply = botReply + resultList;
        }
        // adding task to the list
        else {
            Task newTask = null;
            if (reply.contains("todo")) {
                reply = reply.replace("todo ", "");
                newTask = new ToDoTask(reply, false);
            }
            else if (reply.contains("deadline")) {
                reply = reply.replace("deadline ", "");
                reply = reply.replace("/by", "/");
                String[] tempArr = splitReply();
                newTask = new DeadlineTask(tempArr[0], false, handleTime(tempArr[1].trim()));
            }
            else if (reply.contains("event")) {
                reply = reply.replace("event ", "");
                reply = reply.replace("/at", "/");
                String[] tempArr = splitReply();
                newTask = new EventTask(tempArr[0], false, handleTime(tempArr[1].trim()));
            }
            taskManager.addTask(newTask);
            String botReply = "Wow, another task. Added. You sure you can finish them all? \n";
            assert newTask != null;
            String addedTask = indentation + newTask.toString() + "\n";
            String totalTask = indentation + "Now you have a grand total of " + taskManager.getTotalNoOfTasks() + " tasks!";
            fullReply = botReply + addedTask + totalTask;
        }
        printMessage(fullReply);
    }

    public void handleInput() throws IOException {

        taskSaveAndLoadManager.saveTaskManager(taskManager);
        if (taskSaveAndLoadManager.loadTaskManager() != null) {
            taskManager = taskSaveAndLoadManager.loadTaskManager();
        }

        while (!reply.equals("bye"))
        {
            replyArr = splitReply();

            DukeException exception = DukeExceptionHandler.checkForException(reply);
            if (exception != null) {
                printMessage(exception.toString());
            } else {
                handleUserInput();
            }
            reply = sc.nextLine();
        }

        taskSaveAndLoadManager.saveTaskManager(taskManager);
        String byeMessage = "That's all? Sure. See you again (hopefully LOL).";
        System.out.println(topPartOfBotReplyMessage + byeMessage + botPartOfBotReplyMessage);
        sc.close();
    }
}
