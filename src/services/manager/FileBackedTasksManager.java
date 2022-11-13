package services.manager;

import exception.ManagerSaveException;
import models.business.Epic;
import models.business.SubTask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.Status;
import models.business.enums.TaskType;
import services.manager.history.HistoryManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.business.enums.TaskType.*;

public class FileBackedTasksManager extends InMemoryTasksManager {

    public FileBackedTasksManager() {
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int createSubTask(SubTask subtask) {
        int subtaskId = super.createSubTask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    private Task makeTaskFromString(String value) {
        String[] taskInfo = value.split(",");
        int taskId = Integer.parseInt(taskInfo[0]);
        TaskType taskType = TaskType.valueOf(taskInfo[1]);
        String taskName = taskInfo[2];
        Status taskStatus = Status.valueOf(taskInfo[3]);
        String taskDescription = taskInfo[4];

        if (taskType == SUBTASK) {
            int epicId = Integer.parseInt(taskInfo[5]);
            return new SubTask(taskId, taskType, taskName, taskDescription, taskStatus, (epicId));
        } else if (taskType == TASK) {
            return new Task(taskId, taskType, taskName, taskDescription, taskStatus);
        } else {
            return new Epic(taskId, taskType, taskName, taskDescription, taskStatus);
        }
    }


    private void addTaskInMap(String value) {
        Task task = makeTaskFromString(value);
        if (task.getTaskType() == TASK) {
            super.addTaskInMap(task);
        } else if (task.getTaskType() == EPIC) {
            super.addEpicInMap((Epic) task);
        } else {
            super.addSubTaskInMap((SubTask) task);
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter("tasks.csv")) {
            writer.write("id,type,name,status,description,epic\n");

            Map<Integer, Task> tasks = super.getTasks();
            Map<Integer, Epic> epics = super.getEpics();
            Map<Integer, SubTask> subTasks = super.getSubTasks();

            for (Task task : tasks.values()) {
                writer.write(task.toString() + "\n");
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString() + "\n");
            }

            for (SubTask subTask : subTasks.values()) {
                writer.write(subTask.toString() + "\n");
            }

            writer.write("\n");
            writer.write(historyToString(super.getHistoryManager()));
        } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            bf.readLine();
            while (bf.ready()) {

                String line = bf.readLine();

                if (line.isEmpty()) {
                    String historyLine = bf.readLine();
                    if (historyLine == null) {
                        break;
                    }
                    if (!historyLine.isEmpty()) {
                        fileBackedTasksManager.addTaskInHistory(historyFromString(historyLine));
                    }
                    break;
                }
                fileBackedTasksManager.addTaskInMap(line);
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException(e.getMessage());
            } catch (ManagerSaveException ex) {
                throw new RuntimeException(ex);
            }
        }
        return fileBackedTasksManager;
    }

    public void addTaskInHistory(List<Integer> historyIds) {
        if (!historyIds.isEmpty()) {
            for (Integer id : historyIds) {
                if (super.getTasks().containsKey(id)) {
                    super.getHistoryManager().addTaskInHistory(getTask(id));
                }
                if (super.getEpics().containsKey(id)) {
                    super.getHistoryManager().addTaskInHistory(getEpic(id));
                }
                if (super.getSubTasks().containsKey(id)) {
                    super.getHistoryManager().addTaskInHistory(getSubTask(id));
                }
            }
        }
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] ids = value.split(",");
        for (String str : ids) {
            historyIds.add(Integer.parseInt(str));
        }
        return historyIds;
    }

    public static String historyToString(HistoryManager historyManager) {
        if (historyManager.getHistory() == null) {
            return "";
        } else {
            StringBuilder historyTaskId = new StringBuilder();
            for (Task task : historyManager.getHistory()) {
                historyTaskId.append(task.getId());
                historyTaskId.append(",");
            }
            return historyTaskId.toString();
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    public void printAll() {
        super.printAllTask();
        super.printHistory();
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager1 = Managers.getFailBackedTaskManager();
        Task task1 = new Task("Task1", "Task1_description", Status.NEW);
        Task task2 = new Task("Task2", "Task2_description", Status.IN_PROGRESS);
        int taskId1 = fileBackedTasksManager1.createTask(task1);
        int taskId2 = fileBackedTasksManager1.createTask(task2);

        Epic epic1 = new Epic("Epic1", "Epic1_description");
        Epic epic2 = new Epic("Epic2", "Epic2_description");
        int epicId1 = fileBackedTasksManager1.createEpic(epic1);
        int epicId2 = fileBackedTasksManager1.createEpic(epic2);

        SubTask subTask1 = new SubTask("SubTaskInEpic1", "SubTask1_description", Status.NEW, epicId1);
        SubTask subTask2 = new SubTask("SubTaskInEpic1", "SubTask2_description", Status.IN_PROGRESS, epicId1);
        SubTask subTask3 = new SubTask("SubTaskInEpic2", "SubTask3_description", Status.DONE, epicId2);
        SubTask subTask4 = new SubTask("SuTaskInEpic2", "SubTask4_description", Status.IN_PROGRESS, epicId2);
        int subTaskId1 = fileBackedTasksManager1.createSubTask(subTask1);
        int subTaskId2 = fileBackedTasksManager1.createSubTask(subTask2);
        int subTaskId3 = fileBackedTasksManager1.createSubTask(subTask3);
        int subTaskId4 = fileBackedTasksManager1.createSubTask(subTask4);

        System.out.println("Проверка работы класса fileBackedTaskManager");
        System.out.println("Получить созданне задачи и историю их просмотра");
        fileBackedTasksManager1.getTaskById(taskId1);
        fileBackedTasksManager1.getEpicById(epicId1);
        fileBackedTasksManager1.getSubTaskById(subTaskId1);
        fileBackedTasksManager1.getSubTaskById(subTaskId2);
        fileBackedTasksManager1.printAll();
        System.out.println();

        System.out.println("Проверка загрузки с файла");
        FileBackedTasksManager fileBackedTasksManager2 = Managers.loadFromFile(new File("tasks.csv"));
        fileBackedTasksManager2.printAll();
    }
}
