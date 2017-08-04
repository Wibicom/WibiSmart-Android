function updateTemperature(temperature) {
    document.getElementById("temperaturevalue").innerHTML = temperature + "°C";
    document.getElementById("liquidLevel").style.height = (temperature/2 + 50) + "%";
    //document.getElementById("liquidLevel").style.background = "rgb(239," + Math.round(50+(50-temperature)*1.875) + ", 26)";
    
}