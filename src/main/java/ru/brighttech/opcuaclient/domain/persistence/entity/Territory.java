package ru.brighttech.opcuaclient.domain.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Territory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long higherTerritory;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    private boolean isDeactivated;



}