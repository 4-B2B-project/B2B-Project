package edu.kh.project.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Comment;

@Mapper
public interface CommentMapper {

	/** 해당 게시글 댓글 목록 조회
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
