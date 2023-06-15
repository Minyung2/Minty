package com.Reboot.Minty.review.controller;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.service.UserService;
import com.Reboot.Minty.review.dto.ReviewDto;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.service.ReviewService;
import com.oracle.wls.shaded.org.apache.xpath.operations.Mod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;


    // 모든 리뷰를 가져와서 모델에 추가한 후 "reviews" 뷰로 이동
    @GetMapping("/reviews")
    public String getAllReviews( Model model) {
        List<ReviewDto> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "review/reviews";
    }

    // 특정 ID에 해당하는 리뷰를 가져와서 모델에 추가한 후 "review-details" 뷰로 이동
    @GetMapping("/reviews/{id}")
    public String getReviewById(@PathVariable Long id, Model model) {
        ReviewDto review = reviewService.getReviewById(id);
        if (review == null) {
            return "error";
        }
        model.addAttribute("review", review);
        return "review/review-details";
    }

    // 리뷰 작성 폼을 보여줌
    @GetMapping("/review")
    public String showReviewForm(ReviewDto reviewDto, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        reviewDto.setNickname(user.getNickName());
        reviewDto.setId(user.getId());
//        System.out.println("nickname :" + user.getNickName());

        model.addAttribute("reviewDto", reviewDto);
//        model.addAttribute("reviewDto", new ReviewDto());
        return "review/review-form";
    }

    // 리뷰를 생성함
    @PostMapping("/")
    public String createReview(@ModelAttribute("reviewDto") @Valid ReviewDto reviewDto, BindingResult bindingResult, Principal principal,HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        reviewDto.setNickname(user.getNickName());
        reviewDto.setBuyerId(user.getId());
//        System.out.println("id :" + user.getId());
        model.addAttribute("reviewDto", reviewDto);


        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 처리 로직
        }

        MultipartFile imageFile = reviewDto.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            String originalFilename = imageFile.getOriginalFilename();
            // 파일을 저장하는 로직을 구현해야 합니다. (예: Amazon S3, 로컬 디렉토리 등)
            // reviewDto.setImageFile(저장된 파일 경로 또는 파일명);
        }

        // reviewService로 tradeBoardid 가 잇는지 조회
        // 이미 존재하면 어디론가 보내버리고 없으면 밑에 로직 실행
        reviewService.createReview(reviewDto);

        userService.increaseExp(userEmail, 10); //거래완료에도 똑같이 넣으면댐

        return "redirect:/";
    }

    // 특정 ID에 해당하는 리뷰를 삭제함
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }

    @GetMapping("/my-review")
    public String showMyReview(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        User user = userService.getUserInfo(userEmail);
        Long userId = user.getId();

        List<Review> myReviews = reviewService.getReviewsByBuyerId(userId);

        model.addAttribute("myReviews", myReviews);
        return "review/my-review";
    }

}
