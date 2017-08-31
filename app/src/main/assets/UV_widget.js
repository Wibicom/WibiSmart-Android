

var UVGauge = (function () {
    var instance;

    function createInstance() {
        var opts = {
          angle: -0.20, // The length of each line
          lineWidth: 0.20, // The line thickness
          pointer: {
             length: 0.6, // The radius of the inner circle
            // strokeWidth: 0.030, // The rotation offset
            // color: '#000000' // Fill color
          },
          staticZones: [ 
            {strokeStyle: "#8CD600", min: 0, max: 2.5}, 
            {strokeStyle: "#F9E814", min: 2.5, max: 5.5}, 
            {strokeStyle: "#F77F00", min: 5.5, max: 7.5}, 
            {strokeStyle: "#EF2B2D", min: 7.5, max: 10.5},
            {strokeStyle: "#9663C4", min: 10.5, max: 13}  
        ],
        limitMax: true,
        limitMin: true,
        highDPiSupport:true,
          strokeColor: '#E0E0E0'
        };
        var target = document.getElementById('UVgauge'); // your canvas element
        var gauge = new Gauge(target) // create sexy gauge!
        gauge.minValue = 0;
        gauge.maxValue = 13; // set max gauge value
        gauge.animationSpeed = 5; // set animation speed (32 is default value)
        gauge.setOptions(opts);
        gauge.set(5); // set actual value
        return gauge;
        }
    return {
        getInstance: function () {
            if (!instance) {
                instance = createInstance();
            }
            return instance;
        }
    };
})();


function updateUV(UV)
{
    document.getElementById("UVvalue").innerHTML = UV;
    UVGauge.getInstance().set(UV);
}


UVGauge.getInstance();

updateUV(0);

function updateUVLabel(val) {
    var label = document.getElementById('UVlabel');
    var info = document.getElementById('info');
    if(val < 3) {
        label.innerHTML = "LOW";
        label.style.color = "#8CD600";
    }
    else if(val < 6) {
        label.innerHTML = "MODERATE";
        label.style.color = "#F9E814";
    }
    else if(val < 8) {
        label.innerHTML = "HIGH";
        label.style.color = "#F77F00";
    }
    else if(val < 11) {
        label.innerHTML = "VERY HIGH";
        label.style.color = "#EF2B2D";
    }
    else {
        label.innerHTML = "EXTREME";
        label.style.color = "#9663C4";
    }
}

