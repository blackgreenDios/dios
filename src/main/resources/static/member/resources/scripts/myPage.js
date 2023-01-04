const Form = document.getElementById('form');

// 프로필 이미지 삽입
Form.querySelector('[rel = "imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    Form['images'].click();
});


//이미지 파일 선택 후 기본이미지에서 넣은 이미지로 변경
Form['images'].addEventListener('input', () => {
    const imageContainerElement = Form.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
    const imageSrc = URL.createObjectURL(Form['images'].files[0]); //하나만 올라갈꺼니까 0번째
    document.getElementById('imgThumb').setAttribute('src', imageSrc);//교체할꺼니까 밑에 아니고 이거만
});



Form['profileSend'].addEventListener('click', e => {
    e.preventDefault();

    if (Form['nickname'].value === '') {
        alert('닉네임을 입력해주세요.');
        Form['nickname'].focus();
        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();


    formData.append('nickname', Form['nickname'].value);
    formData.append('newImage', Form['images'].files.length > 0 ? Form['images'].files[0] : null);
    console.log(Form['images'].files.length)

    //이미지가 선택되면 길이가 1이상이니까 이미지 컨트롤러로 넘어가고 아니면 null
    xhr.open('PATCH', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_signed':
                        alert('프로필을 수정할 수 있는 권한이 없거나 로그아웃되었습니다. 확인 후 다시 시도해 주세요.');
                        window.location.href = 'login';
                        break;
                    case 'success':
                        alert('프로필 수정이 완료되었습니다.');
                        break;
                    case 'nickname':
                        alert('이미 등록 된 닉네임입니다.');
                        form['nickname'].focus();
                        form['nickname'].select();
                        break;
                    default:
                        alert('알 수 없는 이유로 게시글을 수정하지 못하였습니다. 잠시 후 다시 시도해주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요');
            }
        }
    };
    xhr.send(formData);
});

//취소 누르면 다시 myPage 로 다시 돌아옴
Form['profileCancel'].addEventListener('click', e => {
    e.preventDefault();
    window.location.href = 'myPage';
});

