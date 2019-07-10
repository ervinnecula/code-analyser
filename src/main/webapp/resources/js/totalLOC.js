var widthLoc = $("#loc").width() * 0.90;
var heightLoc = $("#loc").height() * 1.25;

var locChart = d3.select("#loc")
    .append("svg")
    .attr("height", "100%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid meet")
    .attr("class", "pt-5");

var parseTime = d3.timeParse("%Y-%m-%d");

function loadLocChart(csv, startDate, endDate) {

    var data = [];

    var x = d3.scaleTime().range([0, widthLoc]);
    var y = d3.scaleLinear().range([heightLoc, 0]);

    var areaLoc = d3.area()
        .x(function(d) { return x(d.date); })
        .y0(heightLoc)
        .y1(function(d) { return y(d.loc); });

    var startDateObj = startDate;
    var endDateObj = endDate;

    if (startDate instanceof Date === false && endDate instanceof Date === false) {
        var startDateSplit = startDate.split("-");
        var endDateSplit = endDate.split("-");
        startDateObj = new Date(startDateSplit[2], Number(startDateSplit[1]) - 1, startDateSplit[0], 0);
        endDateObj = new Date(endDateSplit[2], Number(endDateSplit[1]) - 1, endDateSplit[0], 0);
    }

    locChart.selectAll("*").remove();

    var rows = csv.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        var splitDate = elements[0].split("-");

        var currentDateObj = new Date(Number(splitDate[0]), splitDate[1] - 1, Number(splitDate[2]));

        if (startDateObj <= currentDateObj && currentDateObj <= endDateObj) {
            data.push({
                date: elements[0],
                loc: elements[1]
            });
        }
    }

    data.forEach(function(d) {
        d.date = parseTime(d.date);
        d.loc = +d.loc;
    });

    x.domain(d3.extent(data, function(d) { return d.date; }));
    y.domain([0, d3.max(data, function(d) {return d.loc; })]);

    // add the area for lines added
    locChart.append("path")
        .data([data])
        .attr("class", "areaLoc")
        .attr("d", areaLoc);

    // Add the X Axis
    locChart.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(0," + heightLoc + ")")
        .call(d3.axisBottom(x).tickFormat(d3.timeFormat("%Y-%m-%d")))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-65)");

    // Add the Y Axis
    locChart.append("g")
        .attr("class", "axis")
        .call(d3.axisLeft(y));

    var tooltip = d3.select("#tooltip-loc");

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
    locChart.append("g")
        .selectAll("dot1")
        .data(data)
        .enter().append("circle")
        .attr("r", 5)
        .attr("cx", function(d) { return x(d.date); })
        .attr("cy", function(d) { return y(d.loc); })
        .on("mouseover", function(d) {
            tooltip.transition()
                .duration(200)
                .style("opacity", .9)
                .style("background", "#7ac368");
            tooltip.html(getFormattedDate(d.date) + "<br/>" + d.loc)
                .style("left", (d3.event.pageX - 300))
                .style("top", (d3.event.pageY - 100));
        })
        .on("mouseout", function(d) {
            tooltip.transition()
                .duration(500)
                .style("opacity", 0);
        });

}