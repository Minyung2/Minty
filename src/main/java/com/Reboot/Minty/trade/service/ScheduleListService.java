package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.ScheduleDay;
import com.Reboot.Minty.trade.entity.ScheduleDuration;
import com.Reboot.Minty.trade.repository.ScheduleDayRepository;
import com.Reboot.Minty.trade.repository.ScheduleDurationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleListService {

    private final ScheduleDayRepository scheduleDayRepository;

    private final ScheduleDurationRepository scheduleDurationRepository;

    public ScheduleListService(ScheduleDayRepository scheduleDayRepository, ScheduleDurationRepository scheduleDurationRepository) {
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleDurationRepository = scheduleDurationRepository;
    }

    public ScheduleDay getScheduleDay(User user) {
        ScheduleDay scheduleday = scheduleDayRepository.findByUserId(user);
        return scheduleday;
    }

    public List<ScheduleDuration> getScheduleDuration(User user) {
        List<ScheduleDuration> scheduleDuration = scheduleDurationRepository.findByUserId(user);
        return scheduleDuration;
    }

}
