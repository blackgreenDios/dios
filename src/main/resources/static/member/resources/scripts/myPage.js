const Form = document.getElementById('form');

// 프로필 이미지 삽입
Form.querySelector('[rel = "imageSelectButton"]').addEventListener('click', e =>{
    e.preventDefault();
    Form['images'].click();
});

Form['images'].addEventListener('input', () => {
    const imageContainerElement = Form.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x=> x.remove());

    const imageSrc = URL.createObjectURL(Form['images'].files);
    const imgElement = document.createElement('img');
    imgElement.classList.add('image');
    imgElement.setAttribute('src',imageSrc);
    imageContainerElement.append(imgElement);
})