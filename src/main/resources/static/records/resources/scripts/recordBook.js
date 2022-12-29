// DIARY
const diaryAddButton = document.querySelector('[rel="diaryAddButton"]');
const diaryWrite = document.querySelector('[rel="diaryWrite"]');
const closeButton = document.querySelector('[rel="closeButton"]');
const dw = document.querySelector('[rel="dw"]');

// DIARY 켜지는 거
diaryAddButton?.addEventListener('click', e => {
    e.preventDefault();
    diaryWrite.classList.add('visible');
});

// DIARY x 누르면 나가지는 거
closeButton.addEventListener('click', e => {
    e.preventDefault();
    diaryWrite.classList.remove('visible');
});

// DIARY 작성 누르면 작성되는 거
diaryWrite.onsubmit = e => {
    e.preventDefault();

    // 아무것도 안 적으면 흔들려
    if (diaryWrite['write'].value === '') {
        dw.classList.add('blank');
        diaryWrite['write'].focus();

        setTimeout(()=>{
            dw.classList.remove('blank');
        },200);

        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('diary', diaryWrite['write'].value);

    xhr.open('POST', './recordBook');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        diaryWrite.classList.remove('visible');
                        const date = responseObject['date'];
                        window.location.href = `./recordBook?dt=${date}`;
                        break;
                    default:
                        alert('오류가 발생했습니다.');
                }
            } else {
                alert('서버와 통신 오류가 발생했습니다.');
            }
        }
    }
    xhr.send(formData);
};



// ADD
const addAddButton = document.querySelector('[rel="addAddButton"]');
const addWrite = document.querySelector('[rel="addWrite"]');
const addCloseButton = document.querySelector('[rel="addCloseButton"]');
const aw = document.querySelector('[rel="aw"]');

// ADD 켜지는 거
addAddButton?.addEventListener('click', e => {
    e.preventDefault();
    addWrite.classList.add('visible');
});

// ADD x 누르면 나가지는 거
addCloseButton.addEventListener('click', e => {
    e.preventDefault();
    addWrite.classList.remove('visible');
});

// ADD 작성 누르면 작성되는 거
addWrite.onsubmit = e => {
    e.preventDefault();

    // 아무것도 안 적으면 흔들려
    if (addWrite['add'].value === '') {
        aw.classList.add('blank');
        addWrite['add'].focus();

        setTimeout(()=>{
            aw.classList.remove('blank');
        },200);

        return false;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('add', addWrite['add'].value);

    xhr.open('POST', './recordBook');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        addWrite.classList.remove('visible');
                        const date = responseObject['date'];
                        window.location.href = `recordBook?dt=${date}`;
                        break;
                    default:
                        alert('오류가 발생했습니다.');
                }
            } else {
                alert('서버와 통신 오류가 발생했습니다.');
            }
        }
    }
    xhr.send(formData);
};




// PHOTO
const book = document.getElementById('book');

book.querySelector('[rel="imageSelectButton"]').addEventListener('click', e => {
    e.preventDefault();
    book['images'].click();
});

book['images']?.addEventListener('input', () => {
    const imageContainerElement = book.querySelector('[rel="photoContainer"]');
    imageContainerElement.querySelectorAll('img.image').forEach(x => x.remove());

    const imageSrc = URL.createObjectURL(images.files[0]); //하나만 올라갈꺼니까 0번째

    // if (photo['image'].files.length > 0) { //선택한 파일이 있다
    //     photo.querySelector('[rel = "noImage"]').classList.add('hidden');
    // } else {//선택한 파일이 없다
    //     photo.querySelector('[rel = "noImage"]').classList.remove('hidden');
    // }
    //
    // const imageSrc = URL.createObjectURL(file);

    const imgElement = document.createElement('img');
    imgElement.classList.add('image');
    imgElement.setAttribute('src', imageSrc);
    imageContainerElement.append(imgElement);
});

// PHOTO 올리는 거
book.onsubmit = e => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    for (let file of book['images'].files) {
        formData.append('image', file);
    }

    xhr.open('POST', './recordBook');
    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success' :
                        alert('성공');
                        break;
                    default:
                        alert('오류가 발생했습니다.');
                }
            } else {
                alert('서버와 통신 오류가 발생했습니다.');
            }
        }
    }
    xhr.send(formData);
};


// 글 수정하는 거
const modifyDiary = document.querySelector('[rel="modifyDiary"]');

modifyDiary?.addEventListener('click', e => {
    e.preventDefault();

    diaryWrite.classList.add('visible');
});

const modifyAdd = document.querySelector('[rel="modifyAdd"]');

modifyAdd?.addEventListener('click', e => {
    e.preventDefault();

    addWrite.classList.add('visible');
});


// diary 내용삭제
const deleteDiary = document.querySelector('[rel="deleteDiary"]');
const realDelete = document.querySelector('[rel="realDelete"]');
const yes = document.querySelector('[rel="yes"]');
const no = document.querySelector('[rel="no"]');

deleteDiary?.addEventListener('click', e => {
    e.preventDefault();

    realDelete.classList.add('visible');

    yes?.addEventListener('click', e => {
        e.preventDefault();

        const xhr = new XMLHttpRequest();

        xhr.open('DELETE', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            const date = responseObject['date'];
                            window.location.href = `./recordBook?dt=${date}`;
                            break;
                        default:
                            alert('실패');
                    }
                } else {
                    alert('서버와 통신하지 못했어');
                }
            }
        }
        xhr.send();
    });

    no?.addEventListener('click', e => {
        e.preventDefault();

        realDelete.classList.remove('visible');
    });
});


