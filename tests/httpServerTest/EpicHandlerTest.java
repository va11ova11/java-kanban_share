package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import httpServer.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpServer.Util.LocalDateTimeAdapter;
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

public class EpicHandlerTest {

  private static URI url;
  private static HttpClient client;
  private static HttpTaskServer httpTaskServer;
  private static KVServer kvServer;
  private static Gson gsonCreateEpic;
  private static Gson gsonForSubtask;

  @BeforeAll
  public static void beforeAll() throws IOException, InterruptedException {
    url = URI.create("http://localhost:8080/tasks/epic/");
    client = HttpClient.newHttpClient();
    gsonCreateEpic = new GsonBuilder()
        .create();
    gsonForSubtask = new GsonBuilder()
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
  public void afterAll() {
    kvServer.stop();
    httpTaskServer.stop();
  }

  @Test
  public void shouldRequestBodyNotEmptyAndContentTypeEqualsJson() throws IOException, InterruptedException {
    Epic epic1 = new Epic("Epic", "Epic_desc");

    String json = gsonCreateEpic.toJson(epic1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();

    assertTrue(request.headers().map().get("Content-Type").contains("application/json"),
        "Передан не верный формат данных");

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertNotNull(response.body(), "Тело запроса пусто");
  }

  @Test
  public void shouldDeleteEpicByIdWhenTaskIsExistAndCorrectId() throws IOException, InterruptedException {
    createEpic();
    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
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
  public void shouldGetAllEpic() throws IOException, InterruptedException {
    createEpic();
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
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
  private static void createSubtask() throws IOException, InterruptedException {
    Subtask subtask1 = new Subtask("First_Subtask", "FirstSubtask_description", TaskStatus.NEW,
        LocalDateTime.of(2022, 12, 14, 10, 0), 60, 1);

    String json = gsonForSubtask.toJson(subtask1);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
    HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).header("Content-Type", "application/json").build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
  }
  @Test
  public void shouldGetEpicByIdIfEpicExistsAndIdIsCorrect() throws IOException, InterruptedException {
    createEpic();
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 200, "Задача не возвращается");
  }

  @Test
  public void shouldNumberFormatExceptionWhenIncorrectIdWhenGetEpicById()
      throws IOException, InterruptedException {
    createEpic();
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 400, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldNullPointerExceptionWhenGetEpicWhichIsNot()
      throws IOException, InterruptedException {
    createEpic();
    URI url = URI.create("http://localhost:8080/tasks/epic/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(response.statusCode(), 404, "Обрабатывается задача с некорректным Id");
  }

  @Test
  public void shouldGetSubtaskInEpicIfIdIsCorrectAndEpicIsExist()
      throws IOException, InterruptedException {
    createEpic();
    createSubtask();
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Не возвращаются подзадачи Эпика");
  }

  @Test
  public void shouldStatusCode404WhenGetSubtaskEpicWhenEpicIsNot()
      throws IOException, InterruptedException {
    createEpic();
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=100");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 404, "Такого Эпика не существует");
  }

  @Test
  public void shouldStatusCode400WhenIncorrectIdWhenGetSubtasksInEpic()
      throws IOException, InterruptedException {
    createEpic();
    createSubtask();
    URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=incorrect");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 400, "Такого Эпика не существует");
  }
}
