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
import com.labmanager.projects.dto.CreateProjectRequest;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/projects")
public class ProjectsController {

	private final ProjectsService projectsService;

	public ProjectsController(ProjectsService projectsService) {
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
	public ResponseEntity<List<Project>> getByUser(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getProjectsByUserId(userId));
	}

	@GetMapping("/user/{userId}/active")
	public ResponseEntity<List<Project>> getByUserActive(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getCurrentProjectsByUserId(userId));
	}

	@GetMapping("/user/{userId}/completed")
	public ResponseEntity<List<Project>> getByUserCompleted(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getCompletedProjectsByUserId(userId));
	}

	@GetMapping("/lab/{labId}")
	public ResponseEntity<List<Project>> getByLab(@PathVariable("labId") String labId) {
		return ResponseEntity.ok(projectsService.getProjectsByLabId(labId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Project> getById(@PathVariable("id") Long id) {
		Project p = projectsService.getProjectById(id);
		if (p == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(p);
	}

	@PostMapping("")
	public ResponseEntity<Project> create(@RequestBody CreateProjectRequest req) {
		if (req == null || req.getProject() == null) {
			return ResponseEntity.badRequest().build();
		}

		Project created = projectsService.createProject(req.getProject(), req.getEquipmentRequests());
		if (created == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Project> setStatus(@PathVariable("id") Long id, @RequestParam Project.Status status) {
		Project updated = projectsService.setProjectStatus(id, status);
		if (updated == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		boolean removed = projectsService.deleteProject(id);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

}
