const form = document.getElementById('form');
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
        form['colors'].append(optionElement);
        e.target.value = '';
        e.target.focus();
    }
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
        form['sizes'].append(optionElement);
        e.target.value = '';
        e.target.focus();
    }
});

// 사진 올리기
form['images'].addEventListener('input', () => {
    const imageContainerElement = form.querySelector('[rel="imageContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());

    const imageSrc = URL.createObjectURL(form['images'].files[0]);

    document.getElementById('imgThumb').setAttribute('src', imageSrc);
});

document.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    form['images'].click();
});

// 상품등록
submit.addEventListener('click', e => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    formData.append('name', form['inputGoodsName'].value);
    formData.append('categoryId', '테스트');
    formData.append('brand', '테스트');
    formData.append('price', form['price'].value);
    formData.append('detail', form['detail'].value);
    for (let file of form['images'].files) {
        formData.append('images', file);
    }
    form['colors'].querySelectorAll(':scope > option').forEach(option => {
        formData.append('colors', option.value);
    });

    form['sizes'].querySelectorAll(':scope > option').forEach(option => {
        formData.append('sizes', option.value);
    });

    xhr.open('POST', './product');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        break;
                    default:
                        alert('오류가 발생했습니다.');
                }
            } else {
                alert('서버와 통신 오류가 발생했습니다.');
            }
        }
    }
    xhr.send(formData);
});

// // color
// submit.addEventListener('click', e => {
//     e.preventDefault();
//
//     const xhr = new XMLHttpRequest();
//     let formData = new FormData();
//
//     for (let i = 0; i < Color.length; i++) {
//         formData.append('productIndex', form['inputGoodsName'].value);
//         formData.append('color', Color.options[i].value);
//
//         xhr.open('POST', './productColor');
//         xhr.onreadystatechange = () => {
//             if (xhr.readyState === XMLHttpRequest.DONE) {
//                 if (xhr.status >= 200 && xhr.status < 300) {
//                     const responseObject = JSON.parse(xhr.responseText);
//                     switch (responseObject['result']) {
//                         case 'success' :
//                             break;
//                         default:
//                             alert('오류가 발생했습니다.');
//                     }
//                 } else {
//                     alert('서버와 통신 오류가 발생했습니다.');
//                 }
//             }
//         }
//         xhr.send(formData);
//         formData = new FormData();
//     }
// });
//
// //size
// submit.addEventListener('click', e => {
//     e.preventDefault();
//
//     const xhr = new XMLHttpRequest();
//     let formData = new FormData();
//
//     // Array.from(Color.options).forEach(option => {
//     //     formData.append('color', option.value);
//     //     formData.append('itemName', form['inputGoodsName'].value);
//     // });
//
//     for (let i = 0; i < Size.length; i++) {
//         formData.append('productIndex', form['inputGoodsName'].value);
//         formData.append('size', Size.options[i].value);
//
//         xhr.open('POST', './productSize');
//         xhr.onreadystatechange = () => {
//             if (xhr.readyState === XMLHttpRequest.DONE) {
//                 if (xhr.status >= 200 && xhr.status < 300) {
//                     const responseObject = JSON.parse(xhr.responseText);
//                     switch (responseObject['result']) {
//                         case 'success' :
//                             alert('성공');
//                             break;
//                         default:
//                             alert('오류가 발생했습니다.');
//                     }
//                 } else {
//                     alert('서버와 통신 오류가 발생했습니다.');
//                 }
//             }
//         }
//         xhr.send(formData);
//         formData = new FormData();
//     }
// });



