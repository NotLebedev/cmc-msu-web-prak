function flipValue(element, id, val0, val1) {
    if (element.getAttribute('value') === val1) {
        element.setAttribute('value', val0);
    } else {
        element.setAttribute('value', val1);
    }

    const input = document.getElementById(id);
    input.setAttribute('value',
        input.getAttribute('value') === 'true' ? 'false' : 'true');
}