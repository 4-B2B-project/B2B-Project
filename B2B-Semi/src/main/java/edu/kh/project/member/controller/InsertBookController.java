package edu.kh.project.member.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.book.model.dto.Book;
import edu.kh.project.book.model.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("insertBook")
@RequiredArgsConstructor
public class InsertBookController {

	private final BookService service;
	
	// 도서 추가 페이지.
	@GetMapping("addBook")
	public String addBook() {
		
		return "/adminBoard/insertBook";
	}
	
		
	// 도서 검색API
	@ResponseBody
	@PostMapping("srchBookList")
	public List<Book> srchBookList(@RequestBody Map<String, Object> book) {
	
		// 입력한 제목 도서 목록 검색
		String title = (String) book.get("bookTitle");
		List<Book> bookList = service.srchBookList(title);
		log.debug("bookList : " + bookList);
		
		return bookList;
	}
		
}
