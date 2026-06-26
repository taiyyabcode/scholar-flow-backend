package com.scholarflow.config;

import com.scholarflow.entity.*;
import com.scholarflow.enums.AttendanceStatus;
import com.scholarflow.enums.ExamResultStatus;
import com.scholarflow.enums.StudentStatus;
import com.scholarflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Seeds initial data for development: roles, classes, admin user, and sample students.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final FeeRecordRepository feeRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Data already seeded — skipping.");
            return;
        }

        log.info("🌱 Seeding initial data...");

        // ── Roles ──
        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role managerRole = roleRepository.save(Role.builder().name("MANAGER").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());
        log.info("✓ Roles created");

        // ── Admin User ──
        User admin = User.builder()
                .fullName("Admin User")
                .email("admin@scholarflow.com")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(adminRole, managerRole, userRole))
                .build();
        userRepository.save(admin);
        log.info("✓ Admin user created (admin@scholarflow.com / admin123)");

        // ── Classes ──
        ClassEntity cls1 = classRepository.save(ClassEntity.builder().classId("cls_1").className("Grade 8").build());
        ClassEntity cls2 = classRepository.save(ClassEntity.builder().classId("cls_2").className("Grade 9").build());
        ClassEntity cls3 = classRepository.save(ClassEntity.builder().classId("cls_3").className("Grade 10").build());
        ClassEntity cls4 = classRepository.save(ClassEntity.builder().classId("cls_4").className("Grade 11").build());
        ClassEntity cls5 = classRepository.save(ClassEntity.builder().classId("cls_5").className("Grade 12").build());
        log.info("✓ Classes created (cls_1 to cls_5)");

        // ── Sample Student ──
        Student student1 = Student.builder()
                .studentId("stu_1")
                .firstName("Abdul")
                .lastName("Jabir Parwan")
                .rollNumber("001")
                .email("abdul-jabir-parwan-1@school.edu")
                .phone("+91 8412989048")
                .classEntity(cls5)
                .year(2025)
                .status(StudentStatus.ACTIVE)
                .dateOfBirth(LocalDate.of(2008, 6, 15))
                .address("Sangli, Maharashtra")
                .notes("Enrolled via Anglo Urdu College, Sangli. Friday shift.")
                .build();
        student1 = studentRepository.save(student1);

        // Guardian
        Guardian guardian1 = Guardian.builder()
                .name("Guardian of Abdul")
                .phone("+91 8412989048")
                .email("guardian@example.com")
                .relation("Parent")
                .student(student1)
                .build();
        student1.setGuardian(guardian1);
        student1 = studentRepository.save(student1);
        log.info("✓ Sample student created (stu_1)");

        // ── Attendance Records ──
        attendanceRepository.saveAll(List.of(
                Attendance.builder()
                        .attendanceId("att_1").date(LocalDate.of(2025, 6, 20))
                        .status(AttendanceStatus.PRESENT).student(student1).classEntity(cls5)
                        .recordedAt(Instant.parse("2025-06-20T08:30:00Z")).build(),
                Attendance.builder()
                        .attendanceId("att_2").date(LocalDate.of(2025, 6, 19))
                        .status(AttendanceStatus.ABSENT).student(student1).classEntity(cls5)
                        .recordedAt(Instant.parse("2025-06-19T08:30:00Z")).build(),
                Attendance.builder()
                        .attendanceId("att_3").date(LocalDate.of(2025, 6, 18))
                        .status(AttendanceStatus.LATE).student(student1).classEntity(cls5)
                        .recordedAt(Instant.parse("2025-06-18T08:45:00Z")).build()
        ));
        log.info("✓ Attendance records created");

        // ── Exam ──
        Exam exam = examRepository.save(Exam.builder().examId("exam_3").title("Exam #exam_3").build());

        // ── Exam Result with Subject Marks ──
        ExamResult result = ExamResult.builder()
                .resultId("result_1")
                .student(student1)
                .exam(exam)
                .classEntity(cls5)
                .total(246)
                .maxTotal(300)
                .percentage(82)
                .status(ExamResultStatus.PASS)
                .marks(new ArrayList<>())
                .build();

        // Add subject marks
        SubjectMark math = SubjectMark.builder().subject("Mathematics").score(82).maxScore(100).examResult(result).build();
        SubjectMark english = SubjectMark.builder().subject("English").score(76).maxScore(100).examResult(result).build();
        SubjectMark science = SubjectMark.builder().subject("Science").score(88).maxScore(100).examResult(result).build();
        result.getMarks().addAll(List.of(math, english, science));

        examResultRepository.save(result);
        log.info("✓ Exam results created");

        // ── Fee Record ──
        feeRecordRepository.save(FeeRecord.builder()
                .student(student1)
                .totalDue(BigDecimal.valueOf(1000))
                .totalPaid(BigDecimal.valueOf(500))
                .lastPaymentDate(LocalDate.of(2025, 5, 15))
                .isOverdue(false)
                .build());
        log.info("✓ Fee records created");

        log.info("🎉 Data seeding completed!");
    }
}
