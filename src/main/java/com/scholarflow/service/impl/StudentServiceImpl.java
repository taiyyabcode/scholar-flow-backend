package com.scholarflow.service.impl;

import com.scholarflow.dto.request.CreateStudentRequest;
import com.scholarflow.dto.request.UpdateStudentRequest;
import com.scholarflow.dto.response.StudentListResponse;
import com.scholarflow.dto.response.StudentProfileResponse;
import com.scholarflow.dto.response.StudentProfileResponse.*;
import com.scholarflow.dto.response.StudentResponse;
import com.scholarflow.entity.*;
import com.scholarflow.enums.AttendanceStatus;
import com.scholarflow.enums.ExamResultStatus;
import com.scholarflow.enums.StudentStatus;
import com.scholarflow.exception.BadRequestException;
import com.scholarflow.exception.DuplicateResourceException;
import com.scholarflow.exception.ResourceNotFoundException;
import com.scholarflow.mapper.StudentMapper;
import com.scholarflow.repository.*;
import com.scholarflow.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final ExamResultRepository examResultRepository;
    private final FeeRecordRepository feeRecordRepository;
    private final StudentMapper studentMapper;

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/v1/students/list
    // ────────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public StudentListResponse getStudentList(int page, int limit, String search, String classId, String status, Integer year) {
        Pageable pageable = PageRequest.of(page - 1, limit); // API is 1-indexed

        StudentStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = StudentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        }

        Page<Student> studentPage = studentRepository.findAllWithFilters(
                search != null && !search.isBlank() ? search : null,
                classId != null && !classId.isBlank() ? classId : null,
                statusEnum,
                year,
                pageable
        );

        List<StudentListResponse.StudentItem> items = studentPage.getContent()
                .stream()
                .map(studentMapper::toStudentListItem)
                .collect(Collectors.toList());

        StudentListResponse.Meta meta = StudentListResponse.Meta.builder()
                .total(studentPage.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(studentPage.getTotalPages())
                .build();

        return StudentListResponse.builder()
                .data(items)
                .meta(meta)
                .build();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // POST /api/v1/students
    // ────────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Student email already exists: " + request.getEmail());
        }

        ClassEntity classEntity = classRepository.findByClassId(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "classId", request.getClassId()));

        String studentId = "stu_" + UUID.randomUUID().toString().substring(0, 8);

        Student student = Student.builder()
                .studentId(studentId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .rollNumber(request.getRollNumber())
                .email(request.getEmail())
                .phone(request.getPhone())
                .classEntity(classEntity)
                .year(request.getYear())
                .status(StudentStatus.valueOf(request.getStatus().toUpperCase()))
                .dateOfBirth(request.getDateOfBirth() != null ? LocalDate.parse(request.getDateOfBirth()) : null)
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();

        student = studentRepository.save(student);

        // Save guardian if provided
        if (request.getGuardian() != null) {
            Guardian guardian = Guardian.builder()
                    .name(request.getGuardian().getName())
                    .phone(request.getGuardian().getPhone())
                    .email(request.getGuardian().getEmail())
                    .relation(request.getGuardian().getRelation())
                    .student(student)
                    .build();
            student.setGuardian(guardian);
            student = studentRepository.save(student);
        }

        log.info("Student created: {}", studentId);
        return studentMapper.toStudentResponse(student);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/v1/students/{id}?profile=true
    // ────────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        // Force-load lazy associations
        ClassEntity cls = student.getClassEntity();
        Guardian guardian = student.getGuardian();

        String displayName = student.getFirstName() + " " + student.getLastName();
        String initials = ("" + student.getFirstName().charAt(0) + student.getLastName().charAt(0)).toUpperCase();

        return StudentProfileResponse.builder()
                .header(buildHeader(student, cls, displayName, initials))
                .overview(buildOverviewTab(student, guardian))
                .academic(buildAcademicTab(student, cls))
                .attendance(buildAttendanceTab(student, displayName))
                .exams(buildExamsTab(student, displayName))
                .fees(buildFeesTab(student, displayName, cls))
                .build();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/students/{id}
    // ────────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public StudentResponse updateStudent(String studentId, UpdateStudentRequest request) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (request.getFirstName() != null) student.setFirstName(request.getFirstName());
        if (request.getLastName() != null) student.setLastName(request.getLastName());
        if (request.getRollNumber() != null) student.setRollNumber(request.getRollNumber());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getYear() != null) student.setYear(request.getYear());
        if (request.getStatus() != null) student.setStatus(StudentStatus.valueOf(request.getStatus().toUpperCase()));
        if (request.getDateOfBirth() != null) student.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getNotes() != null) student.setNotes(request.getNotes());

        if (request.getClassId() != null) {
            ClassEntity classEntity = classRepository.findByClassId(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class", "classId", request.getClassId()));
            student.setClassEntity(classEntity);
        }

        if (request.getGuardian() != null) {
            Guardian guardian = student.getGuardian();
            if (guardian == null) {
                guardian = new Guardian();
                guardian.setStudent(student);
                student.setGuardian(guardian);
            }
            if (request.getGuardian().getName() != null) guardian.setName(request.getGuardian().getName());
            if (request.getGuardian().getPhone() != null) guardian.setPhone(request.getGuardian().getPhone());
            if (request.getGuardian().getEmail() != null) guardian.setEmail(request.getGuardian().getEmail());
            if (request.getGuardian().getRelation() != null) guardian.setRelation(request.getGuardian().getRelation());
        }

        student = studentRepository.save(student);
        log.info("Student updated: {}", studentId);
        return studentMapper.toStudentResponse(student);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // DELETE /api/v1/students/{id}
    // ────────────────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public Map<String, Boolean> deleteStudent(String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        studentRepository.delete(student);
        log.info("Student deleted: {}", studentId);
        return Map.of("success", true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Private helpers for building profile tabs
    // ═══════════════════════════════════════════════════════════════════════════

    private Header buildHeader(Student s, ClassEntity cls, String displayName, String initials) {
        return Header.builder()
                .id(s.getStudentId())
                .displayName(displayName)
                .initials(initials)
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .rollNumber(s.getRollNumber())
                .classId(cls.getClassId())
                .className(cls.getClassName())
                .year(s.getYear())
                .status(s.getStatus().name().toLowerCase())
                .profileBadge("Student Profile")
                .build();
    }

    private OverviewTab buildOverviewTab(Student s, Guardian g) {
        PersonalInfo pi = PersonalInfo.builder()
                .sectionTitle("Personal Information")
                .sectionDescription("Core identity and contact details")
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .rollNumber(s.getRollNumber())
                .dateOfBirth(s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : null)
                .email(s.getEmail())
                .phone(s.getPhone())
                .address(s.getAddress())
                .build();

        GuardianSection gs = null;
        GuardianContact gc = null;
        if (g != null) {
            gs = GuardianSection.builder()
                    .sectionTitle("Guardian")
                    .sectionDescription("Primary emergency contact")
                    .name(g.getName())
                    .relation(g.getRelation())
                    .phone(g.getPhone())
                    .email(g.getEmail())
                    .build();
            gc = GuardianContact.builder()
                    .sectionTitle("Guardian contact")
                    .phone(g.getPhone())
                    .build();
        }

        NotesSection notes = NotesSection.builder()
                .sectionTitle("Notes")
                .sectionDescription("Administrative remarks")
                .content(s.getNotes())
                .enrolledAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .enrolledAtFormatted(s.getCreatedAt() != null ? formatInstantDisplay(s.getCreatedAt()) : null)
                .lastUpdatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null)
                .lastUpdatedAtFormatted(s.getUpdatedAt() != null ? formatInstantDisplay(s.getUpdatedAt()) : null)
                .build();

        return OverviewTab.builder()
                .tabName("Overview")
                .tabId("overview")
                .isEditable(true)
                .personalInformation(pi)
                .guardian(gs)
                .notes(notes)
                .quickContact(QuickContact.builder()
                        .sectionTitle("Quick contact")
                        .phone(s.getPhone())
                        .email(s.getEmail())
                        .build())
                .residence(Residence.builder()
                        .sectionTitle("Residence")
                        .address(s.getAddress())
                        .build())
                .guardianContact(gc)
                .build();
    }

    private AcademicTab buildAcademicTab(Student s, ClassEntity cls) {
        AcademicSummaryCards cards = AcademicSummaryCards.builder()
                .currentClass(LabeledCard.builder().label("Current class").classId(cls.getClassId()).className(cls.getClassName()).build())
                .academicYear(LabeledCard.builder().label("Academic year").year(s.getYear()).build())
                .enrollmentStatus(LabeledCard.builder().label("Enrollment status").status(s.getStatus().name().toLowerCase()).build())
                .build();

        AcademicRecord record = AcademicRecord.builder()
                .sectionTitle("Academic record")
                .sectionDescription("Class placement and enrollment timeline")
                .classSection(cls.getClassName())
                .classId(cls.getClassId())
                .rollNumber(s.getRollNumber())
                .cohortYear(s.getYear())
                .status(s.getStatus().name().toLowerCase())
                .classDetailsLink("/classes/" + cls.getClassId())
                .classDetailsLinkLabel("View class details")
                .enrolledAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .enrolledAtFormatted(s.getCreatedAt() != null ? formatInstantFull(s.getCreatedAt()) : null)
                .build();

        return AcademicTab.builder()
                .tabName("Academic")
                .tabId("academic")
                .isEditable(true)
                .summaryCards(cards)
                .academicRecord(record)
                .build();
    }

    private AttendanceTab buildAttendanceTab(Student s, String displayName) {
        List<Attendance> records = attendanceRepository.findByStudentStudentIdOrderByDateDesc(s.getStudentId());

        int present = 0, absent = 0, late = 0;
        for (Attendance a : records) {
            switch (a.getStatus()) {
                case PRESENT -> present++;
                case ABSENT -> absent++;
                case LATE -> late++;
            }
        }
        int total = records.size();
        int rate = total > 0 ? (int) Math.round((double) present / total * 100) : 0;

        List<AttendanceRecord> historyRecords = records.stream()
                .map(a -> AttendanceRecord.builder()
                        .id(a.getAttendanceId())
                        .date(a.getDate().toString())
                        .dateFormatted(a.getDate().format(DISPLAY_FORMAT))
                        .classId(a.getClassEntity().getClassId())
                        .className(a.getClassEntity().getClassName())
                        .studentId(s.getStudentId())
                        .studentName(displayName)
                        .rollNumber(s.getRollNumber())
                        .status(a.getStatus().name().toLowerCase())
                        .recordedAt(a.getRecordedAt() != null ? a.getRecordedAt().toString() : null)
                        .recordedAtFormatted(a.getRecordedAt() != null ? formatInstantDisplay(a.getRecordedAt()) : null)
                        .build())
                .collect(Collectors.toList());

        return AttendanceTab.builder()
                .tabName("Attendance")
                .tabId("attendance")
                .isEditable(false)
                .summaryCards(AttendanceSummaryCards.builder()
                        .attendanceRate(ValueCard.builder().label("Attendance rate").value(rate).unit("%").build())
                        .present(ValueCard.builder().label("Present").value(present).build())
                        .absent(ValueCard.builder().label("Absent").value(absent).build())
                        .late(ValueCard.builder().label("Late").value(late).build())
                        .build())
                .attendanceSummary(com.scholarflow.dto.response.StudentProfileResponse.AttendanceSummary.builder()
                        .studentId(s.getStudentId())
                        .studentName(displayName)
                        .rollNumber(s.getRollNumber())
                        .present(present)
                        .absent(absent)
                        .late(late)
                        .total(total)
                        .build())
                .attendanceHistory(AttendanceHistory.builder()
                        .sectionTitle("Attendance history")
                        .sectionDescription("Recent recorded sessions")
                        .records(historyRecords)
                        .emptyMessage("No attendance records yet for this student.")
                        .build())
                .build();
    }

    private ExamsTab buildExamsTab(Student s, String displayName) {
        List<ExamResult> results = examResultRepository.findByStudentStudentId(s.getStudentId());

        int examsTaken = results.size();
        int passed = (int) results.stream().filter(r -> r.getStatus() == ExamResultStatus.PASS).count();
        int avgScore = examsTaken > 0 ? (int) results.stream().mapToInt(ExamResult::getPercentage).average().orElse(0) : 0;

        List<ExamResultItem> resultItems = results.stream()
                .map(er -> ExamResultItem.builder()
                        .id(er.getResultId())
                        .examId(er.getExam().getExamId())
                        .examTitle("Exam #" + er.getExam().getExamId())
                        .studentId(s.getStudentId())
                        .studentName(displayName)
                        .rollNumber(s.getRollNumber())
                        .classId(er.getClassEntity().getClassId())
                        .total(er.getTotal())
                        .maxTotal(er.getMaxTotal())
                        .percentage(er.getPercentage())
                        .status(er.getStatus().name().toLowerCase())
                        .marksSummary(er.getTotal() + "/" + er.getMaxTotal() + " marks · " + er.getPercentage() + "%")
                        .marks(er.getMarks().stream()
                                .map(m -> SubjectMarkItem.builder()
                                        .subject(m.getSubject())
                                        .score(m.getScore())
                                        .maxScore(m.getMaxScore())
                                        .display(m.getSubject() + ": " + m.getScore() + "/" + m.getMaxScore())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return ExamsTab.builder()
                .tabName("Exams")
                .tabId("exams")
                .isEditable(false)
                .summaryCards(ExamSummaryCards.builder()
                        .examsTaken(ValueCard.builder().label("Exams taken").value(examsTaken).build())
                        .passed(ValueCard.builder().label("Passed").value(passed).build())
                        .averageScore(ValueCard.builder().label("Average score").value(avgScore).unit("%").build())
                        .build())
                .examResults(StudentProfileResponse.ExamResults.builder()
                        .sectionTitle("Exam results")
                        .sectionDescription("Performance across assessments")
                        .results(resultItems)
                        .emptyMessage("No exam results recorded yet.")
                        .build())
                .build();
    }

    private FeesTab buildFeesTab(Student s, String displayName, ClassEntity cls) {
        Optional<FeeRecord> feeOpt = feeRecordRepository.findByStudentStudentId(s.getStudentId());

        if (feeOpt.isEmpty()) {
            return FeesTab.builder()
                    .tabName("Fees")
                    .tabId("fees")
                    .isEditable(false)
                    .hasFeeRecords(false)
                    .emptyMessage("No fee records found for this student.")
                    .build();
        }

        FeeRecord fee = feeOpt.get();
        int totalDue = fee.getTotalDue().intValue();
        int totalPaid = fee.getTotalPaid().intValue();
        int balance = totalDue - totalPaid;

        return FeesTab.builder()
                .tabName("Fees")
                .tabId("fees")
                .isEditable(false)
                .hasFeeRecords(true)
                .summaryCards(FeeSummaryCards.builder()
                        .totalDue(CurrencyCard.builder().label("Total due").value(totalDue).currency("USD").display("$" + String.format("%,d", totalDue)).build())
                        .paid(CurrencyCard.builder().label("Paid").value(totalPaid).currency("USD").display("$" + String.format("%,d", totalPaid)).build())
                        .balance(CurrencyCard.builder().label("Balance").value(balance).currency("USD").display("$" + String.format("%,d", balance)).build())
                        .paymentStatus(StatusCard.builder().label("Status").isOverdue(fee.getIsOverdue()).display(fee.getIsOverdue() ? "Overdue" : "On track").build())
                        .build())
                .paymentSummary(PaymentSummary.builder()
                        .sectionTitle("Payment summary")
                        .studentId(s.getStudentId())
                        .studentName(displayName)
                        .rollNumber(s.getRollNumber())
                        .className(cls.getClassName())
                        .totalDue(totalDue)
                        .totalPaid(totalPaid)
                        .balance(balance)
                        .lastPaymentDate(fee.getLastPaymentDate() != null ? fee.getLastPaymentDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toString() : null)
                        .lastPaymentDateFormatted(fee.getLastPaymentDate() != null ? fee.getLastPaymentDate().format(DISPLAY_FORMAT) : null)
                        .isOverdue(fee.getIsOverdue())
                        .build())
                .emptyMessage("No fee records found for this student.")
                .build();
    }

    // ── Date Formatting helpers ──
    private String formatInstantDisplay(java.time.Instant instant) {
        return java.time.LocalDate.ofInstant(instant, java.time.ZoneOffset.UTC).format(DISPLAY_FORMAT);
    }

    private String formatInstantFull(java.time.Instant instant) {
        return java.time.LocalDate.ofInstant(instant, java.time.ZoneOffset.UTC).format(FULL_FORMAT);
    }
}
