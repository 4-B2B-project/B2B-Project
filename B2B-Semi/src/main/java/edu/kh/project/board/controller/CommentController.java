package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("comment")
@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

	private final CommentService service;
	
	/** 해당 게시글 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	@GetMapping("")
	public List<Comment> select(@RequestParam("boardNo") int boardNo) {
		log.debug("service.commentList(boardNo)" + service.commentList(boardNo));
		return service.commentList(boardNo);
	}
	
	
	/** 댓글 수정
	 * @param Comment
	 * @return
	 */
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
		return service.commentUpdate(comment);
	}
	
	/** 댓글 삭제
	 * @param commentNo
	 * @return
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		return service.commentDelete(commentNo);
	}
	
	
	/** 답글 등록
	 * @param comment
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {
		return service.replyInsert(comment);
	}
	
	
}
