var iso;
var aperture;
var shutter;
var drive;

window.onload = function getParams() {
    const req = new XMLHttpRequest();
    req.open("GET", "./getValues");
    req.responseType = 'json';
    req.send();

    req.onload = function () {
        var params = req.response;

        // Populate each select               
        for (var i = 0; i < params.settingsList.iso.length; i++) {
            $('#iso').append($("<option>").text(params.settingsList.iso[i]).val(params.settingsList.iso[i]));
        }

        for (var i = 0; i < params.settingsList.fstop.length; i++) {
            $('#fstop').append($("<option>").text(params.settingsList.fstop[i]).val(params.settingsList.fstop[i]));
        }

        for (var i = 0; i < params.settingsList.shutter.length; i++) {
            $('#shutter').append($("<option>").text(params.settingsList.shutter[i]).val(params.settingsList.shutter[i]));
        }

        for (var i = 0; i < params.settingsList.mode.length; i++) {
            $('#mode').append($("<option>").text(params.settingsList.mode[i]).val(params.settingsList.mode[i]));
        }

        // Set each select to the current value
        $('#iso').val(params.currentSettings.iso);
        $('#fstop').val(params.currentSettings.fstop);
        $('#shutter').val(params.currentSettings.shutter);
        $('#mode').val(params.currentSettings.mode);
    }
}

function changeISO() {
    var temp = document.getElementById("iso").value;
    if (temp != iso) {
        iso = temp;
        const req = new XMLHttpRequest();
        req.open("POST", "./cameraControl");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("iso=" + iso);
    }
}

function changeAperture() {
    var temp = document.getElementById("aperture").value;
    if (temp != aperture) {
        aperture = temp;
        const req = new XMLHttpRequest();
        req.open("POST", "./cameraControl");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("aperture=" + aperture);
    }
}

function changeShutter() {
    var temp = document.getElementById("shutter").value;
    if (temp != shutter) {
        shutter = temp;
        const req = new XMLHttpRequest();
        req.open("POST", "./cameraControl");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("shutter=" + shutter);
    }
}

function changeMode() {
    var temp = document.getElementById("drive").value;
    if (temp != drive) {
        drive = temp;
        const req = new XMLHttpRequest();
        req.open("POST", "./cameraControl");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("drivemode=" + drive);
    }
}

function snap() {
    const req = new XMLHttpRequest();
    req.open("POST", "./cameraControl");
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("snap=");
}