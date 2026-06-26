package com.scholarflow.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a class/grade (e.g. "Grade 12").
 * Uses a business key classId like "cls_5".
 */
@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false, unique = true, length = 50)
    private String classId;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;
}
