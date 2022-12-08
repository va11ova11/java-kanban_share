package models.business;

import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String subName, String subtaskDescription, TaskStatus subtaskTaskStatus,
                   int epicId) {
        super(subName, subtaskDescription, subtaskTaskStatus);
        this.epicId = epicId;
    }

    public Subtask(int taskId, TaskType taskType, String taskName, String subtaskDescription,
                   TaskStatus subtaskStatus, int epicId) {
        super(taskId, taskType, taskName, subtaskDescription, subtaskStatus);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, int epicId) {
      super(taskName, taskDescription);
      this.epicId = epicId;
    }

    public TaskType getSubtaskType(){
        return taskType;
    }

    public String getSubtaskName() {
        return taskName;
    }

    public void setSubtaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSubtaskDescription() {
        return taskDescription;
    }

    public void setSubtaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getSubtaskStatus() {
        return taskStatus;
    }

    public void setSubtaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + "," + TaskType.SUBTASK + "," + taskName + "," + taskStatus + "," +
            taskDescription + "," + epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return (id == subtask.id) &&
                Objects.equals(taskName, subtask.taskName) &&
                    Objects.equals(taskDescription, subtask.taskDescription) &&
                        Objects.equals(taskStatus, subtask.taskStatus) &&
                            Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription, taskStatus, epicId);
    }
}
