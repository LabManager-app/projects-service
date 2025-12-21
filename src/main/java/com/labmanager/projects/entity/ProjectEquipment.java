package com.labmanager.projects.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "project_equipment")
public class ProjectEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    private Long equipmentId;
    private Integer usedQuantity;

    public ProjectEquipment() {}

    public ProjectEquipment(Project project, Long equipmentId, Integer usedQuantity) {
        this.project = project;
        this.equipmentId = equipmentId;
        this.usedQuantity = usedQuantity;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
}

