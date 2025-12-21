package com.labmanager.projects.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.labmanager.projects.entity.Project;

public interface ProjectsRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByStatus(Project.Status status);
    List<Project> findAllByLabId(String labId);
    List<Project> findAllByParticipantsContaining(Long userId);
    List<Project> findAllByProjectLeader(Long projectLeader);
}
