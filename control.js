var iso;
var aperture;
var shutter;
var drive;

window.onload = function getParamTest() {
    const req = new XMLHttpRequest();
    req.open("GET", "./test");
    req.responseType = 'json';
    req.send();

    req.onload = function () {
        var params = req.response;
        console.log(params);
    }
}

function changeISO() {
    var temp = document.getElementById("iso").value;
    if (temp != iso) {
        iso = temp;
        const req = new XMLHttpRequest();
        req.open("POST", ".");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("iso=" + iso);
    }
}

function changeAperture() {
    var temp = document.getElementById("aperture").value;
    if (temp != aperture) {
        aperture = temp;
        const req = new XMLHttpRequest();
        req.open("POST", ".");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("aperture=" + aperture);
    }
}

function changeShutter() {
    var temp = document.getElementById("shutter").value;
    if (temp != shutter) {
        shutter = temp;
        const req = new XMLHttpRequest();
        req.open("POST", ".");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("shutter=" + shutter);
    }
}

function changeDrive() {
    var temp = document.getElementById("drive").value;
    if (temp != drive) {
        drive = temp;
        const req = new XMLHttpRequest();
        req.open("POST", ".");
        req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        req.send("drivemode=" + drive);
    }
}

function snap() {
    const req = new XMLHttpRequest();
    req.open("POST", ".");
    req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    req.send("snap=");
}