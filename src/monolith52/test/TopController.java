package monolith52.test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import monolith52.bookminer.repository.Book;
import monolith52.bookminer.repository.BookDAO;

@Controller
public class TopController {

	@Autowired private BookDAO bookDAO;
	
	@RequestMapping({"/","/index.html"})
	public String top(ModelMap model) {
		return "forward:/page:1";
	}
	
	@RequestMapping("/page:{page}")
	public String topWithPage(ModelMap model, @PathVariable Integer page) {
		if (page < 1) return "";

		int count = bookDAO.getCount();
		int pageMax = Math.max(1, ((count-1)/30)+1);
		List<Book> books = bookDAO.select(30, (page -1)*30);
		List<Integer> bookPages = IntStream.range(1, pageMax+1).boxed().collect(Collectors.toList());
		Integer next = (page < pageMax) ? page+1 : null;
		Integer back = (page > 1) ? page-1 : null;
		
		model.addAttribute("books", books);
		model.addAttribute("title", "this is title");
		model.addAttribute("topPage", page);
		model.addAttribute("topBookPages", bookPages);
		model.addAttribute("topNext", next);
		model.addAttribute("topBack", back);
		return "/index.html";
	}

	@RequestMapping({"/index_files/*"})
	public @ResponseBody FileSystemResource directFile(HttpServletRequest request) {
		String basedir = request.getServletContext().getRealPath("");
		String target = new File(request.getRequestURI()).getName();
		return new FileSystemResource(basedir + "index_files/" + target);
	}
}
