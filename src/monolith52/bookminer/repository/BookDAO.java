package monolith52.bookminer.repository;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.stereotype.Repository;

import javafx.embed.swing.SwingFXUtils;

@Repository
public class BookDAO {
	
	@Autowired private JdbcTemplate db;
	
	public boolean hasEntry(String siteId) {
		List<String> result = db.queryForList(
				"select siteId from `book` where siteId = ?", 
				String.class, siteId);
		return !result.isEmpty();
	}

	public boolean hasBinary(String siteId) {
		List<String> result = db.queryForList(
				"select siteId from `book` where siteId = ? and md5 is not null", 
				String.class, siteId);
		return !result.isEmpty();
	}
	
	public byte[] findThumbnailByBookId(Integer bookId) {
		byte[][] bytes = new byte[1][];
		db.query("select `thumbnail` from book where bookId = ?", (ResultSet rs) -> {
			bytes[0] = rs.getBytes("thumbnail");
		}, bookId);
		return bytes[0];
	}
	
	public int getCount() {
		return db.queryForObject("select count(bookId) from book", Integer.class); 
	}
	
	public List<Book> select(int limit, int offset) {
		return db.query("select `bookId`,`title`,`siteId`,`fileName`,`md5` from book order by `bookId` desc limit ? offset ?", this::mapRow, limit, offset);
	}
	
	private Book mapRow(ResultSet rs, int rowNum) throws SQLException {
		Book book = new Book();
		book.setBookId(rs.getInt("bookId"));
		book.setTitle(rs.getString("title"));
		book.setSiteId(rs.getString("siteId"));
		book.setFilename(rs.getString("fileName"));
		book.setMd5(rs.getString("md5"));
		return book;
	}
	public boolean insert(Book book) {
		BufferedImage img = SwingFXUtils.fromFXImage(book.getThumbnail(), null);
		SqlLobValue lob = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(img, "jpeg", out);
			lob = new SqlLobValue(out.toByteArray(), new DefaultLobHandler());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int result = db.update("insert into `book`(`title`,`siteId`,`fileName`,`thumbnail`,`md5`) values(?,?,?,?,?);",
				book.getTitle(),
				book.getSiteId(),
				book.getFilename(),
				new SqlParameterValue(Types.BLOB, lob),
				book.getMd5());
		return result > 0;
	}

}
