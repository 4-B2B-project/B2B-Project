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

if (comment.profileImg == null) {
	profileImg.src = userDefaultImage; // 기본 이미지 사용
} else {
	profileImg.src = comment.profileImg; // 회원 이미지 사용
}
