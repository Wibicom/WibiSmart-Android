

var TemperatureGauge = (function () {
    var instance;

    function createInstance() {
        var opts = {
          lines: 12, // The number of lines to draw
          angle: 0.01, // The length of each line
          lineWidth: 0.20, // The line thickness
          pointer: {
            //length: 1, // The radius of the inner circle
            //strokeWidth: 0.030, // The rotation offset
            //color: '#000000' // Fill color
          },
           colorStart: '#f39c12',   // Colors
           colorStop: '#f39c12',
          strokeColor: '#E0E0E0',   //grey part to see which ones work best for you
          generateGradient: true
        };
        var target = document.getElementById('temperaturegauge'); // your canvas element
        var gauge = new Gauge(target).setOptions(opts); // create sexy gauge!
        gauge.maxValue = 45; // set max gauge value
        gauge.minValue = -30
        gauge.animationSpeed = 1; // set animation speed (32 is default value)
        gauge.set(0); // set actual value
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

function updateTemperature(temperature)
{
    document.getElementById("temperaturevalue").innerHTML = temperature + " C";
    TemperatureGauge.getInstance().set(temperature);
}

TemperatureGauge.getInstance();

updateTemperature(0);


