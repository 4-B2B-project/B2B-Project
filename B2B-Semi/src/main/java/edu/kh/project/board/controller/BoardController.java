package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("board")
@Controller
@Slf4j
@SessionAttributes({ "loginMember" })
@RequiredArgsConstructor
public class BoardController {

	private final BoardService service;
	
	
	/** 게시판 페이지
	 * @param model
	 * @param cp
	 * @param paramMap
	 * @return
	 */
	@GetMapping("community/{boardCode:[0-9]+}")
	public String community(Model model, 
							@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
							@RequestParam Map<String, Object> paramMap,
							@SessionAttribute(value = "loginMember", required = false) Member loginMember,
							@PathVariable("boardCode") int boardCode) {
		
		Map<String, Object> map = new HashMap<>();
		
		if (!paramMap.isEmpty()) {
			paramMap.remove("cp"); // paramMap에서 cp 제거
		}

		if (paramMap.isEmpty()) {
			// 게시판 글 목록 조회
			map = service.communityList(cp, boardCode);

		} else {
			// 게시판 글 목록 검색 조회
			paramMap.put("boardCode", boardCode);
			map = service.communitySearchList(cp, paramMap);

		}
		
		
		// 데이터 전달
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		if(loginMember != null) {
			model.addAttribute("memberAuth", loginMember.getMemberAuth());
			model.addAttribute("memberNo", loginMember.getMemberNo());
			
		} else { 
			model.addAttribute("memberAuth", 0);
			model.addAttribute("memberNo", 0);
			
		}
		
		// 검색 데이터 전달
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("cp", cp);
		model.addAttribute("searchType", paramMap.get("searchType"));
		model.addAttribute("searchInput", paramMap.get("searchInput"));
		
		return "board/community";
	}
	
	
	/** 게시판 상세 페이지
	 * @param boardCode
	 * @param boardNo
	 * @param loginMember
	 * @param req
	 * @param resp
	 * @param board
	 * @param model
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String detailCommunity(@PathVariable("boardCode") int boardCode,
								  @PathVariable("boardNo") int boardNo,
								  @SessionAttribute(value = "loginMember", required = false) Member loginMember,
								  HttpServletRequest req,
								  HttpServletResponse resp,
								  Board board,
								  Model model) {
		
		Board detailCommunity = service.detailCommunity(board);
		
		if(detailCommunity != null) {
			/* ------------------- 쿠키를 이용한 조회 수 증가 ------------------- */
			
			// 비회원 또는 로그인한 회원의 글이 아닌 경우 ( == 글쓴이를 뺀 다른 사람)
			if(loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
				
				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				
				Cookie c = null;
				
				if(cookies != null) {
				
					for(Cookie temp : cookies) {
						
						// 요청에 담긴 쿠키에 "readBoardNo" 가 존재 할 때
						if(temp.getName().equals("readBoardNo")) {
							c = temp;
							break;
						}
						
					}
				}
				
				int result = 0; // 조회수 증가 결과를 저장할 변수
				
				// "readBoardNo" 가 쿠키에 없을 때
				if(c == null) {
					
					// 새 쿠키 생성("readBoardNo", [게시글번호])
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					result = service.updateReadCount(boardNo);
					
				} else {
					// "readBoardNo" 가 쿠키에 있을 때
					// "readBoardNO" : [2][30][400][2000][4000]
					
					// 현재 글을 처음 읽는 경우
					if(c.getValue().indexOf("[" + boardNo + "]") == -1) {
						
						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "[" + boardNo + "]");
						// [2][30][400][2000][4000][2003]
						result = service.updateReadCount(boardNo);
						
					}
						
				}
				
				// 조회 수 증가 성공 / 조회 성공 시
				if(result > 0) {
					
					// 먼저 조회된 board의 readCount 값을
					// result 값으로 다시 세팅
					detailCommunity.setReadCount(result);
					
					// 쿠키 적용 경로 설정
					c.setPath("/");	//	"/" 이하 경로 요청 시 쿠키 서버로 전달
					
					// 쿠키 수명 지정
					// 현재 시간을 얻어오기
					LocalDateTime now = LocalDateTime.now();
					
					// 다음날 자정 지정
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					
					// 다음날 자정까지 남은 시간 계산 (초 단위)
					long secondUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					
					// 쿠키 수명 설정
					c.setMaxAge((int) secondUntilNextDay);
					
					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
					
				}
				
			}
			
			model.addAttribute("board", detailCommunity);
			
		}
		
		return "board/detailCommunity";
	}
	
	/** 게시판 등록 페이지 이동
	 * @param boardNo
	 * @return
	 */
	@GetMapping("boardInsert/{boardCode:[0-9]+}")
	public String boardInsert(@PathVariable("boardCode") int boardCode,
			Model model) {
		
		return "board/boardInsert";
	}
	
	
}
