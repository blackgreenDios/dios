const cartContainer = window.document.getElementById('cartContainer');
const priceContainer = window.document.querySelector('[rel="price"]');
const discountContainer = window.document.querySelector('[rel="discount"]');
const deliveryContainer = window.document.querySelector('[rel="delivery"]');
const priceAllContainer = window.document.querySelector('[rel="priceAll"]');

const cancelButton = document.querySelector('[rel="cancel"]');
const orderAll = document.querySelector('[rel="orderAll"]');
const selectOrder = document.querySelector('[rel="selectOrder"]');

// cart item 불러오기
const loadCart = () => {
    cartContainer.innerHTML = '';
    let innerPrice = parseInt('0');
    let innerDiscount = parseInt('0');
    let innerDelivery = parseInt('0');
    let innerPriceAll = parseInt('0');

    const xhr = new XMLHttpRequest();

    xhr.open('GET', './cartItem');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const cartObject of responseArray) {
                    const cartHtmlText = `
                    <table>
                    <tbody>
                    <tr class="line" rel="line">
                        <td style="width: 40px">
                            <div class="product">
                                <div class="tdcell first">
                                    <input type="checkbox" rel="checkbox" checked="" name="select" value="" title="배송상품 선택">
                                </div>
                            </div>
                        </td>
                        <td style="width: 390px; border-right: 0.1rem solid rgb(204,204,204);">
                            <div class="product">
                                <div class="tdcell product">
                                    <div class="product-info">
                                        <a class="product-img" href="#"><img class="image"
                                                                             src="/goods/titleImage?index=${cartObject['itemIndex']}"
                                                                             alt="">
                                        </a>
    
                                        <div class="information">
                                            <a class="product-title">${cartObject['itemName']}</a>
                                            <div class="information-option">
                                                <a class="product-option">옵션 | </a>
                                                <a class="product-optionColor">color : ${cartObject['orderColor']}</a>
                                                <a class="product-optionSize">, size : ${cartObject['orderSize']}</a>
                                            </div>
                                        </div>
    
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td style="width: 110px; border-right: 0.1rem solid rgb(204,204,204);">
                            <div class="product">
                                <div class="tdcell price">
                                    <span class="product-price">
                                        <a class="num">${cartObject['price'].toLocaleString()} 원</a></span>
    
                                </div>
                            </div>
                        </td>
                        <td style="width: 100px; border-right: 0.1rem solid rgb(204,204,204);">
                            <div class="product">
                                <div class="tdcell count">
                                    <span class="product-count">
                                       <div class="count-container">
                                           <i rel="minus" class="minus icon fa-solid fa-minus"></i>
                                           <a class="count"> ${cartObject['count']} </a>
                                           <input rel="countResult" type="hidden" value="${cartObject['count']}">
                                           <i rel="plus" class="plus icon fa-solid fa-plus"></i>
                                       </div>
                                    </span>
    
                                </div>
                            </div>
                        </td>
                        <td style="width: 110px; border-right: 0.1rem solid rgb(204,204,204);">
                            <div class="product">
                                <div class="tdcell price">
                                    <span class="product-price">
                                        <a class="num">${(cartObject['price'] * cartObject['count']).toLocaleString()} 원</a></span>
    
                                </div>
                            </div>
    
                        </td>
                        <td style="width: 120px">
                            <div class="product">
                                <div class="tdcell delivery">
                                    <span class="product-delivery">
                                        <a class="text">무료배송</a></span>
    
                                </div>
                            </div>
                        </td>
                    </tr>                    
                    </tbody>                    
                    </table>`;

                    // 총 판매가
                    innerPrice += cartObject['price'] * cartObject['count'];

                    const domParser = new DOMParser();
                    const dom = domParser.parseFromString(cartHtmlText, 'text/html');

                    console.log(dom);

                    const cartElement = dom.querySelector('[rel="line"]');
                    cartElement.dataset.index = cartObject['index'];
                    cartElement.dataset.itemIndex = cartObject['itemIndex'];
                    cartElement.dataset.count = cartObject['count'];
                    cartElement.dataset.orderColor = cartObject['orderColor'];
                    cartElement.dataset.orderSize = cartObject['orderSize'];

                    // 수량 변경 : 더하기 버튼 눌렀을 때
                    const plus = dom.querySelector('[rel="plus"]');
                    const countResult = dom.querySelector('[rel="countResult"]');

                    plus?.addEventListener('click', e => {
                        e.preventDefault();

                        countResult.value++;

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', cartObject['index']);
                        formData.append('count', cartObject['count']);

                        xhr.open('PATCH', './plusCount');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success':
                                            loadCart();
                                            break;
                                        default:
                                            alert('잠시후 다시 시도해 주세요.');
                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }
                        };
                        xhr.send(formData);
                    });

                    // 수량 변경 : 빼기 버튼 눌렀을 때
                    const minus = dom.querySelector('[rel="minus"]');

                    minus?.addEventListener('click', e => {
                        e.preventDefault();

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', cartObject['index']);
                        formData.append('count', cartObject['count']);

                        xhr.open('PATCH', './minusCount');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success':
                                            loadCart();
                                            break;
                                        case 'out_of_range':
                                            alert('수량은 1개 이상이어야 합니다.');
                                            break;
                                        default:
                                            alert('잠시후 다시 시도해 주세요.');
                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }
                        };
                        xhr.send(formData);
                    });

                    cartContainer.append(cartElement);

                    // 총 결제금액 : 총 판매가 + 배송비
                    innerPriceAll = innerPrice + innerDelivery;
                    // 총 판매가
                    priceContainer.innerText = innerPrice.toLocaleString() + ' 원';
                    // 총 결제금액
                    priceAllContainer.innerText = innerPriceAll.toLocaleString() + ' 원';


                }
            } else {

            }
        }
    };
    xhr.send();
}

