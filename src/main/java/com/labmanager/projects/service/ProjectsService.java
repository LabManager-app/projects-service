package com.labmanager.projects.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labmanager.projects.entity.Project;
import com.labmanager.projects.repository.ProjectsRepository;
import com.labmanager.projects.client.LabServiceClient;
import com.labmanager.projects.dto.EquipmentRequest;
import com.labmanager.projects.entity.ProjectEquipment;

@Service
public class ProjectsService {

    private final ProjectsRepository repo;
    private final LabServiceClient labServiceClient;

    public ProjectsService(ProjectsRepository repo, LabServiceClient labServiceClient) {
        this.repo = repo;
        this.labServiceClient = labServiceClient;
    }

    // get current projects (ACTIVE)
    public List<Project> getCurrentProjects() {
        return repo.findAllByStatus(Project.Status.ACTIVE);
    }

    // get completed projects
    public List<Project> getCompletedProjects() {
        return repo.findAllByStatus(Project.Status.COMPLETED);
    }

    // get projects by userId (as leader or participant)
    public List<Project> getProjectsByUserId(Long userId) {
        if (userId == null) return List.of();
        List<Project> result = new ArrayList<>();
        result.addAll(repo.findAllByProjectLeader(userId));
        result.addAll(repo.findAllByParticipantsContaining(userId));
        return result.stream().distinct().collect(Collectors.toList());
    }

    // get projects by labId
    public List<Project> getProjectsByLabId(String labId) {
        return repo.findAllByLabId(labId);
    }

    // get current projects for a specific user
    public List<Project> getCurrentProjectsByUserId(Long userId) {
        return getProjectsByUserId(userId).stream()
            .filter(p -> p.getStatus() == Project.Status.ACTIVE)
            .collect(Collectors.toList());
    }

    // get completed projects for a specific user
    public List<Project> getCompletedProjectsByUserId(Long userId) {
        return getProjectsByUserId(userId).stream()
            .filter(p -> p.getStatus() == Project.Status.COMPLETED)
            .collect(Collectors.toList());
    }

    
    // set project status
    public Project setProjectStatus(Long projectId, Project.Status status) {
        return repo.findById(projectId)
            .map(p -> {
                p.setStatus(status);
                // set or clear endDate depending on new status
                if (status == Project.Status.COMPLETED || status == Project.Status.CANCELED) {
                    p.setEndDate(LocalDate.now());
                } else {
                    p.setEndDate(null);
                }
                return repo.save(p);
            }).orElse(null);
    }

    // Get project by id
    public Project getProjectById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // Delete project
    @Transactional
    public boolean deleteProject(Long id) {
        if (id == null) return false;
        Project project = repo.findById(id).orElse(null);
        if (project == null) return false;

        // If project reserved equipment in a lab, attempt to free it first
        String labId = project.getLabId();
        if (labId != null && project.getEquipment() != null && !project.getEquipment().isEmpty()) {
            java.util.List<EquipmentRequest> toFree = project.getEquipment().stream()
                    .map(pe -> new EquipmentRequest(pe.getName(), pe.getUsedQuantity() == null ? 0 : pe.getUsedQuantity()))
                    .collect(Collectors.toList());
            try {
                Boolean freed = labServiceClient.free(labId, toFree);
                if (freed == null || !freed) {
                    // failed to free resources in lab -> abort deletion
                    return false;
                }
            } catch (Exception ex) {
                // error calling lab service -> abort deletion
                return false;
            }
        }

        repo.deleteById(id);
        return true;
    }

    
    // Create project + reserve equipment in a lab
    @Transactional
    public Project createProject(Project projectData, List<EquipmentRequest> equipmentRequests) {
        if (projectData == null) return null;

        // 1. reserve equipment
        String labId = projectData.getLabId();
        if (labId != null && equipmentRequests != null && !equipmentRequests.isEmpty()) {
            boolean reserved = labServiceClient.reserve(labId, equipmentRequests);
            if (!reserved) {
                // Reservation failed -> do not create project
                return null;
            }
        }

        // 2. attach project equipment entities to project
        if (equipmentRequests != null && !equipmentRequests.isEmpty()) {
            for (EquipmentRequest er : equipmentRequests) {
                ProjectEquipment pe = new ProjectEquipment();
                pe.setName(er.getName());
                pe.setUsedQuantity(er.getStock());
                projectData.addEquipment(pe);
            }
        }

        // 3. save project (cascade will persist ProjectEquipment)
        Project saved = repo.save(projectData);
        return saved;
    }
}


