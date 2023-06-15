package com.Reboot.Minty.trade.service;

import com.Reboot.Minty.member.entity.User;
import com.Reboot.Minty.trade.entity.Trade;
import com.Reboot.Minty.trade.repository.TradeRepository;
import com.Reboot.Minty.tradeBoard.entity.TradeBoard;
import com.Reboot.Minty.tradeBoard.repository.TradeBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeBoardRepository tradeBoardRepository;

    @Autowired
    public TradeService(TradeRepository tradeRepository, TradeBoardRepository tradeBoardRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeBoardRepository = tradeBoardRepository;
    }

    public Page<Trade> getList(User user, Pageable pageable) {
        return tradeRepository.findAllByBuyerIdOrSellerId(user, user, pageable);
    }

    public Trade getTradeDetail(Long tradeId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
        }

        return trade;
    }

    public Trade getReviewDetail(Long boardId) {
        Trade trade = tradeRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        return trade;
    }


    public Trade save(TradeBoard tradeBoard, User buyer, User seller) {
        tradeBoard = tradeBoardRepository.save(tradeBoard);
        Trade existingTrades = tradeRepository.findByBoardIdAndBuyerIdAndSellerId(tradeBoard, buyer, seller);
        if(existingTrades != null){
            throw new IllegalStateException(String.valueOf(existingTrades.getId()));
        }
        else {
            Trade trade = new Trade();
            trade.setBoardId(tradeBoard);
            trade.setBuyerId(buyer);
            trade.setSellerId(seller);
            trade.setStatus("대화요청");
            trade.setSellerCheck("N");
            trade.setBuyerCheck("N");
            trade.setStartDate(LocalDateTime.now());
            return tradeRepository.save(trade);
        }
    }

    public String getRoleForTrade(Long tradeId, Long userId) {
        Trade trade = tradeRepository.findById(tradeId).orElse(null);
        if (trade == null) {
            // Trade가 존재하지 않는 경우 처리할 로직을 작성하세요.
        }

        String role = "";
        if (userId.equals(trade.getBuyerId().getId())) {
            role = "buyer";
        } else if (userId.equals(trade.getSellerId().getId())) {
            role = "seller";
        } else {
            // userId가 해당 거래의 구매자나 판매자가 아닌 경우 처리할 로직을 작성하세요.
        }

        return role;
    }
}
