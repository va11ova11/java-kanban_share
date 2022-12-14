package models.business.Util;

import static models.business.Util.Constants.formatter;

import java.time.LocalDateTime;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

public class LineConverter {

  public static Task convertLineToTask(String line) {
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

  public static Subtask convertLineToSubtask(String line) {
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

  public static Epic convertLineToEpic(String line) {
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
}
