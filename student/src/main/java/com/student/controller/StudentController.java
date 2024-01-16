package com.student.controller;

import com.student.entity.Student;
import com.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Optional<Student> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping("/save")
    public Student saveStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @PatchMapping("/{id}")
    public HttpEntity<?> partialUpdateStudent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Student> existingStudentOptional = studentService.getStudentById(id);

        return existingStudentOptional.map(existingStudent -> {
            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                switch (fieldName.toLowerCase()) {
                    case "name":
                        existingStudent.setName(value.toString());
                        break;

                    case "age":
                        try {
                            existingStudent.setAge(value != null ? Integer.parseInt(value.toString()) : existingStudent.getAge());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case "city":
                        existingStudent.setCity(value.toString());
                        break;

                    default:
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }

            Student updatedStudentEntity = studentService.saveStudent(existingStudent);
            return new ResponseEntity<>(updatedStudentEntity, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}