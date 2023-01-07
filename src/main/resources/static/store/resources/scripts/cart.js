const cartContainer = window.document.getElementById('cartContainer');
const totalPrice = window.document.getElementById('totalPrice');

// cart item 불러오기
const loadCart = () => {
    cartContainer.innerHTML = '';
    totalPrice.innerHTML = '';

    const xhr = new XMLHttpRequest();

    xhr.open('GET', './cartItem');
    xhr.onreadystatechange = () => {
        let price = parseInt('0');
        let finalPrice = parseInt('0');
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const cartObject of responseArray) {
                    const cartHtmlText = `
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
                                                                         src="/store/cartItemImage?id=${cartObject['index']}"
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
                                       <i class="minus icon fa-solid fa-minus" onclick='count("minus")'></i>
                                       <a class="count" rel="countResult"> ${cartObject['count']} </a>
                                       <i class="plus icon fa-solid fa-plus"onclick='count("plus")'></i>
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
                </tr>`;

                    const cartPriceHtmlText = `
                    <div class="button-container">
                    <input type="button" class="cancel" rel="cancel" value="선택상품 삭제">
                </div>

                <div class="sum-price">
                    <a class="title">총 판매가</a>
                    <a class="num">${(price += cartObject['price'] * cartObject['count']).toLocaleString()} 원</a>

                    <a class="symbol">-</a>

                    <a class="title">총 할인 금액</a>
                    <a class="num">0원</a>

                    <a class="symbol">+</a>

                    <a class="title">배송비</a>
                    <a class="num">0원</a>

                    <a class="symbol">=</a>

                    <a class="total-title">총 결제 금액</a>
                    <a class="total-num">${(finalPrice += cartObject['price'] * cartObject['count']).toLocaleString()} 원</a>
                </div>`;

                    cartContainer.innerHTML += cartHtmlText;
                    totalPrice.innerHTML = cartPriceHtmlText;
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

// 상품수량 올리기
function count (type) {
    const countResult = document.querySelector('[rel="countResult"]');

    // 현재 화면에 표시된 값
    let number = countResult.innerText;

    // 더하기/빼기
    if(type === 'plus') {
        number = parseInt(number) + 1;
    } else if(type === 'minus')  {
        number = parseInt(number) - 1;
    }

    // 결과 출력
    countResult.innerText = number;
}



// 주문버튼 눌렀을 때
const orderAll = document.querySelector('[rel="orderAll"]');

orderAll.addEventListener('click', e => {
    e.preventDefault();

    window.location.href = `./order`;
});
