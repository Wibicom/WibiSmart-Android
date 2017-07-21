

function render_graph(name, data)
{
    var graphContainer = document.getElementById('graphContainer');
    graphContainer.style.width = "100%";
    graphContainer.style.height = "90%";
    graph = new Dygraph(document.getElementById('graphContainer') , data, {
        connectSeparatedPoints: true,
        interactionModel: {}
    });

    graphContainer.style.width = "100%";
    graphContainer.style.height = "90%";

    document.getElementById('sensorName').innerHTML = name;
}
