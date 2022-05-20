package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.dto.HistoryInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;
import jp.co.seattle.library.rowMapper.HistoryInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id, title, author, publisher, publish_date, thumbnail_url, thumbnail_name from books ORDER BY title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }
    /**
     * 貸出し履歴の書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<HistoryInfo> getHistoryList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<HistoryInfo> getedHistoryList = jdbcTemplate.query(
                "select books.title,books.id, rentbooks.rending_date, rentbooks.return_date from books inner join rentbooks on books.id = rentbooks.book_id",
                new HistoryInfoRowMapper());

        return getedHistoryList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    
    
    public int max_id() {
        // JSPに渡すデータを設定する
        String sql = "SELECT max(id) FROM books WHERE id = (SELECT MAX(id) FROM books)";
        int bookId = jdbcTemplate.queryForObject(sql, Integer.class);

        return bookId;
    }
    
    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "select books.id, title, author, publisher, publish_date, thumbnail_url, thumbnail_name, description, isbn ,case when rending_date notnull then '貸出し中' else '貸出し可' end as status from books left join rentbooks on books.id = rentbooks.book_id where books.id = "+ bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, author,publisher,publish_date,thumbnail_name,thumbnail_url, isbn, description, reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" 
        		+ bookInfo.getAuthor() + "','" 
                + bookInfo.getPublisher() + "','" 
        		+ bookInfo.getPublishDate() +"','" 
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "',"
                + "now(),"
                + "now())";

        jdbcTemplate.update(sql);
    }
    /**
     * 書籍を一括登録する
     *
     * @param bookInfo 書籍情報
     */
    public void bulkRegist(BookDetailsInfo bookInfo) {

        String sql = "INSERT INTO books (title, author,publisher,publish_date, isbn, description, reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" 
        		+ bookInfo.getAuthor() + "','" 
                + bookInfo.getPublisher() + "','" 
        		+ bookInfo.getPublishDate() +"','" 
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "',"
                + "now(),"
                + "now())";

        jdbcTemplate.update(sql);
    }
    
    /**
     * 書籍を削除する
     *
     * @param bookId 書籍情報
     */
public void deleteBook(int bookId) {

	    String sql = "delete from books where id =" + bookId;
	
	    jdbcTemplate.update(sql);
	}

/**
 * 書籍の情報を更新する
 *
 * @param bookInfo 書籍情報
 */
public void editBook(BookDetailsInfo bookInfo) {
	String sql = "UPDATE books SET title ='" + bookInfo.getTitle() 
	+ "', author = '" + bookInfo.getAuthor() 
	+ "', publisher = '" +  bookInfo.getPublisher() 
	+ "', publish_date = '" + bookInfo.getPublishDate() 
	+ "', thumbnail_url ='" + bookInfo.getThumbnailUrl() 
	+ "', isbn ='"  + bookInfo.getIsbn() 
    + "', upd_date = now(), description ='" + bookInfo.getDescription() 
	+ "' Where id = " + bookInfo.getBookId();
	
	 jdbcTemplate.update(sql);
}

/**
 * 書籍情報を貸出しテーブルに追加する
 *
 * @param bookId 書籍ID
 * @return 書籍情報
 */
public void rentBook(int bookId) {

    String sql = "INSERT INTO rentbooks (book_id, rending_date) SELECT " + bookId + ", now() where NOT EXISTS (select book_id from rentbooks where rentbooks.book_id = " + bookId +")";
    
	    jdbcTemplate.update(sql);
	}

public void reRentBook(int bookId) {
	 String sql = "UPDATE rentbooks SET rending_date  = now(), return_date = null WHERE book_id = " + bookId;
	 
	 jdbcTemplate.update(sql);
}

public boolean exist(int bookId) {
	String sql = "SELECT EXISTS (SELECT book_id FROM rentbooks WHERE book_id =" + bookId + ")";
	return jdbcTemplate.queryForObject(sql, boolean.class);
}

/**
 * 書籍の貸出し状況を確認する
 *
 * @param bookId 書籍情報
 */
public int count(int bookId) {
	String sql = "SELECT COUNT(rending_date) FROM rentbooks WHERE book_id =" + bookId;
	
	return jdbcTemplate.queryForObject(sql, int.class);
}


/**
 * 書籍を返却する
 *
 * @param bookId 書籍情報
 */
public void returnBook(int bookId) {
	String sql = "UPDATE rentbooks SET rending_date  = null, return_date = now() WHERE book_id =" +bookId;
	jdbcTemplate.update(sql);
}

//書籍を検索する
//検索結果に一致した本リスト
public List<BookInfo> getSearchBookList(String title) {

  // TODO 取得したい情報を取得するようにSQLを修正
  List<BookInfo> getedSearchBookList = jdbcTemplate.query(
          "select id, title, author, publisher, publish_date, thumbnail_url, thumbnail_name from books where title like '%" + title + "%' ORDER BY title asc",
          new BookInfoRowMapper());

  return getedSearchBookList;
}

}