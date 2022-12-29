// More API functions here:
// https://github.com/googlecreativelab/teachablemachine-community/tree/master/libraries/pose
const countContainer = document.getElementById('countContainer');
// the link to your model provided by Teachable Machine export panel
const URL = "https://teachablemachine.withgoogle.com/models/B_nn-4vLK/";
const progress = document.getElementById('progress');
let model, webcam, ctx, labelContainer, maxPredictions;
let goal = parseInt(progress.querySelector('[rel="goal"]').dataset.goal);

async function init() {
    const modelURL = URL + "model.json";
    const metadataURL = URL + "metadata.json";

    // load the model and metadata
    // Refer to tmImage.loadFromFiles() in the API to support files from a file picker
    // Note: the pose library adds a tmPose object to your window (window.tmPose)
    model = await tmPose.load(modelURL, metadataURL);
    maxPredictions = model.getTotalClasses();

    // Convenience function to setup a webcam
    const size = 500;
    const flip = true; // whether to flip the webcam
    webcam = new tmPose.Webcam(size, size, flip); // width, height, flip
    await webcam.setup(); // request access to the webcam
    await webcam.play();
    window.requestAnimationFrame(loop);

    // append/get elements to the DOM
    const canvas = document.getElementById("canvas");
    canvas.width = size;
    canvas.height = size;
    ctx = canvas.getContext("2d");
    labelContainer = document.getElementById("label-container");
    for (let i = 0; i < maxPredictions; i++) { // and class labels
        labelContainer.appendChild(document.createElement("div"));
    }
}

let breakLoop = false;
async function loop(timestamp) {
    webcam.update(); // update the webcam frame
    await predict();
    if (!breakLoop) {
        window.requestAnimationFrame(loop);
    }
}


const success = document.getElementById('congratulation');
let count = 0;
let status = "stand";
async function predict() {
    // Prediction #1: run input through posenet
    // estimatePose can take in an image, video or canvas html element
    const {pose, posenetOutput} = await model.estimatePose(webcam.canvas);
    // Prediction 2: run input through teachable machine classification model
    const prediction = await model.predict(posenetOutput);

    const stand = prediction[0].probability.toFixed(2);
    const squat = prediction[1].probability.toFixed(2);
    if ( stand > 0.90) {
        if (status === "squat") {
            count++;
            // labelContainer.innerHTML = count + " 개&nbsp&nbsp";


            // progress bar
            const meters = document.querySelectorAll('svg[value] .meter');

            meters.forEach((path) => {
                // Get the length of the path
                let length = path.getTotalLength();


                // Get the value of the meter
                let value = parseInt(path.parentNode.getAttribute('value'));
                // Calculate the percentage of the total length
                let to = length * (1 - (count / value));
                // Trigger Layout in Safari hack https://jakearchibald.com/2013/animated-line-drawing-svg/
                path.getBoundingClientRect();
                // Set the Offset
                path.style.strokeDashoffset = Math.max(0, to);

                let pathValue = path.nextElementSibling;
                pathValue.textContent = `${count} 개`;
                pathValue.nextElementSibling.textContent = `/ ${value} 개`;
                pathValue.nextElementSibling.nextElementSibling.textContent = `${ Math.round((count / value) * 100) } %`;
            });
        }
        status = "stand";
    } else if ( parseInt(squat) === 1) {
        status = "squat";
    }

    if (count === goal) {
        console.log('다함');

        setTimeout(()=>{
            success.classList.add('visible');
        },2000);


        // 폭죽
        let particles = [];
        const colors = ["#eb6383","#fa9191","#ffe9c5","#b4f2e1"];
        function pop () {
            for (let i = 0; i < 150; i++) {
                const p = document.createElement('particule');
                p.x = window.innerWidth * 0.5;
                p.y = window.innerHeight + (Math.random() * window.innerHeight * 0.3);
                p.vel = {
                    x: (Math.random() - 0.5) * 10,
                    y: Math.random() * -20 - 15
                };
                p.mass = Math.random() * 0.2 + 0.8;
                particles.push(p);
                p.style.transform = `translate(${p.x}px, ${p.y}px)`;
                const size = Math.random() * 15 + 5;
                p.style.width = size + 'px';
                p.style.height = size + 'px';
                p.style.background = colors[Math.floor(Math.random()*colors.length)];
                document.body.appendChild(p);
            }
        }

        function render () {
            for (let i = particles.length - 1; i--; i > -1) {
                const p = particles[i];
                p.style.transform = `translate3d(${p.x}px, ${p.y}px, 1px)`;

                p.x += p.vel.x;
                p.y += p.vel.y;

                p.vel.y += (0.5 * p.mass);
                if (p.y > (window.innerHeight * 2)) {
                    p.remove();
                    particles.splice(i, 1);
                }
            }
            requestAnimationFrame(render);
        }
        pop();
        window.setTimeout(render, 700);
        window.addEventListener('click', pop);


        breakLoop = true;
    }



    // for (let i = 0; i < 2; i++) {
    //     const classPrediction =
    //         prediction[i].className + ": " + prediction[i].probability.toFixed(2);
    //     labelContainer.childNodes[i].innerHTML = classPrediction;
    //     }

    // finally draw the poses
    drawPose(pose);
}

