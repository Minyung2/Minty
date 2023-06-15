package com.Reboot.Minty.trade.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.service.TradeService;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.service.TradeBoardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class TradeController {
    private final TradeService tradeService;

    private final TradeBoardService tradeBoardService;

    @Autowired
    private UserService userService;


    @Autowired
    public TradeController(TradeService tradeService, TradeBoardService tradeBoardService) {
        this.tradeService = tradeService;
        this.tradeBoardService = tradeBoardService;
    }
//
//    @GetMapping("/{tradeId}")
//    public String getTradeDetail(@PathVariable Long tradeId, HttpSession session, Model model) {
//        TradeDto tradeDto = tradeService.getTradeDetail(tradeId);
//        Long userId = (Long) session.getAttribute("userId");
//        String role = tradeService.getRoleForTrade(tradeId, userId);
//        model.addAttribute("tradeDto", tradeDto);
//        model.addAttribute("role", role);
//        return "trade/detail";
//    }
//
//    @GetMapping("/{tradeId}/review")
//    public String getReviewDetail(@PathVariable Long tradeId, HttpSession session, Model model) {
//        ReviewDto reviewDto = tradeService.getReviewDetail(tradeId);
//        Long userId = (Long) session.getAttribute("userId");
//        String role = tradeService.getRoleForTrade(tradeId, userId);
//        model.addAttribute("reviewDto", reviewDto);
//        model.addAttribute("role", role);
//        return "trade/review";
//    }
//
//    @GetMapping("/{tradeId}/review/create")
//    public String showReviewForm(@PathVariable Long tradeId, HttpSession session, Model model) {
//        TradeDto tradeDto = tradeService.getTradeDetail(tradeId);
//        Long userId = (Long) session.getAttribute("userId");
//        String role = tradeService.getRoleForTrade(tradeId, userId);
//        model.addAttribute("tradeDto", tradeDto);
//        model.addAttribute("role", role);
//        model.addAttribute("reviewDto", new ReviewDto());
//        return "trade/review-form";
//    }

//    @PostMapping("/{tradeId}/review/create")
//    public String submitReviewForm(@PathVariable Long tradeId, @ModelAttribute("reviewDto") ReviewDto reviewDto) {
//        // 후기 작성 처리 로직
//        return "redirect:/trade/" + tradeId;
//    }

    @GetMapping("/trade")
    public String getTradeList(@PathVariable(value="page") Optional<Integer> page, Model model, HttpSession session) {
        User user = userService.getUserInfoById((Long) session.getAttribute("userId"));
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() - 1 : 0, 10, Sort.by("startDate").descending());
        Page<Trade> trades = tradeService.getList(user, pageable);
        List<Trade> tradeList = trades.getContent();
        System.out.println(tradeList);
        model.addAttribute("trade", tradeList);
        return "trade/tradeList";
    }

    @GetMapping(value = "trade/{tradeId}")
    public String trade(@PathVariable(value = "tradeId") Long tradeId, Model model, HttpServletRequest request)  {
        HttpSession session = request.getSession();
        Long userId = (Long)session.getAttribute("userId");
        Trade trade = tradeService.getTradeDetail(tradeId);
        String role = tradeService.getRoleForTrade(tradeId, userId);
        User buyer= userService.getUserInfoById(trade.getBuyerId().getId());
        User seller= userService.getUserInfoById(trade.getSellerId().getId());
        model.addAttribute("trade", trade);
        model.addAttribute("role",role);
        model.addAttribute("buyer",buyer);
        model.addAttribute("seller",seller);

        return "trade/trade";
    }

    @PostMapping("/api/purchasingReq")
    @ResponseBody
    public ResponseEntity<?> purchasingReq(@RequestBody TradeBoard tradeBoard, HttpSession session) {
        try {
            User buyer = userService.getUserInfoById((Long) session.getAttribute("userId"));
            User seller = userService.getUserInfoById(tradeBoard.getUser().getId());
            Trade trade = tradeService.save(tradeBoard, buyer, seller);
            return ResponseEntity.ok("/trade/" + trade.getId());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("/trade/"+e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

}
