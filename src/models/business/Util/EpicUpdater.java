package models.business.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import models.business.Epic;
import models.business.Subtask;
import models.business.enums.TaskStatus;

public class EpicUpdater {

  public Epic updateEpicOnSubtaskOperation(Subtask subtask, Epic updateEpic,
      Map<Integer, Subtask> subtasks) {
    checkEpicStatus(updateEpic, subtasks);
    //Если задача с временем
    if(subtask.getStartTime() != null) {
      updateEpicStartTime(subtask, updateEpic, subtasks);
      updateEpicEndTime(subtask, updateEpic, subtasks);
      updateEpicDuration(updateEpic, subtasks);
    }
    return updateEpic;
  }

  private void updateEpicDuration(Epic updateEpic, Map<Integer, Subtask> subtasks) {
    List<Integer> subtasksId = updateEpic.getSubtasksId();
    long epicDuration = 0;
    for(int id : subtasksId) {
      Subtask s = subtasks.get(id);
      epicDuration += s.getDuration();
    }
    updateEpic.setDuration(epicDuration);
  }

  private void updateEpicStartTime(Subtask subtask, Epic updateEpic, Map<Integer, Subtask> subtasks) {
    List<Integer> idSubtasksInEpic = updateEpic.getSubtasksId();
    LocalDateTime epicStartTime = subtask.getStartTime();
    for(int subtaskId : idSubtasksInEpic) {
      Subtask s = subtasks.get(subtaskId);
      if (s.getStartTime() != null && s.getStartTime().isBefore(epicStartTime)) {
        epicStartTime = s.getStartTime();
      }
    }
    updateEpic.setStartTime(epicStartTime);
  }

  private void updateEpicEndTime(Subtask subtask, Epic updateEpic, Map<Integer, Subtask> subtasks) {
    if(subtask.getStartTime() == null) {
      return;
    }
    List<Integer> idSubtasksInEpic = updateEpic.getSubtasksId();
    LocalDateTime epicEndTime = subtask.getEndTime();
    for(int subtaskId : idSubtasksInEpic) {
      Subtask s = subtasks.get(subtaskId);
      if(s.getStartTime() != null && s.getEndTime().isAfter(epicEndTime)) {
        epicEndTime = s.getEndTime();
      }
      updateEpic.setEndTime(epicEndTime);
    }
  }

  public void checkEpicStatus(Epic updateEpic, Map<Integer, Subtask> subtasks) {
    int newCount = 0;
    int doneCount = 0;
    for (int subTaskIdInEpic : updateEpic.getSubtasksId()) {
        if (subtasks.get(subTaskIdInEpic).getStatus() == TaskStatus.NEW) {
          newCount++;
        } else if (subtasks.get(subTaskIdInEpic).getStatus() == TaskStatus.DONE) {
          doneCount++;
        }
    }
    updateEpicStatus(newCount, doneCount, updateEpic);
  }

  private void updateEpicStatus(int newCount, int doneCount, Epic updateEpic) {
    if (updateEpic.getSubtasksId().isEmpty() || updateEpic.getSubTasksSize() == newCount) {
      updateEpic.setStatus(TaskStatus.NEW);
    } else if (updateEpic.getSubTasksSize() == doneCount) {
      updateEpic.setStatus(TaskStatus.DONE);
    } else {
      updateEpic.setStatus(TaskStatus.IN_PROGRESS);
    }
  }
}
