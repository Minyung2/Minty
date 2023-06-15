package com.Reboot.Minty.review.service;

import com.Reboot.Minty.member.repository.UserRepository;
import com.Reboot.Minty.review.dto.ReviewDto;
import com.Reboot.Minty.review.entity.Review;
import com.Reboot.Minty.review.repository.ReviewRepository;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        List<ReviewDto> reviewDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewDto reviewDto = convertToDto(review);
            reviewDtos.add(reviewDto);
        }

        return reviewDtos;
    }


    public ReviewDto getReviewById(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isEmpty()) {
            return null; // 해당 ID의 리뷰가 존재하지 않을 경우 null 반환
        }
        Review review = reviewOptional.get(); // Optional에서 Review 객체 추출
        return convertToDto(review);
    }

    public void createReview(ReviewDto reviewDto) {
        System.out.println("Creating review: " + reviewDto.getContents());
        // ReviewDto를 Review 엔티티로 변환합니다
        Review review = convertToEntity(reviewDto);

        LocalDateTime currentTime = LocalDateTime.now();
        review.setCreatedAt(currentTime.toString());

        // 리뷰를 리포지토리에 저장합니다
        review = reviewRepository.save(review);

        // 이미지 파일을 저장하고 이미지 파일의 경로를 엔티티에 저장합니다
        String imageUrl = storeImageFile(review.getId(), reviewDto.getImageFile());
        review.setImageUrl(imageUrl);

        // 리뷰를 업데이트합니다 (이미지 파일 경로를 업데이트하기 위해)
        reviewRepository.save(review);
    }

    public void updateReview(Long id, ReviewDto reviewDto) {
        // 리포지토리에서 기존 리뷰를 가져옵니다
        Review existingReview = reviewRepository.findById(id).orElse(null);
        if (existingReview == null) {
            // 리뷰가 존재하지 않는 경우 처리합니다
            return;
        }

        // 기존 리뷰의 속성을 업데이트합니다
//        existingReview.setTitle(reviewDto.getTitle());
        existingReview.setContents(reviewDto.getContents());
        existingReview.setRating(reviewDto.getRating());

        // 이미지 파일을 저장하고 이미지 파일의 경로를 엔티티에 저장합니다
        String imageUrl = storeImageFile(id, reviewDto.getImageFile());
        existingReview.setImageUrl(imageUrl);

        // 업데이트된 리뷰를 리포지토리에 저장합니다
        reviewRepository.save(existingReview);
    }

    public void deleteReview(Long id) {
        // 리포지토리에서 리뷰를 가져옵니다
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            // 리뷰가 존재하지 않는 경우 처리합니다
            return;
        }

        // 리포지토리에서 리뷰를 삭제합니다
        reviewRepository.delete(review);

        // 연관된 이미지 파일을 삭제합니다
        deleteImageFile(id);
    }

    private Review convertToEntity(ReviewDto reviewDto) {
        return Review.builder()
                .contents(reviewDto.getContents())
                .rating(reviewDto.getRating())
                .createdAt(reviewDto.getCreatedAt())
                .buyerId(reviewDto.getBuyerId())
                .nickname(reviewDto.getNickname())
                .build();
    }
    private ReviewDto convertToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setContents(review.getContents());
        reviewDto.setNickname(review.getNickname());
        reviewDto.setCreatedAt(review.getCreatedAt());
        reviewDto.setRating(review.getRating());
        reviewDto.setBuyerId(review.getBuyerId());

        TradeBoard tradeBoard = review.getTradeBoard();
        if (tradeBoard != null) {
            reviewDto.setSellBoardId(tradeBoard.getId());
        } else {
            reviewDto.setSellBoardId(null); // 또는 적절한 기본값으로 설정
        }

        // 이미지 파일의 URL을 설정합니다
        String imageUrl = review.getImageUrl(); // 이미지 파일의 경로를 가져옴
        reviewDto.setImageUrl(imageUrl);

        return reviewDto;
    }

    private String storeImageFile(Long reviewId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        try {
            // 이미지 파일을 저장할 디렉토리를 정의합니다
            String storageDirectory = "D:/Reboot/Minty/src/main/resources/static/image/review/";
            File directory = new File(storageDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 리뷰 ID를 사용하여 고유한 파일명을 생성합니다
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String uniqueFilename = reviewId + fileExtension;

            // 이미지 파일을 저장 디렉토리에 저장합니다
            File file = new File(storageDirectory + uniqueFilename);
            imageFile.transferTo(file);

            // 저장된 파일 경로를 반환합니다
            return "/image/review/" + uniqueFilename;
        } catch (IOException e) {
            // 파일 저장 중에 오류가 발생한 경우 예외를 처리합니다
            e.printStackTrace();
            return null;
        }
    }

    private void deleteImageFile(Long reviewId) {
        // 이미지 파일을 삭제할 디렉토리와 파일명을 정의합니다
        String storageDirectory = "D:/Minty/Storage/";
        String uniqueFilename = reviewId + ".jpg"; // 이미지 파일 확장자에 맞게 수정하세요

        // 디렉토리와 파일을 삭제합니다
        File file = new File(storageDirectory + uniqueFilename);
        file.delete();
    }

    public List<Review> getReviewsByBuyerId(Long buyerId) {
        return reviewRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    //페이징 관련
//    private final int PAGE_SIZE = 3; // 페이지 당 리뷰 개수
//
//    public List<Review> getReviews(int page) {
//        List<Review> reviews = new ArrayList<>();
//
//        // 리뷰 조회 로직
//        // 페이지 번호(page)와 페이지 당 리뷰 개수(PAGE_SIZE)를 기반으로 DB에서 리뷰를 조회하여 reviews 리스트에 추가합니다.
//
//        return reviews;
//    }

}

