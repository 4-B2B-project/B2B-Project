// .bookInfo 클래스를 가진 모든 DOM 요소를 선택
const bookInfoRows = document.querySelectorAll('.book-card');

// 선택된 요소들에 대해 반복 처리
bookInfoRows.forEach(row => {
	// 각 row에 클릭 이벤트 리스너 추가
	row.addEventListener('click', () => {
		const modal = new bootstrap.Modal(document.getElementById('bookDetailModal'));

		// 아래부터 row 클릭된 요소의 데이터를 가져와 세팅작업 진행

		// tr 요소에서 필요한 데이터 가져오기 (data-attributes 사용)
		const bookId = row.getAttribute('data-bookId');
		const bookTitle = row.getAttribute('data-title');
		const bookCover = row.getAttribute('data-coverUrl');
		const bookAuthor = row.getAttribute('data-author');
		const bookRating = row.getAttribute('data-rating');
		const bookGenres = row.getAttribute('data-genres');
		const bookDescription = row.getAttribute('data-description');
		const reviewCount = row.getAttribute('data-reviewCount');
		const steamCount = row.getAttribute('data-steamCount');


		// 모달 내 요소 업데이트
		document.querySelector('.book-detail-title').innerHTML = bookTitle;
		document.querySelector('.book-detail-cover').src = bookCover;
		document.querySelector('.book-detail-author').innerHTML = bookAuthor;
		document.querySelector('.book-detail-stats .stat-item:first-child span').textContent = bookRating;
		document.querySelector('.book-detail-stats .stat-item:nth-child(2) span').textContent = reviewCount;
		document.querySelector('.book-detail-stats .stat-item:last-child span').textContent = steamCount;
		document.querySelector('.avgScore').textContent = "평균 " + bookRating + " : 10.0";
		document.querySelector('.reviewCount').textContent = "총 " + reviewCount + "개의 리뷰";

		// bookId 저장
		document.querySelector('#selectBookId').value = bookId;

		// 찜 여부 조회
		isBookSteam(bookId);

		// 장르 업데이트
		const genreContainer = document.querySelector('.book-detail-genres');
		genreContainer.innerHTML = ''; // 기존 장르 제거
		bookGenres.split(',').forEach(genre => {

			// 태그 생성 및 class, text입히기
			const genreBadge = document.createElement('span');
			genreBadge.className = 'badge bg-primary me-2';
			genreBadge.textContent = genre;

			// 장르 태그 안에 자식으로 추가
			genreContainer.appendChild(genreBadge);
		});


		// 책 소개 업데이트
		document.querySelector('.book-synopsis-text').innerHTML = bookDescription;

		// 모달 보여주기
		modal.show();

		// 댓글 조회 비동기 함수
		selectReviewList(bookId);
	});
});

// 찜하기 구현
const steamBtn = document.querySelector("#steamBtn");
if(steamBtn != null) {
	steamBtn.addEventListener("click", () => {
		
		const bookId = document.getElementById("selectBookId").value;
		
		fetch("/book/steamBook", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(bookId)
		})
		.then(resp => resp.json())
		.then(result => {
			if(result == 1) {
				steamBtn.style.backgroundColor = "#4f46e5";
				steamBtn.style.color = "white";
				alert("찜 완료");
				
			} else if(result == 2) {
				steamBtn.style.backgroundColor = "white";
				steamBtn.style.color = "#4F46E5";
				alert("찜 취소")
				
			} else {
				alert("찜하기 오류 발생");
				
			}
			
		});
		
	});
	
}

// 찜 여부 조회
function isBookSteam(bookId) {
	
	if(loginMember != null) {
		memberNo = loginMember.memberNo;
		
		obj = {
			"memberNo" : memberNo,
			"bookId" : bookId
		};
		
		fetch("/book/isBookSteam"	, {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(obj)
		})
		.then(resp => resp.json())
		.then(result => {
			console.log();
			if(result == 1) {
				steamBtn.style.backgroundColor = "#4f46e5";
				steamBtn.style.color = "white";
				
			} else {
				steamBtn.style.backgroundColor = "white";
				steamBtn.style.color = "#4F46E5";
				
			}
			 		
		});
	}
	
}