function drawPose(pose) {
    if (webcam.canvas) {
        ctx.drawImage(webcam.canvas, 0, 0);
        // draw the keypoints and skeleton
        if (pose) {
            const minPartConfidence = 0.5;
            tmPose.drawKeypoints(pose.keypoints, minPartConfidence, ctx);
            tmPose.drawSkeleton(pose.keypoints, minPartConfidence, ctx);
        }
    }
}

// 목표 개수 성공했을 때 기록하기
const goRecord = document.querySelector('[rel="goRecord"]');

goRecord.addEventListener('click', e => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();

    formData.append('squatCount', count);
    formData.append('squatSetting', goal);

    xhr.open('POST', './squat');

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status >= 200 && xhr.status < 300) {
                const responseObject = JSON.parse(xhr.responseText);
                switch (responseObject['result']) {
                    case 'success':
                        const date = responseObject['date']
                        window.location.href = `recordBook?dt=${date}`;
                        break;
                    default:
                        alert('알 수 없는 이유로 실패');
                }
            } else {
                alert('서버와 통신 실패');
            }
        }
    }
    xhr.send(formData);
});


// 목표 개수 실패했을 때 기록하기
const recordButton = document.querySelector('[rel="recordButton"]');
const stopRecord = document.querySelector('[rel="stop"]');
const stopRecordButton = document.querySelector('[rel="stopRecord"]');
const continueButton = document.querySelector('[rel="continue"]');

recordButton.addEventListener('click', e => {
    e.preventDefault();

    stopRecord.classList.add('visible');

    // 그냥 중단하고 기록한다고 했을 때
    // (? 붙이는 이유는 조건부라서 : 버튼이 보일 때도 있고 안 보일 때도 있기 때문에)
    stopRecordButton?.addEventListener('click', e => {

        e.preventDefault();

        const xhr = new XMLHttpRequest();
        const formData = new FormData();

        formData.append('squatCount', count);
        formData.append('squatSetting', goal);

        xhr.open('POST', './squat');

        xhr.onreadystatechange = () => {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    const responseObject = JSON.parse(xhr.responseText);
                    switch (responseObject['result']) {
                        case 'success':
                            const date = responseObject['date']
                            window.location.href = `recordBook?dt=${date}`;
                            break;
                        default:
                            alert('알 수 없는 이유로 실패');
                    }
                } else {
                    alert('서버와 통신 실패');
                }
            }
        }
        xhr.send(formData);
    });

    // continue 버튼 눌러서 계속하기
    continueButton.addEventListener('click', e => {
        e.preventDefault();

        stopRecord.classList.remove('visible');

    });

});


// 목표 변경하기
const changeButton = document.querySelector('[rel="changeButton"]');
const changeRecord = document.querySelector('[rel="change"]');
const changeCount = document.querySelector('[rel="changeCount"]');
const continueRecord = document.querySelector('[rel="continueRecord"]');

changeButton.addEventListener('click', e => {
    e.preventDefault();

    changeRecord.classList.add('visible');

    changeCount.addEventListener('click', e => {
        window.location.href = 'setting';
    });

    // continue 버튼 눌러서 계속하기
    continueRecord.addEventListener('click', e => {
        e.preventDefault();

        changeRecord.classList.remove('visible');

    });

});



