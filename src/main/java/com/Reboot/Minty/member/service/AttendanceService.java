package com.Reboot.Minty.member.service;

import com.Reboot.Minty.member.entity.Attendance;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Attendance getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public Attendance saveAttendance(User user, LocalDate date) {
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(date);
        attendance.setPoint(100);

        return attendanceRepository.save(attendance);
    }

    public Attendance getAttendanceByDateAndUser(LocalDate date, User user) {
        return attendanceRepository.findByDateAndUser(date, user);
    }
}