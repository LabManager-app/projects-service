package com.labmanager.projects.dto;

import java.util.List;

public class EquipmentGenerationRequest {
    private String description;
    private List<String> availableEquipment;

    public EquipmentGenerationRequest() {}

    public EquipmentGenerationRequest(String description, List<String> availableEquipment) {
        this.description = description;
        this.availableEquipment = availableEquipment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAvailableEquipment() {
        return availableEquipment;
    }

    public void setAvailableEquipment(List<String> availableEquipment) {
        this.availableEquipment = availableEquipment;
    }
}
