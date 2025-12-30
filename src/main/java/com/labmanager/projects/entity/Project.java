package com.labmanager.projects.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class Project {

    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String labId;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long projectLeader; // userId

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    // team members
    @ElementCollection
    @CollectionTable(name = "project_participants", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "user_id")
    private List<Long> participants = new ArrayList<>();  // userIds

    // equipment
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectEquipment> equipment = new ArrayList<>();


    public Project() {}

    public Project(String name, String labId) {
        this.name = name;
        this.labId = labId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabId() {
        return labId;
    }

    public void setLabId(String labId) {
        this.labId = labId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(Long projectLeader) {
        this.projectLeader = projectLeader;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public List<ProjectEquipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<ProjectEquipment> equipment) {
        this.equipment = equipment;
        // ensure back-references
        if (this.equipment != null) {
            for (ProjectEquipment pe : this.equipment) {
                pe.setProject(this);
            }
        }
    }

    public void addEquipment(ProjectEquipment pe) {
        pe.setProject(this);
        this.equipment.add(pe);
    }

    public void removeEquipment(ProjectEquipment pe) {
        this.equipment.remove(pe);
        pe.setProject(null);
    }

}
