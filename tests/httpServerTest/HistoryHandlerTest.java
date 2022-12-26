package httpServerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HistoryHandlerTest {

  private static HttpClient client;

  @BeforeAll
  public static void beforeAll() {
    client = HttpClient.newHttpClient();
  }

  @Test
  public void shouldStatusCode200WhenHistoryIsNotEmpty() throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks/history/");
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }

  @Test
  public void shouldStatusCode200WhenGetPrioritizedTaskIsNotEmpty()
      throws IOException, InterruptedException {
    URI url = URI.create("http://localhost:8080/tasks");
    HttpRequest request1 = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
    assertEquals(response.statusCode(), 200, "Задачи не возвращаются");
  }
}
