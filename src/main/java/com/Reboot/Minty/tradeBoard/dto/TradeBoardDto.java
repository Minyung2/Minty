package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.entity.QSubCategory;
import com.Reboot.Minty.categories.entity.QTopCategory;
import com.Reboot.Minty.categories.entity.SubCategory;
import com.Reboot.Minty.categories.entity.TopCategory;
import com.Reboot.Minty.member.entity.QUser;
import com.Reboot.Minty.member.entity.QUserLocation;
import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.member.entity.UserLocation;
import com.Reboot.Minty.tradeBoard.constant.TradeStatus;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class TradeBoardDto {
    private Long id;
    private int price;
    private String title;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private int interesting;
    private int visit_count;
    private TopCategory topCategory;
    private SubCategory subCategory;
    private String thumbnail;
    private User user;
    private UserLocation userLocation;
    @Enumerated(EnumType.STRING)
    private TradeStatus status;


    @QueryProjection
    public TradeBoardDto(Long id, int price, String title,
                         Timestamp createdDate, Timestamp modifiedDate, int interesting,
                         int visit_count, TopCategory topCategory, SubCategory subCategory,
                         String thumbnail, User user, UserLocation userLocation,
                         TradeStatus status) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.interesting = interesting;
        this.visit_count = visit_count;
        this.topCategory = topCategory;
        this.subCategory = subCategory;
        this.thumbnail = thumbnail;
        this.user = user;
        this.userLocation = userLocation;
        this.status = status;
    }


}