// 댓글 리뷰 목록 조회
function selectReviewList(bookId) {

	// 기존 html 비우기
	document.querySelector(".review-list").innerHTML = '';

	// 댓글 목록 비동기 get 방식 조회
	fetch(`/book/selectReviewList?bookId=${bookId}`, {
		method: "GET",
		headers: { "Content-Type": "application/json" }
	})
		.then(resp => resp.json())
		.then(result => {

			// 조회되는 댓글 목록이 있을때
			if (result.length > 0) {
				result.forEach(({ MEMBER_NO, MEMBER_NICKNAME, COMMENT, STAR_POINT, WRITE_DATE, PROFILE_IMG }) => {
					// 별점은 따로 불러오기
					const starHTML = getStarHTML(STAR_POINT);
					let memberNo = "";

					if (loginMember != null) {
						memberNo = loginMember.memberNo;
					}
					
					if(PROFILE_IMG != null) {
						PROFILE_IMG = userDefaultIamge;
					}

					const reviewHTML = `
					<div class="review-item">
						<div class="review-header d-flex justify-content-between align-items-center mb-2">
							<div class="reviewer-info d-flex align-items-center">
								<img src="${PROFILE_IMG}" alt="User" class="reviewer-avatar">
								<div class="ms-2">
									<div class="reviewer-name">${MEMBER_NICKNAME}</div>
									<div class="review-date text-muted">${WRITE_DATE}</div>
								</div>
							</div>
							<div class="review-actions">
								<div class="review-rating mb-1">
									${starHTML}
								</div>
							</div>
						</div>
						<p class="review-text mb-2">${COMMENT}</p>
						<div class="review-buttons" ${memberNo === MEMBER_NO ? "" : "style='display:none;'"}> 
							<button class="btn btn-sm btn-primary me-2 edit-btn" data-action="edit">수정</button>
							<button class="btn btn-sm btn-danger" data-action="delete">삭제</button>
						</div>
					</div>
	            `;

					// html 안에 대입
					document.querySelector(".review-list").innerHTML += reviewHTML;
				});

			} else {

				// 댓글이 없을때
				const reviewHTML = `
				<div class="review-item">
					<p class="review-text mb-2">작성된 리뷰가 없습니다.</p>
				</div>
            `;

				document.querySelector(".review-list").innerHTML += reviewHTML;
			}

		});

}

// 별개수 생성 html
function getStarHTML(starPoint) {
	let starPointHTML = '';

	// 별1개 짜리 생성
	for (let i = 1; i <= starPoint / 2; i++) {
		starPointHTML += `<i class="fas fa-star text-warning"></i>`;
	}

	// 별 반개짜리 생성
	if (starPoint % 2 != 0) {
		starPointHTML += `<i class="fas fa-star-half-alt text-warning"></i>`;
	}

	// 총 점수
	starPointHTML += `&nbsp;<span>${starPoint}점</span>`;

	return starPointHTML;
}

// 별점 선택 기능
const ratingSelect = document.querySelector('.rating-select');
const stars = document.querySelectorAll('.star-rating');
const selectedRating = document.querySelector('.selected-rating');
let currentRating = 0;

// 별점 선택시
function updateStars(rating) {
	const starRating = rating / 2; // 10점 만점을 5점 만점으로 변환
	stars.forEach(star => {
		// 기존의 클래스 삭제
		star.classList.remove('full', 'half');
		const thisStarRating = parseFloat(star.dataset.rating);

		// 별점 선택한 부분의 크기값 비교
		if (thisStarRating <= Math.floor(starRating)) {
			star.classList.add('full');
		} else if (thisStarRating === Math.ceil(starRating) && starRating % 1 !== 0) {
			star.classList.add('half');
		}
	});
}


if (ratingSelect != null) {

	// 마우스 올리게되면 표시될 별점 디자인
	ratingSelect.addEventListener('mousemove', (e) => {
		const star = e.target;
		if (!star.classList.contains('star-rating')) return;

		// 별의 위치 정보를 가져옴 (좌측 상단의 좌표와 너비, 높이)
		const rect = star.getBoundingClientRect();

		// 마우스 위치가 별의 왼쪽 절반에 있는지 확인
		const isLeftHalf = e.clientX - rect.left < rect.width / 2;

		// 별의 데이터 속성에서 별점 값 가져오기
		const rating = parseFloat(star.dataset.rating);

		// 마우스 위치에 따른 별점 표시 값 계산
		// 마우스가 왼쪽 절반에 있으면 반 개 별로 계산
		const displayRating = isLeftHalf ? (rating * 2 - 1) : rating * 2;

		// 모든 별에 대해 상태를 업데이트 (전체, 반개, 비어있는 상태)
		stars.forEach(s => {
			const sRating = parseFloat(s.dataset.rating);
			s.classList.remove('full', 'half');

			if (sRating < rating) {
				s.classList.add('full');
			} else if (sRating === rating) {
				if (isLeftHalf) {
					s.classList.add('half');
				} else {
					s.classList.add('full');
				}
			}
		});

		// 선택된 별점 표시
		selectedRating.textContent = displayRating;
	});

	// 클릭시 별점 저장
	ratingSelect.addEventListener('click', (e) => {
		const star = e.target;
		if (!star.classList.contains('star-rating')) return;

		const rect = star.getBoundingClientRect();
		const isLeftHalf = e.clientX - rect.left < rect.width / 2;
		const rating = parseFloat(star.dataset.rating);

		currentRating = isLeftHalf ? (rating * 2 - 1) : rating * 2;
		selectedRating.textContent = currentRating;
		updateStars(currentRating);
	});

	// 마우스가 떠났을때
	ratingSelect.addEventListener('mouseleave', () => {
		updateStars(currentRating);
		selectedRating.textContent = currentRating;
	});

}

