// 다음 주소 API
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
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
const searchAddress = document.querySelector("#searchAddress");

console.log(searchAddress);

if(searchAddress != null) { // 화면상에 id가 searchAddress인 요소가 존재하는 경우에만
    searchAddress.addEventListener("click", execDaumPostcode);
}

/* 회원 정보 수정 페이지 */
const updateInfo = document.querySelector("#updateInfo"); // form 태그

// #updateInfo 요소가 존재 할 때만 수행
if(updateInfo != null) {

    // form 제출 시
    updateInfo.addEventListener("submit", async (e) => {

        // 가장 먼저 기본 이벤트 동작 중단
        e.preventDefault();

        const memberNickname = document.querySelector("#memberNickname");
        const memberTel = document.querySelector("#memberTel");
        const memberAddress = document.querySelectorAll("[name='memberAddress']");

        // 닉네임 유효성 검사
        if(memberNickname.value.trim().length === 0) {
            alert("닉네임을 입력해주세요");
            // e.preventDefault(); // 제출 막기
            return;
        }

        // 닉네임 정규식에 맞지 않으면
        let regExp = /^[가-힣\w\d]{2,10}$/;
        if( !regExp.test(memberNickname.value)) {
            alert("닉네임이 유효하지 않습니다.");
            // e.preventDefault(); // 제출 막기
            return;
        }

        // *********** 닉네임 중복검사는 개별적으로 해보기 ***********

        // 기존 닉네임이 저장되어있는 요소의 value값 얻어오기
        const currentNickname = document.querySelector("#currentNickname").value;

        
        // 기존 닉네임과 새로 입력된 닉네임이 다르면 중복검사 시도하기
        // -> 변경된적 없다 -> 중복검사 진행안함
        if(currentNickname !== memberNickname.value) {

            // 비동기 요청 (fetch() API 이용)
            // async / await
            // async : 비동기 함수를 만들 때 사용하는 키워드 ("이 함수 내에는 오래 걸리는 작업이 있어요!")
            // await : 비동기 작업의 결과를 기다릴 때 사용 -> !!! 반드시 async 함수 안에서만 사용 가능!!!
            //         -> "이 작업이 끝날때까지 기다려 주세요."
            const resp = await fetch("/member/checkNickname?memberNickname=" + memberNickname.value);
            const count = await resp.text();

            if(count == 1) {
                alert("이미 사용중인 닉네임입니다!");
                // e.preventDefault();
                return;
            }

        } 

        // ***********************************************************

        // 전화번호 유효성 검사
        if(memberTel.value.trim().length === 0) {
            alert("전화번호를 입력해 주세요");
            // e.preventDefault();
            return;
        }

        // 전화번호 정규식에 맞지 않으면
        regExp = /^01[0-9]{1}[0-9]{3,4}[0-9]{4}$/;
        if( !regExp.test(memberTel.value)) {
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
        if( !(result1 || result2) ) {
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

if(changePw != null) {
    // 제출 되었을 때
    changePw.addEventListener("submit", e => {
        

        const currentPw = document.querySelector("#currentPw");
        const newPw = document.querySelector("#newPw");
        const newPwConfirm = document.querySelector("#newPwConfirm");

        // - 값을 모두 입력했는가

        let str; // undefined 상태
        if( currentPw.value.trim().length == 0 ) str = "현재 비밀번호를 입력해주세요";
        else if( newPw.value.trim().length == 0 ) str = "새 비밀번호를 입력해주세요";
        else if( newPwConfirm.value.trim().length == 0 ) str = "새 비밀번호 확인을 입력해주세요";

        if(str != undefined) { // str에 값이 대입됨 == if 중 하나 실행됨
            alert(str);
            e.preventDefault();
            return;
        }

        // 새 비밀번호 정규식
        const regExp = /^[a-zA-Z0-9!@#_-]{6,20}$/;

        if( !regExp.test(newPw.value) ) {
            alert("새 비밀번호가 유효하지 않습니다");
            e.preventDefault();
            return;
        }

        // 새 비밀번호 == 새 비밀번호 확인
        if( newPw.value != newPwConfirm.value ) {
            alert("새 비밀번호가 일치하지 않습니다");
            e.preventDefault();
            return;
        }


    });
};

// -------------------------------------
/* 탈퇴 유효성 검사 */

// 탈퇴 form 태그
const secession = document.querySelector("#secession");

if(secession != null) {

    secession.addEventListener("submit", e => {

        const memberPw = document.querySelector("#memberPw");
        const agree = document.querySelector("#agree");

        // - 비밀번호 입력 되었는지 확인
        if(memberPw.value.trim().length == 0) {
            alert("비밀번호를 입력해주세요.");
            e.preventDefault(); // 제출막기
            return;
        }

        // 약관 동의 체크 확인
        // checkbox 또는 radio checked 속성
        // - checked -> 체크 시 true, 미체크시 false 반환

        if(!agree.checked) { // 체크 안됐을 때
            alert("약관에 동의해주세요");
            e.preventDefault();
            return;
        }

        // 정말 탈퇴? 물어보기
        if( !confirm("정말 탈퇴 하시겠습니까?") ) {
            alert("취소 되었습니다.");
            e.preventDefault();
            return;
        }
    });
}



// -------------------------------------------------------
// 이미지 업로드 구간

/*  [input type="file" 사용 시 유의 사항]

  1. 파일 선택 후 취소를 누르면 
    선택한 파일이 사라진다  (value == '')

  2. value로 대입할 수 있는 값은  '' (빈칸)만 가능하다

  3. 선택된 파일 정보를 저장하는 속성은
    value가 아니라 files이다
*/

// 요소 참조
const profileForm = document.getElementById("profile");  // 프로필 form
const profileImg = document.getElementById("profileImg");  // 미리보기 이미지 img
const imageInput = document.getElementById("imageInput");  // 이미지 파일 선택 input
const deleteImage = document.getElementById("deleteImage");  // 이미지 삭제 버튼
const MAX_SIZE = 1024 * 1024 * 5;  // 최대 파일 크기 설정 (5MB) -> 바이트 단위
// 1024B == 1KB
// 1024KB == 1MB

const defaultImageUrl = `${window.location.origin}/images/user.png`;
// 절대경로로 기본 이미지 URL 설정
// -> http://localhost/images/user.png

let statusCheck = -1; // -1 : 초기 상태, 0 : 이미지 삭제, 1: 새 이미지 선택
let previousImage = profileImg.src; // 이전 이미지 URL 기록 (초기 상태의 이미지 URL 저장)
let previousFile = null; // 이전에 선택된 파일 객체를 저장

// 이미지 선택 시 미리보기 및 파일 크기 검사
imageInput.addEventListener("change", () => {
    // change 이벤트 : 기존에 있던 값과 달라지면 change 이벤트 일어남

    // console.log(imageInput.files); // FileList (input 태그는 FileList 로 저장)

    const file = imageInput.files[0]; // 선택한 File 객체 가져오기

    if(file) { // 파일이 선택된 경우
        if(file.size <= MAX_SIZE) { // 파일 크기가 허용범위 이내인 경우
            const newImageUrl = URL.createObjectURL(file); // 임시 URL 생성
            // 미리보기 이미지 url 용도
            profileImg.src = newImageUrl // 미리보기 이미지 설정(img 태그의 src에 선택한 파일 임시 경로)
            statusCheck = 1; // 새 이미지 선택 상태 기록
            previousImage = newImageUrl; // 현재 선택된 이미지를 이전 이미지로 저장(다음에 바뀔일에 대비)
            previousFile = file; // 혀재 선택된 파일 객체를 이전 파일로 저장(다음에 바뀔일에 대비)

        } else { // 파일 크기가 허용 범위를 초과한 경우
            alert("5MB 이하의 이미지를 선택해주세요!");
            imageInput.value = ""; // 1, 파일 선택 초기화
                                   //(alert 창은 띄웠지만 이미 선택된 큰 사이즈 파일을 비우는건 따로 해야함)
                                   // == imageInput.file = null;
            profileImg.src = previousImage; // 2. 이전 미리보기 이미지로 복원
            // 3. 파일 입력 복구 : 이전 파일이 존재하면 다시 할당
            if(previousFile) {
                const dataTransfer = new DataTransfer();
                // DataTransfer : 자바스크립트로 파일을 조작할 때 사용되는 인터페이스
                // DataTransfer.items.add() : 파일 추가
                // DataTransfer.items.remove() : 파일 제거
                // DataTransfer.files : FileList 객체를 반환
                // -> <input type="file"> 요소에 파일을 동적으로 설정 가능
                // --> input 태그의 files 속성은 FileList만 저장 가능하기 때문에
                // DataTransfer를 이용하여 현재 File 객체를 FileList 변환하여 할당
                dataTransfer.items.add(previousFile);
                // 이전 파일을 추가해두기 : DataTransfer에 File 객체를 추가
                imageInput.files = dataTransfer.files;
                // 이전 파일로 input 요소의 files 속성을 복구 : DataTransfer에 저장된
                // 파일의 리스트를 FileList 객체로 반환

            }

        }

    } else { // 파일 선택이 취소된 경우
        profileImg.src = previousImage; // 이전 미리보기 이미지로 복원

        // 파일 입력 복구 : 이전 파일이 존재하면 다시 할당
        if(previousFile) {
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add() = previousFile;
            imageInput.files = dataTransfer.files;
        }
    }

});


// 이미지 삭제 버튼 클릭 시
deleteImage.addEventListener("click", () => {
    // 기본 이미지 상태가 아니면 삭제 처리
    if(profileImg.src !== defaultImageUrl) {
        imageInput.value = ""; // 파일 선택 초기화
        profileImg.src = defaultImageUrl;
        statusCheck = 0; // 삭제 상태 기록
        previousFile = null; // 이전 파일 초기화 기록

    } else {
        // 기본 이미지 상태에서 삭제 버튼 클릭 시 상태를 변경하지  않음
        statusCheck = -1; // 변경 사항 없음 상태 유지

    }

});


// 폼 제출 시 유효성 검사
profileForm.addEventListener("submit", e => {
    if(statusCheck === -1) { // 변경 사항이 없는 경우 제출 막기
        e.preventDefault();
        alert("이미지 변경 후 제출하세요")
    }

});