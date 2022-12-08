package models.business;

import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

import java.text.DateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected TaskType taskType;
    protected String taskName;
    protected TaskStatus taskStatus;
    protected String taskDescription;
    protected LocalDateTime startTime;
    protected long duration;

    public LocalDateTime getEndTime() {
        try {
            return LocalDateTime.from(startTime).plusMinutes(duration);
        } catch (NullPointerException ex) {
            throw new RuntimeException("Время начала выполнения задачи не указано");
        }
    }

    public Task(String taskName, String taskDescription, TaskStatus taskStatus, LocalDateTime startTime, long duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }
    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task (int id, TaskType taskType, String taskName, String taskDescription,
        TaskStatus taskStatus) {
        this.id = id;
        this.taskType = taskType;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
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
