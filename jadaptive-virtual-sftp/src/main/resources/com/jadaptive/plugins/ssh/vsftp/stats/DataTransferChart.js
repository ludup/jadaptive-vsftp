am5.ready(function() {

// Create transferChart element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var transferChart = am5.Root.new("chart1");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
transferChart.setThemes([
  am5themes_Animated.new(transferChart)
]);

am5.addLicense("AM5C387765625");

// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart1 = transferChart.container.children.push(am5xy.XYChart.new(transferChart, {
  panX: false,
  panY: false,
  wheelX: "panX",
  wheelY: "zoomX"
}));


// Add cursor
// https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
var cursor1 = chart1.set("cursor", am5xy.XYCursor.new(transferChart, {
  behavior: "zoomX"
}));
cursor1.lineY.set("visible", false);

var date1 = new Date();
date1.setHours(0, 0, 0, 0);
var value = 100;

// Add series
// https://www.amcharts.com/docs/v5/charts/xy-chart/series/
var series1 = chart1.series.push(am5xy.ColumnSeries.new(transferChart, {
  name: "Series",
  xAxis: chart1.xAxes.push(am5xy.DateAxis.new(transferChart, {
		  maxDeviation: 0,
		  baseInterval: {
		    timeUnit: "day",
		    count: 1
		  },
		  renderer: am5xy.AxisRendererX.new(transferChart, {}),
		  tooltip: am5.Tooltip.new(transferChart, {})
		})),
  yAxis: chart1.yAxes.push(am5xy.ValueAxis.new(transferChart, {
		  renderer: am5xy.AxisRendererY.new(transferChart, {})
		})),
  valueYField: "value",
  valueXField: "date",
  tooltip: am5.Tooltip.new(transferChart, {
    labelText: "{valueY}"
  })
}));

am5.net.load("/app/vfs/stats/throughput").then(function(result) {
  // This gets executed when data finishes loading
  var data  = am5.JSONParser.parse(result.response);
  
  series1.data.setAll(data);
  series1.appear(1000);
  chart1.appear(1000, 100);

}).catch(function(result) {
  console.log("Error loading " + result.xhr.responseURL);
});



}); // end am5.ready()