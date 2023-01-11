const form = document.getElementById('form');
const squat = document.querySelector('[rel="squat"]');
const lunge = document.querySelector('[rel="lunge"]');
const plank = document.querySelector('[rel="plank"]');

form.querySelectorAll('input.select').forEach(select => {
    select.addEventListener('click', () => {
        form.querySelectorAll('input.select').forEach(x => x.classList.remove('selected'));
        select.classList.add('selected');
    });
});


form['submit'].addEventListener('click', () => {
    form.querySelector('[rel="warning"]').classList.remove('visible');

    // 아무것도 선택하지 않았을 때
    if (!squat.classList.contains("selected") && !lunge.classList.contains("selected") && !plank.classList.contains("selected")) {
        form.querySelector('[rel="selectWarning"]').classList.add('visible');
        return false;
    }

    form.querySelector('[rel="selectWarning"]').classList.remove('visible');

    // 목표개수를 입력하지 않았을 때
    if (form['setting'].value === '') {
        form.querySelector('[rel="warning"]').classList.add('visible');
        form['setting'].focus();
        return false;
    }

    form.querySelector('[rel="warning"]').classList.remove('visible');

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('goalCount', form['setting'].value);

    xhr.open('PATCH', './setting');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        if (squat.classList.contains("selected")) {
                            window.location.href = 'squat';
                        } else if (lunge.classList.contains("selected")) {
                            window.location.href = 'lunge';
                        } else if (plank.classList.contains("selected")) {
                            window.location.href = 'plank';
                        }
                        break;
                    default:
                        alert('실패했어');
                }
            } else {
                alert('서버와 통신하지 못했어');
            }
        }
    }
    xhr.send(formData);
});


// 이미지 슬라이드
let index = 0;   //이미지에 접근하는 인덱스
window.onload = function() {
    slideShow();
}

function slideShow() {
    let i;
    const slide = document.getElementsByClassName("slide");  //slide1에 대한 dom 참조
    for (i = 0; i < slide.length; i++) {
        slide[i].style.display = "none";   //처음에 전부 display를 none으로 한다.
    }
    index++;

    if (index > slide.length) {
        index = 1;  //인덱스가 초과되면 1로 변경
    }
    slide[index - 1].style.display = "block";  //해당 인덱스는 block으로

    setTimeout(slideShow, 2700);   //함수를 4초마다 호출
}