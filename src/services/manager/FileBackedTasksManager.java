package services.manager;

import static java.time.Month.FEBRUARY;
import static models.business.Util.LineConverter.convertLineToEpic;
import static models.business.Util.LineConverter.convertLineToSubtask;
import static models.business.Util.LineConverter.convertLineToTask;

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
import models.business.Util.Printer;
import models.business.enums.TaskStatus;
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

  public void save() {
    try (FileWriter writer = new FileWriter("tasks.csv")) {
      writer.write("id,type,name,status,description,startTime,endTime,epicId\n");

      final Map<Integer, Task> tasks = super.getTasks();
      final Map<Integer, Epic> epics = super.getEpics();
      final Map<Integer, Subtask> subTasks = super.getSubTasks();

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
      writer.write(historyManager.historyToString());
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
            fileBackedTasksManager.addTaskInHistory(HistoryManager.historyFromString(historyLine));
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


  public static void main(String[] args) {
    FileBackedTasksManager fileBackedTasksManager1 = Managers.getFailBackedTaskManager();
//
//    Task taskHasTime = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;09:00", 60);
//    int task1 = fileBackedTasksManager1.createTask(taskHasTime);
//
//    Task taskHasTime2 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;10:00", 60);
//    int task2 = fileBackedTasksManager1.createTask(taskHasTime2);
//
//    Epic epic = new Epic("Epic1", "Epic1_desc");
//    int epicId = fileBackedTasksManager1.createEpic(epic);
//
//    Subtask subtask = new Subtask("Subtask1", "Subtask_desc1", TaskStatus.DONE, "10.01.2022;08:00", 30, epicId);
//    fileBackedTasksManager1.createSubTask(subtask);
//
//    Task taskHasTime3 = new Task("TaskTime", "Task has time", TaskStatus.NEW, "10.01.2022;07:00", 30);
//    fileBackedTasksManager1.createTask(taskHasTime3);
//
//    fileBackedTasksManager1.getTaskById(task1);
//    fileBackedTasksManager1.getEpicById(epicId);
//
//    Printer.printAllTask(fileBackedTasksManager1);
//
//
//    System.out.println("------------");
//    System.out.println("Задачи отсортированные по времени выполнения");
//    System.out.println(fileBackedTasksManager1.getPrioritizedTasks());
//
//    System.out.println("Проверка загрузки с файла");
//    FileBackedTasksManager fileBackedTasksManager2 = Managers.loadFromFile(new File("tasks.csv"));
//    Printer.printTaskAndHistory(fileBackedTasksManager1);
  }
}
