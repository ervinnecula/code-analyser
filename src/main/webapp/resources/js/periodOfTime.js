var periodOfTimeSvg = d3.select("#periodoftime")
    .append("svg")
    .attr("height", "90%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid");

var widthPeriodOfTime = widthLoc;
var heightPeriodOfTime = heightLoc;

var colorPeriodOfTime = ['#4afe00', '#a4e897', '#626a60', '#322f2f'];

function periodOfTime(data) {
    var array = [];
    var max = -1;
    periodOfTimeSvg.selectAll("*").remove();

    var rows = data.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");

        if (getNumericPeriod(elements[1]) > max) {
            max = getNumericPeriod(elements[1]);
        }
        array.push({
            path: elements[0],
            period: getNumericPeriod(elements[1]),
            periodInString: elements[1]
        });

    }

    // Color palette for continents?
    var color = d3.scaleOrdinal()
        .domain(["RECENT", "MEDIUM", "OLD", "VERY_OLD"])
        .range(colorPeriodOfTime);

    // Size scale for countries
    var size = d3.scaleLinear()
        .domain([0, max])
        .range([5,20]);

    // create a tooltip
    var Tooltip = d3.select("#periodoftime")
        .append("div")
        .style("opacity", 0)
        .attr("class", "tooltip")
        .style("background-color", "white")
        .style("border", "solid")
        .style("border-width", "2px")
        .style("border-radius", "5px")
        .style("padding", "5px");

    // Three function that change the tooltip when user hover / move / leave a cell
    var mouseover = function(d) {
        Tooltip
            .style("opacity", 1)
    };

    var mousemove = function(d) {
        Tooltip
            .html('<u>' + d.path + '</u>' + "<br>" + d.periodInString)
            .style("left", (d3.mouse(this)[0]+20) + "px")
            .style("top", (d3.mouse(this)[1]) + "px")
    };

    var mouseleave = function(d) {
        Tooltip
            .style("opacity", 0)
    };

    // Initialize the circle: all located at the center of the svg area
    var node = periodOfTimeSvg.append("g")
        .selectAll("circle")
        .data(array)
        .enter()
        .append("circle")
        .attr("class", "node")
        .attr("r", function(d){
            return d.period
        })
        .attr("cx", widthPeriodOfTime / 2)
        .attr("cy", heightPeriodOfTime / 2)
        .style("fill", function(d){ return color(d.periodInString)})
        .style("fill-opacity", 0.8)
        .attr("stroke", "black")
        .style("stroke-width", 1)
        .on("mouseover", mouseover) // What to do when hovered
        .on("mousemove", mousemove)
        .on("mouseleave", mouseleave)
        .call(d3.drag() // call specific function when circle is dragged
            .on("start", dragstarted)
            .on("drag", dragged)
            .on("end", dragended));

    // Features of the forces applied to the nodes:
    var simulation = d3.forceSimulation()
        .force("center", d3.forceCenter().x(widthPeriodOfTime / 2).y(heightPeriodOfTime / 2)) // Attraction to the center of the svg area
        .force("charge", d3.forceManyBody().strength(.1)) // Nodes are attracted one each other of value is > 0
        .force("collide", d3.forceCollide().strength(.2).radius(function(d){ return (size(d.period + 2)) }).iterations(1)); // Force that avoids circle overlapping

    // Apply these forces to the nodes and update their positions.
    // Once the force algorithm is happy with positions ('alpha' value is low enough), simulations will stop.
    simulation
        .nodes(array)
        .on("tick", function(d){
            node
                .attr("cx", function(d){ return d.x; })
                .attr("cy", function(d){ return d.y; })
        });

    // What happens when a circle is dragged?
    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(.03).restart();
        d.fx = d.x;
        d.fy = d.y;
    }
    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }
    function dragended(d) {
        if (!d3.event.active) simulation.alphaTarget(.03);
        d.fx = null;
        d.fy = null;
    }

}

function getNumericPeriod(stringPeriod) {
    var number;
    if (stringPeriod === "RECENT") {
        number = 20;
    }
    if (stringPeriod === "MEDIUM") {
        number = 10;
    }
    if (stringPeriod === "OLD") {
        number = 7;
    }
    if (stringPeriod === "VERY_OLD") {
        number = 5;
    }
    return number;
}