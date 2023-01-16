const itemForm = document.getElementById('form');
const reviewContainer = window.document.querySelector('[rel="reviewContainer"]');

const pageContainer = window.document.querySelector('[rel="pageContainer"]');

const reviewForm = document.getElementById('reviewForm');
const itemDelete = document.getElementById('itemDelete');

const colorOption = document.getElementById('colorOption');

const sizeOption = document.getElementById('sizeOption');

const resultForm = document.getElementById('resultForm');

const resultElement = document.getElementById('result');
const resultColor = document.getElementById('resultColor');
const resultSize = document.getElementById('resultSize');

const cartButton = window.document.querySelector('[rel="cartButton"]');

const blockBox = document.getElementById('blockBox');

document.addEventListener('DOMContentLoaded', function(){ //DOM 생성 후 이벤트 리스너 등록
    //더보기 버튼 이벤트 리스너
    document.querySelector('.btn_open').addEventListener('click', function(e){

        let classList = document.querySelector('.detailinfo').classList; // 더보기 프레임의 클래스 정보 얻기

        if(classList.contains('showstep1')){
            classList.remove('showstep1');

        }
        //전체보기시 더보기 버튼 감추기 & 감추기 버튼 표시
        if(!classList.contains('showstep1')){
            e.target.classList.add('hide');
            document.querySelector('.btn_close').classList.remove('hide');

        }

    });
});

// 감추기 버튼 이벤트 리스너
document.querySelector('.btn_close').addEventListener('click', function(e){
    e.target.classList.add('hide');
    document.querySelector('.btn_open').classList.remove('hide'); // 더보기 버튼 감춤
    document.querySelector('.detailinfo').classList.add('showstep1'); // 초기 감춤 상태로 복귀

});



function result() {
    Array.from(sizeOption.selectedOptions).forEach(option => {
        resultSize.innerText = option.text;

        Array.from(colorOption.selectedOptions).forEach(option => {
            resultColor.innerText = option.text;
        });
    });
    // 모든 옵션이 선택되었을때 resultElement 를 보여주기
    colorOption.options[colorOption.selectedIndex].value === '0' ||
    sizeOption.options[sizeOption.selectedIndex].value === '0'
        ? resultElement.classList.remove('visible')
        : resultElement.classList.add('visible');
}

//사용자가 선택할 수량
function Count(type) {
    const countElement = document.getElementById('count');
    // 현재 화면에 표시된 값
    let number = countElement.innerText;
    let countValue = resultForm['count'].value;

    // 더하기/빼기


    if (type === 'plus') {
        number = parseInt(number) + 1;
        countValue = parseInt(countValue) + 1;
    } else if (type === 'minus') {
        number = parseInt(number) - 1;
        countValue = parseInt(countValue) - 1;
    }

    resultForm['count'].value = countValue;

    number <= 0
        ? resultElement.classList.remove('visible')
        : countElement.innerText = number;
    // 결과 출력
}


