package com.labmanager.projects.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

    // add a new project
    public Project addProject(Project project) {
        if (project == null) return null;
        return repo.save(project);
    }
    
    // set project status
    public Project setProjectStatus(Long projectId, Project.Status status) {
        return repo.findById(projectId)
            .map(p -> {
                p.setStatus(status);
                return repo.save(p);
            }).orElse(null);
    }

    

    
}


