package com.labmanager.projects.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.labmanager.projects.entity.Project;
import com.labmanager.projects.service.ProjectsService;

@RestController
@RequestMapping("/projects")
public class ProjectsConroller {

	private final ProjectsService projectsService;

	public ProjectsConroller(ProjectsService projectsService) {
		this.projectsService = projectsService;
	}

    // return current projects
	@GetMapping({"", "/"})
	public ResponseEntity<List<Project>> getCurrent() {
		return ResponseEntity.ok(projectsService.getCurrentProjects());
	}

    // return completed projects
	@GetMapping("/completed")
	public ResponseEntity<List<Project>> getCompleted() {
		return ResponseEntity.ok(projectsService.getCompletedProjects());
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Project>> getByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(projectsService.getProjectsByUserId(userId));
	}

	@GetMapping("/lab/{labId}")
	public ResponseEntity<List<Project>> getByLab(@PathVariable String labId) {
		return ResponseEntity.ok(projectsService.getProjectsByLabId(labId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getById(@PathVariable Long id) {
		Project p = projectsService.getProjectById(id);
		if (p == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(p);
	}

    /* 
	@PostMapping("")
	public ResponseEntity<Project> create(@RequestBody Project project) {
		Project created = projectsService.createProject(project);
		return created == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(created);
	}
    */

	@PutMapping("/{id}/status")
	public ResponseEntity<Project> setStatus(@PathVariable Long id, @RequestParam Project.Status status) {
		Project updated = projectsService.setProjectStatus(id, status);
		if (updated == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		boolean removed = projectsService.deleteProject(id);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

}
