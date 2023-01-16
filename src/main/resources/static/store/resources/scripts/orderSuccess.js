const button = document.querySelector('[rel="button"]');

button.addEventListener("click", e => {
    e.preventDefault();

    window.location.href = '/dios/myPageDeliveryCheck';
});