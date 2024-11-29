package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;

public interface CommentService {

	/** 해당 게시글 번호 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	List<Comment> commentList(int boardNo);

	/** 댓글 수정
	 * @param comment
	 * @return
	 */
	int commentUpdate(Comment comment);

	/** 댓글 삭제
	 * @param commentNo
	 * @return
	 */
	int commentDelete(int commentNo);

	/** 답글 등록
	 * @param comment
	 * @return
	 */
	int replyInsert(Comment comment);


}
