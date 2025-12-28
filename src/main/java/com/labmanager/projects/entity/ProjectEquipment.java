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
    
    private String name;
    private Integer usedQuantity;

    public ProjectEquipment() {}

    public ProjectEquipment(Project project, String name, Integer usedQuantity) {
        this.project = project;
        this.name = name;
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

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(Integer usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
}

