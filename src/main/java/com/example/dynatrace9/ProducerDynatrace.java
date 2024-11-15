package com.example.dynatrace9;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

//import com.macys.qod.dynatrace.utils.Constant;
//import com.macys.qod.utils.bigQuery.GoogleBigQueryUtils;
//import org.springframework.kafka.core.KafkaTemplate;


@Service
public class ProducerDynatrace {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;

//    @Autowired
//    private GoogleBigQueryUtils googleBigQueryUtils;

    public void fetchAndProcessData() throws Exception {
        System.out.println("Hello World");
        processData("host_data", Constant.ENTITY_SERVICE_URL, Constant.HOST_PARAMS);
//        processData("host_tags", Constant.ENTITY_SERVICE_URL, Constant.HOST_TAGS_PARAMS);
//        processData("host_to", Constant.ENTITY_SERVICE_URL, Constant.HOST_TO_PARAMS);
//        processData("kubernetes_data", Constant.ENTITY_SERVICE_URL, Constant.KUBERNETES_DATA_PARAMS);
//        processData("kubernetes_data_tags", Constant.ENTITY_SERVICE_URL, Constant.KUBERNETES_DATA_TAGS_PARAMS);
//        processData("kubernetes_data_from", Constant.ENTITY_SERVICE_URL, Constant.KUBERNETES_DATA_FROM_PARAMS); // need to format
//        processData("kubernetes_data_to", Constant.ENTITY_SERVICE_URL, Constant.KUBERNETES_DATA_TO_PARAMS);
//        processData("kubernetes_workload", Constant.METRICS_URL, Constant.KUBERNETES_WORKLOAD_PARAMS); // need to format
//        processData("nodes_data", Constant.ENTITY_SERVICE_URL, Constant.NODES_PARAMS);
//
//        processData("pods_data", Constant.METRICS_URL, Constant.PODS_PARAMS); //need to format
//        processData("problems_data", Constant.PROBLEMS_URL, Constant.PROBLEMS_PARAMS);
//        processData("request_count", Constant.METRICS_URL, Constant.REQUEST_COUNT_PARAMS); // need to format
//        processData("response_time", Constant.METRICS_URL, Constant.RESPONSE_TIME_PARAMS); //need to format
//        processData("service_data", Constant.ENTITY_SERVICE_URL, Constant.SERVICE_DATA_PARAMS);
//        processData("service_tags", Constant.ENTITY_SERVICE_URL, Constant.SERVICE_TAGS_PARAMS);
//        processData("service_from", Constant.ENTITY_SERVICE_URL, Constant.SERVICE_FROM_PARAMS);
//        processData("service_to", Constant.ENTITY_SERVICE_URL, Constant.SERVICE_TO_PARAMS);
//        processData("slo_data", Constant.METRICS_URL, Constant.SLO_PARAMS); // need to format

    }

    private void processData(String tableType, String apiUrl, Map<String, String> params) throws Exception {
        System.out.println("Hello india");
        List<JsonNode> data = fetchData(apiUrl, params);
//        System.out.println(data);

        if(tableType == "kubernetes_workload" || tableType == "pods_data" || tableType == "request_count" || tableType == "response_time" || tableType == "slo_data" )
        {
            List<JsonNode> dataList = new ArrayList<>();
            for (JsonNode node : data) {
                JsonNode dataNode = node.get("data");
                if (dataNode != null && dataNode.isArray()) {
                    dataNode.forEach(dataList::add);
                }
            }
            data.clear();
            data.addAll(dataList);
        }
//        if (tableType == "pods_data")
//        {
//            List<JsonNode> dataList = new ArrayList<>();
//            for (JsonNode node : data) {
//                JsonNode dataNode = node.get("data");
//                if (dataNode != null && dataNode.isArray()) {
//                    dataNode.forEach(dataList::add);
//                }
//            }
//            data.clear();
//            data.addAll(dataList);
//        }

        for (JsonNode item : data) {
            Map<String, Object> record = new HashMap<>();
            record.put("table", tableType);
            record.put("json_data",item);
//            record.put("json_data", objectMapper.writeValueAsString(item));

            System.out.println(record);

//            googleBigQueryUtils.writeToBigQuery(Constant.PROJECT_ID, Constant.DATASET_ID, Constant.TABLE_ID, record);
//            kafkaTemplate.send(Constant.TOPIC_NAME, objectMapper.writeValueAsString(record));
        }
    }

