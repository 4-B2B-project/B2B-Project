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

         // 새 비밀번호가 현재 비밀번호와 같은지 확인
         if( currentPw.value === newPw.value ) {
          alert("새 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
          e.preventDefault();
          return;
      }


    });
};

