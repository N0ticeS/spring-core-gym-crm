package com.example.trainer_workload_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer_workload")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(
            mappedBy = "trainer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MonthlyWorkload> monthlyWorkloads = new ArrayList<>();
}
