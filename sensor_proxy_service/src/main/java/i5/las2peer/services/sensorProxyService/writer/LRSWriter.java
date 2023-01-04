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

        URL url = new URL(lrsDomain + statementsEndpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("X-Experience-API-Version", "1.0.3");
        conn.setRequestProperty("Authorization", "Basic " + lrsAuth);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setUseCaches(false);

        OutputStream os = conn.getOutputStream();
        os.write(statement.toString().getBytes(StandardCharsets.UTF_8));
        os.flush();

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        logger.info("Wrote LRS entry: " + response);
        conn.disconnect();
    }

    private String getStoreIdOfAdmin() throws IOException {
        URL url = new URL(lrsDomain + clientEndpoint + lrsClientId);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("X-Experience-API-Version", "1.0.3");
        conn.setRequestProperty("Authorization", lrsAuthAdmin);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setUseCaches(false);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line = "";
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        Object obj = JSONValue.parse(response.toString());
        String storeId = (obj != null) ? (String) ((JSONObject) obj).get("lrs_id") : "";

        return storeId;
    }

    private Object searchIfIncomingClientExists(String moodleToken) throws IOException {
        URL url = new URL(lrsDomain + clientEndpoint);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("X-Experience-API-Version", "1.0.3");
        conn.setRequestProperty("Authorization", lrsAuthAdmin);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setUseCaches(false);

        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        Object obj = JSONValue.parse(response.toString());

        for (int i = 0; i < ((JSONArray) obj).size(); i++) {
            JSONObject client = (JSONObject) ((JSONArray) obj).get(i);
            Object title = client.get("title");
            if (title != null && title.equals(moodleToken)) {
                return client.get("api");
            }
        }
        return "newClient";
    }

    private Object createNewClient(String token, String storeId) throws IOException {
        URL url = new URL(lrsDomain + clientEndpoint);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("X-Experience-API-Version", "1.0.3");
        conn.setRequestProperty("Authorization", lrsAuthAdmin);
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setUseCaches(false);

        List<String> scopes = new ArrayList<>();
        scopes.add("statements/read/mine");
        scopes.add("statements/write");

        NewClient newClient = new NewClient(token, storeId, token, scopes);
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = mapper.writeValueAsString(newClient);

        OutputStream os = conn.getOutputStream();
        os.write(jsonString.getBytes(StandardCharsets.UTF_8));
        os.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();

        String line = "";
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        Object obj = JSONValue.parse(response.toString());
        return ((JSONObject) obj).get("api");
    }
}
