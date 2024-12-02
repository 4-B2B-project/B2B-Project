// 댓글 목록 조회
const selectCommentList = () => {
	fetch("/myPage/comment?boardNo=" + boardNo)
			.then(response => response.json())
			.then(commentList => {
					console.log(commentList);
					const ul = document.querySelector("#commentList");
					ul.innerHTML = ""; // 기존 댓글 목록 삭제

					for (let comment of commentList) {
							// 행(li) 생성 + 클래스 추가
							const commentRow = document.createElement("li");
							commentRow.classList.add("comment-row");

							// 대댓글(자식 댓글)인 경우 "child-comment" 클래스 추가
							if (comment.parentCommentNo != 0)
									commentRow.classList.add("child-comment");

							// 만약 삭제된 댓글이지만 자식 댓글이 존재하는 경우
							if (comment.commentDelFl == 'Y')
									commentRow.innerText = "삭제된 댓글 입니다";
							else {
									const commentWriter = document.createElement("p");
									commentWriter.classList.add("comment-writer");

									// 프로필 이미지
									const profileImg = document.createElement("img");
									if (comment.profileImg == null)
											profileImg.src = userDefaultIamge; // 기본 이미지
									else
											profileImg.src = comment.profileImg; // 회원 이미지

									// 닉네임
									const nickname = document.createElement("span");
									nickname.innerText = comment.memberNickname;

									// 날짜(작성일)
									const commentDate = document.createElement("span");
									commentDate.classList.add("comment-date");
									commentDate.innerText = comment.commentWriteDate;

									// 작성자 영역(commentWriter)에 프로필, 닉네임, 날짜 추가
									commentWriter.append(profileImg, nickname, commentDate);
									commentRow.append(commentWriter);

									// 댓글 내용
									const content = document.createElement("p");
									content.classList.add("comment-content");
									content.innerText = comment.commentContent;
									commentRow.append(content);

									// 버튼 영역
									const commentBtnArea = document.createElement("div");
									commentBtnArea.classList.add("comment-btn-area");

									if (loginMemberNo != null) {
											// 답글 버튼
											const childCommentBtn = document.createElement("button");
											childCommentBtn.className = "btn btn-sm btn-info";
											childCommentBtn.innerText = "답글";
											// 답글 버튼에 onclick 이벤트 리스너 추가 
											childCommentBtn.setAttribute("onclick", `showInsertComment(${comment.commentNo}, this)`);
											commentBtnArea.append(childCommentBtn);
									}

									// 로그인한 회원 번호가 댓글 작성자 번호와 같을 때 댓글 수정/삭제 버튼 출력
									if (loginMemberNo != null && loginMemberNo == comment.memberNo) {
											// 수정 버튼
											const updateBtn = document.createElement("button");
											updateBtn.className = "btn btn-sm btn-primary";
											updateBtn.innerText = "수정";
											updateBtn.setAttribute("onclick", `showUpdateComment(${comment.commentNo}, this)`);

											// 삭제 버튼
											const deleteBtn = document.createElement("button");
											deleteBtn.className = "btn btn-sm btn-danger";
											deleteBtn.innerText = "삭제";
											deleteBtn.setAttribute("onclick", `deleteComment(${comment.commentNo})`);

											commentBtnArea.append(updateBtn, deleteBtn);
									}
									commentRow.append(commentBtnArea);
							}
							ul.append(commentRow);
					}
			});
}

selectCommentList();

/* ***** 댓글 등록(ajax) ***** */
const addContent = document.querySelector("#addComment");
const commentContent = document.querySelector("#commentContent");

addContent.addEventListener("click", e => {
	if (loginMemberNo == null) {
			alert("로그인 후 이용해 주세요");
			return;
	}

	// 유효성 검사
	if (commentContent.value.trim().length === 0) {
			alert("내용 작성 후 등록 버튼을 클릭해 주세요");
			commentContent.focus();
			return;
	}

	const data = {
			"commentContent": commentContent.value,
			"boardNo": boardNo,
			"memberNo": loginMemberNo
	};

	fetch("/comment", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data)
	})
	.then(response => response.text())
	.then(result => {
			if (result > 0) {
					alert("댓글이 등록 되었습니다");
					commentContent.value = "";
					selectCommentList(); // 댓글 목록 다시 조회
			} else {
					alert("댓글 등록 실패");
			}
	})
	.catch(err => console.log(err));
});

