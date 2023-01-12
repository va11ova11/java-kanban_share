package models.business;

import static models.business.Util.Constants.formatter;

import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void reset() {
        removeSubTasksIdInEpic();
        taskStatus = TaskStatus.NEW;
        startTime = null;
        endTime = null;
    }

    public Epic(String epicName, String epicDescription) {
        super(epicName, epicDescription);
        subtasksId = new ArrayList<>();
    }

    public Epic(int taskId, TaskType taskType, String taskName, String taskDescription,
        TaskStatus taskStatus, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(taskId, taskType, taskName, taskDescription, taskStatus, startTime, duration);
        subtasksId = new ArrayList<>();
        this.endTime = endTime;
    }

    public Epic(int taskId, TaskType taskType, String taskName, String taskDescription,
        TaskStatus taskStatus) {
        super(taskId, taskType, taskName, taskDescription, taskStatus);
        subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubTaskInEpicList(int subTaskId) {
        if(subtasksId == null) {
            this.subtasksId = new ArrayList<>();
        }
        subtasksId.add(subTaskId);
    }

    public int getSubTasksSize() {
        return subtasksId.size();
    }

    public void deleteSubTaskInEpic(int subTaskId) {
        subtasksId.remove((Integer) subTaskId);
    }
    public void removeSubTasksIdInEpic() {
        subtasksId.clear();
    }

    public String toString() {
        if (startTime == null) {
            return id + "," + TaskType.EPIC + "," + taskName + "," + taskStatus + ","
                + taskDescription;
        } else {
            return id + "," + TaskType.EPIC + "," + taskName + "," + taskStatus + ","
                + taskDescription
                + "," + startTime.format(formatter) + "," + endTime.format(formatter);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return id == epic.id &&
                Objects.equals(epic.taskName, taskName) &&
                Objects.equals(epic.taskDescription, taskDescription) &&
                Objects.equals(epic.taskStatus, taskStatus);
    }

    public int hashCode() {
        return Objects.hash(id, taskName, taskStatus, taskDescription);
    }

}
