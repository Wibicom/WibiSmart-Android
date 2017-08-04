

var PressureGauge = (function () {
    var instance;

    function createInstance() {
        var opts = {
          lines: 12, // The number of lines to draw
          angle: 0.01, // The length of each line
          lineWidth: 0.40, // The line thickness
          pointer: {
            // length: 1, // The radius of the inner circle
            // strokeWidth: 0.030, // The rotation offset
            // color: '#000000' // Fill color
          },
          colorStart: '#01579B',   // Colors
          colorStop: '#01579B',    // just experiment with them
          strokeColor: '#E0E0E0',   // to see which ones work best for you
          generateGradient: true
        };
        var target = document.getElementById('pressuregauge'); // your canvas element
        var gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
        gauge.maxValue = 1500; // set max gauge value
        gauge.animationSpeed = 1; // set animation speed (32 is default value)
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


function updatePressure(pressure)
{
    document.getElementById("pressurevalue").innerHTML = pressure + " mbar";
    PressureGauge.getInstance().set(pressure);
}


PressureGauge.getInstance();

updatePressure(0);

