package models.business;

import static models.business.Util.Constants.formatter;
import java.time.LocalDateTime;
import java.util.Objects;
import models.business.enums.TaskStatus;
import models.business.enums.TaskType;

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
    public Task(String taskName, String taskDescription, TaskStatus taskStatus,
        LocalDateTime startTime, long duration) {
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
        TaskStatus taskStatus, LocalDateTime startTime, long duration) {
        this.id = id;
        this.taskType = taskType;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task (int id, TaskType taskType, String taskName, String taskDescription,
        TaskStatus taskStatus) {
        this.id = id;
        this.taskType = taskType;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        if(startTime == null) {
            this.startTime = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime, formatter);
        }
    }

    public void setStartTime(LocalDateTime startTimee) {
        if(startTime == null) {
        }
        this.startTime = LocalDateTime.from(startTimee);
    }


    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TaskType getType(){
        return taskType;
    }

    public String getName() {
        return taskName;
    }

    public void setName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return taskDescription;
    }

    public void setDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
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
        if (startTime == null) {
            return id + "," + TaskType.TASK + "," + taskName + "," + taskStatus + ","
                + taskDescription;
        }
        return id + "," + TaskType.TASK + "," + taskName + "," + taskStatus + "," + taskDescription + ","
            + startTime.format(formatter) + "," + getEndTime().format(formatter);
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
