package services.manager;

import exception.ManagerSaveException;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import models.business.enums.TaskType;
import services.manager.history.HistoryManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.business.enums.TaskType.*;

public class FileBackedTasksManager extends InMemoryTasksManager implements TasksManager {

  private final HistoryManager historyManager = super.getHistoryManager();

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
  public int createSubTask(Subtask subtask) {
    int subtaskId = super.createSubTask(subtask);
    save();
    return subtaskId;
  }
  @Override
  public void deleteTaskById(int id) {
    super.deleteTaskById(id);
    save();
  }
  @Override
  public Task getTaskById(int id) throws ManagerSaveException{
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
  public Subtask getSubTaskById(int id) {
    Subtask subTask = super.getSubTaskById(id);
    save();
    return subTask;
  }

  private Task makeTaskFromString(String value) {
    String[] taskInfo = value.split(",");
    int taskId = Integer.parseInt(taskInfo[0]);
    TaskType taskType = TaskType.valueOf(taskInfo[1]);
    String taskName = taskInfo[2];
    TaskStatus taskStatus = TaskStatus.valueOf(taskInfo[3]);
    String taskDescription = taskInfo[4];

    if (taskType == SUBTASK) {
      int epicId = Integer.parseInt(taskInfo[5]);
      return new Subtask(taskId, taskType, taskName, taskDescription, taskStatus, (epicId));
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
      super.addSubTaskInMap((Subtask) task);
    }
  }


  public void save()  {
    try (FileWriter writer = new FileWriter("tasks.csv")) {
      writer.write("id,type,name,status,description,epicId\n");

      final Map<Integer, Task> tasks = super.getTasks();
      final Map<Integer, Epic> epics = super.getEpics();
      final Map<Integer, Subtask> subTasks = super.getSubTasks();

      if(tasks.isEmpty() && subTasks.isEmpty() && epics.isEmpty()) {
        throw new ManagerSaveException("Не созданно ни одной задачи");
      }
      for (Task task : tasks.values()) {
        writer.write(task.toString() + "\n");
      }

      for (Epic epic : epics.values()) {
        writer.write(epic.toString() + "\n");
      }

      for (Subtask subTask : subTasks.values()) {
        writer.write(subTask.toString() + "\n");
      }

      writer.write("\n");
      writer.write(historyToString());
    } catch (IOException e) {
      throw new ManagerSaveException("Ошибка записи в файл");
    }
  }

  public static FileBackedTasksManager loadFromFile(File file) {
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
      bf.readLine();
      while (bf.ready()) {

        String line = bf.readLine();

        if(!line.isEmpty()){
          fileBackedTasksManager.addTaskInMap(line);
        }
        else {
          String historyLine = bf.readLine();
          if (historyLine == null) {
            break;
          } else {
            fileBackedTasksManager.addTaskInHistory(historyFromString(historyLine));
          }
          break;
        }
      }
    } catch (IOException e) {
        throw new ManagerSaveException("Загружаемый файл пуст");
    }
    return fileBackedTasksManager;
  }

  private void addTaskInHistory(List<Integer> historyIds) {
    if (!historyIds.isEmpty()) {

      Map<Integer, Task> tasks = super.getTasks();
      Map<Integer, Epic> epics = super.getEpics();
      Map<Integer, Subtask> subTasks = super.getSubTasks();

      for (int id : historyIds) {
        if (tasks.containsKey(id)) {
          historyManager.addTaskInHistory(tasks.get(id));
        }
        if (epics.containsKey(id)) {
          historyManager.addTaskInHistory(epics.get(id));
        }
        if (subTasks.containsKey(id)) {
          historyManager.addTaskInHistory(subTasks.get(id));
        }
      }
    }
  }

  private static List<Integer> historyFromString(String value) {
    List<Integer> historyIds = new ArrayList<>();
    String[] ids = value.split(",");
    for (String str : ids) {
      historyIds.add(Integer.parseInt(str));
    }
    return historyIds;
  }

  private String historyToString() {
    List<Task> history = historyManager.getHistory();
    if(history == null) {
      return "";
    }
      StringBuilder idTaskFromHistory = new StringBuilder();
      for (Task task : history) {
        idTaskFromHistory.append(task.getId());
        idTaskFromHistory.append(",");
      }
      return idTaskFromHistory.toString();
    }




  public void printAll() {
    super.printAllTask();
    super.printHistory();
  }

  public static void main(String[] args) {
    FileBackedTasksManager fileBackedTasksManager1 = Managers.getFailBackedTaskManager();
    Task task1 = new Task("Task1", "Task1_description", TaskStatus.NEW);
    Task task2 = new Task("Task2", "Task2_description", TaskStatus.IN_PROGRESS);
    int taskId1 = fileBackedTasksManager1.createTask(task1);
    int taskId2 = fileBackedTasksManager1.createTask(task2);

    Epic epic1 = new Epic("Epic1", "Epic1_description");
    Epic epic2 = new Epic("Epic2", "Epic2_description");
    int epicId1 = fileBackedTasksManager1.createEpic(epic1);
    int epicId2 = fileBackedTasksManager1.createEpic(epic2);

    Subtask subtask1 = new Subtask("SubTaskInEpic1", "SubTask1_description",
        TaskStatus.NEW, epicId1);
    Subtask subtask2 = new Subtask("SubTaskInEpic1", "SubTask2_description",
        TaskStatus.IN_PROGRESS, epicId1);
    Subtask subtask3 = new Subtask("SubTaskInEpic2", "SubTask3_description",
        TaskStatus.DONE, epicId2);
    Subtask subtask4 = new Subtask("SuTaskInEpic2", "SubTask4_description",
        TaskStatus.IN_PROGRESS, epicId2);

    int subTaskId1 = fileBackedTasksManager1.createSubTask(subtask1);
    int subTaskId2 = fileBackedTasksManager1.createSubTask(subtask2);
    int subTaskId3 = fileBackedTasksManager1.createSubTask(subtask3);
    int subTaskId4 = fileBackedTasksManager1.createSubTask(subtask4);

    System.out.println("Проверка работы класса fileBackedTaskManager");
    System.out.println("Получить созданные задачи и историю их просмотра");
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
