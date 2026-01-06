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
import com.labmanager.projects.service.EquipmentSuggestionService;
import com.labmanager.projects.dto.CreateProjectRequest;
import org.springframework.http.HttpStatus;
import java.net.URI;
import org.springframework.http.MediaType;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

// Swagger API dokumentacija: http://localhost:8082/swagger-ui/index.html#/

@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "API za upravljanje projektov")
public class ProjectsController {

	private final ProjectsService projectsService;
	private final EquipmentSuggestionService suggestionService;
	private final com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();

	public ProjectsController(ProjectsService projectsService, EquipmentSuggestionService suggestionService) {
		this.projectsService = projectsService;
		this.suggestionService = suggestionService;
	}

    // return current projects
	@Operation(summary = "Pridobi tekoče projekte", description = "Vrne seznam trenutnih (aktivnih) projektov")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam projektov vrnjen")
	})
	@GetMapping({"", "/"})
	public ResponseEntity<List<Project>> getCurrent() {
		return ResponseEntity.ok(projectsService.getCurrentProjects());
	}

    // return completed projects
	@Operation(summary = "Pridobi zaključene projekte", description = "Vrne seznam zaključenih projektov")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam zaključenih projektov")
	})
	@GetMapping("/completed")
	public ResponseEntity<List<Project>> getCompleted() {
		return ResponseEntity.ok(projectsService.getCompletedProjects());
	}

	@Operation(summary = "Pridobi projekte uporabnika", description = "Vrne vse projekte za podan `userId`")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam projektov za uporabnika")
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Project>> getByUser(@Parameter(description = "ID uporabnika") @PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getProjectsByUserId(userId));
	}

	@Operation(summary = "Pridobi aktivne projekte uporabnika", description = "Vrne aktivne projekte za podan `userId`")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam aktivnih projektov za uporabnika")
	})
	@GetMapping("/user/{userId}/active")
	public ResponseEntity<List<Project>> getByUserActive(@Parameter(description = "ID uporabnika") @PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getCurrentProjectsByUserId(userId));
	}

	@Operation(summary = "Pridobi zaključene projekte uporabnika", description = "Vrne zaključene projekte za podan `userId`")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam zaključenih projektov za uporabnika")
	})
	@GetMapping("/user/{userId}/completed")
	public ResponseEntity<List<Project>> getByUserCompleted(@Parameter(description = "ID uporabnika") @PathVariable("userId") Long userId) {
		return ResponseEntity.ok(projectsService.getCompletedProjectsByUserId(userId));
	}

	@Operation(summary = "Pridobi projekte po laboratoriju", description = "Vrne projekte za podani `labId`")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Seznam projektov za laboratorij")
	})
	@GetMapping("/lab/{labId}")
	public ResponseEntity<List<Project>> getByLab(@Parameter(description = "ID laboratorija") @PathVariable("labId") String labId) {
		return ResponseEntity.ok(projectsService.getProjectsByLabId(labId));
	}

	@Operation(summary = "Pridobi projekt po ID", description = "Vrne projekt z danim ID, ali 404 če ne obstaja")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Projekt najden"),
		@ApiResponse(responseCode = "404", description = "Projekt ni najden")
	})
	@GetMapping("/{id}")
	public ResponseEntity<Project> getById(@Parameter(description = "ID projekta") @PathVariable("id") Long id) {
		Project p = projectsService.getProjectById(id);
		if (p == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(p);
	}

	@Operation(summary = "Ustvari projekt", description = "Ustvari nov projekt. Vrne 201 in Location header z lokacijo novega projekta.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Projekt uspešno ustvarjen"),
		@ApiResponse(responseCode = "400", description = "Neveljaven zahtevek"),
		@ApiResponse(responseCode = "409", description = "Projekt ni bil ustvarjen zaradi konflikta")
	})
	@PostMapping("")
	public ResponseEntity<Project> create(@RequestBody CreateProjectRequest req) {
		if (req == null || req.getProject() == null) {
			return ResponseEntity.badRequest().build();
		}

		Project created = projectsService.createProject(req.getProject(), req.getEquipmentRequests());
		if (created == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		URI location = URI.create(String.format("/projects/%d", created.getId()));
		return ResponseEntity.created(location).body(created);
	}

	@Operation(summary = "Posodobi status projekta", description = "Nastavi status projekta (npr. ACTIVE/COMPLETED).")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Status posodobljen"),
		@ApiResponse(responseCode = "404", description = "Projekt ni najden")
	})
	@PutMapping("/{id}/status")
	public ResponseEntity<Project> setStatus(@Parameter(description = "ID projekta") @PathVariable("id") Long id, @RequestParam Project.Status status) {
		Project updated = projectsService.setProjectStatus(id, status);
		if (updated == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(updated);
	}

	@Operation(summary = "Izbriši projekt", description = "Izbriše projekt z danim ID.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Projekt izbrisan"),
		@ApiResponse(responseCode = "404", description = "Projekt ni najden")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@Parameter(description = "ID projekta") @PathVariable("id") Long id) {
		boolean removed = projectsService.deleteProject(id);
		return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
	}

	// with OpenAI generate equipment from description
	@Operation(summary = "Generiraj predlog opreme", description = "Ustvari predlog opreme na podlagi opisa (uporaba OpenAI/heuristik).")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Predlog opreme vrnjen"),
		@ApiResponse(responseCode = "503", description = "Storitev za generiranje ni na voljo")
	})
	@PostMapping("/generateEquipment")
	public ResponseEntity<?> generateEquipment(@RequestBody com.labmanager.projects.dto.EquipmentGenerationRequest req) {
	if (req == null || req.getDescription() == null) return ResponseEntity.badRequest().build();
	try {
		String jsonArray = suggestionService.suggestEquipment(req.getDescription(), req.getAvailableEquipment());
		// vrnemo surovi JSON z Content-Type application/json
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(om.readValue(jsonArray, Object.class));
	} catch (IllegalStateException ise) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", ise.getMessage()));
	} catch (Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
	}
	}

}
