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

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipment")
    private String equipment;

    @Column(name = "used_quantity", nullable = false)
    private Integer usedQuantity;
}

