/**
 * Installer
 */

/**
 * Wait for the DOM to load
 */
window.addEventListener("DOMContentLoaded", _ => {
    const container = document.getElementById('install-page');
    const installForm = document.getElementById('installer-form');
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
                mapArea: formData.get("map_area")
            });
        }
    });

    /**
     * Confirm with the user if everything is correct
     *
     * @param userSelection
     */
    function confirmDetails(userSelection) {

        confirmDiv.setAttribute("style", "padding-top: 10px");
        const p = document.createElement("p");
        p.innerHTML = `<div class="card"> Your chosen Operationg System: <b>${userSelection.os}</b><br>
             Your organisation's name: <b>${userSelection.orgName}</b><br>
             Install type: <b>${userSelection.deviceInstallType}</b><br>
             Map area: <b>${userSelection.mapArea}</b></div>`;

        const downloadButton = document.createElement('button');
        downloadButton.setAttribute("class", "button");
        downloadButton.setAttribute("type", "button");
        downloadButton.innerText = "Download";

        downloadButton.addEventListener("click", _ => {
            let postUrl = new URL("/installer/create", window.location.origin);
            postUrl.searchParams.set("os", userSelection.os);
            postUrl.searchParams.set("org", userSelection.orgName);
            postUrl.searchParams.set("deviceInstallType", userSelection.deviceInstallType);
            postUrl.searchParams.set("mapArea", userSelection.mapArea);

            confirmDiv.innerHTML = "<div class='card' style='text-align: center; min-height: 400px; justify-content: center;'>" +
                "<h2>Working on it</h2>" +
                "<p>Your download link will appear here when it's ready. Please be patient whilst we get it ready for you.</p><br><br>" +
                "<p>Please <b>do not</b> refresh the page.</p>" +
                "</div>";

            fetch(postUrl, {
                method: "POST",
            }).then(
                res => res.json()
            ).then(res => presentDownload(res));
        });

        const editButton = document.createElement('button');
        editButton.setAttribute("class", "button");
        editButton.setAttribute("type", "button");
        editButton.textContent = "Edit Install";
        editButton.addEventListener("click", _ => {
            container.removeChild(confirmDiv);
            container.appendChild(installForm)
        });

        const buttonInputGroup = document.createElement('div');

        const confP = document.createElement("p");
        confP.innerHTML
            = `If you are happy with the above settings, you can proceed with the installation. 
            Otherwise, you can go back and edit them.`;

        if (confirmDiv.childElementCount === 0) {
            confirmDiv.append(p);
            confirmDiv.append(confP);
            buttonInputGroup.setAttribute('class', 'input-group');
            buttonInputGroup.append(downloadButton);
            buttonInputGroup.append(editButton);
            confirmDiv.append(buttonInputGroup);
        }
        container.removeChild(installForm);
        container.appendChild(confirmDiv);
    }

    /**
     * Present the download
     *
     * @param result
     */
    function presentDownload(result) {
        downloadDiv.setAttribute("id", "download-form");
        downloadDiv.setAttribute("class", "card");
        downloadDiv.innerHTML = "<b>Your download is ready.</b>"

        const backToHome = document.createElement("a");
        backToHome.text = "here";
        backToHome.href = "/";

        const backToHomeText = document.createElement("p");
        backToHomeText.innerHTML = `Click ${backToHome.outerHTML} to go back to the home page.`;

        const installInstructions = document.createElement("a");
        installInstructions.text = "here";
        installInstructions.href = "/";

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
