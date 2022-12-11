package services.manager;

import static java.time.Month.FEBRUARY;

import exception.ManagerSaveException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import models.business.enums.TaskType;
import services.manager.history.HistoryManager;

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
  public Task getTaskById(int id) throws ManagerSaveException {
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

  @Override
  public void addTaskInMap(Task task) {
    super.addTaskInMap(task);
  }

  @Override
  public void addSubtaskInMap(Subtask subtask) {
    super.addSubtaskInMap(subtask);
  }

  @Override
  public void addEpicInMap(Epic epic) {
    super.addEpicInMap(epic);
  }


  public static Task convertLineToTask(String line, DateTimeFormatter formatter) {
    String[] taskInfo = line.split(",");
    int taskId = Integer.parseInt(taskInfo[0]);
    TaskType taskType = TaskType.valueOf(taskInfo[1]);
    String taskName = taskInfo[2];
    TaskStatus taskStatus = TaskStatus.valueOf(taskInfo[3]);
    String taskDescription = taskInfo[4];
    if (taskInfo.length < 7) {
      return new Task(taskId, taskType, taskName, taskDescription, taskStatus);
    } else {
      LocalDateTime startTime = LocalDateTime.parse(taskInfo[5], formatter);
      LocalDateTime endTime = LocalDateTime.parse(taskInfo[6], formatter);
      long duration = endTime.getMinute() - startTime.getMinute();
      return new Task(taskId, taskType, taskName, taskDescription, taskStatus, startTime,
          duration);
    }
  }

  public static Subtask convertLineToSubtask(String line, DateTimeFormatter formatter) {
    String[] subtaskInfo = line.split(",");
    int taskId = Integer.parseInt(subtaskInfo[0]);
    TaskType taskType = TaskType.valueOf(subtaskInfo[1]);
    String taskName = subtaskInfo[2];
    TaskStatus taskStatus = TaskStatus.valueOf(subtaskInfo[3]);
    String taskDescription = subtaskInfo[4];
    if (subtaskInfo.length < 7) {
      int epicId = Integer.parseInt(subtaskInfo[5]);
      return new Subtask(taskId, taskType, taskName, taskDescription, taskStatus, epicId);
    } else {
      LocalDateTime startTime = LocalDateTime.parse(subtaskInfo[5], formatter);
      LocalDateTime endTime = LocalDateTime.parse(subtaskInfo[6], formatter);
      long duration = endTime.getMinute() - startTime.getMinute();
      int epicId = Integer.parseInt(subtaskInfo[7]);
      return new Subtask(taskId, taskType, taskName, taskDescription, taskStatus, startTime,
          duration, epicId);
    }
  }

  public static Epic convertLineToEpic(String line, DateTimeFormatter formatter) {
    String[] epicInfo = line.split(",");
    int taskId = Integer.parseInt(epicInfo[0]);
    TaskType taskType = TaskType.valueOf(epicInfo[1]);
    String taskName = epicInfo[2];
    TaskStatus taskStatus = TaskStatus.valueOf(epicInfo[3]);
    String taskDescription = epicInfo[4];
    if (epicInfo.length < 7) {
      return new Epic(taskId, taskType, taskName, taskDescription, taskStatus);
    } else {
      LocalDateTime startTime = LocalDateTime.parse(epicInfo[5], formatter);
      LocalDateTime endTime = LocalDateTime.parse(epicInfo[6], formatter);
      long duration = endTime.getMinute() - startTime.getMinute();
      return new Epic(taskId, taskType, taskName, taskDescription, taskStatus, startTime, duration,
          endTime);
    }
  }


  public void save() {
    try (FileWriter writer = new FileWriter("tasks.csv")) {
      writer.write("id,type,name,status,description,startTime,endTime,epicId\n");

      final Map<Integer, Task> tasks = super.getTasks();
      final Map<Integer, Epic> epics = super.getEpics();
      final Map<Integer, Subtask> subTasks = super.getSubTasks();

      if (tasks.isEmpty() && subTasks.isEmpty() && epics.isEmpty()) {
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy;HH:mm");
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
      bf.readLine();
      while (bf.ready()) {

        String line = bf.readLine();

        if (!line.isEmpty()) {
          if (line.contains("SUBTASK")) {
            Subtask subtask = convertLineToSubtask(line, formatter);
            fileBackedTasksManager.addSubtaskInMap(subtask);
          } else if (line.contains("TASK")) {
            Task task = convertLineToTask(line, formatter);
            fileBackedTasksManager.addTaskInMap(task);
          } else if (line.contains("EPIC")) {
            Epic epic = convertLineToEpic(line, formatter);
            fileBackedTasksManager.addEpicInMap(epic);
          }
        } else {
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
    for (String id : ids) {
      historyIds.add(Integer.parseInt(id));
    }
    return historyIds;
  }

  private String historyToString() {
    List<Task> history = historyManager.getHistory();
    if (history == null) {
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
    Task t = new Task("T1", "T1_D", TaskStatus.NEW,
        LocalDateTime.of(2022, FEBRUARY, 16, 10, 0), 30);
    fileBackedTasksManager1.createTask(t);
    Epic epic1 = new Epic("Epic1", "Epic1_desc");
    int epicId = fileBackedTasksManager1.createEpic(epic1);

    Subtask subtask1 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
        LocalDateTime.of(2022, FEBRUARY, 15, 10, 0), 30, epicId);
    fileBackedTasksManager1.createSubTask(subtask1);

    Subtask subtask2 = new Subtask("Sub1", "Sub1_desc", TaskStatus.NEW,
        LocalDateTime.of(2022, FEBRUARY, 14, 10, 0), 30, epicId);
    fileBackedTasksManager1.createSubTask(subtask2);

    fileBackedTasksManager1.printAll();

    System.out.println("Проверка загрузки с файла");
    FileBackedTasksManager fileBackedTasksManager2 = Managers.loadFromFile(new File("tasks.csv"));
    fileBackedTasksManager2.printAll();
  }
}
