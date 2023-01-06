const form = window.document.getElementById('form');
const loginButton = window.document.getElementById('loginButton');
const loginContainer = window.document.getElementById('loginContainer');

const Warning = {
    getElementById: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const warningRow = Warning.getElementById();
        warningRow.querySelector('.text').innerText = text;
        warningRow.classList.add('visible');
    },
    hide: () => Warning.getElementById().classList.remove('visible')
};


form.onsubmit = (e) => {
    e.preventDefault();
    //자동으로 경고창이 안나오게 해주는거
    Warning.hide();
    if (form['email'].value === '') {
        alert('이메일을 입력해주세요');
        form['email'].focus();
        return;
    }

    if (form['password'].value === '') {
        alert('비밀번호를 입력해주세요');
        form['password'].focus();
        return;
    }

    Cover.show('로그인 하는 중입니다.\n 잠시만 기다려주세요. ');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('password', form['password'].value);
    xhr.open('POST', './login');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                //"{"result":"success"}"
                switch (responseObject['result']) {
                    case 'success':
                        window.history.length < 2 ?
                            window.location.href = 'login' : window.history.back();
                        break;
                    default:
                        alert('알맞은 이메일과 비밀번호를 입력해주세요.')

                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};

loginButton?.addEventListener('click', e => {
    e.preventDefault();
    loginContainer.classList.add('visible');
    window.open('https://kauth.kakao.com/oauth/authorize?client_id=b53a656bcd965d745a55ca52a6ccd639&redirect_uri=http://localhost:8080/dios/kakao&response_type=code', '_blank', 'width=500; height=750'); //팝업 창 염
});

//https://developers.kakao.com 가서 카카오 로그인 활성화
// 맨 밑에 url -> http://localhost:8080/member/login 추가
// 동의항목 들어가서  닉네임 설정 -> 필수동의 누르고 밑에  동의목적 : 사용자 식별

// index.html 에서
//<div class="button-container">
//<a class="button" href="https://kauth.kakao.com/oauth/authorize?client_id=c2204b6ef796ea3923e04483a8e6a9c5&redirect_uri=http://localhost:8080/member/kakao&response_type=code" id="loginButton" target="_blank" >로그인</a>
//</div>
//client_id 뒤에는 REST API 키 붙여넣기
//redirect_uri 뒤에는 로그인 활성화할 때 맨 밑에 추가해준 url 이랑 같게 해줘야함
