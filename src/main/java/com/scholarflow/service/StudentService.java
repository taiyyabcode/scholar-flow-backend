package com.scholarflow.service;

import com.scholarflow.dto.request.CreateStudentRequest;
import com.scholarflow.dto.request.UpdateStudentRequest;
import com.scholarflow.dto.response.StudentListResponse;
import com.scholarflow.dto.response.StudentProfileResponse;
import com.scholarflow.dto.response.StudentResponse;

import java.util.Map;

public interface StudentService {

    StudentListResponse getStudentList(int page, int limit, String search, String classId, String status, Integer year);

    StudentResponse createStudent(CreateStudentRequest request);

    StudentProfileResponse getStudentProfile(String studentId);

    StudentResponse updateStudent(String studentId, UpdateStudentRequest request);

    Map<String, Boolean> deleteStudent(String studentId);
}
