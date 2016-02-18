

function getSensorData(base, sensor, year, week) {
  var xhttp = new XMLHttpRequest();
  document.getElementById("retrieval-time").innerHTML = "querying " + base + " service..."
  xhttp.onreadystatechange = function() {
    if (xhttp.readyState == 4 && xhttp.status == 200) {
      var sensorData = JSON.parse(xhttp.responseText);
      document.getElementById("retrieval-time").innerHTML +=
        "retrieved " + sensorData.data.length + " sensor data in " + sensorData.timeSpent + "ms.";
      drawSensorChart(sensorData);
    }
  };
  xhttp.open("GET", "/" + base + "/sensor/weekly/data/" + sensor + "/" + year + "/" + week, true);
  xhttp.send();
}

function drawSensorChart(sensorData) {
    console.log(sensorData);
    var labels = [];
    var temperatureData = [];
    var hygrometryData = [];

    for(var i = 0 ; i < sensorData.data.length ; i++) {
        var at = sensorData.data[i].at;
        labels.push(at[0] + '/' + at[1] + '/' + at[2] + ' ' + at[3] + ':' + at[4]);
        temperatureData.push(sensorData.data[i].temperature);
        hygrometryData.push(sensorData.data[i].hygrometry);
    }

    var ctx = document.getElementById("sensor-chart").getContext("2d");

    var data = {
        labels: labels,
        datasets: [
            {
                label: "Temperature",
                fillColor: "rgba(220,220,220,0.2)",
                strokeColor: "rgba(220,220,220,1)",
                pointColor: "rgba(220,220,220,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(220,220,220,1)",
                data: temperatureData
            },
            {
                label: "Hygrometry",
                fillColor: "rgba(151,187,205,0.2)",
                strokeColor: "rgba(151,187,205,1)",
                pointColor: "rgba(151,187,205,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(151,187,205,1)",
                data: hygrometryData
            }
        ]
    };

    var sensorChart = new Chart(ctx).Line(data, {});
}