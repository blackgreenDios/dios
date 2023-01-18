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



// 메뉴얼
const manual = document.querySelector('[rel="manual"]');
const manualContainer = document.querySelector('[rel="manualContainer"]');

manual.addEventListener('click', e => {
    e.preventDefault();

    manualContainer.classList.add('visible');
});

// 메뉴얼 끄기
const exits = document.querySelectorAll('[rel="exit"]');

exits.forEach(exit  => {
    exit.addEventListener('click', e => {
        e.preventDefault();

        manualContainer.classList.remove('visible');
    });
});

// 메뉴얼
/*
  div사이즈 동적으로 구하기
*/
const outer = document.querySelector('.outer');
const innerList = document.querySelector('.inner-list');
const inners = document.querySelectorAll('.inner');
let currentIndex = 0; // 현재 슬라이드 화면 인덱스

inners?.forEach((inner) => {
    inner.style.width = 900; // inner의 width를 모두 outer의 width로 만들기
})

innerList.style.width = `${900 * 5}px`; // innerList의 width를 inner의 width * inner의 개수로 만들기

/*
  버튼에 이벤트 등록하기
*/
const buttonLeft = document.querySelector('.button-left');
const buttonRight = document.querySelector('.button-right');

buttonLeft?.addEventListener('click', () => {
    currentIndex--;
    currentIndex = currentIndex < 0 ? 0 : currentIndex; // index값이 0보다 작아질 경우 0으로 변경
    innerList.style.marginLeft = `-${outer.clientWidth * currentIndex}px`; // index만큼 margin을 주어 옆으로 밀기
});

buttonRight?.addEventListener('click', () => {
    currentIndex++;
    currentIndex = currentIndex >= inners.length ? inners.length - 1 : currentIndex; // index값이 inner의 총 개수보다 많아질 경우 마지막 인덱스값으로 변경
    innerList.style.marginLeft = `-${outer.clientWidth * currentIndex}px`; // index만큼 margin을 주어 옆으로 밀기
});








