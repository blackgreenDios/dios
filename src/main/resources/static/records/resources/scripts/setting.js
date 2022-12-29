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
                        // window.location.href = 'squat';
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