const write = document.querySelector('[rel="write"]');

write?.addEventListener('click', e => {
    e.preventDefault();

    window.location.href = `/goods/write`;
});


// 이미지 슬라이드
let index = 0;   //이미지에 접근하는 인덱스
window.onload = function() {
    slideShow();
}

function slideShow() {
    let i;
    const slide1 = document.getElementsByClassName("slide1");  //slide1에 대한 dom 참조
    const slide2 = document.getElementsByClassName("slide2");
    const slide3 = document.getElementsByClassName("slide3");
    const slide4 = document.getElementsByClassName("slide4");

    for (i = 0; i < slide1.length; i++) {
        slide1[i].style.display = "none";   //처음에 전부 display를 none으로 한다.
        slide2[i].style.display = "none";
        slide3[i].style.display = "none";
        slide4[i].style.display = "none";
    }
    index++;

    if (index > slide1.length) {
        index = 1;  //인덱스가 초과되면 1로 변경
    }

    slide1[index - 1].style.display = "block";  //해당 인덱스는 block으로
    slide2[index - 1].style.display = "block";
    slide3[index - 1].style.display = "block";
    slide4[index - 1].style.display = "block";


    setTimeout(slideShow, 1500);
}
