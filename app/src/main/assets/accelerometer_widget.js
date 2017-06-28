

function render_progressbar_accelerometer(live_acc, axis){
    var firstSegment_progress;
    var secondSegment_progress;
    var thirdSegment_progress;
    if (live_acc>0) {
        firstSegment_progress = "50%";
        secondSegment_progress = (((live_acc/1500) * 100)/2).toString() + "%";
        thirdSegment_progress = (50 - (((live_acc/1500)*100))/2).toString + "%";


    } else if (live_acc<0) {

        firstSegment_progress = (50 - (((Math.abs(live_acc)/1500)*100)/2)).toString() + "%";
        secondSegment_progress = (((Math.abs(live_acc)/1500)*100)/2).toString() + "%";
        thirdSegment_progress = "50%";

    } else {
        firstSegment_progress = "100%";
        secondSegment_progress = "0%";
        thirdSegment_progress = "0%";
    }

    switch(axis){
        case 'x':
            document.getElementById("accxvalue").innerHTML = " X Axis: " + live_acc + " mg";
            document.getElementById("firstSegment_progress_x").setAttribute("style", "width: " + firstSegment_progress);
            document.getElementById("secondSegment_progress_x").setAttribute("style", "width: " + secondSegment_progress);
            document.getElementById("thirdSegment_progress_x").setAttribute("style", "width: " + thirdSegment_progress);
            break;
        case 'y':
            document.getElementById("accyvalue").innerHTML = " Y Axis: " + live_acc + " mg";
            document.getElementById("firstSegment_progress_y").setAttribute("style", "width: " + firstSegment_progress);
            document.getElementById("secondSegment_progress_y").setAttribute("style", "width: " + secondSegment_progress);
            document.getElementById("thirdSegment_progress_y").setAttribute("style", "width: " + thirdSegment_progress);
            break;
        case 'z':
            document.getElementById("acczvalue").innerHTML = " Z Axis: " + live_acc + " mg";
            document.getElementById("firstSegment_progress_z").setAttribute("style", "width: " + firstSegment_progress);
            document.getElementById("secondSegment_progress_z").setAttribute("style", "width: " + secondSegment_progress);
            document.getElementById("thirdSegment_progress_z").setAttribute("style", "width: " + thirdSegment_progress);
            break;
    }

}

function render_total_acceleration (accx, accy, accz){
    var total_acceleration = (Math.sqrt(Math.pow(accx,2)+ Math.pow(accy,2)+ Math.pow(accz,2))).toFixed(2);
     document.getElementById('totalaccelerationvalue').innerHTML = total_acceleration + " mg";

}

function set_accelerometer_data(accx, accy, accz){
    render_progressbar_accelerometer(accx, 'x');
    render_progressbar_accelerometer(accy, 'y');
    render_progressbar_accelerometer(accz, 'z');
    render_total_acceleration(accx, accy, accz);
}

