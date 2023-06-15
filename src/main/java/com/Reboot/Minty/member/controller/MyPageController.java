package com.Reboot.Minty.member.controller;


import com.Reboot.Minty.member.dto.AttendanceDto;
import com.Reboot.Minty.member.entity.Attendance;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.member.service.AttendanceService;
import com.Reboot.Minty.member.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MyPageController {

    private  final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public MyPageController(AttendanceService attendanceService, UserRepository userRepository, UserService userService) {
        this.attendanceService = attendanceService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("mypage")
    public String showMyPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        System.out.println(user.getNickName());
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("errorMessage", "회원 정보를 찾을 수 없습니다.");
        }

        return "member/myPage";
    }

    @PostMapping("/attendance/save")
    @ResponseBody
    public Attendance saveAttendance(@RequestBody AttendanceDto attendanceDto, HttpSession session) {
        try {
            LocalDate date = attendanceDto.getDate();
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

            if (user == null) {
                System.out.println("사용자를 찾을 수 없습니다.");
                return null;
            }

            Attendance existingAttendance = attendanceService.getAttendanceByDateAndUser(date, user);
            if (existingAttendance != null) {
                System.out.println("해당 날짜에 이미 출석 정보가 있습니다.");
                return null;
            }

            Attendance savedAttendance = attendanceService.saveAttendance(user, date);
            return savedAttendance;
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("출석 정보를 저장하는 중에 오류가 발생했습니다.");
        }
    }

    @GetMapping("/attendance/checkStatus/{date}")
    @ResponseBody
    public boolean getCheckStatusByDate(@PathVariable("date") String dateString, HttpSession session) {
        try {
            if (dateString.equals("favicon.ico")) {
                return false;
            }
            LocalDate date = LocalDate.parse(dateString);
            Long userId = (Long) session.getAttribute("userId");
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                System.out.println("사용자를 찾을 수 없습니다.");
                return false;
            }
            Attendance attendance = attendanceService.getAttendanceByDateAndUser(date, user);
            return attendance != null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("출석 정보를 확인하는 중에 오류가 발생했습니다.");
        }
    }

}