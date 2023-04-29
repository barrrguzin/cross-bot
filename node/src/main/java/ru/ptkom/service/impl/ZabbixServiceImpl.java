package ru.ptkom.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ptkom.service.ZabbixService;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Service
public class ZabbixServiceImpl implements ZabbixService {
    private static final Logger log = Logger.getLogger(ZabbixServiceImpl.class);


    @Value("${zabbix.api.url}")
    private String zabbixUrl;
    @Value("${zabbix.api.login}")
    private String zabbixLogin;
    @Value("${zabbix.api.password}")
    private String zabbixPassword;

    private static final String API_URL_SUFFIX = "/api_jsonrpc.php";

    private static final String AUTHENTICATE_REQUEST_BODY = "{\"jsonrpc\":\"2.0\"," +
            "\"method\":\"user.login\"," +
            "\"params\":{\"user\":\"%s\",\"password\":\"%s\"}," +
            "\"id\":1}";

    private static final String HOST_OUTAGES_STATISTIC_REQUEST_BODY = "{" +
            "\"jsonrpc\": \"2.0\"," +
            "\"method\": \"problem.get\"," +
            "\"params\": {" +
            "\"output\": \"extend\"," +
            "\"time_from\": \"%s\"," +
            "\"time_till\": \"%s\"," +
            "\"countOutput\": true," +
            "\"selectHosts\": \"extend\"," +
            "\"host\": \"%s\"" +
            "}," +
            "\"auth\": \"%s\"," +
            "\"id\": 1" +
            "}";


    private static final String LIST_OF_HOST_GRAPHS_REQUEST_BODY = "{" +
            "\"jsonrpc\": \"2.0\"," +
            "\"method\": \"graph.get\"," +
            " \"params\": {" +
            "\"output\": \"extend\"," +
            "\"hostids\": \"%s\"" +
            "}," +
            "\"auth\": \"%s\"," +
            "\"id\": 1" +
            "}";

    private static final String GET_FULL_EVENT_DATA_REQUEST_BODY = "{" +
            "\"jsonrpc\": \"2.0\"," +
            "\"method\": \"event.get\"," +
            " \"params\": {" +
            "\"output\": \"extend\"," +
            "\"eventids\": \"%s\"" +
            "}," +
            "\"auth\": \"%s\"," +
            "\"id\": 1" +
            "}";

    private static String TOKEN;


    private static final HttpHeaders defaultZabbixHeaders;
    private static String zabbixApiUrl;

    private final ObjectMapper mapper = new ObjectMapper();




    static {
        defaultZabbixHeaders = new HttpHeaders();
        defaultZabbixHeaders.set("Content-Type","application/json-rpc");
    }






    private final RestTemplate restTemplate;


    public ZabbixServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public String getUnavailableHosts() {

        return null;
    }

    @Override
    public String getHostOutagesStatisticById(String hostId) {
        long current_time = Instant.now().getEpochSecond();
        long start_time = current_time - 2592000;

        String getHostOutagesStatisticRequestBody = String.format(HOST_OUTAGES_STATISTIC_REQUEST_BODY, start_time, current_time, hostId, TOKEN);

        HttpEntity<String> request = new HttpEntity(getHostOutagesStatisticRequestBody, defaultZabbixHeaders);
        ResponseEntity<String> response = restTemplate.exchange(zabbixApiUrl, HttpMethod.POST, request, String.class);
        String responseBody = response.getBody();

        checkAuthAndReAuth(responseBody);

        return responseBody;
    }

    @Override
    public String getListOfHostGraphsById(String hostId) {

        String getHostListOfGraphsRequestBody = String.format(LIST_OF_HOST_GRAPHS_REQUEST_BODY, hostId, TOKEN);

        HttpEntity<String> request = new HttpEntity(getHostListOfGraphsRequestBody, defaultZabbixHeaders);
        ResponseEntity<String> response = restTemplate.exchange(zabbixApiUrl, HttpMethod.POST, request, String.class);
        String responseBody = response.getBody();

        checkAuthAndReAuth(responseBody);

        return responseBody;
    }

    @Override
    public String getFullAlertInfo(String eventId) {

        String getHostListOfGraphsRequestBody = String.format(GET_FULL_EVENT_DATA_REQUEST_BODY, eventId, TOKEN);

        HttpEntity<String> request = new HttpEntity(getHostListOfGraphsRequestBody, defaultZabbixHeaders);
        ResponseEntity<String> response = restTemplate.exchange(zabbixApiUrl, HttpMethod.POST, request, String.class);
        String responseBody = response.getBody();

        checkAuthAndReAuth(responseBody);

        return responseBody;
    }

    @Override
    public byte[] getGraphById(String graphId) {
        String url = zabbixUrl + "/chart2.php?graphid=" + graphId + "&period=7200";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "image/avif,image/webp,*/*\\r\\n");
        headers.add("Cookie", "zbx_sessionid=" + TOKEN);
        HttpEntity request = new HttpEntity(null, headers);


        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);

        return response.getBody();
    }


    private void authenticate() {

        String authenticateRequestBody = String.format(AUTHENTICATE_REQUEST_BODY, zabbixLogin, zabbixPassword);

        HttpEntity<String> request = new HttpEntity(authenticateRequestBody, defaultZabbixHeaders);
        ResponseEntity<String> response = restTemplate.exchange(zabbixApiUrl, HttpMethod.POST, request, String.class);

        try {

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode result = root.get("result");

            if (result == null) {
                JsonNode error = root.get("error");
                String errorMessage = error.get("message").asText();
                String dataMessage = error.get("data").asText();
                log.debug("Unable to authenticate to Zabbix. Error: " + errorMessage + "; Data: " + dataMessage);

            } else {
                String resultString = result.asText();
                TOKEN = resultString;
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to authenticate in Zabbix server: " + response.getBody());
        }
    }


    private void checkAuthAndReAuth(String responseBody) {

        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode error = root.get("error");
            if (error != null) {
                authenticate();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @PostConstruct
    private void getStart(){
        zabbixApiUrl = zabbixUrl + API_URL_SUFFIX;
        authenticate();
    }
}
