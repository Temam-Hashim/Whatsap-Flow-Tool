package com.temx.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temx.workflow.FlowData;
import com.temx.workflow.service.EncryptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/flow")
@AllArgsConstructor
public class FlowController {

    private EncryptionService encryptionService;

    @GetMapping("/public-key")
    public String getPublicKey() {
        return encryptionService.getPublicKey();
    }

    @PostMapping("/generate-keys")
    public void generateKeys() throws Exception {
        encryptionService.generateKeys();
    }

    @PostMapping("/decrypt-request")
    public ResponseEntity<?> decryptRequest(@RequestBody Map<String, String> encryptedRequest) {
        try {
            if (encryptedRequest == null || !encryptedRequest.containsKey("encryptedData")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Missing or invalid encrypted data"));
            }

            String encryptedData = encryptedRequest.get("encryptedData");
            if (encryptedData == null || encryptedData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Encrypted data is empty"));
            }

            String decryptedBody = encryptionService.decrypt(encryptedData);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = objectMapper.readValue(decryptedBody, new TypeReference<Map<String, Object>>() {});

            return ResponseEntity.ok(getNextScreen(requestBody));
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid encrypted data"));
        }
    }


    @PostMapping("/encrypt-response")
    public ResponseEntity<Map<String, String>> encryptResponse(@RequestBody Map<String, Object> responseData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(responseData);
        String encryptedResponse = encryptionService.encrypt(responseJson);

        Map<String, String> encryptedResponseBody = new HashMap<>();
        encryptedResponseBody.put("encryptedData", encryptedResponse);
        return ResponseEntity.ok(encryptedResponseBody);
    }

    private Map<String, Object> getNextScreen(Map<String, Object> decryptedBody) {
        String action = (String) decryptedBody.get("action");
        String screen = (String) decryptedBody.get("screen");
        Map<String, Object> data = (Map<String, Object>) decryptedBody.get("data");

        if ("INIT".equalsIgnoreCase(action)) {
            return new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("LOAN"));
        }

        if ("data_exchange".equalsIgnoreCase(action)) {
            if ("LOAN".equalsIgnoreCase(screen)) {
                return handleLoanScreen(data);
            }
            if ("DETAILS".equalsIgnoreCase(screen)) {
                return handleDetailsScreen(data);
            }
            if ("SUMMARY".equalsIgnoreCase(screen)) {
                return handleSummaryScreen(data);
            }
            if ("COMPLETE".equalsIgnoreCase(screen)) {
                return new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("COMPLETE"));
            }
            if ("SUCCESS".equalsIgnoreCase(screen)) {
                return new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("SUCCESS"));
            }
        }

        return Collections.emptyMap();
    }

    private Map<String, Object> handleLoanScreen(Map<String, Object> data) {
        String selectedAmount = (String) data.get("selected_amount");
        String selectedTenure = (String) data.get("selected_tenure");

        if (data == null) {
            throw new IllegalArgumentException("Data map is null");
        }

        Object emi = data.get("emi");
        if (emi == null) {
            throw new IllegalArgumentException("EMI is required");
        }

        String emiString = (String) emi;
        if (emiString.isEmpty()) {
            throw new IllegalArgumentException("EMI cannot be empty");
        }

        Map<String, Object> response = new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("LOAN"));
        Map<String, Object> responseData = (Map<String, Object>) response.get("data");

        responseData.put("selected_amount", selectedAmount);
        responseData.put("selected_tenure", selectedTenure);
        responseData.put("emi", FlowData.LOAN_OPTIONS.get(selectedAmount).get(selectedTenure));

        return response;
    }

    private Map<String, Object> handleDetailsScreen(Map<String, Object> data) {
        String paymentMode = (String) data.get("payment_mode");
        String upiId = (String) data.get("upi_id");
        String accountNumber = (String) data.get("account_number");

        System.out.println("Received account number: " + accountNumber);

        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number is required");
        }

        Map<String, Object> response = new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("SUMMARY"));
        Map<String, Object> responseData = (Map<String, Object>) response.get("data");

        String paymentString = upiId != null ? "UPI xxxx" + upiId.substring(upiId.length() - 4)
                : "account xxxx" + accountNumber.substring(accountNumber.length() - 4);

        responseData.put("payment_mode", "Transfer to " + paymentString);

        return response;
    }

    private Map<String, Object> handleSummaryScreen(Map<String, Object> data) {
        // This method should handle the SUMMARY screen, e.g., saving data to a database or any final processing
        // For now, we just return the COMPLETE response as per the existing logic.
        return new HashMap<>((Map) FlowData.SCREEN_RESPONSES.get("COMPLETE"));
    }


}
