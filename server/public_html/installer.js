/**
 * Installer
 */

window.addEventListener("DOMContentLoaded", _ => {

    let installForm = document.getElementById('installer-form');
    const confirmDiv = document.createElement("div");
    const downloadDiv = document.createElement("div");

    installForm.addEventListener('submit', e => {
        e.preventDefault();

        let formData = new FormData(installForm);

        if (confirm("Are you sure you want to go ahead?")) {
            confirmDetails({
                os: formData.get('os'),
                orgName: formData.get('org-name'),
                deviceInstallType: formData.get('device_install_type'),
                mapArea: formData.get("map-area")
            });
        }
    });

    function confirmDetails(userSelection) {

        confirmDiv.setAttribute("style", "padding-top: 10px");
        const p = document.createElement("p");
        p.innerHTML = `Your chosen Operationg System: <b>${userSelection.os}</b><br>
             Your organisation's name: <b>${userSelection.orgName}</b><br>
             Install type: <b>${userSelection.deviceInstallType}</b>`;

        const downloadButton = document.createElement('button');
        downloadButton.setAttribute("class", "button");
        downloadButton.setAttribute("href", "#");
        downloadButton.setAttribute("type", "button");
        downloadButton.innerText = "Download";

        downloadButton.addEventListener("click", _ => {
            let postUrl = new URL("/installer/create", window.location.origin);
            postUrl.searchParams.set("os", userSelection.os);
            postUrl.searchParams.set("org", userSelection.orgName);
            postUrl.searchParams.set("deviceInstallType", userSelection.deviceInstallType);
            postUrl.searchParams.set("mapArea", userSelection.mapArea);

            fetch(postUrl, {
                method: "POST",
            }).then(
                res => res.json()
            ).then(res => presentDownload(res))

        });

        const editButton = document.createElement('button');
        editButton.setAttribute("class", "button");
        editButton.setAttribute("type", "button");
        editButton.textContent = "Edit Install";
        editButton.addEventListener("click", _ => {
            confirmDiv.replaceWith(installForm);
        })

        const buttonInputGroup = document.createElement('div');
        confirmDiv.append(p);
        const confP = document.createElement("p");
        confP.innerHTML
            = `If you are happy with the above settings, you can proceed with the installation. 
            Otherwise, you can go back and edit them.`;
        confirmDiv.append(confP);
        buttonInputGroup.setAttribute('class', 'input-group');
        buttonInputGroup.append(downloadButton);
        buttonInputGroup.append(editButton);
        confirmDiv.append(buttonInputGroup);
        installForm.replaceWith(confirmDiv);
    }

    function presentDownload(result) {
        downloadDiv.setAttribute("id", "download-form");
        downloadDiv.innerHTML = "hello";

        const backToHome = document.createElement("a");
        backToHome.text = "here";
        backToHome.href = "/";

        const backToHomeText = document.createElement("p");
        backToHomeText.innerHTML = `Click ${backToHome.outerHTML} to go back to the home page.`;

        const installInstructions = document.createElement("a");
        installInstructions.text = "here";
        installInstructions.href = "/"

        const downloadButton = document.createElement("a");
        downloadButton.text = "Click to download";
        downloadButton.setAttribute("class", "button");
        downloadButton.href = `/installer/download?id=${result.zip_hash}`;

        const downloadText = document.createElement("p");
        downloadText.innerHTML
            = `You can find install instructions ${installInstructions.outerHTML}.`;


        downloadDiv.appendChild(downloadText)
        downloadDiv.appendChild(downloadButton)
        downloadDiv.appendChild(backToHomeText);
        confirmDiv.replaceWith(downloadDiv);
    }
});


