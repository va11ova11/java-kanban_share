import httpServer.HttpTaskServer;
import java.io.IOException;
import kvServer.KVServer;
import models.business.Util.Managers;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
      KVServer server = Managers.getDefaultKVServer();
      server.start();


      HttpTaskServer httpTaskServer = new HttpTaskServer();
      httpTaskServer.start();
    }
}
