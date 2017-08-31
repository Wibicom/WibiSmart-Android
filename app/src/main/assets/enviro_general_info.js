function set_rssi(rssi){
    document.getElementById("rssi-indicator").innerHTML = rssi + " dBm";
    var bars;
    if(rssi > -50) {
        bars = 5;
    }
    else if(rssi > -70) {
        bars = 4;
    }
    else if(rssi > -80) {
        bars = 3;
    }
    else if(rssi > -95) {
        bars = 2
    }
    else {
        bars = 1;
    }
    document.getElementById("rssiPic").setAttribute("src", "img/signal"+bars+".png");
}

function set_battery(battery){
    document.getElementById("batteryvalue").innerHTML = battery + " %";

    if (battery > 100) {
        document.getElementById("batteryLevel").style.width = "100%";
    }
    else {
        document.getElementById("batteryLevel").style.width = battery + "%";
    }

    if(battery <= 20) {
        document.getElementById("batteryLevel").style.background = "#e20d0d";
    }
    else if(battery <=40) {
        document.getElementById("batteryLevel").style.background = "#bc7036";
    }
    else if(battery <=50) {
        document.getElementById("batteryLevel").style.background = "#c9b60e";
    }
    else {
        document.getElementById("batteryLevel").style.background = "#61c613";
    }
}

function set_light(light) {
    document.getElementById("light_indicator_bottom").innerHTML = light + " mV";
    document.getElementById("sun").style.filter = "brightness(" + (Math.round(light/4) + 30) + "%)";
}

function set_sensor_name(name){
    document.getElementById("sensor-name-tag").innerHTML = name;
}