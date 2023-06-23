package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.entity.SubCategory;
import lombok.Data;

@Data
public class TradeBoardSearchDto {
    String sortBy;
    String searchQuery="";
    SubCategory subCategory;
    int minPrice;
    int maxPrice;
}
