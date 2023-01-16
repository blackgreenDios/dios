const form = window.document.getElementById('form');
let editor;
ClassicEditor
    .create(form['content'], {
        simpleUpload: {
            uploadUrl: './image'
        }
    })
    .then(e => editor = e);

const submit = document.querySelector('[rel="submit"]');


const Color = document.getElementById('colorOption');
Color.ariaMultiSelectable = 'true';

const Size = document.getElementById('sizeOption');
Size.ariaMultiSelectable = 'true';

form.onsubmit = e => {
    e.preventDefault();
};

// color
form['newColor'].addEventListener('keyup', e => {

    if (e.key === 'Enter') {

        if (form['newColor'].value === '') {
            return;
        }

        const optionElement = document.createElement('option');
        optionElement.innerText = e.target.value;
        optionElement.setAttribute('value', e.target.value);
        optionElement.dataset.virtual = "true";
        form['colors'].append(optionElement);
        e.target.value = '';
        e.target.focus();
    }
});

const colorDeleteButton = document.getElementById('colorDeleteButton');
colorDeleteButton.addEventListener('click', () => {
    const selectedOptions = Array.from(form['colors']
        .querySelectorAll(':scope > option'))
        .filter(x => x.selected);
    const formData = new FormData();
    let added = 0;
    for (let selectedOption of selectedOptions) {
        if (selectedOption.dataset.virtual === 'true') {
            selectedOption.remove();
        } else {
            formData.append('color', selectedOption.value);
            added++;
        }
    }
    if (added === 0) {
        return;
    }
    const xhr = new XMLHttpRequest();
    formData.append('itemIndex', form['itemIndex'].value);
    xhr.open('DELETE', './colors');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert('선택한 색상이 삭제되었습니다!');
                window.location.href = window.location.href;
            }
        }
    };
    xhr.send(formData);
});

// size
form['newSize'].addEventListener('keyup', e => {
    if (e.key === 'Enter') {

        if (form['newSize'].value === '') {
            return;
        }

        const optionElement = document.createElement('option');
        optionElement.innerText = e.target.value;
        optionElement.setAttribute('value', e.target.value);
        optionElement.dataset.virtual = "true";
        form['sizes'].append(optionElement);
        e.target.value = '';
        e.target.focus();
    }
});

const sizeDeleteButton = document.getElementById('sizeDeleteButton');
sizeDeleteButton.addEventListener('click', () => {
    const selectedOptions = Array.from(form['sizes']
        .querySelectorAll(':scope > option'))
        .filter(x => x.selected);
    const formData = new FormData();
    let added = 0;
    for (let selectedOption of selectedOptions) {
        if (selectedOption.dataset.virtual === 'true') {
            selectedOption.remove();
        } else {
            formData.append('size', selectedOption.value);
            added++;
        }
    }
    if (added === 0) {
        return;
    }
    const xhr = new XMLHttpRequest();
    formData.append('itemIndex', form['itemIndex'].value);
    xhr.open('DELETE', './sizes');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                alert('선택한 사이즈가 삭제되었습니다!');
                window.location.href = window.location.href;
            }
        }
    };
    xhr.send(formData);
});


const ImageForm = document.getElementById('form');
const itemCategory = document.getElementById('itemCategory');
const seller = document.getElementById('seller');

//상품 분류에서 의류 누르면 의류 사이즈 나오고, 신발 누르면 신발 사이즈 보이게 하는 코드

itemCategory.addEventListener('input', () => {
    if (itemCategory.options[itemCategory.selectedIndex].value === 'clothes') {
        Size.classList.add('visible');
    } else if (itemCategory.options[itemCategory.selectedIndex].value === 'shoes') {
        Size.classList.add('visible');
    } else {
        Size.classList.remove('visible');
    }
});

//등록 취소 누르면 뒤로가기
form['back'].addEventListener('click', () => window.history < 2 ? window.close() : window.history.back());

//프로필 이미지 삽입
ImageForm.querySelector('[rel = "imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    ImageForm['images'].click();
});

ImageForm['images'].addEventListener('input', () => {
    const imageContainerElement = ImageForm.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());
//img 태그 이면서 image클래스 가지는 전부 다 !
    const imageSrc = URL.createObjectURL(ImageForm['images'].files[0]);

    document.getElementById('imgThumb').setAttribute('src', imageSrc);

    // const imgElement = document.createElement('img');
    // imgElement.classList.add('image');
    // imgElement.setAttribute('src', imageSrc);
    // imageContainerElement.append(imgElement);

})

//등록하기 눌렀을때
submit.addEventListener('click', e => {

    e.preventDefault();

    if (form['itemName'].value === '') {
        alert('상품 이름을 입력해주세요.');
        form['itemName'].focus();
        return false;
    }


    if (editor.getData() === '') {
        alert('상품 상세페이지를 입력해 주세요.');
        editor.focus();
        return false;
    }


    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    for (let file of ImageForm['images'].files) {
        formData.append('images', file);
    }

    // formData.append('images', ImageForm['images'].files);// 이미지 파일
    formData.append('categoryId', itemCategory.options[itemCategory.selectedIndex].value); // 상품 분류
    formData.append('itemName', form['itemName'].value);//상품 이름
    formData.append('sellerIndex', seller.options[seller.selectedIndex].value);//상품 브랜드 네임
    formData.append('itemDetail', editor.getData());
    formData.append('price', form['itemPrice'].value);
    formData.append('count', form['count'].value);

    form['colors'].querySelectorAll(':scope > option').forEach(option => {
        if (option.dataset.virtual === 'true') {
            formData.append('colors', option.value);
        }
    });

    form['sizes'].querySelectorAll(':scope > option').forEach(option => {
        if (option.dataset.virtual === 'true') {
            formData.append('sizes', option.value);
        }
    });


    xhr.open('PATCH', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                if (responseObject['result'] === 'not_allowed') {
                    alert('상품 등록의 권한이 없습니다.');
                } else if (responseObject['result'] === 'success') {
                    alert('상품이 수정되었습니다!');
                    window.location.href = './read?gid=' + responseObject['gid'];
                    ImageForm['image'].value;
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해주세요.');
            }
        }
    }
    xhr.send(formData);
});

