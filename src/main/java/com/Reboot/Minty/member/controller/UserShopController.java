package com.Reboot.Minty.member.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserShopController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "usershop")
    public String usershop(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Long userId = user.getId();

        List<Review> myReviews = reviewService.getReviewsByBuyerId(userId);

        model.addAttribute("myReviews", myReviews);

        return "member/userShop";
    }

}
