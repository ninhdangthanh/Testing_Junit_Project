package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldCheckWhenStudentExistsEmail() {
        //give
        String email = "ninh@gmail.com";
        Student student = new Student(
                "Ninh Dang Thanh",
                email,
                Gender.MALE
        );
        underTest.save(student);

        //when
        Boolean expected = underTest.selectExistsEmail("ninh@gmail.com");

        //then
        assertThat(expected).isEqualTo(true);
    }

    @Test
    void itShouldCheckIfStudentEmailDoesNotExists() {
        //give
        String email = "ninh@gmail.com";

        //when
        Boolean expected = underTest.selectExistsEmail(email);

        //then
        assertThat(expected).isEqualTo(false);
    }

    @Test
    void itShouldCheckWhenStudentConflictEmail() {
        //give
        String email1 = "ninh@gmail.com";
        Student student1 = new Student(
                "Ninh Dang Thanh",
                email1,
                Gender.MALE
        );
        underTest.save(student1);

        String email2 = "thanh@gmail.com";
        Student student2 = new Student(
                "Thanh Ninh",
                email2,
                Gender.MALE
        );
        underTest.save(student2);

        String email3 = "dang@gmail.com";
        Student student3 = new Student(
                "DANG THANH NINH",
                email3,
                Gender.MALE
        );
        underTest.save(student3);

        //when
        Long studentId = 1L;
        Boolean expected = underTest.selectConflictEmail("dang@gmail.com", studentId);
        //if not conflict --> false
        //if conflict --> true

        //then
        assertThat(expected).isEqualTo(true);
    }

    @Test
    void itShouldCheckWhenStudentNOTConflictEmail() {
        //give
        String email1 = "ninh@gmail.com";
        Student student1 = new Student(
                "Ninh Dang Thanh",
                email1,
                Gender.MALE
        );
        underTest.save(student1);

        String email2 = "thanh@gmail.com";
        Student student2 = new Student(
                "Thanh Ninh",
                email2,
                Gender.MALE
        );
        underTest.save(student2);

        String email3 = "dang@gmail.com";
        Student student3 = new Student(
                "DANG THANH NINH",
                email3,
                Gender.MALE
        );
        underTest.save(student3);

        //when
        Long studentId = 1L;
        Boolean expected = underTest.selectConflictEmail("ninh@gmail.com", studentId);

        //then
        assertThat(expected).isEqualTo(false);
        //false is NOT conflict
        //true is conflict
    }
}