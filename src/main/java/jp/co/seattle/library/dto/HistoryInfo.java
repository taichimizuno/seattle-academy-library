package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍基本情報格納DTO
 */
@Configuration
@Data
public class HistoryInfo {

    private int bookId;

    private String title;

    private String rendingDate;
    
    private String returnDate;
    
}