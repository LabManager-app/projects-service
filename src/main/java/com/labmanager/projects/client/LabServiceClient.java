package com.labmanager.projects.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LabServiceClient {

    private final WebClient webClient;

    public LabServiceClient(WebClient.Builder builder, @Value("${lab.service.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    // rezervira opremo v lab (POST /labs/{labId}/reservation)
    public Boolean reserve(String labId, java.util.List<com.labmanager.projects.dto.EquipmentRequest> equipmentRequests) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/labs/{labId}/reservation").build(labId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipmentRequests)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}