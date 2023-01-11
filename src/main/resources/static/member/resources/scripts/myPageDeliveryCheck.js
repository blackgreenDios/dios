
const Form = document.getElementById('form');
// const orderContainer = window.document.getElementById('orderContainer');
// const Form = document.getElementById('form');

// const loadCart = () => {
//     orderContainer.innerHTML = '';
//
//     const xhr = new XMLHttpRequest();

//     xhr.open('GET', './orderList');
//     xhr.onreadystatechange = () => {
//         if (xhr.readyState === XMLHttpRequest.DONE) {
//             if (xhr.status >= 200 && xhr.status < 300) {
//                 const responseArray = JSON.parse(xhr.responseText);
//                 for (const orderObject of responseArray) {
//                     const orderHtmlText = `
//                     <table>
//                      <tbody class="history">
//                     <tr rel="line">
//                         <td rowspan="1">
//                             <ul>
//                                 <li>
//                                     <span class="store-address">DIOS</span>
//                                     <br>
//                                 </li>
//                                 <li class="order-date">${orderObject['orderDate']}</li>
//                                 <li>
//                                     <a href="#" class="btn-detail">
//                                         상세보기
//                                     </a>
//                                 </li>
//                             </ul>
//                         </td>
//
//                         <td class="subject">
//                             <div class="area">
//                                 <a class="pro-photo" href="#">
//                                     <img src="/goods/titleImage?index=${orderObject['itemIndex']}" alt="">
//                                 </a>
//                                 <div class="pro-text">
//                                     <a href="#">
//                                         <span class="title" >${orderObject['storeName']}</span>
//                                         <span class="text" >${orderObject['itemName']}</span>
//                                     </a>
//
//                                     <span class="colorSize">
//                                         <i class="title">옵션
//                                         </i>${orderObject['orderColor']}, ${orderObject['orderSize']}</span>
//
//                                 </div>
//                             </div>
//                         </td>
//
//                         <td class="count">${orderObject['count']}</td>
//
//                         <td class="buy-money">
//                             <strong>${(orderObject['price'] * orderObject['count']).toLocaleString()} 원</strong>
//                         </td>
//
//                         <td>
//                             <strong>${orderObject['status']}</strong>
//                         </td>
//                     </tr>
//
//                     </tbody>
//                     </table>`;
//
//                     const domParser = new DOMParser();
//                     const dom = domParser.parseFromString(orderHtmlText, 'text/html');
//
//                     console.log(dom);
//
//
//                     const orderElement = dom.querySelector('[rel="line"]');
//                     orderElement.dataset.itemIndex = orderObject['itemIndex'];
//                     orderElement.dataset.count = orderObject['count'];
//                     orderElement.dataset.orderColor = orderObject['orderColor'];
//                     orderElement.dataset.orderSize = orderObject['orderSize'];
//
//
//
//
//                     orderContainer.append(orderElement);
//
//
//                 }
//             }
//         }
//     };
//     xhr.send();
// }

// loadCart();








