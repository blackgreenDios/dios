const reviewContainer = window.document.querySelector('[rel="reviewContainer"]');
const reviewForm = document.getElementById('reviewForm');
const itemDelete = document.getElementById('itemDelete');
//상품 삭제 코드
itemDelete.addEventListener('click',e=>{
    e.preventDefault();

    if (!confirm('정말로 이 상품을 삭제할까요?')) {
        return;
    }
    const xhr = new XMLHttpRequest();

    xhr.open('DELETE', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        window.location.href = '/store/list';
                        break;
                    default:
                        alert('알 수 없는 이유로 게시글을 삭제하지 못했습니다.\n\n잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send();

});

/*리뷰 불러오는 함수 만들기*/
const loadReviews = () => {
    reviewContainer.innerHTML = '';

    const url = new URL(window.location.href);
    const searchParams = url.searchParams;  // 이건 comment?aid= 뒤에 있는 숫자를 의미한다.
    const gid = searchParams.get('gid');

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    xhr.open('GET', `./review?gid=${gid}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const reviewObject of responseArray) {
                    const itemHtml = `
                  <div class="item visible" rel="item">
                    <div class="head">
                    <span class="nickname" name="userEmail">${reviewObject['userEmail']}</span>
                    <span class="star-container" rel="starContainer">
                    <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 1 ? 'filled' : ''}"></i>
                    <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 2 ? 'filled' : ''}"></i>
                    <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 3 ? 'filled' : ''}"></i>
                    <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 4 ? 'filled' : ''}"></i>
                    <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 5 ? 'filled' : ''}"></i>
                    </span>
                    <span class="date" rel="date">2022-01-01</span>
                    <span class="modify" rel="modify">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </span>
                    <span class="delete" rel="delete">
                        <i  class="icon fa-regular fa-circle-xmark"></i>
                    </span>
                </div>
                <div class="body">
                    <div class="content" rel="content">${reviewObject['content']}</div>
                    <div class="image-container" rel="imageContainer">
                    </div>
                </div>
                
                <form class="modify-form" rel="modifyForm" id="modifyForm">
                 <input type="hidden" name="score" value="0">
                <div class="score-container">
                    <span class="score" rel="score">-</span>
                    <span class="full-score">/5</span>
                    <span class="star-container" rel="starContainer">
                                <i class="star  fa-solid fa-star    ${reviewObject['score'] >= 1 ? 'selected' : ''}"></i>
                                <i class="star  fa-solid fa-star    ${reviewObject['score'] >= 2 ? 'selected' : ''}"></i>
                                <i class="star  fa-solid fa-star    ${reviewObject['score'] >= 3 ? 'selected' : ''}"></i>
                                <i class="star  fa-solid fa-star    ${reviewObject['score'] >= 4 ? 'selected' : ''}"></i>
                                <i class="star  fa-solid fa-star    ${reviewObject['score'] >= 5 ? 'selected' : ''}"></i>
                            </span>
                </div>

                <div class="content-container">
                    <label class="label">
                        <span hidden>리뷰내용</span>
                        <input type="text" maxlength="100" name="content" placeholder="리뷰를 작성해주세요."
                               class="input">
                    </label>
                    <input name="modifyButton" class="--object-button" type="submit" value="수정">
                </div>
            </form>
                
            </div>
            
