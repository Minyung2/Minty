package com.Reboot.Minty.tradeBoard.dto;

import com.Reboot.Minty.categories.dto.SubCategoryDto;
import com.Reboot.Minty.categories.entity.SubCategory;
import lombok.Data;

@Data
public class TradeBoardSearchDto {
    String sortBy;
    String searchQuery="";
    Long subCategoryId;
    int minPrice;
    int maxPrice;
}
