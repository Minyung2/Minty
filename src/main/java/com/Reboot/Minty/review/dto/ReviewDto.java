package com.Reboot.Minty.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String writer;
    private String imageUrl; // 이미지 파일 URL 추가
    private String contents;
    private int rating;
    private Long sellBoardId;
    private MultipartFile imageFile;
    private MultipartFile productImage;
    private String productInfo;
    private String nickname;
    private Long buyerId;
    private String createdAt;


//    public ReviewDto(Long id, String writer, String imageUrl, String contents, int rating, Long sellBoardId,
//                     MultipartFile imageFile, MultipartFile productImage, String productInfo, String nickname,
//                     String createdAt) {
//        this.id = id;
//        this.writer = writer;
//        this.imageUrl = imageUrl;
//        this.contents = contents;
//        this.rating = rating;
//        this.sellBoardId = sellBoardId;
//        this.imageFile = imageFile;
//        this.productImage = productImage;
//        this.productInfo = productInfo;
//        this.nickname = nickname;
//        this.createdAt = createdAt;
//    }
}