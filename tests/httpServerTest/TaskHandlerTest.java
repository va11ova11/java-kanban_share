package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import httpServer.HttpTaskServer;
import httpServer.Util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import kvServer.KVServer;
import models.business.Task;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.manager.HttpTaskManager;

public class TaskHandlerTest {

  private static URI url;
  private static HttpClient client;
  private static Gson gson;
  private static KVServer kvServer;
  private static HttpTaskServer httpTaskServer;

  @BeforeAll
  public static void beforeAll() {
    url = URI.create("http://localhost:8080/tasks/task/");
    client = HttpClient.newHttpClient();
    gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
  }

  @BeforeEach
  public void beforeEach() throws IOException, InterruptedException {
    kvServer = Managers.getDefaultKVServer();
    kvServer.start();
    httpTaskServer = new HttpTaskServer();
    httpTaskServer.start();
  }

  @AfterEach
  public void afterEach() {
    kvServer.stop();
    httpTaskServer.stop();
  }

  @Test
  public void shouldRequestBodyNotEmptyAndContentTypeEqualsJson() {
    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60);

    String json = gson.toJson(task1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();

    assertTrue(request.headers().map().get("Content-Type").contains("application/json"),
        "Передан не верный формат данных");

    assertNotNull(body, "Тело запроса пусто");
  }


  @Test
  public void shouldStatusCode400WhenIncorrectJson() throws IOException, InterruptedException {
    String incorrectJson = "{\"1\":{\"id\":1,\"incorrectTaskType\":\"incorrect\",\"taskName\":\"Create_Task\",\"taskStatus\":\"NEW\",\"taskDescription\":\"CreteTask_description\",\"startTime\":{\"date\":{\"year\":2022,\"month\":1,\"day\":2}}";
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(incorrectJson);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Передан не верный Json");
  }

  private void createTwoTask() throws IOException, InterruptedException {
    Task task1 = new Task("First_Task", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60);
    Task task2 = new Task("SameTimeTask", "FirstTask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 15, 10, 0), 60);

    String taskOne = gson.toJson(task1);
    String taskTwo = gson.toJson(task2);

    final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(taskOne);
    final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(taskTwo);
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).header("Content-Type", "application/json").build();
    HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).header("Content-Type", "application/json").build();
    client.send(request1, HttpResponse.BodyHandlers.ofString());
    client.send(request2, HttpResponse.BodyHandlers.ofString());
  }


  @Test
  public void shouldGetAllTask() throws IOException, InterruptedException {
    createTwoTask();

    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldGetTaskByIdIfTaskExistsAndIdIsCorrect() throws IOException, InterruptedException {
    createTwoTask();

    URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response2.statusCode(), 200, "Задача не возвращается");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenGetTaskById()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNullPointerExceptionWhenGetTaskWhichIsNot()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldDeleteTasks() throws IOException, InterruptedException {
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не удаляются");
  }

  @Test
  public void shouldDeleteTaskByIdWhenTaskIsExistAndCorrectId() throws IOException, InterruptedException {
    createTwoTask();
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
    HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200, "Задача не удаляется");
  }

  @Test
  public void shouldNullPointerExceptionWhenDeleteTaskWhichIsNot()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenDeleteTaskById()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }
}
