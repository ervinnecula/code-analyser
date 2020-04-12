var widthAddDelete = $("#add-delete").width() * 0.90;
var heightAddDelete = $("#add-delete").height() * 1.20;

var addRemoveLineChart = d3.select("#add-delete")
    .append("svg")
    .attr("height", "100%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid meet")
    .attr("class", "pt-5");

var parseTime = d3.timeParse("%Y-%m-%d");


function loadAddRemoveLineChart(csv, startDate, endDate) {

    var data = [];

    var x = d3.scaleTime().range([0, widthAddDelete]);
    var y = d3.scaleLinear().range([heightAddDelete, 0]);

    var valuesLinesAdded = d3.line()
        .x(function (d) {
            return x(d.date);
        })
        .y(function (d) {
            return y(d.linesAdded);
        });

    var areaLinesAdded = d3.area()
        .x(function (d) {
            return x(d.date);
        })
        .y0(heightAddDelete)
        .y1(function (d) {
            return y(d.linesAdded);
        });

    var valuesLinesRemoved = d3.line()
        .x(function (d) {
            return x(d.date);
        })
        .y(function (d) {
            return y(d.linesRemoved);
        });

    var areaLinesRemoved = d3.area()
        .x(function (d) {
            return x(d.date);
        })
        .y0(heightAddDelete)
        .y1(function (d) {
            return y(d.linesRemoved);
        });

    var startDateObj = startDate;
    var endDateObj = endDate;

    if (startDate instanceof Date === false && endDate instanceof Date === false) {
        var startDateSplit = startDate.split("-");
        var endDateSplit = endDate.split("-");
        startDateObj = new Date(startDateSplit[2], Number(startDateSplit[1]) - 1, startDateSplit[0], 0);
        endDateObj = new Date(endDateSplit[2], Number(endDateSplit[1]) - 1, endDateSplit[0], 0);
    }
    addRemoveLineChart.selectAll("*").remove();

    var rows = csv.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        var splitDate = elements[0].split("-");

        var currentDateObj = new Date(Number(splitDate[0]), splitDate[1] - 1, Number(splitDate[2]));
        if (startDateObj <= currentDateObj && currentDateObj <= endDateObj) {
            data.push({
                date: elements[0],
                linesAdded: elements[1],
                linesRemoved: elements[2]
            });
        }
    }

    data.forEach(function (d) {
        d.date = parseTime(d.date);
        d.linesAdded = +d.linesAdded;
        d.linesRemoved = +d.linesRemoved;
    });

    x.domain(d3.extent(data, function (d) {
        return d.date;
    }));
    y.domain([0, d3.max(data, function (d) {
        return Math.max(d.linesAdded, d.linesRemoved);
    })]);

    // add the lines added blue line
    addRemoveLineChart.append("path")
        .data([data])
        .attr("class", "lineLinesAdded")
        .attr("d", valuesLinesAdded);

    // add the area for lines added
    addRemoveLineChart.append("path")
        .data([data])
        .attr("class", "areaLinesAdded")
        .attr("d", areaLinesAdded);

    addRemoveLineChart.append("path")
        .data([data])
        .attr("class", "lineLinesRemoved")
        .attr("d", valuesLinesRemoved);

    // add the area for lines added
    addRemoveLineChart.append("path")
        .data([data])
        .attr("class", "areaLinesRemoved")
        .attr("d", areaLinesRemoved);

    // Add the X Axis
    addRemoveLineChart.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + heightAddDelete + ")")
        .call(d3.axisBottom(x).tickFormat(d3.timeFormat("%Y-%m-%d")))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-65)");

    // Add the Y Axis
    addRemoveLineChart.append("g")
        .attr("class", "axis")
        .call(d3.axisLeft(y));

    var tooltip = d3.select("#tooltip-add-remove");

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
    addRemoveLineChart.append("g")
        .selectAll("dot1")
        .data(data)
        .enter().append("circle")
        .attr("r", 5)
        .attr("cx", function (d) {
            return x(d.date);
        })
        .attr("cy", function (d) {
            return y(d.linesAdded);
        })
        .on("mouseover", function (d) {
            tooltip.transition()
                .duration(200)
                .style("opacity", .9)
                .style("background", "#b0c4debd");
            tooltip.html(getFormattedDate(d.date) + "<br/>" + d.linesAdded)
                .style("left", (d3.event.pageX - 300))
                .style("top", (d3.event.pageY - 100));
        })
        .on("mouseout", function (d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });

    addRemoveLineChart.append("g")
        .selectAll("dot2")
        .data(data)
        .enter().append("circle")
        .attr("r", 5)
        .attr("cx", function (d) {
            return x(d.date);
        })
        .attr("cy", function (d) {
            return y(d.linesRemoved);
        })
        .on("mouseover", function (d) {
            tooltip.transition()
                .duration(200)
                .style("opacity", .9)
                .style("background", "#ff8086");
            tooltip.html(getFormattedDate(d.date) + "<br/>" + d.linesRemoved)
                .style("left", (d3.event.pageX - 300))
                .style("top", (d3.event.pageY - 100));
        })
        .on("mouseout", function (d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });

}