// 취소버튼 눌렀을때 로그인 페이지로 돌아가기
document.querySelectorAll('#cancelButton').forEach(button => {
  button.addEventListener('click', function () {
      if (confirm("수정을 취소하시겠습니까?")) {
          window.location.href = '/myPage/info';
      }
  });
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



console.log(searchAddress);

if (searchAddress != null) { // 화면상에 id가 searchAddress인 요소가 존재하는 경우에만
  searchAddress.addEventListener("click", execDaumPostcode);
}

/* 회원 정보 수정 페이지 */
const updateInfo = document.querySelector("#editInfoForm"); // form 태그

// #updateInfo 요소가 존재 할 때만 수행
if (updateInfo != null) {

  // form 제출 시
  updateInfo.addEventListener("submit", async (e) => {

      // 가장 먼저 기본 이벤트 동작 중단
      e.preventDefault();


      const memberNickname = document.querySelector("#memberNickname");
      const memberTel = document.querySelector("#memberTel");
      const memberAddress = document.querySelectorAll("[name='memberAddress']");


      // 닉네임 유효성 검사
      if (memberNickname.value.trim().length === 0) {
          alert("닉네임을 입력해주세요");
          // e.preventDefault(); // 제출 막기
          return;
      }

      // 닉네임 정규식에 맞지 않으면
      let regExp = /^[가-힣\w\d]{2,10}$/;
      if (!regExp.test(memberNickname.value)) {
          alert("닉네임이 유효하지 않습니다.");
          // e.preventDefault(); // 제출 막기
          return;
      }

      // *********** 닉네임 중복검사는 개별적으로 해보기 ***********

      // 기존 닉네임이 저장되어있는 요소의 value값 얻어오기
      const currentNickname = document.querySelector("#currentNickname").value;


      // 기존 닉네임과 새로 입력된 닉네임이 다르면 중복검사 시도하기
      // -> 변경된적 없다 -> 중복검사 진행안함
      if (currentNickname !== memberNickname.value) {

          // 비동기 요청 (fetch() API 이용)
          // async / await
          // async : 비동기 함수를 만들 때 사용하는 키워드 ("이 함수 내에는 오래 걸리는 작업이 있어요!")
          // await : 비동기 작업의 결과를 기다릴 때 사용 -> !!! 반드시 async 함수 안에서만 사용 가능!!!
          //         -> "이 작업이 끝날때까지 기다려 주세요."
          const resp = await fetch("/member/checkNickname?memberNickname=" + memberNickname.value);
          const count = await resp.text();

          if (count == 1) {
              alert("이미 사용중인 닉네임입니다!");
              // e.preventDefault();
              return;
          }

      }

      // ***********************************************************

      // 전화번호 유효성 검사
      if (memberTel.value.trim().length === 0) {
          alert("전화번호를 입력해 주세요");
          // e.preventDefault();
          return;
      }

      // 전화번호 정규식에 맞지 않으면
      regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
      if (!regExp.test(memberTel.value)) {
          alert("전화번호가 유효하지 않습니다");
          // e.preventDefault();
          return;
      }


      // 주소 유효성 검사
      // 입력을 안하면 전부 안해야되고
      // 입력하면 전부 해야된다

      const addr0 = memberAddress[0].value.trim().length == 0; // t/f
      const addr1 = memberAddress[1].value.trim().length == 0; // t/f
      const addr2 = memberAddress[2].value.trim().length == 0; // t/f

      // 모두 true 인 경우만 true 저장
      const result1 = addr0 && addr1 && addr2; // 아무것도 입력 X

      // 모두 false 인 경우만 true 저장
      const result2 = !(addr0 || addr1 || addr2); // 모두 다 입력

      // 모두 입력 또는 모두 미입력이 아니면
      if (!(result1 || result2)) {
          alert("주소를 모두 작성 또는 미작성 해주세요");
          // e.preventDefault();
          return;
      }

      // 모든 검증을 통과했을 때만 폼 제출
      updateInfo.submit();

  });
}


// ------------------------------------------

/* 비밀번호 수정 */

// 비밀번호 변경 form 태그
const changePw = document.querySelector("#changePw");

if (changePw != null) {
  // 제출 되었을 때
  changePw.addEventListener("submit", e => {


      const currentPw = document.querySelector("#currentPw");
      const newPw = document.querySelector("#newPw");
      const newPwConfirm = document.querySelector("#newPwConfirm");

      // - 값을 모두 입력했는가

      let str; // undefined 상태
      if (currentPw.value.trim().length == 0) str = "현재 비밀번호를 입력해주세요";
      else if (newPw.value.trim().length == 0) str = "새 비밀번호를 입력해주세요";
      else if (newPwConfirm.value.trim().length == 0) str = "새 비밀번호 확인을 입력해주세요";

      if (str != undefined) { // str에 값이 대입됨 == if 중 하나 실행됨
          alert(str);
          e.preventDefault();
          return;
      }

      // 새 비밀번호 정규식
      const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

      if (!regExp.test(newPw.value)) {
          alert("새 비밀번호가 유효하지 않습니다");
          e.preventDefault();
          return;
      }

      // 새 비밀번호 == 새 비밀번호 확인
      if (newPw.value != newPwConfirm.value) {
          alert("새 비밀번호가 일치하지 않습니다");
          e.preventDefault();
          return;
      }


  });
};



// (최종)
// 폼 제출 시 유효성 검사

const profileForm = document.querySelector("#editInfoForm");

profileForm.addEventListener("submit", e => {

  // checkObj의 저장된 값(value) 중
  // 하나라도 false가 있으면 제출 X

  // for ~ in (객체 전용 향상된 for 문)
  for (let key in checkObj) { // checkObj 요소의 key 값을 순서대로 꺼내옴

      if (!checkObj[key]) { // 현재 접근중인 checkObj[key]의 value 값이 false 인 경우 (유효하지 않음)

          let str; // 출력할 메시지를 저장할 변수

          switch (key) {

              case "memberNickname":
                  str = "닉네임이 유효하지 않습니다"; break;

              case "memberTel":
                  str = "전화번호가 유효하지 않습니다"; break;

          }

          alert(str);

          document.getElementById(key).focus(); // 초점 이동

          e.preventDefault(); // form 태그 기본 이벤트(제출) 막기
          return;
      }
  }
});