package jp.co.seattle.library.controller;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;


/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class EditBooksController {
    final static Logger logger = LoggerFactory.getLogger(EditBooksController.class);

    @Autowired
    private BooksService booksService;
    @Autowired
    private BooksService bookdService;
    @Autowired
    private ThumbnailService thumbnailService;
    
    /**

    書籍情報を更新する
    @param locale ローケル情報
    @param title 書籍名
    @param author 著者名
    @param publisher 出版社
    @param publish_date 出版日
    @param file サムネファイル
    @palam model モデル
    @param isbn コード
    @param bio 説明文
    @param id 書籍ID
    @return 遷移先画面
    */
    @RequestMapping(value = "/editBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String editBook(Locale locale,
          @RequestParam("bookId") Integer bookId,
          Model model) {
      // デバッグ用ログ
      logger.info("Welcome detailsControler.java! The client locale is {}.", locale);

      model.addAttribute("bookDetailsInfo", bookdService.getBookInfo(bookId));

      return "editBook";
  }
    
    /**

    書籍情報を更新する
    @param locale ローケル情報
    @param title 書籍名
    @param author 著者名
    @param publisher 出版社
    @param publish_date 出版日
    @param file サムネファイル
    @palam model モデル
    @param isbn コード
    @param bio 説明文
    @param id 書籍ID
    @return 遷移先画面
    */
    
    @Transactional
    @RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
    		@RequestParam("bookId") Integer bookId,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            Model model) {
    	
    	List<String>list = new ArrayList<String>();
    	
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setBookId(bookId);
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "editBook";
            }
        }

        // 書籍情報を新規登録する
              
        if (bookInfo.getTitle().isEmpty() || bookInfo.getAuthor().isEmpty() || bookInfo.getPublisher().isEmpty() ||
        		bookInfo.getPublishDate().isEmpty()) {
        	list.add("必須項目に入力してください");
        	    
        }
        if (publishDate.length() != 8 || !publishDate.matches ("^[0-9]*$")) {
        	list.add("出版日は半角数字のYYYYMMDD形式で入力してください");
        }
        if ( isbn.length() != 0 && (!isbn.matches ("^[0-9]*$") || (isbn.length() != 10 && isbn.length() != 13))) {
        	list.add("ISBNは半角数字かつ10文字か13文字で入力してください");
        }
        if (!(list == null || list.size() ==0)) {
        	model.addAttribute("errorMessageDetails", list);
        	model.addAttribute("bookDetailsInfo", bookInfo);
    		return "editBook";
        }
        
       
        booksService.editBook(bookInfo);
        // TODO 登録した書籍の詳細情報を表示するように実装
        
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        //  詳細画面に遷移する
        return "details";
    }

}

   