package models.business;

import models.business.enums.Status;
import models.business.enums.TaskType;

import java.util.Objects;

public class Task {
    protected int id;
    protected TaskType taskType;
    protected String taskName;
    protected Status taskStatus;
    protected String taskDescription;



    public Task(String taskName, String taskDescription, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }
    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task (int id, TaskType taskType, String taskName, String taskDescription, Status taskStatus) {
        this.id = id;
        this.taskType = taskType;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task fromString(String value) {
        String[] taskInfo = value.split(",");
        int taskId = Integer.parseInt(taskInfo[0]);
        TaskType taskType = TaskType.valueOf(taskInfo[1]);
        String taskName = taskInfo[2];
        Status taskStatus = Status.valueOf(taskInfo[3]);
        String taskDescription = taskInfo[4];
        return new Task(taskId, taskType, taskName, taskDescription, taskStatus);
    }

    public TaskType getTaskType(){
        return taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + "," + TaskType.TASK + "," + taskName + "," + taskStatus + "," + taskDescription;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return (id == task.id) &&
                Objects.equals(taskName, task.taskName) &&
                 Objects.equals(taskDescription, task.taskDescription) &&
                    Objects.equals(taskStatus, task.taskStatus);
    }
    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskStatus, id);
    }
}
