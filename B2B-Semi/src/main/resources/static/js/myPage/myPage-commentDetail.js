/* 목록으로 돌아가는 버튼 */
document.addEventListener("DOMContentLoaded", function() {
	const goToListBtn = document.querySelector("#commentListBack");
	if(goToListBtn) {
			goToListBtn.addEventListener("click", () => {
					window.location.href = "/myPage/commentList";
			});
	}
});







// userDefaultImage 변수가 HTML에서 Thymeleaf로부터 전달된 상태

const profileImg = document.querySelector("#profileImage"); // 실제 이미지 태그를 선택하는 예시
if (profileImg) {
    if (comment && comment.profileImg == null) {
        profileImg.src = userDefaultImage; // 기본 이미지 사용
    } else {
        profileImg.src = comment.profileImg; // 회원 이미지 사용
    }
}

document.addEventListener("DOMContentLoaded", function() {
	const comments = /*[[${commentList}]]*/ [];  // 서버에서 commentList를 받아오는 경우

	comments.forEach(comment => {
			let profileImg = document.querySelector(`.profile-img[data-comment-id="${comment.commentNo}"]`);
			if (profileImg) {
					if (comment.profileImg == null || comment.profileImg === "") {
							profileImg.src = userDefaultImage; // 기본 이미지 사용
					} else {
							profileImg.src = comment.profileImg; // 댓글 작성자의 프로필 이미지 사용
					}
			}
	});
});
