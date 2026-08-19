package com.example.trainer_workload_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "monthly_workload",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"trainer_id", "workload_year", "workload_month"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerWorkload trainer;

    @Column(name = "workload_year", nullable = false)
    private Integer year;

    @Column(name = "workload_month", nullable = false)
    private Integer month;

    @Column(name = "training_summary_duration", nullable = false)
    private Integer trainingSummaryDuration;
}
