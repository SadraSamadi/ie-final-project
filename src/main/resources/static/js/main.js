document.addEventListener('DOMContentLoaded', function () {
    var year = document.getElementById('year');
    var now = new Date();
    year.innerHTML = now.getFullYear() + '';
});
