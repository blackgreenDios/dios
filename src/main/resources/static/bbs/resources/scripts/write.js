const form=window.document.getElementById('form');

const Warning = {
    show: (text) => {
        form.querySelector('[rel="warningText"]').innerText = text;
        form.querySelector('[rel="warningRow"]').classList.add('visible');
    },
    hide: () => {
        form.querySelector('[rel="warningRow"]').classList.remove('visible');
    }
};
let editor;
ClassicEditor
    .create(form['content'],{
        simpleUpload:{
            uploadUrl:'./image'
        }
    })
    .then( e =>editor =e);

form['back'].addEventListener('click', () =>
    alert('글쓰기를 취소하시겠습니까?')
);
form['back'].addEventListener('click', () =>
    window.history.length < 2 ? window.close() : window.history.back());

form.onsubmit=e=>{
    e.preventDefault();
    Warning.hide();

    if(form['title'].value === ''){
        Warning.show('제목을 입력해 주세요.')
        form['title'].focus();
        return false;
    }
    if(editor.getData()===''){
        Warning.show('내용을 입력해 주세요.')
        editor.focus();
        return false;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('bid',form['bid'].value)
    formData.append("title", form['title'].value);
    formData.append("content", editor.getData());
    xhr.open('POST','./write');
    xhr.onreadystatechange=()=>{
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'not_allowed':
                        Warning.show('게시글을 작성할 수 있는 권한이 없거나 로그아웃되었습니다. 확인 후 다시 시도해 주세요.')
                        break;
                    case 'success':
                        const aid=responseObject['aid'];
                        window.location.href=`read?aid=${aid}`;
                        break;
                    default:
                        Warning.show('알 수 없는 이유로 게시글을 작성하지 못하였습니다. 잠시 후 다시 시도해 주세요');

                }
            } else {
                Warning.show('서버와 통신하지 못하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
    }
    xhr.send(formData);
}
