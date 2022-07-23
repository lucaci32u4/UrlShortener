$(document).ready(() => {
    $('.header').height($(window).height());
})
$(document).resize(() => {
    $('.header').height($(window).height());
})



function setupJs() {

    document.getElementById("alias-input").placeholder = window.location + "..."

    document.getElementById("switchAlias").onchange = ((e) => {
        document.getElementById("alias-input").hidden = !document.getElementById("switchAlias").checked;
    })

}

async function onClickSubmit() {
    let formin = document.getElementById('url-input');
    let track = document.getElementById("switchTracking")
    let inpage = document.getElementById("switchInpage")
    let url = formin.value;
    formin.value = "";

    let alias = (document.getElementById("switchAlias").checked ? document.getElementById("alias-input").value : null)

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
            alias: alias,
            tracking: track.checked,
            inpageRedirect: inpage.checked,
        })
    }).then(value => {
        if (value.status !== 200) {
            // todo: treat boatload of errors from spring ?!?!?!

        }
        value.text().then(res => {
            let formout = document.getElementById('url-output');
            formout.value = JSON.parse(res).completeUrl;
            formout.hidden = false;
            formout.focus();
            track.checked = false;
            inpage.checked = false;
        });
    }).catch(reason => {
        alert(reason); // other network errors
    });
}