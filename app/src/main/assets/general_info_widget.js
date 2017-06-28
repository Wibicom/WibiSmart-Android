


function set_rssi(rssi){
    document.getElementById("rssi-indicator").innerHTML = rssi + " dBm";
}

function set_battery(battery){
    document.getElementById("batteryvalue").innerHTML = battery + " %";
}

function set_light(light){
    document.getElementById("light_indicator_bottom").innerHTML = light + " mV";
}

