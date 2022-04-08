window.onload = setupRefresh;

const blinkTimeMs = 2000;
let blinkOff = new Map([["SMALL", null], ["MEDIUM", null], ["LARGE", null]]);

function setupRefresh() {
    refreshElements();
    setInterval("refreshElements();", 500);
}

function refreshElements() {
    fetch(window.location.href + "/refresh")
        .then(response => {
            return response.json()
        })
        .then(jsonObj => {
            updateElements(jsonObj)
        })
}

function updateElements(jsonData) {
    document.getElementById("level-height").style["height"] = jsonData.fillLevel;
    document.getElementById("level-height").innerText = jsonData.fillLevel;
    document.getElementById("overflow").style["backgroundColor"] = jsonData.overflow ?
        getColor("warn") : getColor("off");
    document.getElementById("error").style["backgroundColor"] = jsonData.error ?
        getColor("warn") : getColor("off");
    document.getElementById("mount").style["backgroundColor"] = jsonData.mounted ?
        getColor("confirm") : getColor("off");
    document.getElementById("unmount").style["backgroundColor"] = jsonData.mounted ?
        getColor("off") : getColor("warn");
    document.getElementById("start").style["backgroundColor"] = jsonData.started ?
        getColor("confirm") : getColor("off");
    document.getElementById("stop").style["backgroundColor"] = jsonData.started ?
        getColor("off") : getColor("warn");
    //alert(jsonData.tapStatus.SMALL + " --- " + jsonData.tapStatus.MEDIUM + " --- " + jsonData.tapStatus.LARGE);
    if (jsonData.tapStatus.SMALL) {
        blinkOnce("SMALL", jsonData.tapStatus.SMALL)
    }
    if (jsonData.tapStatus.MEDIUM) {
        blinkOnce("MEDIUM", jsonData.tapStatus.MEDIUM)
    }
    if (jsonData.tapStatus.LARGE) {
        blinkOnce("LARGE", jsonData.tapStatus.LARGE)
    }
    if (jsonData.operatorAlert) {
        alert(jsonData.operatorAlert)
    }
    if (jsonData.customerAlert) {
        alert(jsonData.customerAlert)
    }
}

function getColor(mode) {
    switch (mode) {
        case "confirm":
            return "#23a177";
            break;
        case "warn":
            return "crimson";
            break;
        case "off":
            return "#e1e1e1";
            break;
        default:
            return "#e1e1e1"
    }
}

function blinkOnce(tapSize, mode) {
    clearTimeout(blinkOff.get(tapSize))
    if (mode === "SUCCESS") {
        document.getElementById(tapSize).style["backgroundColor"] = getColor("confirm")
    } else {
        document.getElementById(tapSize).style["backgroundColor"] = getColor("warn")
    }
    blinkOff.set(tapSize, setTimeout(function() {
        document.getElementById(tapSize).style["backgroundColor"] = getColor("off")},
        blinkTimeMs)
    )
}