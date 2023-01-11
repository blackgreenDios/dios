// 이미지 슬라이드
let index = 0;   //이미지에 접근하는 인덱스
window.onload = function() {
    slideShow();
}

function slideShow() {
    let i;
    const slide = document.getElementsByClassName("slide");  //slide1에 대한 dom 참조
    const slide1 = document.getElementsByClassName("slide1");
    const slide2 = document.getElementsByClassName("slide2");
    for (i = 0; i < slide.length; i++) {
        slide[i].style.display = "none";   //처음에 전부 display를 none으로 한다.
        slide1[i].style.display = "none";
        slide2[i].style.display = "none";
    }
    index++;

    if (index > slide.length) {
        index = 1;  //인덱스가 초과되면 1로 변경
    }

    slide[index - 1].style.display = "block";  //해당 인덱스는 block으로
    slide1[index - 1].style.display = "block";
    slide2[index - 1].style.display = "block";

    setTimeout(slideShow, 2000);   //함수를 4초마다 호출
}


// start 누르면 시작
const startContainer = document.querySelector('[rel="startContainer"]');

startContainer.addEventListener('click', e => {
    e.preventDefault();

    window.location.href = `/record/setting`;
});




