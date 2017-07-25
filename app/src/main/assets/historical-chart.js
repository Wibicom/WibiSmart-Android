

function render_graph(name, data)
{
    var graphContainer = document.getElementById('graphContainer');
    graphContainer.style.width = "100%";
    graphContainer.style.height = "90%";
    if(name.indexOf("Acceleration") > -1) {
        graph = new Dygraph(document.getElementById('graphContainer') , data, {
            connectSeparatedPoints: true,
            interactionModel: {}
        });
    }
    else if (name.indexOf("%") > -1) {
        graph = new Dygraph(document.getElementById('graphContainer') , data, {
            connectSeparatedPoints: true,
            interactionModel: {},
            fillGraph: true,
            valueRange: [0, 105]
        });
    }
    else if (name.indexOf("RSSI") > -1) {
        graph = new Dygraph(document.getElementById('graphContainer') , data, {
            connectSeparatedPoints: true,
            interactionModel: {},
            valueRange: [-115, -30]
        });
    }
    else {
        graph = new Dygraph(document.getElementById('graphContainer') , data, {
            connectSeparatedPoints: true,
            interactionModel: {},
            fillGraph: true
        });
    }

    graphContainer.style.width = "100%";
    graphContainer.style.height = "90%";

    document.getElementById('sensorName').innerHTML = name;
}
