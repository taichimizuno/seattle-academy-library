package jp.co.seattle.library.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;

/**
 * 削除コントローラー
 */
@Controller //APIの入り口
public class RentBookController {
    final static Logger logger = LoggerFactory.getLogger(RentBookController.class);
    @Autowired
    private BooksService booksService;
    /**
     * 対象書籍を削除する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    
   
    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST)
    public String rentBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome rent! The client locale is {}.", locale);
        int rentCount= booksService.count(bookId);
        boolean existBookId = booksService.exist(bookId);
        
        if (existBookId == false) {
        	booksService.rentBook(bookId);
        }else if (rentCount == 0) {
        	booksService.reRentBook(bookId);
        } else {
        	model.addAttribute("rentMessage", "貸出し済みです");
        }
        
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";

    }

}