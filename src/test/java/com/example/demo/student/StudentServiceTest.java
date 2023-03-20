package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

//    @Test
//    void cangetAllStudents() {
//        //when
//        underTest.getAllStudents();
//        //then
//        verify(studentRepository).findAll();
//    }

    @Test
    public void testGetAllStudents() {
        // Given
        List<Student> students = new ArrayList<>();
        students.add(new Student("John", "John@gmail.com", Gender.MALE));
        students.add(new Student("Jane", "Jane@gmail.com", Gender.MALE));
        when(studentRepository.findAll()).thenReturn(students);

        // When
        List<Student> result = underTest.getAllStudents();

        // Then
        assertThat(result).isEqualTo(students);
    }

    @Test
    public void testGetStudentById() {
        // Mock the repository response
        Long id = 1L;
        Student student = new Student();
        student.setId(id);
        Mockito.when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        // Call the service method
        Optional<Student> result = underTest.getStudentById(id);

        // Verify the result
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(id, result.get().getId());
    }

    @Test
    void canAddStudent() {
        //given
        String email = "ninh@gmail.com";
        Student student = new Student(
                "Ninh Dang Thanh",
                email,
                Gender.MALE
        );

        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student captureStudent = studentArgumentCaptor.getValue();

        assertThat(captureStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        //given
        String email = "ninh@gmail.com";
        Student student = new Student(
                "Ninh Dang Thanh",
                email,
                Gender.MALE
        );

        given(studentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);
        //when

        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void willThrowWhenEmailIsConflict() {
        //give
        Student student = new Student(
                "Ninh Dang Thanh",
                "ninh@gmail.com",
                Gender.MALE
        );

        Long studentId = 1L;
        given(studentRepository.selectConflictEmail(student.getEmail(), studentId))
                .willReturn(true);
        //when

        //then
        assertThatThrownBy(() -> underTest.saveEditStudent(student, studentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " conflict");

        verify(studentRepository, never()).save(any());
    }

    @Test
    public void testDeleteStudent() {
        // create some test data
        Long studentId = 1L;

        // mock the repository method to return true, indicating that the student exists
        when(studentRepository.existsById(studentId)).thenReturn(true);

        // call the method being tested
        underTest.deleteStudent(studentId);

        // verify that the repository method was called with the correct arguments
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    public void testDeleteNonexistentStudent() {
        // create some test data
        Long studentId = 1L;

        // mock the repository method to return false, indicating that the student doesn't exist
        when(studentRepository.existsById(studentId)).thenReturn(false);

        // call the method being tested
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");
    }

    @Test
    void canSaveEditStudent() {
        //given
        String email = "ninh@gmail.com";
        Student student = new Student(
                "Ninh Dang Thanh",
                email,
                Gender.MALE
        );
        Long studentId = 1L;

        //when
        underTest.saveEditStudent(student, studentId);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student captureStudent = studentArgumentCaptor.getValue();

        assertThat(captureStudent).isEqualTo(student);
    }

}