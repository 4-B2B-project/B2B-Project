package edu.kh.project.myPage.model.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.book.model.dto.Book;
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

	//BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
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
		Member mem = Member.builder()
				.memberNo(loginMember.getMemberNo())
				.profileImg(updatePath)
				.build();

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

}
