/**
 * Installer
 */

window.addEventListener("DOMContentLoaded", _ => {

    let installForm = document.getElementById('installer-form');

    installForm.addEventListener('submit', e => {
        e.preventDefault();

        let formData = new FormData(installForm);
        console.log(formData.get('os-choice'));

        if (confirm("Are you sure you want to go ahead?")) {
            showDownloadPage({
                os: formData.get('os'),
                orgName: formData.get('org-name'),
                orgEmail: formData.get('org-contact'),
                deviceInstallType: formData.get('device_install_type')
            });
        }

    });

    function showDownloadPage(userSelection) {

        const downloadDiv = document.createElement("div");
        downloadDiv.setAttribute("style", "padding-top: 10px");
        const p = document.createElement("p");
        p.innerHTML = `Your chosen Operationg System: <b>${userSelection.os}</b><br>
             Your organisation's name: <b>${userSelection.orgName}</b><br>
             Your organisation's contact email: <b>${userSelection.orgEmail}</b><br/>
             Install type: <b>${userSelection.deviceInstallType}</b>`;

        const downloadButton = document.createElement('a');
        downloadButton.setAttribute("class", "button");
        downloadButton.setAttribute("href", "#");
        downloadButton.setAttribute("type", "button");
        downloadButton.text = "Download";

        const editButton = document.createElement('a');
        editButton.setAttribute("class", "button");
        editButton.setAttribute("href", "#");
        editButton.setAttribute("type", "button");
        editButton.text = "Edit Install";

        const buttonInputGroup = document.createElement('div');
        downloadDiv.append(p);

        buttonInputGroup.setAttribute('class', 'input-group');
        buttonInputGroup.append(downloadButton);
        buttonInputGroup.append(editButton);
        downloadDiv.append(buttonInputGroup);
        installForm.replaceWith(downloadDiv);
    }
});


