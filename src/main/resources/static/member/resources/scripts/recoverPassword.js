const form = window.document.getElementById('form');

const Warning = {
    getElementById: () => form.querySelector('[rel="warningRow"]'),
    show: (text) => {
        const warningRow = Warning.getElementById();
        warningRow.querySelector('.text').innerText = text;
        warningRow.classList.add('visible');

    },
    hide: () => Warning.getElementById().classList.remove('visible')
};


let emailAuthIndex = null;
//emailAuthIndex=responseObject['index']; 여기에서 온 index 값은 null 이 아니므로
setInterval(() => { //여기가 인증완료된 구문
    if (emailAuthIndex == null) {
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', emailAuthIndex);
    xhr.open('POST', './recoverPasswordEmail');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                console.log(xhr.responseText);
                switch (responseObject['result']) {
                    case'success':
                        //console.log('오'); //1초마다콘솔창에 오가 찍힘
                        form['code'].value = responseObject['code'];
                        form['salt'].value = responseObject['salt'];
                        form.querySelector('[rel="messageRow"]').classList.remove('visible') //위에 세개는 한번만 하면됨

                        form.querySelector('[rel="passwordRow"]').classList.add('visible'); //이 구문이 인증완료 된 구문이라서 여기다가 써야 인증완료되고 나서 비밀번호칸이 뜸
                        emailAuthIndex = null; //그래서 null로 초기화
                        break;
                    default:
                }
            }
        }
    };
    xhr.send(formData);
}, 1000)//1초마다, 한번만 주면 인증안된다고 뜸, 계속 확인해여야함 -> 계속 확인하는게 setInterval이 하는 일


form['emailSend'].addEventListener('click', () => {
    Warning.hide();
    if (form['email'].value === '') {
        Warning.show('이메일을 입력해주세요.');
        form['email'].focus();
        return;
    }
    Cover.show('계정확인을 하고 있습니다. \n\n 잠시만 기다려 주세요.')
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    xhr.open('POST', './recoverPassword');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                //"{"result":"success"}"
                switch (responseObject['result']) {
                    case'success':
                        emailAuthIndex = responseObject['index'];//1초마다 '오'가 찍힘 왜냐 여기서 index값을 받았고 제일 위에로 가서
                        form['email'].setAttribute('disabled', 'disabled');
                        form['emailSend'].setAttribute('disabled', 'disabled');
                        form.querySelector('[rel="messageRow"]').classList.add('visible');

                        break;
                    default:
                        Warning.show('해당 이메일을 사용하는 계정을 찾을 수 없습니다.');
                        form['email'].focus();
                        form['email'].select();
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
            }
        }
    };
    xhr.send(formData);
})


//비밀번호 입력 안했을 때 뜨는 경고창
form['passwordSend'].addEventListener('click', () => {
    Warning.hide();
    if (form['password'].value === '') {
        alert('새로운 비밀번호를 입력해 주세요.');
        form['password'].focus();
        return;
    }
    if (form['password'].value !== form['passwordCheck'].value) {
        alert('비밀번호가 서로 일치하지 않습니다.');
        form['passwordCheck'].focus();
        form['passwordCheck'].select();
        return;
    }
    Cover.show('비밀번호 재설정 하고 있습니다.\n\n 잠시만 기다려 주세요.')
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email', form['email'].value);
    formData.append('code', form['code'].value);
    formData.append('salt', form['salt'].value);
    formData.append('password', form['password'].value);
    xhr.open('PATCH', './recoverPassword'); //patch로 보내기
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case'success':
                        alert('비밀번호 변경이 완료되었습니다.');
                        window.location.href = 'login';
                        break;

                    default:
                        alert('비밀번호를 재설정하지 못하였습니다. 세션이 만료되었을 수도 있습니다. 잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.')
            }
        }
    };
    xhr.send(formData);
});