loadCart();


// 체크박스 전체선택 / 전체해제
function selectAll(selectAll) {
    const checkboxes
        = document.getElementsByName('select');

    checkboxes.forEach((checkbox) => {
        checkbox.checked = selectAll.checked;
    })
}

// 선택주문
selectOrder.addEventListener('click', e => {
    e.preventDefault();

    const items = document.querySelectorAll('[rel="line"]');

    let itemsArray = [];
    for (let item of items) {
        if (item.querySelector('[rel="checkbox"]').checked) {
            itemsArray.push({
                itemIndex: item.dataset.itemIndex,
                count: item.dataset.count,
                orderColor: item.dataset.orderColor,
                orderSize: item.dataset.orderSize
            });
        }
    }
    const formData = new FormData();
    formData.append('order', JSON.stringify(itemsArray));
    const xhr = new XMLHttpRequest();
    xhr.open('POST', './order');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        window.location.href = `./order?num=${responseObject['orderNum']}`;
                        break;
                    default:
                        alert('잠시후 다시 시도해 주세요.')
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});


// 전체 주문
orderAll.addEventListener('click', e => {
    e.preventDefault();

    let itemsArray = [];
    for (let item of items) {
        itemsArray.push({
            itemIndex: item.dataset.itemIndex,
            count: item.dataset.count,
            orderColor: item.dataset.orderColor,
            orderSize: item.dataset.orderSize
        });
    }
    const formData = new FormData();
    formData.append('order', JSON.stringify(itemsArray));
    const xhr = new XMLHttpRequest();
    xhr.open('POST', './order');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        window.location.href = `./order?num=${responseObject['orderNum']}`;
                        break;
                    default:
                        alert('잠시후 다시 시도해 주세요.')
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});


// 선택상품 삭제 버튼 눌렀을 때
cancelButton.addEventListener('click', e => {
    e.preventDefault();

    const items = document.querySelectorAll('[rel="line"]');

    let itemsArray = [];
    for (let item of items) {
        if (item.querySelector('[rel="checkbox"]').checked) {
            itemsArray.push({
                index: item.dataset.index
            });
        }
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('index', JSON.stringify(itemsArray));

    xhr.open('DELETE', './cart');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        loadCart();
                        break;
                    default:
                        alert('잠시후 다시 시도해 주세요.')
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);
});



