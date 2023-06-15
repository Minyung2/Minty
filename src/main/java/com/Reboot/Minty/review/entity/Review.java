package com.Reboot.Minty.review.entity;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "image_url") // 이미지 파일 URL 저장하는 필드 추가
    private String imageUrl;


    @Column(name = "buyerId")
    private Long buyerId;

    @Column(name = "nickname")
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "tradeBoard_id", referencedColumnName = "id")
    private TradeBoard tradeBoard;


    private String createdAt;
    public Review() {
        // 기본 생성자 추가
    }
}
