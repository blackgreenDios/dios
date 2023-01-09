const orderContainer = window.document.getElementById('orderContainer');


const loadOrder = () => {
    orderContainer.innerHTML = '';

    const xhr = new XMLHttpRequest();

    xhr.open('GET', `./orderItem?num=${new URL(window.location.href).searchParams.get('num')}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseArray = JSON.parse(xhr.responseText);
                for (const orderObject of responseArray) {
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

                    // // 총 판매가
                    // innerPrice += cartObject['price'] * cartObject['count'];

                    const domParser = new DOMParser();
                    const dom = domParser.parseFromString(orderHtmlText,'text/html');
                    const orderElement = dom.querySelector('[rel="line"]');

                    orderContainer.append(orderElement);

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


loadOrder()