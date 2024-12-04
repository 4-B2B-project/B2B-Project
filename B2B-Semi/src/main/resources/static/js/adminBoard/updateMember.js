const cancelBtn = document.querySelector("#goToList");
cancelBtn.addEventListener("click", () => {
	
	const URLParams = new URLSearchParams(window.location.search);

	const key = URLParams.get('key');
	const search = URLParams.get('search');
	const cp = URLParams.get('cp');
	
	if(key == null) {
		window.location.href = `memberManage?cp=${cp}`;
	}
	else {
		window.location.href = `memberManage?cp=${cp}&key=${key}&search=${search}`;
	}

});

// 다음 주소 API
function execDaumPostcode() {
  new daum.Postcode({
      oncomplete: function (data) {
          // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

          // 각 주소의 노출 규칙에 따라 주소를 조합한다.
          // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
          var addr = ''; // 주소 변수
          var extraAddr = ''; // 참고항목 변수

          //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
          if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
              addr = data.roadAddress;
          } else { // 사용자가 지번 주소를 선택했을 경우(J)
              addr = data.jibunAddress;
          }

          // 우편번호와 주소 정보를 해당 필드에 넣는다.
          document.getElementById('postcode').value = data.zonecode;
          document.getElementById("address").value = addr;

          // 커서를 상세주소 필드로 이동한다.
          document.getElementById("detailAddress").focus();

          // detailAddress 값 초기화
          document.getElementById("detailAddress").value = "";
      }
  }).open();
}


// 주소 검색 버튼 클릭 시 (myPage-info.html 외에도 문제가 발생하지 않도록 예외처리 해둔 부분!)
document.querySelector("#searchAddress").addEventListener("click", execDaumPostcode);
if(searchAddress != null) { // 화면상에 id가 searchAddress인 요소가 존재하는 경우에만.
    
    searchAddress.addEventListener("click", execDaumPostcode);
};
