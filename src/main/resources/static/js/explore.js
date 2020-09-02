const changeIcon = document.querySelector('.loadMore');
changeIcon.addEventListener('click', function() {
    const x = document.querySelectorAll('.artists .item');
    for (i = 0; i < x.length; i++) {
        x[i].setAttribute("class", "item");
    }
});