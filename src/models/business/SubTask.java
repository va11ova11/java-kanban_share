package models.business;

import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String subName, String subTaskDescription, TaskStatus subTaskTaskStatus,
        int epicId) {
        super(subName, subTaskDescription, subTaskTaskStatus);
        this.epicId = epicId;
    }

    public SubTask(int taskId, TaskType taskType, String taskName, String taskDescription,
        TaskStatus taskStatus, int epicId) {
        super(taskId, taskType, taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public SubTask(String taskName, String taskDescription, int epicId) {
      super(taskName, taskDescription);
      this.epicId = epicId;
    }


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
        return id + "," + TaskType.SUBTASK + "," + taskName + "," + taskStatus + "," +
            taskDescription + "," + epicId;
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
