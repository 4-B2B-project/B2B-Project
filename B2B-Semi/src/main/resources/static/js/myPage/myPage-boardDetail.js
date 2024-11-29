document.addEventListener("DOMContentLoaded", function () {
  // 버튼과 폼 요소 가져오기
  const editButton = document.getElementById("editButton");
  const submitContentEditButton = document.getElementById("submitContentEdit");
  const boardTitleSection = document.getElementById("boardTitleSection");
  const originalContentSection = document.getElementById("boardContentSection");
  const editTitleForm = document.getElementById("editTitleForm");
  const editContentForm = document.getElementById("editContentForm");
  const boardTitleInput = document.getElementById("boardTitleInput");
  const boardContentInput = document.getElementById("boardContentInput");
  const boardUpdateDateSection = document.querySelector('td[th\\:text="${boardDetail.boardUpdateDate}"]');
 
  // 수정 버튼 클릭 시 제목과 내용 폼 표시
  if (editButton) {
    editButton.addEventListener("click", function () {
      // 기존 제목과 내용 숨기기
      boardTitleSection.style.display = "none";
      originalContentSection.style.display = "none";

      // 수정 폼 보이기
      editTitleForm.style.display = "block";
      editContentForm.style.display = "block";

      // 수정, 삭제 버튼 숨기기
      editButton.style.display = "none";
    });
  }

  // 수정 완료 버튼 클릭 시 비동기 요청으로 데이터 전송
  if (submitContentEditButton) {
    submitContentEditButton.addEventListener("click", function () {
      const boardNo = document.querySelector("input[name='boardNo']").value;
      const boardTitle = boardTitleInput.value;
      const updatedContent = boardContentInput.value;

      // 비동기 요청 (AJAX)
      fetch("/myPage/" + boardNo + "/update", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          boardNo: boardNo,
          boardTitle: boardTitle,
          boardContent: updatedContent,
        }),
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("서버 응답에 문제가 발생했습니다.");
          }
          return response.json();
        })
        .then((data) => {
          if (data.success) {
            alert("게시글이 성공적으로 수정되었습니다.");
            
            // 수정된 내용을 화면에 반영
            boardTitleSection.textContent = boardTitle;
            originalContentSection.querySelector("p").textContent = updatedContent;

            // 수정된 작성일 화면에 반영
            if (data.boardUpdateDate) {
              boardUpdateDateSection.textContent = data.boardUpdateDate;
            }

            // 수정 폼 숨기고, 원래 내용을 다시 표시
            editTitleForm.style.display = "none";
            editContentForm.style.display = "none";
            boardTitleSection.style.display = "block";
            originalContentSection.style.display = "block";

            // 수정, 삭제 버튼 보이기
            editButton.style.display = "inline-block";
          } else {
            alert("게시글 수정에 실패했습니다. 다시 시도해주세요.");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("오류가 발생했습니다. 다시 시도해주세요.");
        });
    });
  }
});



//------------------------------------------------------------------------
/* 삭제 버튼 */
/* 삭제(POST) */
const deleteButton = document.querySelector("#deleteButton");

if(deleteButton != null){
  deleteButton.addEventListener("click", () => {

    if( !confirm("삭제 하시겠습니까?") ) {
      alert("취소됨")
      return;
    }

    // 게시글 번호 가져오기
    const boardNo = document.querySelector("input[name='boardNo']").value;

    // 비동기 요청
    fetch(`/myPage/${boardNo}/delete`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("삭제 실패");
      }
      return response.json();
    })
    .then((data) => {
      if (data.success) {
        alert("게시글이 성공적으로 삭제되었습니다.");
        // 목록 페이지로 이동
        location.href = "/myPage/boardList";
      } else {
        alert("게시글 삭제에 실패했습니다.");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("오류가 발생했습니다.");
    });
  });
}


// ---------------------------------------------------

/* 목록으로 돌아가는 버튼 */
const goToListBtn = document.querySelector("#goToListBtn");

goToListBtn.addEventListener("click", () => {

  // 상세조회 : /board/1/2011?cp=1
  // 목록     : /board/1?cp=1

  let url = location.pathname;
  url = url.substring(0, url.lastIndexOf("/"));

  location.href = url + location.search;
                        // 쿼리스트링
});

