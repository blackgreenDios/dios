const commentForm =window.document.getElementById('commentForm');
const commentContainer=window.document.getElementById('commentContainer');

const loadComments = () => {
    commentContainer.innerHTML = '';

    const url = new URL(window.location.href);
    const searchParams = url.searchParams;
    const aid = searchParams.get('aid');
    const xhr = new XMLHttpRequest();

    xhr.open('GET', `./comment?aid=${aid}`);
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const  responseArray=JSON.parse(xhr.responseText);
                const appendComment=(commentObject,isSub)=>{
                    const commentHtmlText=`
                     <div class="comment ${isSub ? 'sub' : ''} ${commentObject['isMine']===true ? 'mine':''}
                     ${commentObject['isLiked'] ? 'liked' : ''}" rel="comment">
                            <div class="head">
                                <span class="writer">${commentObject['userNickname']}</span>
                                <span class="dt">${commentObject['writtenOn']}</span>
                                <span class="count" >${commentObject['likeCount']+' 명이 좋아합니다'}</span>
                                <span class="spring"></span>
                                <span class="action-container">
                                    ${commentObject['isSigned']===true ? '<a class="action reply" href="#" rel="actionReply">답글</a>' :''}
                                    ${commentObject['isMine']===true ? '<a class="action modify" href="#" rel="actionModify">수정</a>' :''}
                                    ${commentObject['isMine']===true ? '<a class="action delete" href="#" rel="actionDelete">삭제</a>' :''}
                                    
                                    <a class="action cancel" href="#" rel="actionCancel">취소</a>
                                </span>
                            </div>
                            <div class="body">
                                <div class="content">
                                    <span class="text">${commentObject['content']}</span>
                                    <div class="like">
                                        <a href="#" class="toggle ${commentObject['isSigned'] === true ? '' : 'prohibited'}" rel="likeToggle">
                                            <i class="icon fa-solid fa-heart"></i>
                                        </a>
                                    </div>
                                </div>
                                <form class="modify-form" rel="modifyForm">
                                    <label class="label">
                                        <span hidden>댓글 수정</span>
                                        <input class="--object-input" maxlength="100" name="content"
                                               placeholder="댓글을 입력해 주세요." type="text">
                                     
                                    </label>
                                    <input class="--object-button" type="submit" value="수정">
                                </form>
                            </div>
                     </div>
                     <form class="reply-form" rel="replyForm">
                        <div class="head">
                            <span class="to">@${commentObject['userNickname']}</span>
                            <a href="#" class="cancel" rel="replyCancel">취소</a>
                        </div>
                        <div class="body">
                            <label class="label">
                                <span hidden>답글 작성</span>
                                <input class="--object-input" maxlength="100" name="content"
                                       placeholder="답글을 입력해 주세요." type="text">
                            </label>
                            <div class="secret" >
                                    <a href="#" class="toggle">
                                        <i class="icon fa-solid fa-unlock"></i>
                                    </a>
                                </div>
                            <input class="--object-button" type="submit" value="작성">
                        </div>
                     </form>`;
                    const domParser=new DOMParser();
                    const dom=domParser.parseFromString(commentHtmlText,'text/html');
                    const commentElement= dom.querySelector('[rel="comment"]');
                    const modifyFormElement=dom.querySelector('[rel="modifyForm"]');
                    const replyFormElement=dom.querySelector('[rel="replyForm"]');
                    const likeToggleElement=dom.querySelector('[rel="liketoggle"]');
                    if(!likeToggleElement.classList.contains('prohibited')){
                        likeToggleElement.addEventListener('click',e=>{
                            e.preventDefault();
                            const xhr=new XMLHttpRequest();
                            const formData=new FormData();
                            const method=commentObject['isLiked']===true ? 'DELETE' : 'POST';
                            formData.append('commentIndex',commentObject['index']);
                            xhr.open(method,'./comment-like');
                            xhr.onreadystatechange=()=>{
                                if(xhr.readyState===XMLHttpRequest.DONE){
                                    if (xhr.status >= 200 && xhr.status < 300) {
                                        const responseObject=JSON.parse(xhr.responseText);
                                        switch (responseObject['result']) {
                                            case 'success':
                                                loadComments();
                                                break;
                                            default:
                                                alert('알 수 없는 이우로 요청을 처리하지 못하였습니다.\n\n 잠시 후 다시 시도해 주세요.');
                                        }
                                    }else {
                                        alert('서버와 통신하지 못하였습니다.\n\n 잠시 후 다시 시도해 주세요.')
                                    }
                                }
                            };
                            xhr.send(formData);
                        });
                    }

                    dom.querySelector('[rel="actionReply"]')?.addEventListener('click',e=>{
                        e.preventDefault();
                        replyFormElement.classList.add('visible');
                        replyFormElement['content'].focus();
                    });
                    dom.querySelector('[rel="actionModify"]')?.addEventListener('click',e=>{
                        e.preventDefault();
                        commentElement.classList.add('modifying');
                        modifyFormElement['content'].value=commentObject['content'];
                        modifyFormElement['content'].focus();
                    });

                    modifyFormElement.onsubmit=e=>{
                        e.preventDefault();

                        if(modifyFormElement['content'].value===''){
                            modifyFormElement['content'].focus();
                            return false;
                        }

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', commentObject['index']);
                        formData.append('content',modifyFormElement['content'].value);

                        xhr.open('PATCH', './comment');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                Cover.hide();
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject=JSON.parse(xhr.responseText);
                                    switch (responseObject['result']){
                                        case 'success':
                                            loadComments();
                                            break;
                                        case 'no_such_comment':
                                            alert('수정하려는 댓글이 더 이상 존재하지 않습니다\n\n이미 삭제되었을 수도 있습니다.')
                                            break;
                                        case 'not_allowed':
                                            alert('해당 댓글을 수정할 권한이 없습니다.')
                                        default:
                                            alert('알 수 없는 이유로 댓글을 수정하지 못하였습니다. \n\n잠시후 다시 시도해 주세요.')

                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }
                        };
                        xhr.send(formData);
                    }
                    dom.querySelector('[rel="actionCancel"]')?.addEventListener('click',e=>{
                        e.preventDefault();
                        commentElement.classList.remove('modifying');
                    });

                    dom.querySelector('[rel="actionDelete"]')?.addEventListener('click',e=>{
                        e.preventDefault();
                        if(!confirm('정말로 댓글을 삭제할까요?')) {
                            return;
                        }
                        const xhr = new XMLHttpRequest();
                        const formData = new FormData();
                        formData.append('index', commentObject['index']);

                        xhr.open('DELETE', './comment');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                Cover.hide();
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject=JSON.parse(xhr.responseText);
                                    switch (responseObject['result']){
                                        case 'success':
                                            loadComments();
                                            break;
                                        case 'no_such_comment':
                                            alert('삭제하려는 댓글이 더 이상 존재하지 않습니다\n\n이미 삭제되었을 수도 있습니다.')
                                            break;
                                        case 'not_allowed':
                                            alert('해당 댓글을 삭제할 권한이 없습니다.')
                                        default:
                                            alert('알 수 없는 이유로 댓글을 삭제하지 못하였습니다. \n\n잠시후 다시 시도해 주세요.')
                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }
                        };
                        xhr.send(formData);

                    });

                    dom.querySelector('[rel="replyCancel"]').addEventListener('click', e => {
                        e.preventDefault();
                        replyFormElement.classList.remove('visible');
                    });
                    replyFormElement.onsubmit=e=>{
                        e.preventDefault();
                        if(replyFormElement['content'].value===''){
                            replyFormElement['content'].focus();
                            return false;
                        }

                        const xhr = new XMLHttpRequest();
                        const formData = new FormData()
                        formData.append('articleIndex', commentForm['aid'].value);
                        formData.append('content', replyFormElement['content'].value);
                        formData.append('commentIndex',commentObject['index']);

                        xhr.open('POST', './read');
                        xhr.onreadystatechange = () => {
                            if (xhr.readyState === XMLHttpRequest.DONE) {
                                Cover.hide();
                                if (xhr.status >= 200 && xhr.status < 300) {
                                    const responseObject = JSON.parse(xhr.responseText);
                                    switch (responseObject['result']) {
                                        case 'success':
                                            loadComments();
                                            break;
                                        default:
                                            alert('알 수 없는 이유로 댓글을 작성하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                    }
                                } else {
                                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                                }
                            }
                        };
                        xhr.send(formData);


                    };

                    commentContainer.append(commentElement, replyFormElement);

                };

                // for (let commentObject of responseArray.filter(x => x['commentIndex'] === undefined)) {
                //     const replyArray = responseArray.filter(x => x['commentIndex'] === commentObject['index']);
                //     appendComment(commentObject, false);
                //     for (let replyObject of replyArray) {
                //         appendComment(replyObject, true);
                //     }
                //
                // }
                const appendReplyOf = parentComment => {
                    const replyArray = responseArray.filter(x => x['commentIndex'] === parentComment['index']);
                    for (let replyObject of replyArray) {
                        appendComment(replyObject, true);
                        appendReplyOf(replyObject);
                    }
                };
                for (let commentObject of responseArray.filter(x => !x['commentIndex'])) {
                    appendComment(commentObject, false);
                    appendReplyOf(commentObject);
                }
            } else {

            }
        }
    };
    xhr.send();
}

