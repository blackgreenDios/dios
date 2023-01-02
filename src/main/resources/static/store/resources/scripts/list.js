const articleCreation = window.document.getElementById('articleCreation');
articleCreation.addEventListener('click',e=>{
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/goods/write`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
               alert('성공 ~');
                //응답코드 출력
            } else {//200-299 아니면
               alert('잠시 후 다시 시도해주세욤');
            }
        }
    };
})