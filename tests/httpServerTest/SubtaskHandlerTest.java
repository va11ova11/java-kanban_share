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
import models.business.Epic;
import models.business.Subtask;
import models.business.Util.Managers;
import models.business.enums.TaskStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubtaskHandlerTest {

  private static URI url;
  private static HttpClient client;
  private static Gson gson;
  private static HttpTaskServer httpTaskServer;
  private static KVServer kvServer;

  @BeforeAll
  public static void beforeAll() {
    url = URI.create("http://localhost:8080/tasks/subtask/");
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

  private static void createEpic() throws IOException, InterruptedException {
    Gson gsonForEpic = new Gson();
    Epic epic = new Epic("Epic1", "Epic1_desc");
    URI createEpicUri = URI.create("http://localhost:8080/tasks/epic/");
    String json = gsonForEpic.toJson(epic);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(createEpicUri).POST(body).header("Content-Type", "application/json").build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @Test
  public void shouldRequestBodyNotEmptyAndContentTypeEqualsJson() throws IOException, InterruptedException {
    createEpic();
    Subtask subtask1 = new Subtask("First_Subtask", "FirstSubtask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 1);

    String json = gson.toJson(subtask1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();

    assertTrue(request.headers().map().get("Content-Type").contains("application/json"),
        "Передан не верный формат данных");

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertNotNull(response.body(), "Тело запроса пусто");
  }

  @Test
  public void shouldDeleteSubtaskByIdWhenTaskIsExistAndCorrectId() throws IOException, InterruptedException {
    createEpic();
    Subtask subtask1 = new Subtask("First_Subtask", "FirstSubtask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 1);

    String json = gson.toJson(subtask1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    client.send(request1, HttpResponse.BodyHandlers.ofString());

    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
    HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
    HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200, "Задача не удаляется");
  }

  @Test
  public void shouldStatusCode400WhenIncorrectJson() throws IOException, InterruptedException {
    String incorrectJson = "{\"1\":{\"id\":1,\"incorrectTaskType\":\"incorrect\",\"taskName\":\"Create_Task\",\"taskStatus\":\"NEW\",\"taskDescription\":\"CreteTask_description\",\"startTime\":{\"date\":{\"year\":2022,\"month\":1,\"day\":2}}";
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(incorrectJson);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Передан не верный Json");
  }

  @Test
  public void shouldGetAllSubtask() throws IOException, InterruptedException {
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldGetTaskByIdIfSubtaskExistsAndIdIsCorrect() throws IOException, InterruptedException {
    createEpic();
    Subtask subtask1 = new Subtask("First_Subtask", "FirstSubtask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 1);

    String json = gson.toJson(subtask1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    client.send(request1, HttpResponse.BodyHandlers.ofString());

    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
    HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode(), "Задача не возвращается");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenGetSubtaskById()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNullPointerExceptionWhenGetSubtaskWhichIsNot()
      throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/subtask/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }
}
