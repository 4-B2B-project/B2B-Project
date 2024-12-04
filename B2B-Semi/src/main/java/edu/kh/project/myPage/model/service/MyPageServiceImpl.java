package edu.kh.project.myPage.model.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.book.model.dto.Book;
import edu.kh.project.common.util.Pagination;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class) // 모든 예외 발생시 롤백 / 아니면 커밋
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:/config.properties")

public class MyPageServiceImpl implements MyPageService {

	private final MyPageMapper mapper;

	// BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	private final BCryptPasswordEncoder bcrypt;

	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/

	@Value("${my.profile.folder-path}")
	private String profileFolderPath; // C:/uploadFiles/profile/

	// 회원정보 수정
	@Override
	public int editInfo(Member inputMember, String[] memberAddress) {

		// 입력된 주소가 있을 경우
		// memberAddress를 A^^^B^^^C 형태로 가공

		// 주소 입력 X -> inputMember.getMemberAddress() -> ",,"
		if (inputMember.getMemberAddress().equals(",,")) {

			// 주소에 null 대입
			inputMember.setMemberAddress(null);
		} else { // 주소 입력 O

			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
		}

		return mapper.editInfo(inputMember);
	}

	// 비밀번호 변경
	public int changePw(Map<String, Object> paramMap, int memberNo) {

		// 1. 현재 비밀번호가 일치하는지 확인하기
		// - 현재 로그인한 회원의 암호화된 비밀번호를 DB에서 조회
		String originPw = mapper.selectPw(memberNo);

		// 입력받은 현재 비밀번호와(평문)
		// DB에서 조회한 비밀번호(암호화)를 비교
		// -> BCryptPasswordEncoder.matches(평문, 암호화된비밀번호) 사용

		// 다를 경우
		if (!bcrypt.matches((String) paramMap.get("currentPw"), originPw)) {
			return 0;
		}

		// 2. 같을경우

		// 새 비밀번호를 암호화 (BCryptPasswordEncoder.encode(평문))

		String encPw = bcrypt.encode((String) paramMap.get("newPw"));

		// 진행후 DB에 업데이트
		// SQL 전달해야하는 데이터 2개 (암호화한 새 비밀번호, 회원번호)
		// -> SQL 전달 인자 1개뿐!
		// -> 묶어서 전달 (paramMap 재활용)

		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);

