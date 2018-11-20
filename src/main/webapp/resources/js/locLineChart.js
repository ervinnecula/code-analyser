var parseTime = d3.timeParse("%Y-%m-%d");
// set the ranges
var margin = {top: 50, right: 20, bottom: 70, left: 50},
    width = 1450 - margin.left - margin.right,
    height = 770 - margin.top - margin.bottom;

var x = d3.scaleTime().range([0, width]);
var y = d3.scaleLinear().range([height, 0]);

var valuesLinesAdded = d3.line()
    .x(function(d) { return x(d.date); })
    .y(function(d) { return y(d.linesAdded); });

var areaLinesAdded = d3.area()
    .x(function(d) { return x(d.date); })
    .y0(height)
    .y1(function(d) { return y(d.linesAdded); });

var valuesLinesRemoved = d3.line()
    .x(function(d) { return x(d.date); })
    .y(function(d) { return y(d.linesRemoved); });

var areaLinesRemoved = d3.area()
    .x(function(d) { return x(d.date); })
    .y0(height)
    .y1(function(d) { return y(d.linesRemoved); });

// moves the 'group' element to the top left margin
var locLineSvg = d3.select("#locLineChart")
    .append("g")
    .attr("transform", "translate(" + 80 + "," + 40 + ")");

// Get the data
function loadLocLineChart(csv) {
    var data = [];

    var rows = csv.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        data.push({
            date: elements[0],
            linesAdded: elements[1],
            linesRemoved: elements[2]
        });
    }

    data.forEach(function(d) {
        d.date = parseTime(d.date);
        d.linesAdded = +d.linesAdded;
        d.linesRemoved = +d.linesRemoved;
    });

    x.domain(d3.extent(data, function(d) { return d.date; }));
    y.domain([0, d3.max(data, function(d) {return Math.max(d.linesAdded, d.linesRemoved); })]);

    // add the lines added blue line
    locLineSvg.append("path")
        .data([data])
        .attr("class", "lineLinesAdded")
        .attr("d", valuesLinesAdded);

    // add the area for lines added
    locLineSvg.append("path")
        .data([data])
        .attr("class", "areaLinesAdded")
        .attr("d", areaLinesAdded);

    locLineSvg.append("path")
        .data([data])
        .attr("class", "lineLinesRemoved")
        .attr("d", valuesLinesRemoved);

    // add the area for lines added
    locLineSvg.append("path")
        .data([data])
        .attr("class", "areaLinesRemoved")
        .attr("d", areaLinesRemoved);

    // Add the X Axis
    locLineSvg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + 650 + ")")
        .call(d3.axisBottom(x).tickFormat(d3.timeFormat("%Y-%m-%d")))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-65)");

    // Add the Y Axis
    locLineSvg.append("g")
        .attr("class", "axis")
        .call(d3.axisLeft(y));

    var tooltip = d3.select("#tooltip");

    function getFormattedDate(date) {
        var dd = date.getDate();
        var mm = date.getMonth() + 1;
        var yyyy = date.getFullYear();
        if (dd < 10) {
            dd = '0' + dd
        }
        if (mm < 10) {
            mm = '0' + mm
        }

        return yyyy + '-' + mm + '-' + dd;
    }

    // add the dots with tooltips
    locLineSvg.append("g")
        .selectAll("dot1")
        .data(data)
        .enter().append("circle")
        .attr("r", 5)
        .attr("cx", function(d) { return x(d.date); })
        .attr("cy", function(d) { return y(d.linesAdded); })
        .on("mouseover", function(d) {
            tooltip.transition()
                .duration(200)
                .style("opacity", .9)
                .style("background", "#b0c4debd");
            tooltip.html(getFormattedDate(d.date) + "<br/>" + d.linesAdded)
                .style("left", (d3.event.pageX - 300))
                .style("top", (d3.event.pageY - 100));
        })
        .on("mouseout", function(d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });

    locLineSvg.append("g")
        .selectAll("dot2")
        .data(data)
        .enter().append("circle")
        .attr("r", 5)
        .attr("cx", function(d) { return x(d.date); })
        .attr("cy", function(d) { return y(d.linesRemoved); })
        .on("mouseover", function(d) {
            tooltip.transition()
                .duration(200)
                .style("opacity", .9)
                .style("background", "#ff8086");
            tooltip.html(getFormattedDate(d.date) + "<br/>" + d.linesRemoved)
                .style("left", (d3.event.pageX - 300))
                .style("top", (d3.event.pageY - 100));
        })
        .on("mouseout", function(d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });

}