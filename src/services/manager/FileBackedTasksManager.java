package services.manager;

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
import java.util.Arrays;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.Util.Managers;
import models.business.Util.Printer;
import models.business.enums.TaskStatus;

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
  public Task getTaskById(int id) {
    Task task = super.getTaskById(id);
    save();
    return task;
  }

  @Override
  public Epic getEpicById(int id)  {
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


  public void save() {
    try (FileWriter writer = new FileWriter("tasks.csv")) {
      writer.write("id,type,name,status,description,startTime,endTime,epicId\n");

      for (Task task : tasks.values()) {
        writer.write(task.toString() + "\n");
      }

      for (Epic epic : epics.values()) {
        writer.write(epic.toString() + "\n");
      }

      for (Subtask subTask : subtasks.values()) {
        writer.write(subTask.toString() + "\n");
      }

      writer.write("\n");
      if(historyManager.getHistoryToString() != null) {
        writer.write(historyManager.getHistoryToString().toString());
      }
    } catch (IOException e) {
      throw new ManagerSaveException("Ошибка записи в файл");
    }
  }


  public static FileBackedTasksManager load(File file) {
    FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
      bf.readLine();
      while (bf.ready()) {
        String line = bf.readLine();

        if (line != null) {
          if (line.contains("SUBTASK")) {
            Subtask subtask = convertLineToSubtask(line);
            fileBackedTasksManager.addSubtaskInMap(subtask);
          } else if (line.contains("TASK")) {
            Task task = convertLineToTask(line);
            fileBackedTasksManager.addTaskInMap(task);
          } else if (line.contains("EPIC")) {
            Epic epic = convertLineToEpic(line);
            fileBackedTasksManager.addEpicInMap(epic);
          }
        } else {
          String historyLine = bf.readLine();
          if (historyLine == null) {
            break;
          } else {
            fileBackedTasksManager.addTaskIdInHistory(historyLine);
          }
          break;
        }
      }
    } catch (IOException e) {
      throw new ManagerSaveException("Загружаемый файл пуст");
    }
    return fileBackedTasksManager;
  }

  private void addTaskIdInHistory(String historyLine) {
    String[] stringHistoryId = historyLine.split(",");
    int[] historyIds = Arrays.stream(stringHistoryId).mapToInt(Integer::valueOf).toArray();
      for (int id : historyIds) {
        if (tasks.containsKey(id)) {
          historyManager.add(tasks.get(id));
        }
        if (epics.containsKey(id)) {
          historyManager.add(epics.get(id));
        }
        if (subtasks.containsKey(id)) {
          historyManager.add(subtasks.get(id));
        }
      }
  }

  public static void main(String[] args) {
    FileBackedTasksManager fileBackedTasksManager1 = Managers.getFailBackedTaskManager();

    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60);
    int taskId1 = fileBackedTasksManager1.createTask(task1);

    Task task2 = new Task("Second_Task", "SecondTask_description", TaskStatus.DONE,
        LocalDateTime.of(2022, 12, 14, 8, 0), 30);
    fileBackedTasksManager1.createTask(task2);

    Epic epic = new Epic("Epic1", "Epic1_desc");
    int epicId1 = fileBackedTasksManager1.createEpic(epic);

    Subtask subtask1 = new Subtask("Subtask", "Subtask_description",
        TaskStatus.IN_PROGRESS,
        LocalDateTime.of(2022, 12, 14, 6, 0), 60, epicId1);
    fileBackedTasksManager1.createSubTask(subtask1);

    Task task4 = new Task("Forth_Task", "ForthTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 20, 0), 120);
    fileBackedTasksManager1.createTask(task4);

     fileBackedTasksManager1.getTaskById(taskId1);
//    fileBackedTasksManager1.getEpicById(epicId1);

    Printer.printAllTaskAndHistory(fileBackedTasksManager1);

    System.out.println("----------------------------------------");
    Printer.printPrioritizedTask(fileBackedTasksManager1);

    System.out.println();
    System.out.println();
    System.out.println("Проверка загрузки с файла");
    FileBackedTasksManager fileBackedTasksManager2 = Managers.loadFromFile(new File("tasks.csv"));
    Printer.printAllTaskAndHistory(fileBackedTasksManager1);
    System.out.println("----------------------------------------");
    Printer.printPrioritizedTask(fileBackedTasksManager2);
  }
}
