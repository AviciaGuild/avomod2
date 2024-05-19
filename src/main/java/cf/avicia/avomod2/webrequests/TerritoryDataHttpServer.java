package cf.avicia.avomod2.webrequests;

import cf.avicia.avomod2.utils.TerritoryData;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;


public class TerritoryDataHttpServer implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers, we want it accessible from avicia map
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String response = "null";
        Gson gson = new Gson();
        if (TerritoryData.advancementsTerritoryData != null) {
            response = gson.toJson(TerritoryData.advancementsTerritoryData);
        }
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

