package services.manager.time;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import models.business.Epic;
import models.business.Subtask;
import models.business.enums.TaskStatus;

public class EpicUpdater {

  public Epic updateEpicOnSubtaskOperation(Subtask subtask, Epic updateEpic,
      Map<Integer, Subtask> subtasks) {
    updateEpicDuration(subtask, updateEpic);
    updateEpicStartTime(subtask, updateEpic, subtasks);
    updateEpicEndTime(subtask, updateEpic, subtasks);
    checkEpicStatus(updateEpic, subtasks);
    return updateEpic;
  }

  public void resetEpic(Epic resetEpic) {
    resetEpic.removeSubTasksIdInEpic();
    resetEpic.setStatus(TaskStatus.NEW);
    resetEpic.setStartTime(null);
    resetEpic.setEndTime(null);
  }

  private void updateEpicDuration(Subtask subtask, Epic updateEpic) {
    long epicDuration = updateEpic.getDuration();
    long subtaskDuration  = subtask.getDuration();
    updateEpic.setDuration(epicDuration + subtaskDuration);
  }

  private void updateEpicStartTime(Subtask subtask, Epic updateEpic, Map<Integer, Subtask> subtasks) {
    List<Integer> idSubtasksInEpic = updateEpic.getSubTasksId();
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
    List<Integer> idSubtasksInEpic = updateEpic.getSubTasksId();
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
    for (int subTaskIdInEpic : updateEpic.getSubTasksId()) {
      if (subtasks.get(subTaskIdInEpic).getStatus() == TaskStatus.NEW) {
        newCount++;
      } else if (subtasks.get(subTaskIdInEpic).getStatus() == TaskStatus.DONE) {
        doneCount++;
      }
    }
    updateEpicStatus(newCount, doneCount, updateEpic);
  }

  private void updateEpicStatus(int newCount, int doneCount, Epic updateEpic) {
    if (updateEpic.getSubTasksId().isEmpty() || updateEpic.getSubTasksSize() == newCount) {
      updateEpic.setStatus(TaskStatus.NEW);
    } else if (updateEpic.getSubTasksSize() == doneCount) {
      updateEpic.setStatus(TaskStatus.DONE);
    } else {
      updateEpic.setStatus(TaskStatus.IN_PROGRESS);
    }
  }
}
