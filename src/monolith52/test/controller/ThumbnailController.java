package monolith52.test.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import monolith52.bookminer.repository.BookDAO;
import monolith52.test.util.ValidateUtil;

@Controller
@RequestMapping("/thumbnail/")
public class ThumbnailController {

	@Autowired private BookDAO bookDAO;

	@RequestMapping("/{bookId}")
	public ResponseEntity<Object> getThumbnail(HttpServletResponse response, @PathVariable String bookId) {

		byte[] bytes = bookDAO.findThumbnailByBookId(ValidateUtil.parseInt(bookId, 0));
		if (bytes == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		
		try (InputStream in = new ByteArrayInputStream(bytes)) {
			HttpHeaders headers = new HttpHeaders();
			InputStreamResource resource = new InputStreamResource(in);
			headers.setContentLength(bytes.length);
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
}
