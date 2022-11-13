package models.business;

import models.business.enums.Status;
import models.business.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String subName, String subTaskDescription, Status subTaskStatus, int epicId) {
        super(subName, subTaskDescription, subTaskStatus);
        this.epicId = epicId;
    }

    public SubTask(int taskId, TaskType taskType, String taskName, String taskDescription, Status taskStatus, int epicId) {
        super(taskId, taskType, taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

//  public SubTask(int taskId, TaskType taskType, String taskName, Status taskStatus, String taskDescription, int epicId) {
//    super(taskId, taskType, taskName, taskStatus, taskDescription, epicId);
//  }

  public TaskType getTaskType() {
        return taskType;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + "," + TaskType.SUBTASK + "," + taskName + "," + taskStatus + "," + taskDescription + "," + epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SubTask subTask = (SubTask) obj;
        return (id == subTask.id) &&
                Objects.equals(taskName, subTask.taskName) &&
                    Objects.equals(taskDescription, subTask.taskDescription) &&
                        Objects.equals(taskStatus, subTask.taskStatus) &&
                            Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription, taskStatus, epicId);
    }
}
