function dropFunction(id) {
    document.getElementById(id).classList.toggle("show");
}

function filterFunction(inp, id) {
    var input, filter, ul, li, a, i;
    input = document.getElementById(inp);
    filter = input.value.toUpperCase();
    div = document.getElementById(id);
    a = div.getElementsByTagName("button");
    for (i = 0; i < a.length; i++) {
        txtValue = a[i].textContent || a[i].innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
}

function updateInput(inp, val) {
    inp = document.getElementById(inp);
    inp.value = val.value;
    val.classList.add("selActive");
    if (updateInput.old !== undefined)
        updateInput.old.classList.remove("selActive");
    updateInput.old = val;
    inp.dispatchEvent(new Event('input'));
}