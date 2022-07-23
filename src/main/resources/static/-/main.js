
$(document).ready(() => {
    $('.header').height($(window).height());
})

$(document).resize(() => {
    $('.header').height($(window).height());
})

async function onClickSubmit() {
    let formin = document.getElementById('url-input');
    let url = formin.value;
    formin.value = "";

    if (!url || url.length === 0 || /^\s*$/.test(url)) {
        formin.focus();
        return;
    }

    await fetch("/-/create", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            url: url,
            alias: "alias",
            tracking: false,
            inpageRedirect: false,
        })
    }).then(value => {
        if (value.status !== 200) {
            // todo: treat kilometers of errors from spring?!?!?!
        }
        value.text().then(res => {
            let formout = document.getElementById('url-output');
            formout.value = JSON.parse(res).completeUrl;
            formout.hidden = false;
            formout.focus();
        });
    }).catch(reason => {
        alert(reason); // other network errors
    });
}