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
    private Node<T> first;
    private Node<T> last;
    private int size = 0;


    private void removeFirst(Node<T> node) {
      final T element = node.data;
      final Node<T> next = node.next;
      node.data = null;
      node.next = null;
      first = next;
      if (next == null) {
        last = null;
      } else {
        next.prev = null;
      }
      size--;
      historyMap.remove(((Task) element).getId());
    }

    private void removeLast(Node<T> node) {
      final T element = node.data;
      final Node<T> prev = node.prev;
      node.data = null;
      node.prev = null;
      last = prev;
      if (prev == null) {
        first = null;
      } else {
        prev.next = null;
      }
      size--;
      historyMap.remove(((Task) element).getId());
    }

    private void unlink(Node<T> node) {
      final T element = node.data;
      final Node<T> next = node.next;
      final Node<T> prev = node.prev;

      if (prev == null) {
        first = next;
      } else {
        prev.next = next;
        node.prev = null;
      }

      if (next == null) {
        last = prev;
      } else {
        next.prev = prev;
        node.next = null;
      }
      size--;
      historyMap.remove(((Task) element).getId());
    }

    private void removeNode(Node<T> node) {
      if (node == first) {
        removeFirst(node);
      } else if (node == last) {
        removeLast(node);
      } else {
        unlink(node);
      }
    }

    private void linkLast(T element) {
      Node<T> oldTail = last;
      Node<T> newNode = new Node<>(oldTail, element, null);
      last = newNode;
      if (oldTail == null) {
        first = newNode;
      } else {
        oldTail.next = newNode;
      }
      size++;
      historyMap.put(((Task) element).getId(), newNode);
    }

    private ArrayList<Task> getHistory() {
      if (size > 0) {
        ArrayList<Task> tasksHistory = new ArrayList<>();
        Node<T> tempNode = first;
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
    if (customLinkedList.historyMap.get(id).data.getType() == TaskType.EPIC) {
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