		return mapper.changePw(paramMap);

	}

	// 찜한 도서 목록
	@Override
	public List<Book> selectFavoriteBooks(int memberNo) { // int memberNo

		return mapper.selectFavoriteBooks(memberNo); // memberNo
	}

	// 프로필 이미지 변경
	@Override
	public int profileImageInfo(MultipartFile profileImg, Member loginMember) throws Exception {
		// 프로필 이미지 경로 (수정할 경로)
		String updatePath = null;

		// 변경명 저장
		String rename = null;

		// 업로드한 이미지가 있을 경우
		// - 있을 경우 : 경로 조합 (클라이언트 접근 경로 + 리네임파일명)
		if (!profileImg.isEmpty()) {
			// updatePath 경로 조합

			// 1. 파일명 변경
			rename = Utility.fileRename(profileImg.getOriginalFilename());

			// 2. /myPage/profile/변경된파일명
			updatePath = profileWebPath + rename;
		}

		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		Member mem = Member.builder().memberNo(loginMember.getMemberNo()).profileImg(updatePath).build();

		// UPDATE 수행
		int result = mapper.profileImageInfo(mem);

		if (result > 0) { // DB에 수정 성공

			// 프로필 이미지를 없앤 경우(NULL로 수정한 경우)를 제외
			// -> 업로드한 이미지가 있을 경우
			if (!profileImg.isEmpty()) {
				// 파일을 서버 지정된 폴더에 저장
				profileImg.transferTo(new File(profileFolderPath + rename));
				// C:/uploadFiles/profile/변경한이름
			}

			// 세션 회원 정보에서 프로필 이미지 경로를
			// 업데이트한 경로로 변경
			loginMember.setProfileImg(updatePath);

		}

		return result;
	}

	// 게시글 목록 조회
	@Override
	public Map<String, Object> selectBoardList(int memberNo, int cp) {
		// 1. 작성자의 전체 게시글 조회
		int listCount = mapper.getBoardListCount(memberNo);

		// 2. 페이징 처리
		Pagination pagination = new Pagination(cp, listCount);
		// System.out.println(pagination); // 콘솔에 pagination 객체가 제대로 생성되었는지 확인

		// 3. RowBounds 객체 생성 (offset, limit)
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		// 4. 전체 게시글 목록 조회
		// List<Board> boardList = mapper.selectAllBoardList(memberNo,
		// pagination.getStartPage(), pagination.getLimit());
		List<Board> boardList = mapper.selectAllBoardList(memberNo, rowBounds);

		// 5. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);

		return map;

	}

	// 게시글 목록 검색 조회
	@Override
	public Map<String, Object> searchBoardList(int cp, Map<String, Object> paramMap) {

		// paramMap (searchType, searchInput, memberNo)

		// 검색된 게시글의 총 개수를 조회
		int listCount = mapper.getSearchBoardListCount(paramMap);

		Pagination pagination = new Pagination(cp, listCount);

		// 페이지네이션 계산
		int limit = pagination.getLimit(); // 10개
		int offset = (cp - 1) * limit; //
		RowBounds rowBounds = new RowBounds(offset, limit);

		// 게시글 목록 조회
		List<Board> boardList = mapper.searchBoardList(paramMap, rowBounds);

		// 결과 맵에 담기
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);

		return map;
	}

	// 댓글 목록 조회
	@Override
	public Map<String, Object> selectCommentList(int memberNo, int cp) {

		// 1. 작성자의 전체 게시글 조회
		int listCount = mapper.getCommentListCount(memberNo);

		// 2. 페이징 처리
		Pagination pagination = new Pagination(cp, listCount);

		// 3. RowBounds 객체 생성 (offset, limit)
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);

		// 4. 전체 게시글 목록 조회
		List<Comment> commentList = mapper.selectAllCommentList(memberNo, rowBounds);

		// 5. 목록 조회 결과 + Pagination 객체를 Map으로 묶음
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("commentList", commentList);

		log.debug("현재 페이지(cp): {}", cp);

		log.debug("RowBounds offset: {}, limit: {}", offset, limit);

		return map;

	}

	// 댓글 목록 검색 조회
	@Override
	public Map<String, Object> commentSearchList(int cp, Map<String, Object> paramMap) {
		// paramMap (searchType, searchInput, memberNo)

		// 검색된 게시글의 총 개수를 조회
		int listCount = mapper.getSearchCommentListCount(paramMap);

		Pagination pagination = new Pagination(cp, listCount);

		// 페이지네이션 계산
		int limit = pagination.getLimit(); // 10개
		int offset = (cp - 1) * limit; //
		RowBounds rowBounds = new RowBounds(offset, limit);

		// 게시글 목록 조회
		List<Comment> commentList = mapper.searchCommentList(paramMap, rowBounds);

		// 결과 맵에 담기
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("commentList", commentList);

		return map;
	}

	// 게시글 상세정보 조회
	@Override
	public Board selectBoardDetail(int boardNo) {

		return mapper.selectBoardDetail(boardNo);
	}

	// 게시글 상세정보 수정
	@Override
	public int boardUpdate(Board inputBoard) {
		// 1. 게시글 부분(제목/내용) 수정
		int result = mapper.boardUpdate(inputBoard);

		// 수정 실패 시 바로 리턴
		if (result == 0)
			return 0;

		return result;
	}

	// 게시글 삭제
	@Override
	public int boardDelete(Map<String, Integer> map) {
		return mapper.boardDelete(map);
	}

	// 댓글 상세정보 조회
	@Override
	public Comment selectCommentDetail(int commentNo) {

		return mapper.selectCommentDetail(commentNo);
	}

	// 댓글,답글 등록
	@Override
	public int replyInsert(Comment comment) {

		if (comment.getCommentContent() == null || comment.getCommentContent().trim().isEmpty()) {
			throw new IllegalArgumentException("댓글 내용은 반드시 입력해야 합니다.");
		}

		return mapper.replyInsert(comment);
	}

	
	// 좋아요한 도서 수(count)
	@Override
	public int getFavoriteBookCount(int memberNo) {
		 return mapper.countFavBooks(memberNo);
	}

	// 댓글 수(count)
	@Override
	public int getCommentCount(int memberNo) {
		return mapper.countComment(memberNo);
	}

	// 게시글 수(count)
	@Override
	public int getBoardCount(int memberNo) {
		return mapper.countBoard(memberNo);
	}
	
	// 댓글 수정
	@Override
	public int updateComment(Comment comment) {
		
		return mapper.updateComment(comment);
	}
	
	// 댓글 삭제
	@Override
	public int deleteComment(int commentNo, int memberNo) {
		
		return mapper.deleteComment(commentNo,memberNo);
	}

}
