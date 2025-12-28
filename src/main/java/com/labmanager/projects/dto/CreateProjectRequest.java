package com.labmanager.projects.dto;

import java.util.List;
import com.labmanager.projects.entity.Project;

public class CreateProjectRequest {
    private Project project;
    private List<EquipmentRequest> equipmentRequests;

    public CreateProjectRequest() {}

    public CreateProjectRequest(Project project, List<EquipmentRequest> equipmentRequests) {
        this.project = project;
        this.equipmentRequests = equipmentRequests;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<EquipmentRequest> getEquipmentRequests() {
        return equipmentRequests;
    }

    public void setEquipmentRequests(List<EquipmentRequest> equipmentRequests) {
        this.equipmentRequests = equipmentRequests;
    }
}
