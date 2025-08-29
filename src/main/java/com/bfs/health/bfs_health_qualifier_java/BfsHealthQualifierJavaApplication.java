package com.bfs.health.bfshealthqualifierjava;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class BfsHealthQualifierJavaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BfsHealthQualifierJavaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate rest = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // Step 1: Call generateWebhook API
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        String body = "{ \"name\": \"Bhagawan Hemitha\", \"regNo\": \"22BCE9618\", \"email\": \"hemitha.22bce9618@vitapstudent.ac.in\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rest.postForEntity(url, request, String.class);
        JsonNode json = mapper.readTree(response.getBody());

        String webhook = json.get("webhook").asText();
        String accessToken = json.get("accessToken").asText();

        System.out.println("Webhook: " + webhook);
        System.out.println("Token: " + accessToken);

        // Step 2: Your SQL query (Question 2 solution)
        String finalQuery = "SELECT e.emp_id, e.first_name, e.last_name, d.department_name, " +
                "COUNT(y.emp_id) AS younger_employees_count " +
                "FROM employee e " +
                "JOIN department d ON e.department = d.department_id " +
                "LEFT JOIN employee y ON e.department = y.department " +
                "AND y.dob > e.dob " +
                "GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name " +
                "ORDER BY e.emp_id DESC;";

        // Step 3: Submit query to webhook
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.setBearerAuth(accessToken);

        String submitBody = "{ \"finalQuery\": \"" + finalQuery.replace("\"", "\\\"") + "\" }";
        HttpEntity<String> request2 = new HttpEntity<>(submitBody, headers2);

        ResponseEntity<String> submitResponse = rest.postForEntity(webhook, request2, String.class);
        System.out.println("Submit Response: " + submitResponse.getBody());
    }
}