/*리뷰 불러오는 함수 만들기*/
const loadReviews = (page) => {
    page ??= 1;
    reviewContainer.innerHTML = '';
    const url = new URL(window.location.href);
    const searchParams = url.searchParams;  // 이건 comment?aid= 뒤에 있는 숫자를 의미한다.
    const gid = searchParams.get('gid');

    const xhr = new XMLHttpRequest();
    const formData = new FormData();


    xhr.open('GET', `./review?gid=${gid}&page=${page}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                for (const reviewObject of responseObject['reviews']) {
                    const date = new Date(reviewObject['createdOn']);
                    const now = new Date();
                    const
                        itemHtml = `

                   <div class="item visible" rel="item">
                    <div class="head">
                    <span class="head-first">
                        <span class="nickname" name="userEmail">${reviewObject['userEmail']}</span>
                        <span class="star-container" rel="starContainer">
                        <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 1 ? 'filled' : ''}"></i>
                        <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 2 ? 'filled' : ''}"></i>
                        <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 3 ? 'filled' : ''}"></i>
                        <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 4 ? 'filled' : ''}"></i>
                        <i class="star  fa-solid fa-star  ${reviewObject['score'] >= 5 ? 'filled' : ''}"></i>
                        </span>
                    </span>
                    
                    <span class="head-second">
                        <span class="date" rel="date">${date.getDate() === now.getDate()
                            ? '오늘' :
                            date.getDate() === now.getDate() - 1 ?
                                '하루전' :
                                date.getFullYear() + '-' + date.getMonth() + 1 + '-' + date.getDate()}</span>
                        <span class="modify" rel="modifyIcon">
                        <i class="icon fa-solid fa-pen-to-square"></i>
                        </span>
                        <span class="delete" rel="delete">
                            <i  class="icon fa-regular fa-circle-xmark"></i>
                        </span>
                    </span>
                    
                    
                </div>
                <div class="body">
                    <div class="content" rel="content">${reviewObject['content']}</div>
                    <div class="image-container" rel="imageContainer">
                    </div>
                </div>
                
                <form class="modify-form" rel="modifyForm" id="modifyForm">
                 <input type="hidden" name="score" value="0">
                <div class="modify" id="modify" rel="modify">
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
                    <div class="modifyButtonContainer">
                        <input name="modifyButton" class="--object-button modify" type="submit" rel="modifyButton" value="수정">
                        <input name="modifyButton" class="--object-button cancel" type="submit" rel="cancelButton" value="취소">
                    </div>
                </div>
                
                </div>
                
                
            </form>
                
            </div>
            
