const form = window.document.getElementById('form');

// const Warning = {
//     getElementById: () => form.querySelector('[rel="warningRow"]'),
//     show: (text) => {
//         const warningRow = Warning.getElementById();
//         warningRow.querySelector('.text').innerText = text;
//         warningRow.classList.add('visible');
//
//     },
//     hide: () => Warning.getElementById().classList.remove('visible')
// };


// form.querySelector('[rel="step1"]').classList.add('visible');
form.querySelector('[rel = "checkSend"]').addEventListener('click', () => {
    // Warning.hide();

    if (form.classList.contains('step1')) {
        // form이 step1을 포함하고 있고

        if (form['password'].value === '') {
            alert('비밀번호를 입력해주세요.');
            form['password'].focus();
            return;
        }
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('password', form['password'].value);
        xhr.open('POST', './myPageModify');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    //"{"result":"success"}"
                    switch (responseObject['result']) {
                        case'success':
                            form.querySelector('[rel="step1"]').classList.remove('visible');
                            // step1클래스를 삭제한다.
                            form.querySelector('[rel="step2"]').classList.add('visible');
                            break;

                        default:
                            alert('비밀번호가 올바르지 않습니다.');
                            form['password'].focus();
                            form['password'].select();
                            break;
                    }
                } else {
                    alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            }
        };
        xhr.send(formData);

    }

});

form.querySelector('[rel = "checkSend2"]').addEventListener('click', () => {
    // Warning.hide();
    if (form['nickname'].value === '') {
        alert('닉네임 작성을 완료해 주세요.');
        return;
    }

    if (form['name'].value === '') {
        alert('이름 작성을 완료해 주세요.');
        return;
    }
    if (form['contact'].value === '' || (form['contact'].value).length < 11) {
        alert('연락처 작성을 완료해 주세요.');
        return;
    }
    if (form['addressPostal'].value === '' || form['addressPrimary'].value === '' || form['addressSecondary'].value === '') {
        alert('주소 작성을 완료해 주세요.');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('nickname', form['nickname'].value);
    formData.append('name', form['name'].value);
    formData.append('contact', form['contact'].value);
    formData.append('addressPrimary', form['addressPrimary'].value);
    formData.append('addressPostal', form['addressPostal'].value);
    formData.append('addressSecondary', form['addressSecondary'].value);
    xhr.open('PATCH', './myPageModify');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case'success':
                        form.querySelector('[rel="step2"]').classList.remove('visible');

                        form.querySelector('[rel="step3"]').classList.add('visible');
                        break;
                    case 'nickname':
                        alert('이미 등록 된 닉네임입니다.');
                        form['nickname'].focus();
                        form['nickname'].select();
                        break;
                    case 'contact':
                        alert('이미 등록 된 연락처입니다.');
                        form['contact'].focus();
                        form['contact'].select();
                        break;
                    default:
                        break;
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});

//주소찾기 지도
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
