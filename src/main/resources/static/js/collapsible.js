const coll = document.getElementsByClassName("collapsible");

function toggle() {
    this.classList.toggle("active");
    const content = this.nextElementSibling;
    if (content.style.maxHeight){
        content.style.maxHeight = null;
    } else {
        content.style.maxHeight = (this.getAttribute('childcount') * this.scrollHeight) + "px";
    }
}

for (let i = 0; i < coll.length; i++) {
    if (coll[i].getAttribute('childcount') !== '0') {
        coll[i].addEventListener("click", toggle);
        coll[i].click();
    } else {
        coll[i].classList.add("childless");
    }
} 