`;

                    const itemElement = new DOMParser().parseFromString(itemHtml, 'text/html').querySelector('[rel="item"]');

                    const modifyForm = itemElement.querySelector('[rel="modifyForm"]');
                    const modifyElement = itemElement.querySelector('[rel="modify"]');


                    /*리뷰 수정*/
                    /*수정 아이콘 눌렀을때 input 박스 설정*/
                    itemElement.querySelector('[rel="modifyIcon"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        modifyElement.classList.add('visible');

                        modifyForm['content'].value = reviewObject['content'];
                        modifyForm.querySelector('[rel="score"]').innerText = reviewObject['score'];
                        modifyForm['content'].focus();
                    });

                    //취소버튼 누르면 수정란 사라지기
                    itemElement.querySelector('[rel="cancelButton"]')?.addEventListener('click', e => {
                        e.preventDefault();
                        modifyElement.classList.remove('visible');
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
                                            loadReviews(responseObject['currentPage']);
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
                                            loadReviews(responseObject['currentPage']);
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

                pageContainer.innerHTML = '';

                if (responseObject['reviews'].length > 0) { //리뷰가 존재한다면

                    const minPageElement = document.createElement('a');
                    minPageElement.setAttribute('class', 'page');
                    // minPageElement.setAttribute('href',`./review?gid=${gid}&page=${responseObject['minPage']}`);

                    const minPageIconElement = document.createElement('i');
                    minPageIconElement.setAttribute('class', 'fa-solid fa-angles-left');
                    minPageElement.append(minPageIconElement);
                    minPageElement.addEventListener('click', () => {
                        loadReviews(responseObject['minPage']);
                    })
                    if (responseObject['currentPage'] > responseObject['minPage']) {
                        pageContainer.append(minPageElement);
                    }

                    const prevPageElement = document.createElement('a');
                    prevPageElement.setAttribute('class', 'page');
                    // prevPageElement.setAttribute('href',`./review?gid=${gid}&page=${responseObject['minPage']}`);


                    const prevPageIconElement = document.createElement('i');
                    prevPageIconElement.setAttribute('class', 'fa-solid fa-angle-left');
                    prevPageElement.append(prevPageIconElement);
                    prevPageElement.addEventListener('click', () => {
                        loadReviews(responseObject['currentPage'] - 1);
                    })
                    if (responseObject['currentPage'] > responseObject['minPage']) {
                        pageContainer.append(prevPageElement);
                    }

                    let numberPageElement = {};
                    for (let i = responseObject['minPage']; i <= responseObject['maxPage']; i++) {
                        numberPageElement[`numberPageElement${i}`] = document.createElement('a');
                        numberPageElement[`numberPageElement${i}`].setAttribute('class', 'page');
                        if(responseObject['currentPage'] === i){
                            numberPageElement[`numberPageElement${i}`].classList.add('selected');
                        }
                        // numberPageElement[`numberPageElement${i}`].setAttribute('href',`./review?gid=${gid}&page=${i}`);
                        numberPageElement[`numberPageElement${i}`].innerText = i;
                        numberPageElement[`numberPageElement${i}`].addEventListener('click', () => {
                            loadReviews(i);
                        });
                        pageContainer.append(numberPageElement[`numberPageElement${i}`]);
                    }

                    const nextPageElement = document.createElement('a');
                    nextPageElement.setAttribute('class', 'page');
                    // nextPageElement.setAttribute('href',`./review?gid=${gid}&page=${responseObject['maxPage']}`);

                    const nextPageElementIconElement = document.createElement('i');
                    nextPageElementIconElement.setAttribute('class', 'fa-solid fa-angle-right');
                    nextPageElement.append(nextPageElementIconElement);
                    nextPageElement.addEventListener('click', () => {
                        loadReviews(responseObject['currentPage'] + 1);
                    })
                    if(responseObject['currentPage'] < responseObject['maxPage']) {
                        pageContainer.append(nextPageElement);
                    }

                    const maxPageElement = document.createElement('a');
                    maxPageElement.setAttribute('class', 'page');
                    // maxPageElement.setAttribute('href',`./review?gid=${gid}&page=${responseObject['maxPage']}`);

                    const maxPageElementIconElement = document.createElement('i');
                    maxPageElementIconElement.setAttribute('class', 'fa-solid fa-angles-right');
                    maxPageElement.append(maxPageElementIconElement);
                    maxPageElement.addEventListener('click', () => {
                        loadReviews(responseObject['maxPage']);
                    })
                    if(responseObject['currentPage'] < responseObject['maxPage']) {
                        pageContainer.append(maxPageElement);
                    }
                } else {
                    pageContainer.innerText = '작성된 리뷰가 없습니다. \n\n 리뷰를 작성해주세요.';
                }


            } else {
                alert('리뷰를 불러오지 못했습니다. 잠시 후 다시 시도해주십시오');
            }


        }
    };
    xhr.send(formData);

    // const itemElement = document.querySelector('[rel="itemElement"]');
    //
    // document.querySelector('[rel="score"]').innerText = itemElement.dataset.scoreAvg;

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
                            window.location.href = '/dios/login/'
                            break;
                        case 'not_allowed':
                            alert('해당 상품을 구매한 적이 없습니다. 구매 후 리뷰를 작성해주세요.');
                            break;
                        case 'success':
                            loadReviews(responseObject['currentPage']);
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


//장바구니 담기 버튼 클릭 시 이벤트
cartButton?.addEventListener('click', e => {
    e.preventDefault();

    if (!confirm('정말로 이 상품을 장바구니에 넣을까요?')) {
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    formData.append('count', resultForm['count'].value);
    formData.append('orderColor', colorOption.options[colorOption.selectedIndex].text);
    formData.append('orderSize', sizeOption.options[sizeOption.selectedIndex].text);
    formData.append('itemIndex', resultForm['itemIndex'].value);

    xhr.open('POST', './read');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        alert('장바구니 등록 성공 !');
                        blockBox.classList.add('visible');

                        blockBox.querySelector('[rel="yes"]').addEventListener('click', e => {
                            e.preventDefault();
                            window.location.href = '/store/cart'
                        });

                        blockBox.querySelector('[rel="no"]').addEventListener('click', e => {
                            e.preventDefault();
                            window.location.href;
                            blockBox.classList.remove('visible');
                        });


                        break;
                    default:
                        alert('알 수 없는 이유로 게시글을 삭제하지 못했습니다.\n\n잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});


//상품 삭제 코드
itemDelete.addEventListener('click', e => {
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
                        window.location.href = '/store/list'
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
