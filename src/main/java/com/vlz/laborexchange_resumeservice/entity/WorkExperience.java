package com.vlz.laborexchange_resumeservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "work_experience")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "resume")
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String position;

    @Column(length = 2000)
    private String description;

    @Column(name = "start_year", nullable = false)
    private Integer startYear;

    @Column(name = "start_month")
    private Integer startMonth;

    @Column(name = "end_year")
    private Integer endYear;

    @Column(name = "end_month")
    private Integer endMonth;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;
}
