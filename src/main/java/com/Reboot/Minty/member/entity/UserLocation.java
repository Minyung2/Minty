package com.Reboot.Minty.member.entity;

import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name="userLocation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String latitude;

    private String longitude;

    private String address;

    //    @Column(name="user_id")
//    @JoinColumn(name = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


}
