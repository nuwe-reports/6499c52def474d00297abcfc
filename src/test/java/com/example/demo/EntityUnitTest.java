package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.demo.entities.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

	@Autowired
	private TestEntityManager entityManager;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private final LocalDateTime startsAt = LocalDateTime.parse("15:30 27/06/2023", formatter);

    private final LocalDateTime finishesAt = LocalDateTime.parse("16:30 27/06/2023", formatter);

	private Doctor doctor = new Doctor("John", "Doe", 30, "johndoe@example.com");

    private Patient patient = new Patient("John", "Doe", 30, "johndoe@example.com");;

    private final Room room1 = new Room("Dentist");

    private final Room room2 = new Room("Osteopath");

    private Appointment appointment1 = new Appointment(patient, doctor, room1, startsAt, finishesAt);
    private Appointment appointment2 = new Appointment(patient, doctor, room1, startsAt.plusHours(3), finishesAt.plusHours(4));
    private Appointment appointment3 = new Appointment(patient, doctor, room2, startsAt, finishesAt);


    @Test
    @Order(1)
    @DisplayName("Test Doctor Entity")
    public void testDoctorEntity() {

        doctor = entityManager.persistAndFlush(doctor);

        assertThat(doctor).isNotNull();
        assertThat(doctor.getId()).isEqualTo(doctor.getId());
        assertThat(doctor.getFirstName()).isEqualTo("John");
        assertThat(doctor.getLastName()).isEqualTo("Doe");
        assertThat(doctor.getAge()).isEqualTo(30);
        assertThat(doctor.getEmail()).isEqualTo("johndoe@example.com");

        entityManager.remove(doctor);
    }


    //PATIENTS

    @Test
    @Order(2)
    @DisplayName("Test Patient Entity")
    public void testPatientEntity() {

        patient = entityManager.persistAndFlush(patient);

        assertThat(patient).isNotNull();
        assertThat(patient.getId()).isEqualTo(patient.getId());
        assertThat(patient.getFirstName()).isEqualTo("John");
        assertThat(patient.getLastName()).isEqualTo("Doe");
        assertThat(patient.getAge()).isEqualTo(30);
        assertThat(patient.getEmail()).isEqualTo("johndoe@example.com");

        entityManager.remove(patient);
    }

    //ROOM

    @Test
    @Order(3)
    @DisplayName("Test Room Entity")
    public void testRoomEntity() {

        entityManager.persistAndFlush(room1);

        Room retrievedRoom = entityManager.find(Room.class, room1.getRoomName());

        assertThat(retrievedRoom).isNotNull();
        assertThat(retrievedRoom.getRoomName()).isEqualTo("Dentist");

        entityManager.remove(room1);
    }

    //APPOINTMENT

    @Test
    @Order(4)
    @DisplayName("Test Appointment Overlap Method Same Room - appointments time is different")
    public void appointmentsSameRoomDifferentTime(){

        boolean overlap = appointment1.overlaps(appointment2);
        assertThat(overlap).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("Test Appointment Overlap Method Different Rooms and same Time Returns False")
    public void appointmentOverlapMethodDifferentRoomsAndTimeReturnsFalse(){

        boolean overlap = appointment1.overlaps(appointment3);
        assertThat(overlap).isFalse();
    }

    @Test
    @Order(6)
    @DisplayName("Test Appointment Overlap Method Same Room and Time Returns True")
    public void appointmentOverlapMethodSameRoomAndTimeReturnsTrue(){

        Appointment appointment4 = appointment1;

        boolean overlap = appointment1.overlaps(appointment4);
        assertThat(overlap).isTrue();
    }

    @Test
    @Order(7)
    @DisplayName("Test Appointment Overlap Method Same Room Start at the Same Time returns True")
    public void appointmentOverlapMethodSameRoomStartAtTheSameTimeReturnsTrue(){

        appointment2.setStartsAt(startsAt);

        boolean overlap = appointment1.overlaps(appointment2);
        assertThat(overlap).isTrue();
    }

    @Test
    @Order(8)
    @DisplayName("Test Appointment Overlap Method Same Room Finish at the Same Time returns True")
    public void appointmentOverlapMethodSameRoomFinishAtTheSameTimeReturnsTrue(){

        appointment2.setFinishesAt(finishesAt);

        boolean overlap = appointment1.overlaps(appointment2);
        assertThat(overlap).isTrue();
    }

    @Test
    @Order(9)
    @DisplayName("Test Appointment Overlap Method Same Room - Second appointment starts before the first appointment is finished returns True")
    public void secondAppointmentStartsBeforeFirstAppointmentIsFinished(){

        appointment2.setStartsAt(finishesAt.minusMinutes(30));
        boolean overlap = appointment1.overlaps(appointment2);

        assertThat(overlap).isTrue();
    }
}