// 댓글 리뷰 작성
const submitReview = document.getElementById('submitReview');

// 리뷰 제출 버튼 클릭 이벤트
if (submitReview != null) {
	submitReview.addEventListener('click', function() {

		// 로그인한 멤버 번호 가져오기
		const memberNo = loginMember.memberNo;

		const reviewContent = document.getElementById('reviewContent').value;
		const bookId = document.querySelector('#selectBookId').value;

		if (currentRating === 0) {
			alert('별점을 선택해주세요.');
			return;
		}

		if (!reviewContent.trim()) {
			alert('리뷰 내용을 입력해주세요.');
			return;
		}


		// 보낼 객체 선언
		const obj = {
			"bookId": bookId,
			"starPoint": currentRating,
			"comment": reviewContent,
			"memberNo": memberNo
		}

		// 리뷰작성 진행
		fetch("/book/insertBookReview", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify(obj) // obj JS 객체를 JSON 으로 변경
		})
			.then(resp => resp.text())
			.then(result => {

				if (result > 0) {
					alert("리뷰 댓글이 등록되었습니다.");

					// 페이지 리로드
					location.reload(true);

					/*
					// 모달 데이터 갱신 및 다시 표시
					selectReviewList(bookId); // 댓글 목록 다시 불러오기
					
					// Bootstrap Modal 재활성화
					const modal = bootstrap.Modal.getInstance(document.getElementById('bookDetailModal'));
					if (modal) {
						modal.show(); // 모달 다시 표시
					}
					*/

				} else {
					alert("이미 리뷰를 작성하셨습니다.");
				}

			});

		// 폼 초기화
		currentRating = 0;
		updateStars(0);
		selectedRating.textContent = '0.0';
		document.getElementById('reviewContent').value = '';

	});
}

// 리뷰 수정 모드 전환 함수
function enterReviewEditMode(reviewItem) {
    const reviewText = reviewItem.querySelector('.review-text');
    const reviewRating = reviewItem.querySelector('.review-rating');
    const editBtn = reviewItem.querySelector('.edit-btn');
    const deleteBtn = editBtn.nextElementSibling;

    // 현재 리뷰의 텍스트와 별점 추출
    const currentText = reviewText.textContent;
    const currentStarPoint = parseFloat(reviewRating.querySelector('span').textContent);
	
	reviewItem.setAttribute('data-original-rating', currentStarPoint);

    // 수정 모드 HTML로 변경
    reviewText.innerHTML = `
        <textarea class="form-control edit-review-content">${currentText}</textarea>
    `;

    // 별점 수정 부분 추가
    const ratingHTML = `
        <div class="rating-select edit-rating" style="display: inline;">
            <span class="star-rating" data-rating="1"></span>
            <span class="star-rating" data-rating="2"></span>
            <span class="star-rating" data-rating="3"></span>
            <span class="star-rating" data-rating="4"></span>
            <span class="star-rating" data-rating="5"></span>
            <span class="selected-rating">${currentStarPoint}</span>
        </div>
    `;
    reviewRating.innerHTML = ratingHTML;

    // 버튼 변경
    editBtn.textContent = '저장';
    editBtn.classList.remove('edit-btn');
    editBtn.classList.add('save-btn');
	editBtn.setAttribute('data-action', 'save');
	
    deleteBtn.textContent = '취소';
    deleteBtn.classList.add('cancel-btn');
	deleteBtn.setAttribute('data-action', 'cancel');

    // 별점 선택 로직 추가
    setupEditRatingLogic(reviewItem, currentStarPoint);
}

