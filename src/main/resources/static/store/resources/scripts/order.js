const orderContainer = window.document.getElementById('orderContainer');

const price = document.querySelector('[rel="price"]');
const delivery = document.querySelector('[rel="delivery"]');
const priceAll = document.querySelector('[rel="priceAll"]');

const loadOrder = () => {
    orderContainer.innerHTML = '';
    let innerPrice = parseInt('0');
    let innerDelivery = parseInt('0');
    let innerPriceAll = parseInt('0');

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `./orderItem?num=${new URL(window.location.href).searchParams.get('num')}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const orderObject of responseArray) {
                    innerDelivery = 0;
                    const orderHtmlText = `
                    <table>
                    <tbody>
                    <tr class="tableBody" rel="line">
                        <td class="info">
                            <div class="photo">
                                <input class="image" type="image" src="/goods/titleImage?index=${orderObject['itemIndex']}">
                            </div>
                            <div class="info-content">
                                <div class="brand">나이키</div>
                                <div class="name">${orderObject['itemName']}</div>
                                <div class="option">옵션 | color: ${orderObject['orderColor']}, size: ${orderObject['orderSize']}</div>
                            </div>
                        </td>
                        <td class="price">${orderObject['price'].toLocaleString()} 원</td>
                        <td class="count">${orderObject['count']}</td>
                    </tr>
                    </tbody>
                    </table>`;

                    // 총 상품금액
                    innerPrice += orderObject['price'] * orderObject['count'];

                    const domParser = new DOMParser();
                    const dom = domParser.parseFromString(orderHtmlText, 'text/html');
                    const orderElement = dom.querySelector('[rel="line"]');

                    orderElement.dataset.cartIndex = orderObject['cartIndex'];

                    orderContainer.append(orderElement);

                    price.innerText = innerPrice.toLocaleString() + " 원";

                    if (innerPrice < 50000) {
                        innerDelivery = 2500;
                    }
                    delivery.innerText = (innerDelivery).toLocaleString() + " 원";
                    priceAll.innerText = (innerPrice + innerDelivery).toLocaleString() + " 원";


                    // // 총 결제금액 : 총 판매가 + 배송비
                    // innerPriceAll = innerPrice + innerDelivery;
                    // // 총 판매가
                    // priceContainer.innerText = innerPrice.toLocaleString() + ' 원';
                    // // 총 결제금액
                    // priceAllContainer.innerText = innerPriceAll.toLocaleString() + ' 원';

                }
            } else {

            }
        }
    };
    xhr.send();
}

loadOrder();


// 주문자 정보와 동일 눌렀을 때 사용자 정보 가져오기
function check(box) {

    const nameText = document.querySelector('[rel="nameText"]');
    const contactText = document.querySelector('[rel="contactText"]');
    const addressPostalText = document.querySelector('[rel="addressPostalText"]');
    const addressPrimaryText = document.querySelector('[rel="addressPrimaryText"]');
    const addressSecondaryText = document.querySelector('[rel="addressSecondaryText"]');

    if (box.checked === true) {
        nameText.value = document.querySelector('[rel="name"]').value;
        contactText.value = document.querySelector('[rel="contact"]').value;
        addressPostalText.value = document.querySelector('[rel="addressPostal"]').value;
        addressPrimaryText.value = document.querySelector('[rel="addressPrimary"]').value;
        addressSecondaryText.value = document.querySelector('[rel="addressSecondary"]').value;
    } else if (box.checked === false) {
        nameText.value = '';
        contactText.value = '';
        addressPostalText.value = '';
        addressPrimaryText.value = '';
        addressSecondaryText.value = '';
    }

}

// 결제 완료 눌렀을 때 : 카트에 담긴 내용 삭제
document.querySelector('[rel="payment"]').addEventListener('click', e => {

    e.preventDefault();

    const items = document.querySelectorAll('[rel="line"]');

    let itemsArray = [];
    for (let item of items) {

        itemsArray.push({
            cartIndex: item.dataset.cartIndex
        });

    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('cartIndex', JSON.stringify(itemsArray));

    xhr.open('DELETE', './cartItem');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        window.location.href = `./orderSuccess`;
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


// 주소찾기 창
const findButton = document.querySelector('[rel="find"]');

findButton.addEventListener('click', () => {

    new daum.Postcode({
        oncomplete: e => {
            document.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
            document.querySelector('[rel="addressPostalText"]').value = e['zonecode'];
            document.querySelector('[rel="addressPrimaryText"]').value = e['address'];
            document.querySelector('[rel="addressSecondaryText"]').value = '';
            document.querySelector('[rel="addressSecondaryText"]').focus();
        }
    }).embed(form.querySelector('[rel="addressFindPanelDialog"]'));
    form.querySelector('[rel="addressFindPanel"]').classList.add('visible');
});

form.querySelector('[rel="addressFindPanel"]').addEventListener('click', () => {
    form.querySelector('[rel="addressFindPanel"]').classList.remove('visible');
});




