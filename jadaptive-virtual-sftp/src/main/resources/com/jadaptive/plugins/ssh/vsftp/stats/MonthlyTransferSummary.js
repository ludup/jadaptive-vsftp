am5.ready(function() {

// Create root element
// https://www.amcharts.com/docs/v5/getting-started/#Root_element
var root = am5.Root.new("chartdiv");


// Set themes
// https://www.amcharts.com/docs/v5/concepts/themes/
root.setThemes([
  am5themes_Animated.new(root)
]);


// Create chart
// https://www.amcharts.com/docs/v5/charts/xy-chart/
var chart = root.container.children.push(am5xy.XYChart.new(root, {
  panX: false,
  panY: false,
  wheelX: "panY",
  wheelY: "zoomY",
  layout: root.verticalLayout
}));

// Create axes
// https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
var yAxis = chart.yAxes.push(am5xy.CategoryAxis.new(root, {
  categoryField: "direction",
  renderer: am5xy.AxisRendererY.new(root, {}),
  tooltip: am5.Tooltip.new(root, {})
}));



var xAxis = chart.xAxes.push(am5xy.ValueAxis.new(root, {
  min: 0,
  numberFormat: "# 'GB'",
  renderer: am5xy.AxisRendererX.new(root, {})
}));


// Add legend
// https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
var legend = chart.children.push(am5.Legend.new(root, {
  centerX: am5.p50,
  x: am5.p50
}));


// Add series
// https://www.amcharts.com/docs/v5/charts/xy-chart/series/
function makeSeries(name, fieldName, data) {
  var series = chart.series.push(am5xy.ColumnSeries.new(root, {
    name: name,
    stacked: true,
    xAxis: xAxis,
    yAxis: yAxis,
    baseAxis: yAxis,
    valueXField: fieldName,
    categoryYField: "direction"
  }));

  series.columns.template.setAll({
    tooltipText: "{name} {valueX}GB",
    tooltipY: am5.percent(90)
  });
  series.data.setAll(data);

  // Make stuff animate on load
  // https://www.amcharts.com/docs/v5/concepts/animations/
  series.appear();

 

  legend.data.push(series);
}



am5.net.load("/app/vfs/stats/monthly").then(function(result) {
  // This gets executed when data finishes loading
  var data  = am5.JSONParser.parse(result.response);
  
  yAxis.data.setAll(data);
  
  makeSeries("SCP", "scp", data);
  makeSeries("SFTP", "sftp", data);
  makeSeries("HTTPS", "https", data);

  // Make stuff animate on load
  // https://www.amcharts.com/docs/v5/concepts/animations/
  chart.appear(1000, 100);

}).catch(function(result) {
  // This gets executed if there was an error loading URL
  // ... handle error
  console.log("Error loading " + result.xhr.responseURL);
});



}); // end am5.ready()