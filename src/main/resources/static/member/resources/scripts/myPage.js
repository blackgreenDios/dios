const Form = document.getElementById('form');

// 프로필 이미지 삽입
Form.querySelector('[rel = "imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    Form['images'].click();
});

Form['images'].addEventListener('input', () => {
    const imageContainerElement = Form.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());

    const imageSrc = URL.createObjectURL(Form['images'].files[0]); //하나만 올라갈꺼니까 0번째

    document.getElementById('imgThumb').setAttribute('src', imageSrc);//교체할꺼니까 밑에 아니고 이거만
    // const imgElement = document.createElement('img');
    // imgElement.classList.add('image');
    // imgElement.setAttribute('src', imageSrc);
    // imageContainerElement.append(imgElement);
})

Form.onsubmit = e => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('images', Form['images'].files);
    formData.append('nickname', Form['nickname'].value);

    xhr.open('GET', './myPage');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_signed':
                        alert('로그인이 되어있지않습니다.');
                        break;
                    case 'success':
                        Form['image'].value;
                        break;
                    default:
                        alert('알 수 없음');
                }
            } else {
                alert('서버와 통신하지 못했다.')
            }
        }
    }
    xhr.send(formData);

}

//마페 프로필 사진 DB에 올라가는거 안됨
//제출 누르면 DB에 올라가게하도록 맹글어야함