
package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;
import java.time.format.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.controllers.*;
import com.example.demo.repositories.*;
import com.example.demo.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DoctorController.class)
class DoctorControllerUnitTest{

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Should create doctor")
    void shouldCreateDoctor() throws Exception {

        Doctor doctor2 = new Doctor("Alma", "Perez", 40, "alma@perez.com");

        mockMvc.perform(post("/api/doctor").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctor2)))
                .andExpect(status().isCreated());
    }

    @Nested
    @DisplayName("Get All Doctors")
    class GetAllDoctors{

        @Test
        @DisplayName("Should get No doctors")
        void shouldGetNoDoctors() throws Exception {
            List<Doctor> doctors = new ArrayList<>();
            when(doctorRepository.findAll()).thenReturn(doctors);
            mockMvc.perform(get("/api/doctors"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should get two doctors")
        void shouldGetTwoDoctors() throws Exception{

            Doctor doctor1 = new Doctor("Alonso", "Gonzalez", 22, "alonso@gonzalez.com");
            Doctor doctor2 = new Doctor("Alma", "Perez", 40, "alma@perez.com");

            when(doctorRepository.findAll()).thenReturn(Stream.of(doctor1, doctor2).collect(Collectors.toList()));
            mockMvc.perform(get("/api/doctors"))
                    .andExpect(status().isOk());

        }
    }

    @Nested
    @DisplayName("Get Doctor By Id")
    class GetDoctorById{
        @Test
        @DisplayName("Should get doctor by id")
        void shouldGetDoctorById() throws Exception {

            Doctor doctor1 = new Doctor("Alonso", "Gonzalez", 22, "alonso@gonzalez.com");
            doctor1.setId(5);
            Optional<Doctor> optionalDoctor = Optional.of(doctor1);
            assertThat(optionalDoctor).isPresent();
            assertThat(optionalDoctor.get().getId()).isEqualTo(doctor1.getId());
            assertThat(doctor1.getId()).isEqualTo(5);

            when(doctorRepository.findById(doctor1.getId())).thenReturn(optionalDoctor);
            mockMvc.perform(get("/api/doctors/" + doctor1.getId()))
                    .andExpect(status().isOk());

        }

        @Test
        @DisplayName("Should get No doctor by id")
        void shouldGetNoDoctorById() throws Exception {
            long id = 1;

            when(doctorRepository.findById(id)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/doctors/" + id))
                    .andExpect(status().isNotFound());

        }
    }

    @Nested
    @DisplayName("Delete Doctor")
    class DeleteDoctor{
        @Test
        @DisplayName("Should delete doctor by Id")
        void shouldDeleteDoctorById() throws Exception {
            Doctor doctor1 = new Doctor("Alonso", "Gonzalez", 22, "alonso@gonzalez.com");
            doctor1.setId(2);

            Optional<Doctor> optionalDoctor = Optional.of(doctor1);

            assertThat(optionalDoctor).isPresent();
            assertThat(optionalDoctor.get().getId()).isEqualTo(doctor1.getId());
            assertThat(doctor1.getId()).isEqualTo(2);

            when(doctorRepository.findById(doctor1.getId())).thenReturn(optionalDoctor);
            mockMvc.perform(delete("/api/doctors/" + doctor1.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should Not delete doctor by Id")
        void shouldNotDeleteDoctorById() throws Exception {
            long id = 3;

            mockMvc.perform(delete("/api/doctors/" + id))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("Delete all doctors")
    void deleteAllDoctors() throws Exception {

        mockMvc.perform(delete("/api/doctors"))
                .andExpect(status().isOk());
    }

}

@WebMvcTest(PatientController.class)
class PatientControllerUnitTest{

    @MockBean
    private PatientRepository patientRepository;

    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get All Patients")
    class GetAllPatients{
        @Test
        @DisplayName("Should get two patients httpResponse")
        void shouldGetTwoPatientsHttpResponse() throws Exception {

            Patient patient1 = new Patient("Antonio", "Roca", 22, "antonio@roca.com");
            Patient patient2 = new Patient("Rosa", "Mayo", 60, "rosa@mayo.com");

            when(patientRepository.findAll()).thenReturn(Stream.of(patient1, patient2).collect(Collectors.toList()));

            mockMvc.perform(get("/api/patients"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get two patients JSONResponse")
        void shouldGetTwoPatientsJsonResponse() throws Exception {

            Patient patient1 = new Patient("Antonio", "Roca", 22, "antonio@roca.com");
            Patient patient2 = new Patient("Rosa", "Mayo", 60, "rosa@mayo.com");

            List<Patient> patients = Stream.of(patient1, patient2).collect(Collectors.toList());

            when(patientRepository.findAll()).thenReturn(patients);

            mockMvc.perform(get("/api/patients")).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(objectMapper.writeValueAsString(patients)));
        }

        @Test
        @DisplayName("Should get No patients")
        void shouldNotGetPatients() throws Exception {

            List<Patient> patients = new ArrayList<>();

            when(patientRepository.findAll()).thenReturn(patients);
            mockMvc.perform(get("/api/patients")).andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Should Get Patient By Id")
    class GetPatientById{
        Patient patient;

        @BeforeEach
        void initialization(){
            patient = new Patient("Antonio", "Roca", 22, "antonio@roca.com");
            patient.setId(3);

            Optional<Patient> optionalPatient = Optional.of(patient);

            assertThat(optionalPatient).isPresent();
            assertThat(patient.getId()).isEqualTo(3);
            assertThat(optionalPatient.get().getId()).isEqualTo(patient.getId());

            when(patientRepository.findById(patient.getId())).thenReturn(optionalPatient);
        }
        @Test
        @DisplayName("Should get patient by Id")
        void shouldGetPatientById() throws Exception {

            mockMvc.perform(get("/api/patients/" + patient.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get patient by Id Response")
        void shouldGetPatientByIdResponse() throws Exception {

            mockMvc.perform(get("/api/patients/" + patient.getId()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(objectMapper.writeValueAsString(patient)));
        }
    }

    @Test
    @DisplayName("Should Not get patient by Id")
    void shouldNotGetPatientById() throws Exception {
        long id = 5;
        when(patientRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/patients/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create patient")
    void shouldCreatePatient() throws Exception {

        Patient patient = new Patient("Alma", "Perez", 40, "alma@perez.com");

        mockMvc.perform(post("/api/patient").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isCreated());
    }

    @Nested
    @DisplayName("Delete Patient")
    class DeletePatient{

        @Test
        @DisplayName("Should delete patient by Id")
        void shouldDeletePatientById() throws Exception {
            Patient patient = new Patient("Alonso", "Gonzalez", 22, "alonso@gonzalez.com");
            patient.setId(1);

            Optional<Patient> optionalPatient = Optional.of(patient);

            assertThat(optionalPatient).isPresent();
            assertThat(optionalPatient.get().getId()).isEqualTo(patient.getId());
            assertThat(patient.getId()).isEqualTo(1);

            when(patientRepository.findById(patient.getId())).thenReturn(optionalPatient);
            mockMvc.perform(delete("/api/patients/" + patient.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should Not delete patient by Id")
        void shouldNotDeletePatientById() throws Exception {
            long id = 3;

            mockMvc.perform(delete("/api/patients/" + id))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("Delete all patients")
    void deleteAllPatients() throws Exception {

        mockMvc.perform(delete("/api/patients"))
                .andExpect(status().isOk());
    }
}

@WebMvcTest(RoomController.class)
class RoomControllerUnitTest{

    @MockBean
    private RoomRepository roomRepository;

    @Autowired 
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Get All Rooms")
    class GetAllRooms{
        @Test
        @DisplayName("Should get two rooms httpResponse")
        void shouldGetTwoRoomsHttpResponse() throws Exception {

            Room room1 = new Room("Osteopath");
            Room room2 = new Room("Dentist");
            when(roomRepository.findAll()).thenReturn(Stream.of(room1, room2).collect(Collectors.toList()));

            mockMvc.perform(get("/api/rooms"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get two rooms JSONResponse")
        void shouldGetTwoRoomsJsonResponse() throws Exception {

            Room room1 = new Room("Osteopath");
            Room room2 = new Room("Dentist");

            List<Room> rooms = Stream.of(room1, room2).collect(Collectors.toList());

            when(roomRepository.findAll()).thenReturn(rooms);

            mockMvc.perform(get("/api/rooms")).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(objectMapper.writeValueAsString(rooms)));
        }

        @Test
        @DisplayName("Should get No rooms")
        void shouldNotGetRooms() throws Exception {

            List<Room> room = new ArrayList<>();

            when(roomRepository.findAll()).thenReturn(room);
            mockMvc.perform(get("/api/rooms")).andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Should Get Room By RoomName")
    class GetRoomByRoomName{
        Room room;

        @BeforeEach
        void initialization(){

            room = new Room("Osteopath");

            Optional<Room> optionalRoom = Optional.of(room);

            assertThat(optionalRoom).isPresent();
            assertThat(room.getRoomName()).isEqualTo("Osteopath");
            assertThat(optionalRoom.get().getRoomName()).isEqualTo(room.getRoomName());

            when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(optionalRoom);
        }
        @Test
        @DisplayName("Should get room by RoomName HttpStatus")
        void shouldGetRoomByRoomNameHttpStatus() throws Exception {

            mockMvc.perform(get("/api/rooms/" + room.getRoomName()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get room by RoomName Response")
        void shouldGetRoomByRoomNameResponse() throws Exception {

            mockMvc.perform(get("/api/rooms/" + room.getRoomName()))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(objectMapper.writeValueAsString(room)));
        }
    }

    @Test
    @DisplayName("Should Not get room by RoomName")
    void shouldNotGetRoomByRoomName() throws Exception {
        String roomName = "Dentist";
        when(roomRepository.findByRoomName(roomName)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/patients/" + roomName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create room")
    void shouldCreateRoom() throws Exception {

        Room room = new Room("Osteopath");
        mockMvc.perform(post("/api/room").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isCreated());
    }

    @Nested
    @DisplayName("Delete Room")
    class DeleteRoom{

        @Test
        @DisplayName("Should delete room by RoomName")
        void shouldDeleteRoomByRomName() throws Exception {
            Room room = new Room("Osteopath");

            Optional<Room> optionalRoom = Optional.of(room);

            assertThat(optionalRoom).isPresent();
            assertThat(optionalRoom.get().getRoomName()).isEqualTo(room.getRoomName());
            assertThat(room.getRoomName()).isEqualTo("Osteopath");

            when(roomRepository.findByRoomName(room.getRoomName())).thenReturn(optionalRoom);
            mockMvc.perform(delete("/api/rooms/" + room.getRoomName()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should Not delete room by RoomName")
        void shouldNotDeleteRoomByRoomName() throws Exception {
            String roomName = "Dentist";

            mockMvc.perform(delete("/api/rooms/" + roomName))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("Delete all rooms")
    void deleteAllRooms() throws Exception {

        mockMvc.perform(delete("/api/rooms"))
                .andExpect(status().isOk());
    }

}
