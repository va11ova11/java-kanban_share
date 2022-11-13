package models.business;

import models.business.enums.Status;
import models.business.enums.TaskType;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTasksId;

    public Epic(String epicName, String epicDescription) {
        super(epicName, epicDescription);
        subTasksId = new ArrayList<>();
    }

    public Epic(int taskId, TaskType taskType, String taskName, String taskDescription, Status taskStatus) {
        super(taskId, taskType, taskName, taskDescription, taskStatus);
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTaskInEpicList(int subTaskId) {
        subTasksId.add(subTaskId);
    }

    public int getSubTasksSize() {
        return subTasksId.size();
    }

    public void deleteSubTask(int subTaskId) {
        subTasksId.remove((Integer) subTaskId);
    }

    @Override
    public String toString() {
        return id + "," + TaskType.EPIC + "," + taskName + "," + taskStatus + "," + taskDescription;
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
