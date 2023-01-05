package i5.las2peer.services.sensorProxyService.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.services.sensorProxyService.pojo.lrs.NewClient;
import i5.las2peer.services.sensorProxyService.pojo.sensor.SensorData;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class LRSWriter {
    private final String lrsDomain = System.getenv("LRS_DOMAIN");
    private final String lrsAuthAdmin = System.getenv("LRS_ADMIN_AUTH");
    private final String lrsClientId = System.getenv("LRS_ADMIN_CLIENT_ID");

    private final static String statementsEndpoint = "/data/xAPI/statements";
    private final static String clientEndpoint = "/api/v2/client/";

    private final L2pLogger logger = L2pLogger.getInstance(LRSWriter.class.getName());
    private final StatementGenerator generator = new StatementGenerator();

    public void write(SensorData sensorData, String userMail) throws IOException {
        // create xAPI statement
        org.json.JSONObject statement = generator.createStatementFromAppData(sensorData, userMail);

        // Checks if the client exists
        Object clientId = searchIfIncomingClientExists(userMail);

        String lrsAuth = "";
        if (!(clientId).equals("newClient")) {
            String clientKey = (String) ((JSONObject) clientId).get("basic_key");
            String clientSecret = (String) ((JSONObject) clientId).get("basic_secret");
            lrsAuth = Base64.getEncoder().encodeToString((clientKey + ":" + clientSecret).getBytes());
        } else {
            String storeID = getStoreIdOfAdmin();
            Object newlyCreatedClient = createNewClient(userMail, storeID);
            if (newlyCreatedClient == "") {
                logger.severe("Store ID does not exist: " + storeID);
                return;
            }
            String clientKey = (String) ((JSONObject) newlyCreatedClient).get("basic_key");
            String clientSecret = (String) ((JSONObject) newlyCreatedClient).get("basic_secret");
            lrsAuth = Base64.getEncoder().encodeToString((clientKey + ":" + clientSecret).getBytes());
        }

        byte[] payload = statement.toString().getBytes(StandardCharsets.UTF_8);
        Object obj = doRequest("POST", lrsDomain + statementsEndpoint, "Basic " + lrsAuth, payload);

        logger.info("Wrote LRS entry: " + obj);
    }

    private String getStoreIdOfAdmin() throws IOException {
        Object obj = doRequest("GET",lrsDomain +  clientEndpoint + lrsClientId, lrsAuthAdmin, null);
        return (obj != null) ? (String) ((JSONObject) obj).get("lrs_id") : "";
    }

    private Object searchIfIncomingClientExists(String token) throws IOException {
        Object obj = doRequest("GET",lrsDomain + clientEndpoint, lrsAuthAdmin, null);

        for (int i = 0; i < ((JSONArray) obj).size(); i++) {
            JSONObject client = (JSONObject) ((JSONArray) obj).get(i);
            Object title = client.get("title");
            if (title != null && title.equals(token)) {
                return client.get("api");
            }
        }
        return "newClient";
    }

    private Object createNewClient(String token, String storeId) throws IOException {
        List<String> scopes = new ArrayList<>();
        scopes.add("statements/read/mine");
        scopes.add("statements/write");

        NewClient newClient = new NewClient(token, storeId, token, scopes);
        ObjectMapper mapper = new ObjectMapper();

        byte[] payload = mapper.writeValueAsString(newClient).getBytes(StandardCharsets.UTF_8);

        Object obj = doRequest("POST", lrsDomain + clientEndpoint, lrsAuthAdmin, payload);
        return ((JSONObject) obj).get("api");
    }

    private Object doRequest(String method, String uri, String auth, byte[] payload) throws IOException {
        URL url = new URL(uri);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("X-Experience-API-Version", "1.0.3");
        conn.setRequestProperty("Authorization", auth);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setUseCaches(false);

        if (payload != null && payload.length > 0) {
            OutputStream os = conn.getOutputStream();
            os.write(payload);
            os.flush();
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

        String line;
        StringBuilder response = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }

        conn.disconnect();
        return JSONValue.parse(response.toString());
    }
}
