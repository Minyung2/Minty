package com.Reboot.Minty.trade.repository;


import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    /*@Query("SELECT t.boardId FROM Trade t WHERE t.id = :tradeId")
    Review findReviewByTradeId(@Param("tradeId") Long tradeId);*/

    Trade findByBoardId(Long boardId);

    Page<Trade> findAllByBuyerIdOrSellerId(User buyer, User seller, Pageable pageable);

    Trade findByBoardIdAndBuyerIdAndSellerId(TradeBoard tradeBoard, User buyer,  User seller);
}