    private String buildQueryParams(Map<String, String> params) {
        StringBuilder queryParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryParams.length() > 0) {
                queryParams.append("&");
            }
            queryParams.append(entry.getKey()).append("=");
            queryParams.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return queryParams.toString();
    }

    private List<JsonNode> fetchData(String apiUrl, Map<String, String> params) throws Exception {
        List<JsonNode> allData = new ArrayList<>();
        boolean isFirstRequest = true; // Flag to check if it's the first API call
        Map<String, String> pagingParams = new HashMap<>();
        int ct=1;

        while (true) {
            String requestUrl;

            // For the first request, build URL with all parameters, otherwise use only nextPageKey
            System.out.println("check loops " + ct);
            if (isFirstRequest == true) {
                requestUrl = apiUrl + "?" + buildQueryParams(params);
                isFirstRequest = false; // Set flag to false after the first request

            } else {
                requestUrl = apiUrl + "?nextPageKey=" + pagingParams.get("nextPageKey");
                System.out.println(" in next loop ");
            }

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(requestUrl))
                        .header("Authorization", "Api-Token " + Constant.DYNATRACE_API_TOKEN)
                        .GET()
                        .build();

                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                ct=ct+1;
                System.out.println(response.statusCode());

                if (response.statusCode() == 200) {
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    JsonNode dataNode = null;

                    // Check for "entities", "result", or "problems" in the response
                    if (rootNode.has("entities")) {
                        dataNode = rootNode.get("entities");
                    } else if (rootNode.has("result")) {
                        dataNode = rootNode.get("result");
                    } else if (rootNode.has("problems")) {
                        dataNode = rootNode.get("problems");
                    }

                    // Add the data to allData list
                    if (dataNode != null) {
                        if (dataNode.isArray()) {
                            dataNode.forEach(allData::add);  // Add each item if dataNode is an array
                        } else {
                            allData.add(dataNode);  // Add single item if dataNode is not an array
                        }
                    }

                    // Check for the nextPageKey to see if there is another page
                    if (rootNode.has("nextPageKey")) {
                        String nextPageKey = rootNode.get("nextPageKey").asText();
                        pagingParams.clear();
                        pagingParams.put("nextPageKey", nextPageKey); // Update params with nextPageKey for the next request
                    } else {
                        break;  // Exit loop if no nextPageKey is found
                    }
                } else {
                    System.out.println("Non-200 response received. Status Code: " + response.statusCode());
                    System.out.println("Response body: " + response.body());
                    break;
                }
            } catch (Exception e) {

                System.out.println("Error occurred during API call to: " + e + isFirstRequest + ct+ requestUrl);
                e.printStackTrace();
                break;
            }
        }

        return allData;
    }


//    private List<JsonNode> fetchData(String apiUrl, Map<String, String> params) throws Exception {
//        List<JsonNode> allData = new ArrayList<>();
//        String fullUrl = apiUrl;
//
//        while (true) {
//            // Build URL with the updated parameters for each request
//            String requestUrl = fullUrl + "?" + buildQueryParams(params);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(requestUrl))
//                    .header("Authorization", "Api-Token " + Constant.DYNATRACE_API_TOKEN)
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JsonNode rootNode = objectMapper.readTree(response.body());
//                JsonNode dataNode = null;
//
//                // Check for "entities", "result", or "problems" in the response
//                if (rootNode.has("entities")) {
//                    dataNode = rootNode.get("entities");
//                } else if (rootNode.has("result")) {
//                    dataNode = rootNode.get("result");
//                } else if (rootNode.has("problems")) {
//                    dataNode = rootNode.get("problems");
//                }
//
////                System.out.println(dataNode);
//
//                // Add the data to allData list
//                if (dataNode != null) {
//                    if (dataNode.isArray()) {
//                        dataNode.forEach(allData::add);  // Add each item if dataNode is an array
//                    } else {
//                        allData.add(dataNode);  // Add single item if dataNode is not an array
//                    }
//                }
//
////                System.out.println(rootNode.get("nextPageKey").asText());
//
//                // Check for the nextPageKey to see if there is another page
//                if (rootNode.has("nextPageKey")) {
//                    String nextPageKey = rootNode.get("nextPageKey").asText();
//                    params.put("nextPageKey", nextPageKey);  // Update params with nextPageKey for the next request
//                } else {
//                    break;  // Exit loop if no nextPageKey is found
//                }
//            } else {
//                System.out.println("Error: " + response.statusCode());
//                break;
//            }
//        }
//
//        return allData;
//    }



//    private List<JsonNode> fetchData(String apiUrl, Map<String, String> params) throws Exception {
//        String fullUrl = apiUrl + "?" + buildQueryParams(params);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(fullUrl))
//                .header("Authorization", "Api-Token " + Constant.DYNATRACE_API_TOKEN)
//                .GET()
//                .build();
//
//        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
////        System.out.println("API Response: " + response.body());
//
//        JsonNode rootNode = objectMapper.readTree(response.body());
//        JsonNode dataNode = null;
//
//        if (rootNode.has("entities")) {
//            System.out.println("entities");
//            dataNode = rootNode.get("entities");
//        } else if (rootNode.has("result")) {
//            System.out.println("result");
//            dataNode = rootNode.get("result");
//        } else if (rootNode.has("problems")) {
//            System.out.println("problems");
//            dataNode = rootNode.get("problems");
//        } else {
//            System.out.println("No recognized root key found in the response.");
//            return List.of(); // Return an empty list or handle accordingly
//        }
////        System.out.println(dataNode);
//        List<JsonNode> data = new ArrayList<>();
//        if (dataNode.isArray()) {
//            dataNode.forEach(data::add);
//        } else {
//            data.add(dataNode);
//        }
//
//        return data;
//
//    }
}
