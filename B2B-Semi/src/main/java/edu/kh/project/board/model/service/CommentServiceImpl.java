package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{

	private final CommentMapper mapper;

	
	// 해당 게시글 댓글 목록 조회
	@Override
	public List<Comment> commentList(int boardNo) {
		return mapper.commentList(boardNo);
	}


	// 댓글 수정
	@Override
	public int commentUpdate(Comment comment) {
		return mapper.commentUpdate(comment);
	}


	// 댓글 삭제
	@Override
	public int commentDelete(int commentNo) {
		return mapper.commentDelete(commentNo);
	}


	// 답글 등록
	@Override
	public int replyInsert(Comment comment) {
		return mapper.replyInsert(comment);
	}
	
}