`;

                    const itemElement = new DOMParser().parseFromString(itemHtml, 'text/html').querySelector('[rel="item"]');

                    const modifyForm = itemElement.querySelector('[rel="modifyForm"]');


                    /*리뷰 수정*/
                    /*수정 아이콘 눌렀을때 input 박스 설정*/
                    itemElement.querySelector('[rel="modify"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        modifyForm.classList.add('modifying');
                        modifyForm['content'].value = reviewObject['content'];
                        modifyForm.querySelector('[rel="score"]').innerText=reviewObject['score'];
                        modifyForm['content'].focus();
                    });

                    const modifyStarArray = Array.from(modifyForm
                        .querySelector('[rel="starContainer"]')
                        .querySelectorAll(':scope > .star'));
                    for (let i = 0; i < modifyStarArray.length; i++) {
                        modifyStarArray[i].addEventListener('click', () => {
                            modifyStarArray.forEach(x => x.classList.remove('selected'));
                            for (let j = 0; j <= i; j++) {
                                modifyStarArray[j].classList.add('selected');
                            }
                            modifyForm.querySelector('[rel="score"]').innerText = i + 1;
                            modifyForm['score'].value = i + 1;
                        });
                    }


                    modifyForm.onsubmit = e => {
                        e.preventDefault();

                        if (!confirm('정말로 리뷰를 수정할까요?')) {
                            return false;
                        }

                        if (modifyForm['content'].value === '') {
                            alert('수정할 내용을 입력하세요.');
                            modifyForm['content'].focus();
                            return false;
                        }

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();

                        formData.append('score', modifyForm['score'].value);
                        formData.append('content', modifyForm['content'].value);
                        formData.append('index', reviewObject['index']);

                        xhr.open('PATCH', './review');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success':
                                            loadReviews(reviewForm['itemIndex'].value);
                                            modifyForm['score'] = '';
                                            modifyForm['content'].value = '';
                                            break;
                                        case 'no_such_review':
                                            alert('수정하려는 리뷰가 더 이상 존재하지 않습니다.\n\n이미 삭제되었을 수도 있습니다.');
                                            break
                                        case 'not_allowed':
                                            alert('해당 댓글을 수정할 권한이 없습니다.');
                                            break;
                                        default:
                                            alert('알 수 없는 이유로 댓글을 수정하지 못했습니다.\n\n잠시 후 다시 시도해 주세요.');
                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }

                        };
                        xhr.send(formData);
                    };


                    /*리뷰 삭제*/
                    itemElement.querySelector('[rel="delete"]')?.addEventListener('click', e => {
                        e.preventDefault();

                        if (!confirm('정말로 리뷰를 삭제할까요?')) {
                            return false;
                        }

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();

                        formData.append('index', reviewObject['index']);

                        xhr.open('DELETE', './review');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'not_signed' :
                                            alert('로그인이 되어있지 않습니다. 로그인 후 다시 시도해 주세요.');
                                            break;
                                        case 'not_allowed':
                                            alert('해당 댓글을 삭제할 권한이 없습니다.');
                                            break;
                                        case 'success':
                                            loadReviews(reviewForm['itemIndex'].value);
                                            break;
                                        default:
                                            alert('알 수 없는 일로 리뷰를 삭제하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                                    }

                                }
                            }
                        }
                        xhr.send(formData);
                    });


                    const imageContainerElement = itemElement.querySelector('[rel="imageContainer"]');
                    if (reviewObject['imageIndexes'].length > 0) {
                        for (const imageIndex of reviewObject['imageIndexes']) {
                            const imageElement = document.createElement('img');
                            imageElement.setAttribute('alt', '');
                            imageElement.setAttribute('src', `./reviewImage?index=${imageIndex}`);
                            imageElement.classList.add('image');
                            imageContainerElement.append(imageElement);
                        }
                    } else {
                        imageContainerElement.remove();
                    }
                    reviewContainer.append(itemElement);
                }


            } else {
                alert('리뷰를 불러오지 못했습니다. 잠시 후 다시 시도해주십시오');
            }


        }
    };
    xhr.send();
};

loadReviews();


if (reviewForm) {
    reviewForm.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
        e.preventDefault();
        reviewForm['images'].click();
    });

    const reviewStarArray = Array.from(reviewForm
        .querySelector('[rel="starContainer"]')
        .querySelectorAll(':scope > .star'));
    for (let i = 0; i < reviewStarArray.length; i++) {
        reviewStarArray[i].addEventListener('click', () => {
            reviewStarArray.forEach(x => x.classList.remove('selected'));
            for (let j = 0; j <= i; j++) {
                reviewStarArray[j].classList.add('selected');
            }
            reviewForm.querySelector('[rel="score"]').innerText = i + 1;
            reviewForm['score'].value = i + 1;
        });
    }

    reviewForm['images'].addEventListener('input', () => {
        const imageContainerElement = reviewForm.querySelector('[rel="imageContainer"]');
        imageContainerElement.querySelectorAll('img.images').forEach(x => x.remove());
        if (reviewForm['images'].files.length > 0) {
            reviewForm.querySelector('[rel="noImage"]').classList.add('hidden');
        } else {
            reviewForm.querySelector('[rel="noImage"]').classList.remove('hidden');
        }
        for (let file of reviewForm['images'].files) {
            const imageSrc = URL.createObjectURL(file);
            const imgElement = document.createElement('img');
            imgElement.classList.add('image');
            imgElement.setAttribute('src', imageSrc);
            imageContainerElement.append(imgElement);
        }
    });


    /*리뷰 작성*/
    reviewForm.onsubmit = e => {
        if (reviewForm['score'].value === '0') {
            alert('별점을 선택해 주세요.');
            return false;
        }
        if (reviewForm['content'].value === '') {
            alert('내용을 입력헤 주세요');
            reviewForm['content'].focus();
            return false;
        }

        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('score', reviewForm['score'].value);
        formData.append('content', reviewForm['content'].value);
        formData.append('itemIndex', reviewForm['itemIndex'].value);

        for (let file of reviewForm['images'].files) {
            formData.append('images', file);
        }

        xhr.open('POST', './review');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'not_signed' :
                            alert('로그인이 되어있지 않습니다. 로그인 후 다시 시도해 주세요.');
                            break;
                        case 'success':
                            loadReviews(reviewForm['itemIndex'].value);
                            reviewForm['score'] = '';
                            reviewForm['content'].value = '';
                            break;
                        default:
                            alert('알 수 없는 일로 리뷰를 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
                }
            }
        }
        xhr.send(formData);
    }
}