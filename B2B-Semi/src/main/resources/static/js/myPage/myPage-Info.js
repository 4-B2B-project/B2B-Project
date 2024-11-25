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
const MAX_SIZE = 1024 * 1024 * 5;  // 최대 파일 크기 설정 (5MB)

const defaultImageUrl = `${window.location.origin}/images/user.png`;
// 절대 경로로 기본 이미 URL 설정
// -> http:// localhost/images/url/user.png


let StatusCheck = -1;  // -1 : 초기상태,  0: 이미지 삭제, 1: 새 이미지 선택
let previousImage = profileImg.src;  // 이전 이미지 url 기록( 초기 상태의 이미지 URL 제공)
let previousFile = null;  // 이전에 선택된 파일 객체를 저장


// 이미지 선택 식 미리보기 및 칠
imageInput.addEventListener("change", () => {
    // change 이벤트 : 기존에 있던 값과 달라지면  change 이벤트 일어남

    //console.log(imageInput.files); // FileList 반환 (input 태그는 FileList 로 저장)

    const file = imageInput.files[0]; // 선택한 File 객체 가져오기

    if(file) {  // 파일이 선택된 경우
        if(file.size <= MAX_SIZE) {        // 파일 크기가 허용범위 이내인 경우
            const newImageUrl = URL.createObjectURL(file);  // 임시 URL 생성

            // blob:http://localhost/
            // 미리보기 이미지 url 용도
            profileImg.src = newImageUrl;   // 미리보기 이미지 설정(img 태그의 src에 선택한 파일 임시 저장)
            StatusCheck = 1;                // 새 이미지 선택 상세 기록
            previousImage = newImageUrl;    // 현재 선택된 이미지를 이전 이미지로 저장(다음에 바뀔일에 대비) - img src
            previousFile = file;            // 현재 선택된 파일 객체를 이전 파일로 저장(다음에 바뀔일에 대비) - input files , 최종적으로 서버에 제출해야하는 것

        } else {    // 파일 크기가 허용 범위를 초과한 경우
            alert("5MB 이하의 이미지를 선택해주세요!");
            imageInput.value = "";  // 1. 파일 선택 초기화
                                    // (alert 창은 띄웠지만 이미 선택된 큰 사이즈 파일을 비우는건 따로 해야함)
                                    // == imageInput.files = null;
            profileImg.src = previousImage; // 2. 이전 미리보기 이미지로 복원
            // 3. 파일 입력 복구 : 이전 파일이 존재하면 다시 할당
            if(previousFile) {
                const dataTransfer = new DataTransfer();
                // DataTransfer : 자바스크립트로 파일을 조작할 때 사용되는 인터페이스
                // DataTransfer.items.add() : 파일 추가
                // DataTransfer.items.remove() : 파일 제거
                // DataTransfer.files() : FileList 객체를 반환
                // -> <input type="file"> 요소에 파일을 동적으로 설정 가능
                // --> input 태그의 files 속성은 FileList만 저장 가능하기 때문에
                // DataTransfer를 이용하여 현재 File 객체를 FileList 변환하여 할당
                dataTransfer.items.add(previousFile);   // DataTransfer 라는 박스에  파일 객체에 들어가고 fileList로 반환
                // 이전 파일을 추가해두기 : DataTransfer에  File 객체를 추가
                imageInput.files = dataTransfer.files;
                // 이전 파일로 input 요소의 files 속성을 복구 : DataTransfer에 저장된 
                // 파일의 리스트를 FileList 객체로 반환
            }
        }


    } else {    // 파일 선택이 취소된 경우
        profileImg.src = previousImage; // 이전 미리보기 이미지로 복원

        // 파일 입력 복구 : 이전 파일이 존재하면 다시 할당
        if(previousFile){
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(previousFile);
            imageInput.files = dataTransfer.files;  // 이전 파일로 input 태그의 files 속성 복구
        }
    }

});


// 이미지 삭제 버튼 클릭시 
deleteImage.addEventListener("click", () =>{ // 현재 기본이미지가 아닌 다른 이미지가 있을때 기본이미지로 바꾸고자 할 때
    // 기본 이미지 상태가 아니면 삭제 처리
    if(profileImg.src !== defaultImageUrl){
        imageInput.value = "";            // 파일 선택 초기화
        profileImg.src = defaultImageUrl; // 기본 이미지로 설정
        StatusCheck = 0;                  // 이미지 삭제
        previousFile = null;              // 이전 파일 초기화 기록
    } else {  
        // 기본이미지 상태에서 삭제 버튼 클릭 시 상태를 변경하지 않음
        StatusCheck = -1;   // 변경사항 없음 상태 유지

    }

});


// 폼 제출 시 유효성 검사
// 변경사항 없을 시 제출 X
profileForm.addEventListener("submit", e => {
    if(StatusCheck === -1){ // 변경 사항이 없는 경우 제출 막기
        e.preventDefault();
        alert("이미지 변경 후 제출 하세요.");
        
    }
});