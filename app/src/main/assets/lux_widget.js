function updateLux(val) {
    document.getElementById("luxvalue").innerHTML = val;
    var immage = document.getElementById("luximmage");
    immage.style.height = ((Math.round(Math.log10(val+10) * 40)-40)*2) + "px";
    immage.style.width = ((Math.round(Math.log10(val+10) * 40)-40)*2) + "px";
    immage.style.margin = -((Math.round(Math.log10(val+10) * 40)-40)) + "px";
}