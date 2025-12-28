package com.labmanager.projects.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.labmanager.projects.entity.Project;
import com.labmanager.projects.repository.ProjectsRepository;

@Service
public class ProjectsService {

    private final ProjectsRepository repo;

    public ProjectsService(ProjectsRepository repo) {
        this.repo = repo;
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

    
    // set project status
    public Project setProjectStatus(Long projectId, Project.Status status) {
        return repo.findById(projectId)
            .map(p -> {
                p.setStatus(status);
                return repo.save(p);
            }).orElse(null);
    }

    // Get project by id
    public Project getProjectById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // Delete project
    public boolean deleteProject(Long id) {
        if (id == null) return false;
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }

    /* 
    @Transactional
    public Project createProject(CreateProjectRequest req) {

        // 1. rezerviraj opremo (v labs-service: reservationService)

        // 2. ustvari projekt
        Project project = new Project(req.getName());

        // 3. shrani eqipment

        return projectRepository.save(project);
    }   
    */ 
}


