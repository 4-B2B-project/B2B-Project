package edu.kh.project.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.project.member.model.service.AdminService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("insertBook")
@RequiredArgsConstructor
public class insertBook {

	private final AdminService Adservice;
	
	// 도서 추가 페이지.
	@GetMapping("addBook")
	public String addBook() {
		
		return "/adminBoard/insertBook";
	}
	
}
