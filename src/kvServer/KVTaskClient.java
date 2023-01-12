package kvServer;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

  private final HttpClient client;
  private final URL url;
  private final String token;

  public KVTaskClient(URL url) throws IOException, InterruptedException {
    this.client = HttpClient.newHttpClient();
    this.url = url;
    token = register();
  }

  private String register() throws IOException, InterruptedException {
    URI registerURI = URI.create(url.toString() + "/register");
    HttpRequest request = HttpRequest.newBuilder().uri(registerURI).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public void put(String key, String value) throws IOException, InterruptedException {
    URI saveURI = URI.create(url.toString() + "/save/" + key + "?API_TOKEN=" + token);
    final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
    HttpRequest request = HttpRequest.newBuilder().uri(saveURI).POST(body)
        .header("Content-Type", "application/json").build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public String load(String key) throws IOException, InterruptedException {
    URI saveURI = URI.create(url.toString() + "/load/" + key + "?API_TOKEN=" + token);
    HttpRequest request = HttpRequest.newBuilder().uri(saveURI).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }
}
