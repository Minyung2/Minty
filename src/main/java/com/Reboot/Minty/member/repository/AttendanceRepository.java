package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.entity.Attendance;
import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findByDate(LocalDate date);
    Attendance findByDateAndUser(LocalDate date, User user);
    List<Attendance> findAllByUser(User user);
    @Query("SELECT SUM(a.point) FROM Attendance a WHERE a.user.id = :userId")
    Integer sumPointByUserId(@Param("userId") Long userId);


}