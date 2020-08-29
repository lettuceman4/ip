import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The TaskSaveAndLoadManager class manages the logic to convert Java objects into data objects
 * and vice versa.</p>
 */
public class TaskSaveAndLoadManager {

    /**
     * Saves the task list with tasks, along with their descriptions as well as
     * their respective statuses and types.
     * @param taskManager The TaskManager that stores the task list
     * @throws IOException An exception thrown when IO operation fails
     */
    public void saveTaskManager(TaskManager taskManager) throws IOException {
        TaskManagerData taskManagerData = new TaskManagerData();
        TaskData taskData = null;
        for (Task task: taskManager.getTaskList()) {
            boolean bool = task.getTaskStatus();
            int boolInt;
            if (bool) {
                boolInt = 1;
            } else {
                boolInt = 0;
            }
            if (task instanceof ToDoTask) {
                taskData = new TaskData("todo", task.getTaskDescription(),
                        boolInt, "");
            } else if (task instanceof DeadlineTask) {
                LocalDate date = ((DeadlineTask) task).getTimeToBeDoneBy().getDate();
                taskData = new TaskData("deadline", task.getTaskDescription(),
                        boolInt, date.toString(), "");
            } else if (task instanceof EventTask) {
                LocalDate date = ((EventTask) task).getEventTime().getDate();
                LocalTime time = ((EventTask) task).getEventTime().getTime();
                taskData = new TaskData("event", task.getTaskDescription(),
                        boolInt, date.toString(), time.toString());
            }
            taskManagerData.getTaskList().add(taskData);
        }
        FileReadWriteIO.saveTaskListData(taskManagerData);
    }

    /**
     * Loads the task list with tasks, along with their descriptions as well as
     * their respective statuses and types since last save.
     * @return The TaskManager that stores the task list
     * @throws IOException An exception thrown when IO operation fails
     */
    public TaskManager loadTaskManager() throws IOException {
        List<String> loadedData = FileReadWriteIO.loadUngroupedSavedTaskList();
        ArrayList<Task> taskList = new ArrayList<>();
        // loop through the loaded data strings, split each string by | and add to task list
        if (loadedData != null) {
            for (String loadedDatum : loadedData) {
                String[] tempArr = loadedDatum.split(" %% ");
                TaskData taskData;
                // a todo item
                if (tempArr.length == 3) {
                    taskData = new TaskData(tempArr[0], tempArr[1], Integer.parseInt(tempArr[2]));
                // a deadline item
                } else if (tempArr.length == 4) {
                    taskData = new TaskData(tempArr[0], tempArr[1], Integer.parseInt(tempArr[2]), tempArr[3]);
                } else {
                    taskData = new TaskData(tempArr[0], tempArr[1],
                            Integer.parseInt(tempArr[2]), tempArr[3], tempArr[4]);
                }
                Task task = loadTask(taskData);
                taskList.add(task);
            }
            return new TaskManager(taskList);
        }
        return null;
    }

    private Task loadTask(TaskData taskData) {
        boolean isDone;
        isDone = taskData.getIsDone() == 1;

        if (taskData.getTaskType().equals("todo")) {
            return new ToDoTask(taskData.getTaskDescription(), isDone);
        } else if (taskData.getTaskType().equals("deadline")) {
            DateAndTime dt = new DateAndTime(LocalDate.parse(taskData.getTime()));
            return new DeadlineTask(taskData.getTaskDescription(), isDone, dt);
        } else {
            DateAndTime dt = new DateAndTime(LocalDate.parse(taskData.getDate()), LocalTime.parse(taskData.getTime()));
            return new EventTask(taskData.getTaskDescription(), isDone, dt);
        }
    }

}