loadComments();

if (commentForm !== null) {
    commentForm.onsubmit = e => {
        e.preventDefault()

        if (commentForm['content'].value === '') {
            alert('댓글을 입력해 주세요.');
            commentForm['content'].focus();
            return false;
        }
        Cover.show('댓글을 작성하고 있습니다.\n잠시만 기다려 주세요.');
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('articleIndex', commentForm['aid'].value);
        formData.append('content', commentForm['content'].value);

        xhr.open('POST', './read');
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                Cover.hide();
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            loadComments();
                            break;
                        default:
                            alert('알 수 없는 이유로 댓글을 작성하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                    }
                } else {
                    alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                }
            }
        };
        xhr.send(formData);

    };

}
const deleteButton=window.document.getElementById('deleteButton');
deleteButton?.addEventListener('click',e=>{
    e.preventDefault();
    if(!confirm('정말로 게시물을 삭제할까요?')){
        return;
    }
    Cover.show('게시글을 삭제하고 있습니다.\n잠시만 기다려 주세요.');
    const xhr=new XMLHttpRequest();
    const formData=new FormData();
    xhr.open('DELETE',window.location.href);
    xhr.onreadystatechange=()=>{
        if (xhr.readyState === XMLHttpRequest.DONE) {
            Cover.hide();
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        const  bid =responseObject['bid'];
                        window.location.href=`./list?bid=${bid}`
                        break;
                    default:
                        alert('알 수 없는 이유로 게시글을 삭제하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);

});

const like = document.querySelector('[rel="like"]');
const likeButton =window.document.getElementById('likeButton');
likeButton?.addEventListener('click',e =>{
    e.preventDefault()
    const xhr=new XMLHttpRequest();
    const formData=new FormData();
    formData.append('aid',new URL(window.location.href).searchParams.get('aid'))
    xhr.open('POST','./article-like');
    xhr.onreadystatechange=()=>{
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':

                        location.href = location.href;


                        break;
                    default:
                        alert('알 수 없는 이유로 요청한 작업을 완료하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
                }
            } else {
                alert('서버와 통신하지 못하였습니다.\n\n잠시 후 다시 시도해 주세요.');
            }
        }
    };
    xhr.send(formData);


});
