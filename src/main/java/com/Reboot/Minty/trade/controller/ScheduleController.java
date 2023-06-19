package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.service.TradeBoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ScheduleController {

    private final TradeService tradeService;

    private final TradeBoardService tradeBoardService;

    @Autowired
    private UserService userService;

    @Autowired
    public ScheduleController(TradeService tradeService, TradeBoardService tradeBoardService) {
        this.tradeService = tradeService;
        this.tradeBoardService = tradeBoardService;
    }

    @GetMapping("/schedule/{tradeId}")
    public String schedule(@PathVariable(value = "tradeId") Long tradeId,Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");
        Trade trade = tradeService.getTradeDetail(tradeId);
        String role = tradeService.getRoleForTrade(tradeId, userId);
        User buyer= userService.getUserInfoById(trade.getBuyerId().getId());
        User seller= userService.getUserInfoById(trade.getSellerId().getId());

        model.addAttribute("trade", trade);
        model.addAttribute("role",role);
        model.addAttribute("buyer",buyer);
        model.addAttribute("seller",seller);
        model.addAttribute("tradeId", tradeId);
        return "trade/schedule";
    }

    @PostMapping("/updateTradeSchedule")
    @Transactional
    public ResponseEntity TradeSchedule(@RequestParam("tradeId") Long tradeId, @RequestParam("tradeDate") LocalDate tradeDate, @RequestParam("tradeTime") LocalTime tradeTime) {
        try {
            tradeService.updateTradeSchedule(tradeId, tradeDate, tradeTime);
            tradeService.updateStatus(tradeId, 2);

            // 현재 페이지를 리로드하는 JavaScript 코드를 반환
            return ResponseEntity.ok("/trade/" + tradeId);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 정보를 로그에 출력하거나 원하는 방식으로 처리할 수 있습니다.
            // 오류 페이지로 리다이렉트하거나 오류 메시지를 표시하는 등의 처리를 수행합니다.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("/schedule/"+tradeId);
        }
    }


}