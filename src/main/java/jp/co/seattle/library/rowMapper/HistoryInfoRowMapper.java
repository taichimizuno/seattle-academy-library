package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.HistoryInfo;

@Configuration
public class HistoryInfoRowMapper implements RowMapper<HistoryInfo> {

    @Override
    public HistoryInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Query結果（ResultSet rs）を、オブジェクトに格納する実装
    	HistoryInfo HistoryInfo = new HistoryInfo();

        // bookInfoの項目と、取得した結果(rs)のカラムをマッピングする
        HistoryInfo.setBookId(rs.getInt("id"));
        HistoryInfo.setTitle(rs.getString("title"));
        HistoryInfo.setRendingDate(rs.getString("rending_date"));
        HistoryInfo.setReturnDate(rs.getString("return_date"));

        
        return HistoryInfo;
    }

}