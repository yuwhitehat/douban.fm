const other = document.querySelector("li.other");
const select = document.querySelector("li.selected");
const otherSubjects = document.querySelector("otherSubjects-list");
const selectSong = document.querySelector("select-song");
   other.addEventListener('click',function () {
        otherSubjects.style.display = 'block';
        selectSong.style.display = 'none';
        other.style.color = 'black';
        select.style.color = 'rgba(0, 0, 0, 0.4)';
   });
    select.addEventListener('click',function () {
        otherSubjects.style.display = 'none';
        selectSong.style.display = 'flex';
        other.style.color = 'rgba(0, 0, 0, 0.4)';
        select.style.color ='black';
    });