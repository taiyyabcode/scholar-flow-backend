package com.scholarflow.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Full student profile response for GET /api/v1/students/{id}?profile=true
 * Contains header + all 5 tabs: Overview, Academic, Attendance, Exams, Fees.
 * Matches the API documentation format exactly.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentProfileResponse {

    private Header header;

    @JsonProperty("Overview")
    private OverviewTab overview;

    @JsonProperty("Academic")
    private AcademicTab academic;

    @JsonProperty("Attendance")
    private AttendanceTab attendance;

    @JsonProperty("Exams")
    private ExamsTab exams;

    @JsonProperty("Fees")
    private FeesTab fees;

    // ── Header ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Header {
        private String id;
        private String displayName;
        private String initials;
        private String firstName;
        private String lastName;
        private String rollNumber;
        private String classId;
        private String className;
        private Integer year;
        private String status;
        private String profileBadge;
    }

    // ── Overview Tab ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OverviewTab {
        private String tabName;
        private String tabId;
        private boolean isEditable;
        private PersonalInfo personalInformation;
        private GuardianSection guardian;
        private NotesSection notes;
        private QuickContact quickContact;
        private Residence residence;
        private GuardianContact guardianContact;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PersonalInfo {
        private String sectionTitle;
        private String sectionDescription;
        private String firstName;
        private String lastName;
        private String rollNumber;
        private String dateOfBirth;
        private String email;
        private String phone;
        private String address;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GuardianSection {
        private String sectionTitle;
        private String sectionDescription;
        private String name;
        private String relation;
        private String phone;
        private String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class NotesSection {
        private String sectionTitle;
        private String sectionDescription;
        private String content;
        private String enrolledAt;
        private String enrolledAtFormatted;
        private String lastUpdatedAt;
        private String lastUpdatedAtFormatted;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuickContact {
        private String sectionTitle;
        private String phone;
        private String email;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Residence {
        private String sectionTitle;
        private String address;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GuardianContact {
        private String sectionTitle;
        private String phone;
    }

    // ── Academic Tab ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AcademicTab {
        private String tabName;
        private String tabId;
        private boolean isEditable;
        private AcademicSummaryCards summaryCards;
        private AcademicRecord academicRecord;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AcademicSummaryCards {
        private LabeledCard currentClass;
        private LabeledCard academicYear;
        private LabeledCard enrollmentStatus;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LabeledCard {
        private String label;
        private String classId;
        private String className;
        private Integer year;
        private String status;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AcademicRecord {
        private String sectionTitle;
        private String sectionDescription;
        private String classSection;
        private String classId;
        private String rollNumber;
        private Integer cohortYear;
        private String status;
        private String classDetailsLink;
        private String classDetailsLinkLabel;
        private String enrolledAt;
        private String enrolledAtFormatted;
    }

    // ── Attendance Tab ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceTab {
        private String tabName;
        private String tabId;
        private boolean isEditable;
        private AttendanceSummaryCards summaryCards;
        private AttendanceSummary attendanceSummary;
        private AttendanceHistory attendanceHistory;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceSummaryCards {
        private ValueCard attendanceRate;
        private ValueCard present;
        private ValueCard absent;
        private ValueCard late;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValueCard {
        private String label;
        private int value;
        private String unit;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceSummary {
        private String studentId;
        private String studentName;
        private String rollNumber;
        private int present;
        private int absent;
        private int late;
        private int total;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceHistory {
        private String sectionTitle;
        private String sectionDescription;
        private List<AttendanceRecord> records;
        private String emptyMessage;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AttendanceRecord {
        private String id;
        private String date;
        private String dateFormatted;
        private String classId;
        private String className;
        private String studentId;
        private String studentName;
        private String rollNumber;
        private String status;
        private String recordedAt;
        private String recordedAtFormatted;
    }

    // ── Exams Tab ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExamsTab {
        private String tabName;
        private String tabId;
        private boolean isEditable;
        private ExamSummaryCards summaryCards;
        private ExamResults examResults;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExamSummaryCards {
        private ValueCard examsTaken;
        private ValueCard passed;
        private ValueCard averageScore;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExamResults {
        private String sectionTitle;
        private String sectionDescription;
        private List<ExamResultItem> results;
        private String emptyMessage;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ExamResultItem {
        private String id;
        private String examId;
        private String examTitle;
        private String studentId;
        private String studentName;
        private String rollNumber;
        private String classId;
        private int total;
        private int maxTotal;
        private int percentage;
        private String status;
        private String marksSummary;
        private List<SubjectMarkItem> marks;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubjectMarkItem {
        private String subject;
        private int score;
        private int maxScore;
        private String display;
    }

    // ── Fees Tab ──
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FeesTab {
        private String tabName;
        private String tabId;
        private boolean isEditable;
        private boolean hasFeeRecords;
        private FeeSummaryCards summaryCards;
        private PaymentSummary paymentSummary;
        private String emptyMessage;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class FeeSummaryCards {
        private CurrencyCard totalDue;
        private CurrencyCard paid;
        private CurrencyCard balance;
        private StatusCard paymentStatus;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CurrencyCard {
        private String label;
        private int value;
        private String currency;
        private String display;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatusCard {
        private String label;
        private boolean isOverdue;
        private String display;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PaymentSummary {
        private String sectionTitle;
        private String studentId;
        private String studentName;
        private String rollNumber;
        private String className;
        private int totalDue;
        private int totalPaid;
        private int balance;
        private String lastPaymentDate;
        private String lastPaymentDateFormatted;
        private boolean isOverdue;
    }
}