// 답글 작성화면 추가
const showInsertComment = (parentCommentNo, btn) => {
	const temp = document.getElementsByClassName("commentInsertContent");

	if (temp.length > 0) {
			if (confirm("다른 답글을 작성 중입니다. 현재 댓글에 답글을 작성 하시겠습니까?")) {
					temp[0].nextElementSibling.remove(); // 버튼 영역부터 삭제
					temp[0].remove(); // textara 삭제
			} else {
					return;
			}
	}

	const textarea = document.createElement("textarea");
	textarea.classList.add("commentInsertContent");
	btn.parentElement.after(textarea);

	const commentBtnArea = document.createElement("div");
	commentBtnArea.classList.add("comment-btn-area");

	const insertBtn = document.createElement("button");
	insertBtn.innerText = "등록";
	insertBtn.className = "btn btn-sm btn-primary";
	insertBtn.setAttribute("onclick", `insertChildComment(${parentCommentNo}, this)`);

	const cancelBtn = document.createElement("button");
	cancelBtn.innerText = "취소";
	cancelBtn.className = "btn btn-sm btn-danger";
	cancelBtn.setAttribute("onclick", "insertCancel(this)");

	commentBtnArea.append(insertBtn, cancelBtn);
	textarea.after(commentBtnArea);
}

// 답글 (자식 댓글) 작성 취소 
const insertCancel = (cancelBtn) => {
	cancelBtn.parentElement.previousElementSibling.remove();
	cancelBtn.parentElement.remove();
}

// 답글 (자식 댓글) 등록
const insertChildComment = (parentCommentNo, btn) => {
	const textarea = btn.parentElement.previousElementSibling;

	// 유효성 검사
	if (textarea.value.trim().length === 0) {
			alert("내용 작성 후 등록 버튼을 클릭해 주세요");
			textarea.focus();
			return;
	}

	const data = {
			"commentContent": textarea.value,
			"boardNo": boardNo,
			"memberNo": loginMemberNo,
			"parentCommentNo": parentCommentNo
	};

	fetch("/myPage/comment", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data)
	})
	.then(response => response.text())
	.then(result => {
			if (result > 0) {
					alert("답글이 등록 되었습니다");
					selectCommentList();
			} else {
					alert("답글 등록 실패");
			}
	})
	.catch(err => console.log(err));
}

// 댓글 삭제
const deleteComment = commentNo => {
	if (!confirm("삭제 하시겠습니까?")) return;

	fetch("/myPage/comment", {
			method: "DELETE",
			headers: { "Content-Type": "application/json" },
			body: commentNo
	})
	.then(resp => resp.text())
	.then(result => {
			if (result > 0) {
					alert("삭제 되었습니다");
					selectCommentList();
			} else {
					alert("삭제 실패");
			}
	})
	.catch(err => console.log(err));
}

// 수정 취소 시 원래 댓글 형태로 돌아가기 위한 백업 변수
let beforeCommentRow;

// 댓글 수정 화면 전환
const showUpdateComment = (commentNo, btn) => {
	const temp = document.querySelector(".update-textarea");

	if (temp != null) {
			if (confirm("수정 중인 댓글이 있습니다. 현재 댓글을 수정 하시겠습니까?")) {
					const commentRow = temp.parentElement;
					commentRow.after(beforeCommentRow);
					commentRow.remove();
			} else {
					return;
			}
	}

	const commentRow = btn.closest("li");
	beforeCommentRow = commentRow.cloneNode(true);
	let beforeContent = commentRow.children[1].innerText;

	commentRow.innerHTML = "";

	const textarea = document.createElement("textarea");
	textarea.classList.add("update-textarea");
	textarea.value = beforeContent;

	commentRow.append(textarea);

	const commentBtnArea = document.createElement("div");
	commentBtnArea.classList.add("comment-btn-area");

	const updateBtn = document.createElement("button");
	updateBtn.className = "btn btn-sm btn-primary";
	updateBtn.innerText = "수정";
	updateBtn.setAttribute("onclick", `updateComment(${commentNo}, this)`);

	const cancelBtn = document.createElement("button");
	cancelBtn.className = "btn btn-sm btn-danger";
	cancelBtn.innerText = "취소";
	cancelBtn.setAttribute("onclick", "updateCancel(this)");

	commentBtnArea.append(updateBtn, cancelBtn);
	commentRow.append(commentBtnArea);
}

// 댓글 수정 취소
const updateCancel = (btn) => {
	if (confirm("취소 하시겠습니까?")) {
			const commentRow = btn.closest("li");
			commentRow.after(beforeCommentRow);
			commentRow.remove();
	}
}

// 댓글 수정
const updateComment = (commentNo, btn) => {
	const textarea = btn.parentElement.previousElementSibling;

	// 유효성 검사
	if (textarea.value.trim().length === 0) {
			alert("댓글 작성 후 수정 버튼을 클릭해 주세요");
			textarea.focus();
			return;
	}

	const data = {
			"commentNo": commentNo,
			"commentContent": textarea.value
	}

	fetch("/myPage/comment", {
			method: "PUT",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(data)
	})
	.then(resp => resp.text())
	.then(result => {
			if (result > 0) {
					alert("댓글이 수정 되었습니다");
					selectCommentList();
			} else {
					alert("댓글 수정 실패");
			}
	})
	.catch(err => console.log(err));
}
