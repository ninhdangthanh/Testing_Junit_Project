package com.example.demo.student;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@WebMvcTest(StudentController.class)
@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new StudentController(studentService)).build();
    }

    @Test
    public void testGetAllStudents() throws Exception {
        // Create a list of mock Student objects to be returned by the mock StudentService
        List<Student> students = Arrays.asList(new Student("John", "john@gmail.com", Gender.MALE), new Student("Jane", "jane@gmail.com", Gender.FEMALE));

        // Mock the getAllStudents() method of the StudentService to return the list of mock Student objects
        when(studentService.getAllStudents()).thenReturn(students);

        // Perform a GET request to the /api/v1/students endpoint and assert the response
        mockMvc.perform(get("/api/v1/students").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(students.size()))
                .andExpect(jsonPath("$[0].name").value(students.get(0).getName()))
                .andExpect(jsonPath("$[1].name").value(students.get(1).getName()))
                .andExpect(jsonPath("$[0].email").value(students.get(0).getEmail()))
                .andExpect(jsonPath("$[1].email").value(students.get(1).getEmail()));

        // Verify that the getAllStudents() method of the StudentService is called exactly once
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    public void testAddStudent() throws Exception {
        // create a sample student object
        Student student = new Student("John", "john@gmail.com", Gender.MALE);
        student.setId(1L);

        // configure the mock behavior of studentService.addStudent()
        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        // perform the POST request
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@gmail.com")));

        // verify that the studentService.addStudent() method was called with the correct arguments
        verify(studentService, times(1)).addStudent(eq(student));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Long studentId = 1L;

        // configure the mock behavior of studentService.deleteStudent()
        doNothing().when(studentService).deleteStudent(studentId);

        // perform the DELETE request
        mockMvc.perform(delete("/api/v1/students/{studentId}", studentId))
                .andExpect(status().isNoContent());

        // verify that the studentService.deleteStudent() method was called with the correct argument
        verify(studentService, times(1)).deleteStudent(studentId);
    }

    @Test
    void testGetStudentById() throws Exception {
        // create a mock student object
        Student student = new Student("John", "john@gmail.com", Gender.MALE);
        student.setId(1L);

        // mock the studentService.getStudentById() method to return the mock student object
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        // perform the GET request
        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@gmail.com")));

        // verify that studentService.getStudentById() was called with the correct argument
        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testGetStudentByIdNotFound() throws Exception {
        // mock the studentService.getStudentById() method to return an empty Optional
        when(studentService.getStudentById(1L)).thenReturn(Optional.empty());

        // perform the GET request
        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isNotFound());

        // verify that studentService.getStudentById() was called with the correct argument
        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testUpdateStudentById() throws Exception {
        // create a Student object to represent the updated student data
        Student updatedStudent = new Student("John", "john@gmail.com", Gender.MALE);
        updatedStudent.setId(1L);

        // mock the studentService.getStudentById() method to return a non-empty Optional
        Student originalStudent = new Student("JohnDoe", "john@gmail.com", Gender.MALE);
        originalStudent.setId(1L);
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(originalStudent));

        // mock the studentService.saveEditStudent() method to return the updated student object
        when(studentService.saveEditStudent(updatedStudent, 1L)).thenReturn(updatedStudent);

        // perform the PUT request
        mockMvc.perform(put("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@gmail.com")));

        // verify that studentService.getStudentById() and studentService.saveEditStudent() were called with the correct arguments
        verify(studentService, times(1)).getStudentById(1L);
        verify(studentService, times(1)).saveEditStudent(updatedStudent, 1L);
    }

    @Test
    void testUpdateStudentByIdNotFound() throws Exception {
        // create a Student object to represent the updated student data
        Student updatedStudent = new Student("John", "john@gmail.com", Gender.MALE);

        // mock the studentService.getStudentById() method to return an empty Optional
        when(studentService.getStudentById(1L)).thenReturn(Optional.empty());

        // perform the PUT request
        mockMvc.perform(put("/api/v1/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedStudent)))
                .andExpect(status().isNotFound());

        // verify that studentService.getStudentById() was called with the correct argument
        verify(studentService, times(1)).getStudentById(1L);

        // verify that studentService.saveEditStudent() was not called
        verifyNoMoreInteractions(studentService);
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
