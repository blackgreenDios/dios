const Form = document.getElementById('form');


Form['deleteButton'].addEventListener('click', e => {
    e.preventDefault();
    if(!confirm('정말로 탈퇴하시겠습니까?')){
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    xhr.open('DELETE', window.location.href);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_signed':
                        alert('회원탈퇴를 진행할 수 있는 권한이 없거나 로그아웃되었습니다. 확인 후 다시 시도해 주세요.');
                        window.location.href = 'login';
                        break;
                    case 'success':
                        alert('회원탈퇴가 완료되었습니다.');
                        window.location.href = "./login";
                        break;
                    default:
                        alert('알 수 없는 이유로 회원탈퇴를 진행하지 못하였습니다. 잠시 후 다시 시도해주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요');
            }
        }
    };
    xhr.send();

});