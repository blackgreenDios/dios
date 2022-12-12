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

const Email = {
    getElementById: () => form.querySelector('[rel="messageRow"]'),
    show: (text) => {
        const messageRow = Email.getElementById();
        messageRow.querySelector('.text').innerText = text;
        messageRow.classList.add('visible');
    },
    hide: () => Email.getElementById().classList.remove('visible')
}


form.onsubmit = (e) => {
    e.preventDefault();
    //자동으로 경고창이 안나오게 해주는 거
    Warning.hide();
    if (form['name'].value === '') {
        Warning.show('이름을 입력해주세요.');
        form['name'].focus();
        return;
    }

    if (form['contact'].value === '') {
        Warning.show('연락처를 입력해주세요.');
        form['contact'].focus();
        return;
    }

    Cover.show('이메일을 찾는 중입니다. \n\n 잠시만 기다려주세요. ')
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('name', form['name'].value);
    formData.append('contact', form['contact'].value);
    xhr.open('POST', './findEmail');

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        form['name'].setAttribute('disabled', 'disabled');
                        form['contact'].setAttribute('disabled', 'disabled');
                        form['findSend'].setAttribute('disabled', 'disabled');

                        Email.show('입력하신 이름과 연락처를 가진 회원님의 이메일은' + '\n' + responseObject['email'] + '\n' + '입니다.');
                        form.querySelector('[rel="messageRow"]').classList.add('visible');
                        //원래 안보이던 messageRow visible 추가해서 보이게 해줌
                        form.querySelector('[rel="loginRow"]').classList.add('visible');
                        break;
                    default:
                        Warning.show('입력하신 정보와 일치하는 회원이 없습니다.');
                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
};

form['loginSend'].addEventListener('click', () => {
    window.location.href = 'login';
})

