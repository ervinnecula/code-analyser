var periodOfTimeSvg = d3.select("#periodoftimeChart"),
    width = +periodOfTimeSvg.attr("width"),
    height = +periodOfTimeSvg.attr("height");

var format = d3.format(",d");

var color = ['#4afe00', '#a4e897', '#626a60','#322f2f'];

var pack = d3.pack()
    .size([width, height])
    .padding(1.5);

function periodOfTime(data) {
    var array = [];

    periodOfTimeSvg.selectAll("*").remove();

    var rows = data.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        if (elements[1] === "PARENT") {
            array.push({
                path: elements[0],
                period: '',
                periodInString: ''
            });
        } else {
            array.push({
                path: elements[0],
                period: getNumericPeriod(elements[1]),
                periodInString: elements[1]
            });
        }
    }

    var root = d3.hierarchy({ children: array })
        .sum(function(d) {
            return d.period;
        });

    var node = periodOfTimeSvg.selectAll(".node")
        .data(pack(root).leaves())
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) {
            return "translate(" + d.x + "," + d.y + ")"; });

    node.append("circle")
        .attr("id", function(d) {
            return d.data.periodInString;
        }).attr("r", function(d) {
            return d.r; })
        .style("fill", function(d) {
            var fillColor;
            switch(d.data.periodInString) {
                case "RECENT":
                    fillColor = color[0];
                    break;
                case "MEDIUM":
                    fillColor = color[1];
                    break;
                case "OLD":
                    fillColor = color[2];
                    break;
                case "VERY_OLD":
                    fillColor = color[3];
                    break;
            }
            return fillColor;
        });

    node.append("clipPath")
        .attr("id", function(d) {
            return "clip-" + d.data.path; })
        .append("use")
        .attr("xlink:href", function(d) {
            return "#" + d.data.path;
        });

    node.append("title")
        .text(function(d) {
            return d.data.path + "\n" + d.data.periodInString;;
        });
}

function getNumericPeriod(stringPeriod) {
    var number;
    if (stringPeriod === "RECENT") {
        number = 20;
    } if (stringPeriod === "MEDIUM") {
        number = 10;
    } if (stringPeriod === "OLD") {
        number = 5;
    } if (stringPeriod === "VERY_OLD") {
        number = 3;
    }
    return number;
}