// add 내용삭제
const deleteAdd = document.querySelector('[rel="deleteAdd"]');
const realDeleteAdd = document.querySelector('[rel="realDeleteAdd"]');
const yesAdd = document.querySelector('[rel="yesAdd"]');
const noAdd = document.querySelector('[rel="noAdd"]');

deleteAdd?.addEventListener('click', e => {
    e.preventDefault();

    realDeleteAdd.classList.add('visible');

    yesAdd?.addEventListener('click', e => {
        e.preventDefault();

        const xhr = new XMLHttpRequest();

        xhr.open('PATCH', window.location.href);
        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            const date = responseObject['date'];
                            window.location.href = `./recordBook?dt=${date}`;
                            break;
                        default:
                            alert('실패');
                    }
                } else {
                    alert('서버와 통신 x');
                }
            }
        }
        xhr.send();
    });

    noAdd?.addEventListener('click', e => {
        e.preventDefault();

        realDeleteAdd.classList.remove('visible');
    });
});


// 달력 버튼
const calendarButton = document.querySelector('[rel="calendarButton"]');
const calendarContainer = document.querySelector('[rel="calendarContainer"]');
const exitButton = document.querySelector('[rel="exitButton"]');

calendarButton.addEventListener('click', e => {
    e.preventDefault();

    calendarContainer.classList.add('visible');

    // 달력 나가기 버튼
    exitButton?.addEventListener('click', e => {
        calendarContainer.classList.remove('visible');
    });
});

// 달력
document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        // Tool Bar 목록 document : https://fullcalendar.io/docs/toolbar
        headerToolbar: {
            left: 'prevYear,prev,next,nextYear today',
            center: 'title',
            right: 'dayGridMonth,dayGridWeek,dayGridDay'
        },

        selectable: true,
        selectMirror: true,

        navLinks: true, // can click day/week names to navigate views
        editable: true,
        // Create new event
        select: function (arg) {
            Swal.fire({
                html: "<div class='mb-7'>Create new event?</div><div class='fw-bold mb-5'>Event Name:</div><input type='text' class='form-control' name='event_name' />",
                icon: "info",
                showCancelButton: true,
                buttonsStyling: false,
                confirmButtonText: "Yes, create it!",
                cancelButtonText: "No, return",
                customClass: {
                    confirmButton: "btn btn-primary",
                    cancelButton: "btn btn-active-light"
                }
            }).then(function (result) {
                if (result.value) {
                    var title = document.querySelector("input[name=;event_name']").value;
                    if (title) {
                        calendar.addEvent({
                            title: title,
                            start: arg.start,
                            end: arg.end,
                            allDay: arg.allDay
                        })
                    }
                    calendar.unselect()
                } else if (result.dismiss === "cancel") {
                    Swal.fire({
                        text: "Event creation was declined!.",
                        icon: "error",
                        buttonsStyling: false,
                        confirmButtonText: "Ok, got it!",
                        customClass: {
                            confirmButton: "btn btn-primary",
                        }
                    });
                }
            });
        },

        // Delete event
        eventClick: function (arg) {
            Swal.fire({
                text: "Are you sure you want to delete this event?",
                icon: "warning",
                showCancelButton: true,
                buttonsStyling: false,
                confirmButtonText: "Yes, delete it!",
                cancelButtonText: "No, return",
                customClass: {
                    confirmButton: "btn btn-primary",
                    cancelButton: "btn btn-active-light"
                }
            }).then(function (result) {
                if (result.value) {
                    arg.event.remove()
                } else if (result.dismiss === "cancel") {
                    Swal.fire({
                        text: "Event was not deleted!.",
                        icon: "error",
                        buttonsStyling: false,
                        confirmButtonText: "Ok, got it!",
                        customClass: {
                            confirmButton: "btn btn-primary",
                        }
                    });
                }
            });
        },
        dayMaxEvents: true, // allow "more" link when too many events
        // 이벤트 객체 필드 document : https://fullcalendar.io/docs/event-object
        events: [
            {
                title: 'All Day Event',
                start: '2022-07-01'
            },
            {
                title: 'Long Event',
                start: '2022-07-07',
                end: '2022-07-10'
            },
            {
                groupId: 999,
                title: 'Repeating Event',
                start: '2022-07-09T16:00:00'
            },
            {
                groupId: 999,
                title: 'Repeating Event',
                start: '2022-07-16T16:00:00'
            },
            {
                title: 'Conference',
                start: '2022-07-11',
                end: '2022-07-13'
            },
            {
                title: 'Meeting',
                start: '2022-07-12T10:30:00',
                end: '2022-07-12T12:30:00'
            },
            {
                title: 'Lunch',
                start: '2022-07-12T12:00:00'
            },
            {
                title: 'Meeting',
                start: '2022-07-12T14:30:00'
            },
            {
                title: 'Happy Hour',
                start: '2022-07-12T17:30:00'
            },
            {
                title: 'Dinner',
                start: '2022-07-12T20:00:00'
            },
            {
                title: 'Birthday Party',
                start: '2022-07-13T07:00:00'
            },
            {
                title: 'Click for Google',
                url: 'http://google.com/',
                start: '2022-07-28'
            }
        ]
    });

    calendar.render();
});



