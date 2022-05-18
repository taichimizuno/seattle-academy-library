package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

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
        String sql = "select books.id, title, author, publisher, publish_date, thumbnail_url, thumbnail_name, description, isbn ,case when book_id notnull then '貸出し中' else '貸出し可' end as status from books left join rentbooks on books.id = rentbooks.book_id where books.id = "+ bookId;

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


//書籍を削除する
public void deleteBook(int bookId) {

	    String sql = "delete from books where id =" + bookId;
	
	    jdbcTemplate.update(sql);
	}

//本の情報を更新する
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

//書籍を貸出し状態にする
public void rentBook(int bookId) {

    String sql = "INSERT INTO rentbooks (book_id) SELECT " + bookId + " where NOT EXISTS (select book_id from rentbooks where rentbooks.book_id = " + bookId +")";
    
	    jdbcTemplate.update(sql);
	}

//書籍を返却する
public int count(int bookId) {
	String sql = "SELECT COUNT(*) FROM rentbooks WHERE book_id =" + bookId;
	return jdbcTemplate.queryForObject(sql, int.class);
}

public void returnBook(int bookId) {
	String sql = "delete from rentbooks where book_id =" + bookId;
	jdbcTemplate.update(sql);
}


}