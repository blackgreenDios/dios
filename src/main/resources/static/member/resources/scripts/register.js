const form = window.document.getElementById('form');
// const Warning = {
//     show: (text) => {
//         form.querySelector('[rel="warningText"]').innerText = text;
//         form.querySelector('[rel="warning"]').classList.add('visible');
//     },
//     hide: () => {
//         form.querySelector('[rel="warning"]').classList.remove('visible');
//     }
// }

const EmailWarning = {
    show: (text) => {
        const emailWarning = form.querySelector('[rel="emailWarning"]');
        emailWarning.innerText = text;
        emailWarning.classList.add('visible')
    },
    hide: () => {
        form.querySelector('[rel="emailWarning"]').classList.remove('visible');
    }
};
form.querySelector('[rel="nextButton"]').addEventListener('click', () => {
    form.querySelector('[rel="warning"]').classList.remove('visible');
    if (form.classList.contains('step1')) {

        if (!form['termAgree'].checked) {
            alert(' 서비스 이용약관 및 개인정보 처리방침을 읽고 동의해 주세요.');
            return;
        }
        form.querySelector('[rel="stepText"]').innerText = '개인정보 입력';
        form.classList.remove('step1');
        form.classList.add('step2');
    } else if (form.classList.contains('step2')) {
        if( !form['emailSend'].disabled || !form['emailVerify'].disabled) {
            alert('이메일 인증을 완료해 주세요.');
            return;
        }
        if(form['password'].value === '' || form['passwordCheck'].value === '') {
            alert('비밀번호 작성을 완료해 주세요.');
            return;
        }
        if(form['password'].value !== form['passwordCheck'].value) {
            alert('비밀번호가 일치하지 않습니다. 다시 작성해 주세요. ');
            return;
        }
        if(form['nickname'].value === '') {
            alert('닉네임 작성을 완료해 주세요.');
            return;
        }
        if(form['name'].value === '') {
            alert('이름 작성을 완료해 주세요.');
            return;
        }
        if(form['contact'].value === '') {
            alert('연락처 작성을 완료해 주세요.');
            return;
        }
        if(form['addressPostal'].value === '' || form['addressPrimary'].value === '' || form['addressSecondary'].value === '') {
            alert('주소 작성을 완료해 주세요.');
            return;
        }
        Cover.show('회원가입을 진행중입니다.\n\n잠시만 기다려 주세요.');
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('email', form['email'].value);
        formData.append('code', form['emailAuthCode'].value);
        formData.append('salt', form['emailAuthSalt'].value);
        formData.append('password', form['password'].value);
        formData.append('nickname', form['nickname'].value);
        formData.append('name', form['name'].value);
        formData.append('contact', form['contact'].value);
        formData.append('addressPostal', form['addressPostal'].value);
        formData.append('addressPrimary', form['addressPrimary'].value);
        formData.append('addressSecondary', form['addressSecondary'].value);
        xhr.open('POST', './register');
        // 경로도 유심히 봐야한다. ./은
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                Cover.hide();
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            form.querySelector('[rel="stepText"]').innerText = '회원가입 완료';
                            // form태그의 요소중 rel값을 stepText를 가진 태그의 Text를 추가한다.
                            form.querySelector('[rel="nextButton"]').innerText = '로그인하러 가기';
                            form.classList.remove('step2');
                            form.classList.add('step3');
                            break;
                        case 'email_not_verified':
                            alert('이메일 인증이 완료되지 않았습니다.');
                            break;
                        // case 'contact':
                        //     Warning.show('이미 등록된 전화번호 입니다.');
                        //     break;
                        default:
                            EmailWarning.show('알 수 없는 이유로 회원가입을 완료하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    EmailWarning.show('서버와 통신하지 못하였습니다.잠시후 다시 시도해 주세요.');
                }
            }
        }
        xhr.send(formData);
    } else if (form.classList.contains('step3')) {
        window.location.href = 'login';
        // /login으로 바로 이동하는데 현재의 경우 페이지를 만들지 않아 404오류가 뜨는 상태이다.
        // step3의 클래스 포함할 경우 login
        // page의 노출을 꺼려하는 경우 location.replace를 통해 뒤로가기 클릭시 숨기는 것을 원하는 page를 노출시키지 않을 수 있다.
    }

});

form['addressFind'].addEventListener('click', () => {
    new daum.Postcode({
        oncomplete: e => {
            form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
            form['addressPostal'].value = e['zonecode'];
            form['addressPrimary'].value = e['address'];
            form['addressSecondary'].value = '';
            form['addressSecondary'].focus();
        }
    }).embed(form.querySelector('[rel="addressFindPanelDialog"]'));
    form.querySelector('[rel="addressFindPanel"]').classList.add('visible');
});

