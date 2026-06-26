package com.scholarflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_id", nullable = false, unique = true, length = 50)
    private String examId;

    @Column(nullable = false, length = 200)
    private String title;
}
