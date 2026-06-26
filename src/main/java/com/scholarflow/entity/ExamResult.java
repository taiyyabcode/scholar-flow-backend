package com.scholarflow.entity;

import com.scholarflow.enums.ExamResultStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "result_id", nullable = false, unique = true, length = 50)
    private String resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Column(nullable = false)
    private Integer total;

    @Column(name = "max_total", nullable = false)
    private Integer maxTotal;

    @Column(nullable = false)
    private Integer percentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ExamResultStatus status;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubjectMark> marks = new ArrayList<>();
}
