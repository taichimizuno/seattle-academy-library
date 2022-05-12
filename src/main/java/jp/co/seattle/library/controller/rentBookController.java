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
public class rentBookController {
    final static Logger logger = LoggerFactory.getLogger(rentBookController.class);
    @Autowired
    private BooksService booksService;
    @Autowired
    private BooksService bookdService;
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
        
        int size = booksService.count();
        booksService.rentBook(bookId);
        int count = booksService.count();
        
        if (size == count) {
        	model.addAttribute("rentMessage", "貸出し済みです");
        }
        
        model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));
        
        if (booksService.getBookInfo(bookId).getRentBookId() == 0) {
        	model.addAttribute("statusMessage","貸出し可");
        } else {
        	model.addAttribute("statusMessage","貸出し中");
        }
        return "details";

    }

}