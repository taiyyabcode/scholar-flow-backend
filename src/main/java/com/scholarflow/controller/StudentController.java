package com.scholarflow.controller;

import com.scholarflow.dto.request.CreateStudentRequest;
import com.scholarflow.dto.request.UpdateStudentRequest;
import com.scholarflow.dto.response.StudentListResponse;
import com.scholarflow.dto.response.StudentProfileResponse;
import com.scholarflow.dto.response.StudentResponse;
import com.scholarflow.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Students", description = "Student CRUD APIs")
public class StudentController {

    private final StudentService studentService;

    /**
     * GET /api/v1/students/list — Paginated student list with filters.
     */
    @GetMapping("/list")
    @Operation(summary = "Fetch paginated student list with search and filters")
    public ResponseEntity<StudentListResponse> getStudentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer year
    ) {
        StudentListResponse response = studentService.getStudentList(page, limit, search, classId, status, year);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/students — Create a new student.
     */
    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<Map<String, StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request
    ) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", response));
    }

    /**
     * GET /api/v1/students/{id} — Fetch student profile (all tabs).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Fetch full student profile with all tabs")
    public ResponseEntity<Map<String, StudentProfileResponse>> getStudentProfile(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "true") boolean profile
    ) {
        StudentProfileResponse response = studentService.getStudentProfile(id);
        return ResponseEntity.ok(Map.of("data", response));
    }

    /**
     * PUT /api/v1/students/{id} — Update a student (partial update).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update student record (partial update supported)")
    public ResponseEntity<Map<String, StudentResponse>> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(Map.of("data", response));
    }

    /**
     * DELETE /api/v1/students/{id} — Delete a student.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student record")
    public ResponseEntity<Map<String, Map<String, Boolean>>> deleteStudent(@PathVariable String id) {
        Map<String, Boolean> result = studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("data", result));
    }
}
