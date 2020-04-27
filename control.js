var iso;
var aperture;
var shutter;
var drive;

window.onload = function getParams() {
    const req = new XMLHttpRequest();
    req.open("GET", "./getSettings");
    req.responseType = 'json';
    req.send();

    req.onload = function () {
        var params = req.response;

        console.log(params.iso);

        // Populate each select               
        for(var i = 0; i < params.iso.length; i++){
            $('#iso').append($("<option>").text(params.iso[i]).val(params.iso[i]));
        } 
        
        for(var i = 0; i < params.fstop.length; i++){
            $('#fstop').append($("<option>").text(params.fstop[i]).val(params.fstop[i]));
        }  

        for(var i = 0; i < params.shutter.length; i++){
            $('#shutter').append($("<option>").text(params.shutter[i]).val(params.shutter[i]));
        }  

        for(var i = 0; i < params.mode.length; i++){
            $('#mode').append($("<option>").text(params.mode[i]).val(params.mode[i]));
        }  
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