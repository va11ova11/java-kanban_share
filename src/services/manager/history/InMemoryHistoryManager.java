package services.manager.history;

import models.business.Epic;
import models.business.Task;
import java.util.*;
import models.business.enums.TaskType;

public class InMemoryHistoryManager implements HistoryManager {

  private final CustomLinkedList<Task> customLinkedList;

  public InMemoryHistoryManager() {
    customLinkedList = new CustomLinkedList<>();
  }

  private static class CustomLinkedList<T> {

    private final Map<Integer, Node<T>> historyMap = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;


    private void removeNode(Node<T> node) {
      if (node == head) {
        head = node.next;
        if (node.next != null) {
          node.next.prev = null;
        }
        if (head == tail) {
          head.next = tail;
          tail.prev = head;
        }
      } else if (node == tail) {
        tail = node.prev;
        if (node.next != null) {
          node.next.prev = null;
        }
      } else {
        node.next.prev = node.prev;
        node.prev.next = node.next;
      }
      node.prev = null;
      node.next = null;
      size--;
      historyMap.remove(((Task) node.data).getId());
    }

    private void linkLast(T element) {
      Node<T> oldTail = tail;
      Node<T> newNode = new Node<>(oldTail, element, null);
      tail = newNode;
      if (oldTail == null) {
        head = newNode;
      } else {
        oldTail.next = newNode;
      }
      size++;
      historyMap.put(((Task) element).getId(), newNode);
    }

    private ArrayList<Task> getHistory() {
      if (size > 0) {
        ArrayList<Task> tasksHistory = new ArrayList<>();
        Node<T> tempNode = head;
        for (int i = 0; i < size; i++) {
          tasksHistory.add((Task) tempNode.data);
          tempNode = tempNode.next;
        }
        return tasksHistory;
      } else {
        return null;
      }
    }

    public Map<Integer, Node<T>> getHistoryMap() {
      return historyMap;
    }
  }

  @Override
  public void addTaskInHistory(Task task) {
    if (customLinkedList.historyMap.containsKey(task.getId())) {
      removeInHistory(task.getId());
    }
    customLinkedList.linkLast(task);
  }

  @Override
  public List<Task> getHistory() {
    return customLinkedList.getHistory();
  }

  @Override
  public Map<Integer, Node<Task>> getHistoryMap() {
    return customLinkedList.getHistoryMap();
  }

  @Override
  public void removeInHistory(int id) {
    if (!customLinkedList.historyMap.containsKey(id)) {
      return;
    }
    if (customLinkedList.historyMap.get(id).data.getTaskType() == TaskType.EPIC) {
      Epic epic = (Epic) customLinkedList.historyMap.get(id).data;
      for (Integer subTaskId : epic.getSubTasksId()) {
        if (customLinkedList.historyMap.containsKey(subTaskId)) {
          customLinkedList.removeNode(customLinkedList.historyMap.get(subTaskId));
        }
      }
      customLinkedList.removeNode(customLinkedList.historyMap.get(id));
    } else {
      customLinkedList.removeNode(customLinkedList.historyMap.get(id));
    }
  }
}