form.querySelector('[rel="addressFindPanel"]').addEventListener('click', () => {
    form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
});


// 이메일인증번호 전송 버튼을 눌렀을때 부터의 JS기능 구현
form['emailSend'].addEventListener('click', () => {
    form.querySelector('[rel="emailWarning"]').classList.remove('visible');
    if (form['email'].value === '') {
        EmailWarning.show('이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return;
    }
    if (!new RegExp('^(?=.{7,50})([\\da-zA-Z_.]{4,})@([\\da-z\\-]{2,}\\.)?([\\da-z\\-]{2,})\\.([a-z]{2,10})(\\.[a-z]{2})?$').test(form['email'].value)) {
        EmailWarning.show('올바른 이메일 주소를 입력해 주세요.');
        form['email'].focus();
        return;
    }

    Cover.show('인증번호를 전송하고 있습니다.\n잠시만 기다려주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    // append를 통해 여러개의 값을 formData라는 것에 실어서 send할 수 있다.
    xhr.open('POST', './email');
    // 경로도 유심히 봐야한다. ./은
    xhr.onreadystatechange = () => {
        Cover.hide();
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        EmailWarning.show('인증 번호를 전송하였습니다. 전송된 인증 번호는 5분간만 유효합니다.');
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form['emailAuthCode'].removeAttribute('disabled');
                        form['emailAuthCode'].focus();
                        form['emailAuthSalt'].value = responseObject['salt'];
                        form['emailVerify'].removeAttribute('disabled');
                        break;
                    /*
                    성공시 1. 인증번호 확인란 disabled 풀어주기
                    2. 인증번호 전송란 disabled 걸어주기
                    3. innerText란 인증번호를 전송했다라고 바꾸어 주어야 함.
                    */

                    case 'email_duplicated':
                        EmailWarning.show('해당 이메일은 이미 사용 중입니다.');
                        form['email'].focus();
                        form['email'].select();
                        break;
                    default:
                        EmailWarning.show('알 수 없는 이유로 인증 번호를 전송하지 못 하였습니다. 잠시 후 다시 시도해 주세요.');
                        form['email'].focus();
                        form['email'].select();
                }
                // console.log('이게');
                // console.log(xhr.responseText);
                // console.log('이렇게 변함');
                // console.log(JSON.parse(xhr.responseText));
                // console.log('----------------');
                // console.log(responseObject['result']);
                // console.log(responseObject['salt']);
                // Json객체로 받아온 "문자열"을 Js에서 오브젝트타입으로 바꾸기 위해서 JSON.parse를 사용하였다.
            } else {
                EmailWarning.show('서버와 통신하지 못하였습니다.잠시후 다시 시도해 주세요.');
            }
        }
    }
    xhr.send(formData);
});

// email 인증을 클릭시 -------------------------------------------

form['emailVerify'].addEventListener('click', () => {
    if (form['emailAuthCode'].value === '') {
        EmailWarning.show('인증번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        return;
    }
    if (!new RegExp('^(\\d{6})$').test(form['emailAuthCode'].value)) {
        EmailWarning.show('올바른 인증 번호를 입력해 주세요.');
        form['emailAuthCode'].focus();
        form['emailAuthCode'].select();
        return;
    }
    Cover.show('인증 번호를 확인하고 있습니다. \n\n잠시만 기다려 주세요.');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['emailAuthCode'].value);
    formData.append('salt', form['emailAuthSalt'].value);
    xhr.open('PATCH', 'email');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'expired':
                        EmailWarning.show('인증 정보가 만료되었습니다. 다시 시도해 주세요.');
                        form['email'].removeAttribute('disabled');
                        form['email'].focus();
                        form['email'].select();
                        form['emailSend'].removeAttribute('disabled');
                        form['emailAuthCode'].value = '';
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailAuthSalt'].value = '';
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        break;
                    case 'success':
                        EmailWarning.show('이메일이 정상적으로 인증되었습니다.');
                        form['emailAuthCode'].setAttribute('disabled', 'disabled');
                        form['emailVerify'].setAttribute('disabled', 'disabled');
                        form['password'].focus();
                        break;
                    default:
                        EmailWarning.show('인증번호가 올바르지 않습니다.');
                        form['emailAuthCode'].focus();
                        form['emailAuthCode'].select();
                }
            } else {
                EmailWarning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    }
    xhr.send(formData);
});

// 상대경로에서 가장 중요한 것은 내가 있는 위치에서 다음으로 이동하는 페이지가 달라 질 수 있다.