// 별점 선택 로직 설정 함수
function setupEditRatingLogic(reviewItem, currentStarPoint) {
    const editRatingSelect = reviewItem.querySelector('.edit-rating');
    const editStars = editRatingSelect.querySelectorAll('.star-rating');
    const editSelectedRating = editRatingSelect.querySelector('.selected-rating');
    let editCurrentRating = currentStarPoint;

    // 초기 별점 상태 설정 함수
    function updateEditStars(rating) {
        const starRating = rating / 2; // 10점 만점을 5점 만점으로 변환
        editStars.forEach(star => {
            star.classList.remove('full', 'half');
            const thisStarRating = parseFloat(star.dataset.rating);

            if (thisStarRating <= Math.floor(starRating)) {
                star.classList.add('full');
            } else if (thisStarRating === Math.ceil(starRating) && starRating % 1 !== 0) {
                star.classList.add('half');
            }
        });
    }

    // 초기 별점 상태로 설정
    updateEditStars(currentStarPoint);

    // 마우스 이동 이벤트 리스너 추가
    editRatingSelect.addEventListener('mousemove', (e) => {
        const star = e.target;
        if (!star.classList.contains('star-rating')) return;

        const rect = star.getBoundingClientRect();
        const isLeftHalf = e.clientX - rect.left < rect.width / 2;
        const rating = parseFloat(star.dataset.rating);
        const displayRating = isLeftHalf ? (rating * 2 - 1) : rating * 2;

        editStars.forEach(s => {
            const sRating = parseFloat(s.dataset.rating);
            s.classList.remove('full', 'half');

            if (sRating < rating) {
                s.classList.add('full');
            } else if (sRating === rating) {
                if (isLeftHalf) {
                    s.classList.add('half');
                } else {
                    s.classList.add('full');
                }
            }
        });

        editSelectedRating.textContent = displayRating;
    });

    // 클릭 이벤트 리스너 추가
    editRatingSelect.addEventListener('click', (e) => {
        const star = e.target;
        if (!star.classList.contains('star-rating')) return;

        const rect = star.getBoundingClientRect();
        const isLeftHalf = e.clientX - rect.left < rect.width / 2;
        const rating = parseFloat(star.dataset.rating);

        editCurrentRating = isLeftHalf ? (rating * 2 - 1) : rating * 2;
        editSelectedRating.textContent = editCurrentRating;
    });
}

// 리뷰 저장 함수
function saveReview(reviewItem) {
    const editedText = reviewItem.querySelector('.edit-review-content').value;
    const editedRating = reviewItem.querySelector('.selected-rating').textContent;
    const bookId = document.querySelector('#selectBookId').value;
	const previousStarPoint = reviewItem.getAttribute('data-original-rating');

    // 유효성 검사
    if (!editedText.trim()) {
        alert('리뷰 내용을 입력해주세요.');
        return;
    }

    // 서버로 수정 요청
    const obj = {
        "bookId": bookId,
        "starPoint": parseFloat(editedRating),
		"previousStarPoint": parseFloat(previousStarPoint),
        "comment": editedText,
        "memberNo": loginMember.memberNo
    };

	// 댓글 리뷰 수정
    fetch("/book/updateBookReview", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj)
    })
    .then(resp => resp.text())
    .then(result => {
        if (result > 0) {
			
            alert("리뷰가 수정되었습니다.");
			// 페이지 리로드
			location.reload(true);
			
        } else {
			
            alert("리뷰 수정에 실패했습니다.");
			
			// 페이지 리로드
			location.reload(true);
			
        }
    });
	
}

// 리뷰 삭제
function deleteReview(reviewItem) {
	
	console.log(reviewItem);
	const editedRating = reviewItem.querySelector('.review-actions').textContent;
	const bookId = document.querySelector('#selectBookId').value;
	
	// 서버로 수정 요청
    const obj = {
        "bookId": bookId,
		"starPoint": parseFloat(editedRating),
        "memberNo": loginMember.memberNo
    };
	
    fetch("/book/deleteReview", {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(obj)
    })
    .then(resp => resp.text())
    .then(result => {
        if (result > 0) {
			
            alert("리뷰가 삭제되었습니다.");
			// 페이지 리로드
			location.reload(true);
			
        } else {
			
            alert("리뷰 삭제에 실패했습니다.");
			
			// 페이지 리로드
			location.reload(true);
			
        }
    });
}
 
// 수정 버튼 클릭시
document.querySelector('.review-list').addEventListener('click', function(e) {
    const button = e.target.closest('button');
    
    if (button) {
        const reviewItem = button.closest('.review-item');
        const action = button.getAttribute('data-action');
		const bookId = document.querySelector('#selectBookId').value;

        switch(action) {
            case 'edit':
                enterReviewEditMode(reviewItem);
                break;
            case 'save':
                saveReview(reviewItem);
                break;
            case 'cancel':
                selectReviewList(bookId);
                break;
			case 'delete':
				deleteReview(reviewItem);
        }
    }
});