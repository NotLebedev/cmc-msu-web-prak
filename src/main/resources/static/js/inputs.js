const inputs = document.getElementsByClassName('inputText1');

function getWidthOfInput(element, text) {
    let tmp = document.createElement("span");
    tmp.className = "inputText1 tmp-element";
    tmp.innerHTML = text;
    element.parentElement.appendChild(tmp);
    let theWidth = tmp.getBoundingClientRect().width;
    element.parentElement.removeChild(tmp);
    return theWidth;
}

function cleanText(text) {
    return text.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

function adjustWidthOfInput() {
    let textWidth = cleanText(this.value);
    let placeholderWidth = cleanText(this.placeholder);
    this.style.width = getWidthOfInput(this, textWidth > placeholderWidth ? textWidth : placeholderWidth) + "px";
}

for (let i = 0; i < inputs.length; i++) {
    inputs[i].addEventListener('input', adjustWidthOfInput);
    inputs[i].dispatchEvent(new Event('input'));
}