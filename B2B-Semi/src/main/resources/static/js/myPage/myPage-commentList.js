// 댓글 삭제 함수
const deleteComment = (commentNo) => {
  if (!confirm("정말 삭제하시겠습니까?")) {
      return;
  }

  // 비동기적으로 서버에 삭제 요청
  fetch(`/myPage/comment/${commentNo}`, {
      method: "DELETE"
  })
  .then(response => response.json())
  .then(result => {
      if (result > 0) {
          alert("댓글이 삭제되었습니다.");
          // 댓글 목록을 새로 렌더링하는 함수 호출
          loadCommentList();
      } else {
          alert("댓글 삭제에 실패했습니다.");
      }
  })
  .catch(error => console.error('Error:', error));
};

// 댓글 목록 불러오기 함수
const loadCommentList = () => {
  fetch('/myPage/commentList')
      .then(response => response.json())
      .then(commentList => {
          const filteredComments = commentList.filter(comment => comment.commentDelFl === 'N');
          renderCommentList(filteredComments);
      });
};

// 댓글 목록을 화면에 렌더링하는 함수
const renderCommentList = (commentList) => {
  const commentListElement = document.querySelector("#commentList");
  commentListElement.innerHTML = '';

  if (commentList.length === 0) {
      commentListElement.innerHTML = '<tr><td colspan="4" style="text-align: center;">댓글이 존재하지 않습니다.</td></tr>';
  } else {
      commentList.forEach(comment => {
          const commentRow = `
              <tr>
                  <td>${comment.commentNo}</td>
                  <td>
                      <div class="comment-title">
                          <a href="/myPage/commentDetail?commentNo=${comment.commentNo}">${comment.commentContent}</a>
                      </div>
                  </td>
                  <td>${comment.memberNickname}</td>
                  <td>${comment.commentWriteDate}</td>
              </tr>
          `;
          commentListElement.insertAdjacentHTML('beforeend', commentRow);
      });
  }
};

// 페이지 로딩 시 댓글 목록 불러오기
document.addEventListener('DOMContentLoaded', () => {
  loadCommentList();
});
