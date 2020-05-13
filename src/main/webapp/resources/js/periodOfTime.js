var periodOfTimeSvg = d3.select("#periodoftime")
    .append("svg")
    .attr("height", "100%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid");

var widthPeriodOfTime = $("#periodoftime").width() * 0.90;
var heightPeriodOfTime = $("#periodoftime").height() * 1.40;

var format = d3.format(",d");

var colorPeriodOfTime = ['#4afe00', '#a4e897', '#626a60', '#322f2f'];

var pack = d3.pack()
    .size([widthPeriodOfTime, heightPeriodOfTime])
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

    var root = d3.hierarchy({children: array})
        .sum(function (d) {
            return d.period;
        });

    var node = periodOfTimeSvg.selectAll(".node")
        .data(pack(root).leaves())
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });

    node.append("circle")
        .attr("id", function (d) {
            return d.data.periodInString;
        }).attr("r", function (d) {
            return d.r;
        }).style("fill", function (d) {
            var fillColor;
            switch (d.data.periodInString) {
                case "RECENT":
                    fillColor = colorPeriodOfTime[0];
                    break;
                case "MEDIUM":
                    fillColor = colorPeriodOfTime[1];
                    break;
                case "OLD":
                    fillColor = colorPeriodOfTime[2];
                    break;
                case "VERY_OLD":
                    fillColor = colorPeriodOfTime[3];
                    break;
            }
            return fillColor;
        });

    node.append("text")
        .attr("class", "periodOfTimeStroke")
        .attr("text-anchor", "middle")
        .attr("alignment-baseline", "central")
        .html(function (d) {
            return d.data.periodInString
        });

    node.append("clipPath")
        .attr("id", function (d) {
            return "clip-" + d.data.path;
        })
        .append("use")
        .attr("xlink:href", function (d) {
            return "#" + d.data.path;
        });

    node.append("title")
        .text(function (d) {
            return d.data.path + "\n" + d.data.periodInString;
        });
}

function getNumericPeriod(stringPeriod) {
    var number;
    if (stringPeriod === "RECENT") {
        number = 25;
    }
    if (stringPeriod === "MEDIUM") {
        number = 15;
    }
    if (stringPeriod === "OLD") {
        number = 10;
    }
    if (stringPeriod === "VERY_OLD") {
        number = 8;
    }
    return number;
}