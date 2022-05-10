package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class BulkRegistController {
    final static Logger logger = LoggerFactory.getLogger(BulkRegistController.class);

    @Autowired
    private BooksService booksService;
    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @RequestMapping(value = "/bulkRegist", method = RequestMethod.GET) 
    //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "bulkRegist";

    }
    
    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/bulkRegist",method = RequestMethod.POST)
    public String file (Locale locale, @RequestParam("file") MultipartFile file, Model model) {
    try (BufferedReader br = new BufferedReader(
    		new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
 
        String line;
        int lineCount = 0;
        List<String> errorMessages = new ArrayList<String>();
		List<BookDetailsInfo> bookLists = new ArrayList<BookDetailsInfo>();
		
		
		if (!br.ready()) {
		errorMessages.add("書籍情報が見つかりません");
		}
		
        while ((line = br.readLine()) != null) {
          final String[] split = line.split(",", -1);
          	
			
			
			// 行数カウントインクリメント
			lineCount++;
			//一括登録のバリデーションチェック
			
			
			if ((split[0].isEmpty() || split[1].isEmpty() || split[2].isEmpty() || split[3].isEmpty()) 
					|| (split[4].length() != 0 && (!split[4].matches ("^[0-9]*$") || (split[4].length() != 10 && split[4].length() != 13))) 
					|| (split[3].length() != 8 || !(split[3].matches ("^[0-9]*$")))) {
				errorMessages.add(lineCount + "行目でバリデーションエラーが発生しました");
			} else {
				BookDetailsInfo bookInfo = new BookDetailsInfo();
				bookInfo.setTitle(split[0]);
				bookInfo.setAuthor(split[1]);
				bookInfo.setPublisher(split[2]);
				bookInfo.setPublishDate(split[3]);
				bookInfo.setIsbn(split[4]);
//				書籍情報をlistに追加
				bookLists.add(bookInfo);
			}
			
		}	
		// エラーメッセージあればrender
		if (CollectionUtils.isEmpty(errorMessages)) {
			bookLists.forEach(bookList -> booksService.bulkRegist(bookList));
			return "redirect:home";
		} else {
			model.addAttribute("errorMessages", errorMessages);
			return "bulkRegist";
		}
	} catch (Exception e) {
		List<String> errorMessages = new ArrayList<String>();
		errorMessages.add("ファイルが読み込めません");
		model.addAttribute("errorMessages", errorMessages);
		return "bulkRegist";
	}
  }
}