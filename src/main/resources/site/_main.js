
$(document).ready(() => {
    $('.header').height($(window).height());
})

$(document).resize(() => {
    $('.header').height($(window).height());
})

async function onClickSubmit() {
    let formin = document.getElementById('formin');
    let url = formin.value;
    formin.value = "";

    if (!url || url.length === 0 || /^\s*$/.test(url)) {
        return;
    }

    await fetch("", {
        method: "POST",
        body: url
    }).then(value => {
        if (value.status !== 200) {
            console.log("Server refused URL with code " + value.status)
            if (value.status === 429) {
                alert("Too many requests! Please wait a little bit.")
                return;
            }
            if (value.status === 403) {
                alert("Only HTTP and HTTPS protocols are supported")
                return;
            }
            alert("The URL you entered looks invalid.")
            return;
        }
        value.text().then(res => {
            let formout = document.getElementById('formout');
            formout.value = res;
            formout.hidden = false;
            formout.focus();
        });
    }).catch(reason => {
        alert(reason);
    });
}