const content = document.querySelector("input.search");
const url = "http://127.0.0.1:8080/searchContent?keyword=";
content.addEventListener('input',function () {
    fetch(url+content.value)
        .then(function(response) {
            return response.json();
        })
        .then(function(myJson) {
            console.log(myJson.songs);
            create(myJson.songs);
        });
});
let searchLiked = document.querySelector('.searchLiked > ul');
function create(songs) {
    for (let i = 0; i < songs.length; i++) {
        const song = songs[i];
        let search = document.createElement('li');
        search.innerHTML = `
        
            <img src="${song.cover}">
            <a>${song.name}</a>
       
        `;
        searchLiked.appendChild(search);
    }


}