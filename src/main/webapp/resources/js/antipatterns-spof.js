var spofTreeSvg = d3.select("#v-pills-aa-spof")
    .append("svg")
    .attr("height", "100%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid");

var format = d3.format(",d");

function loadSinglePointOfFailure(username, repositoryName, data, fewCommitters) {

    var array = [];

    var spofTreemap = d3.treemap()
        .size([widthTotal * 0.84, heightTotal * 0.90])
        .round(true)
        .padding(1);

    var maximumValue = -1;
    var minimumValue = 9999999;

    if (data !== '') {
        spofTreeSvg.selectAll("*").remove();

        var rows = data.split(/\n/);
        for (var i = 0; i < rows.length; i++) {
            var elements = rows[i].split(",");

            array.push({
                path: elements[0],
                size: elements[2]
            });
        }

        var root = d3.stratify()
            .id(function (d) {
                return d.path;
            })
            .parentId(function (d) {
                return d.path.substring(0, d.path.lastIndexOf("/"));
            })
            (array)
            .sum(function (d) {
                if (d.size < minimumValue) {
                    minimumValue = d.size
                }
                if (d.size > maximumValue) {
                    maximumValue = d.size
                }
                return d.size;
            })
            .sort(function (a, b) {
                return b.height - a.height || b.value - a.value;
            });

        spofTreemap(root);

        var cell = spofTreeSvg.selectAll("a")
            .data(root.leaves())
            .enter().append("a")
            .attr("target", "_blank")
            .attr("xlink:href", function (d) {
                return "https://github.com/" + username + "/" + repositoryName + "/blob/master/" + d.data.path.replace("__project__/", "");
            })
            .attr("transform", function (d) {
                return "translate(" + d.x0 + "," + d.y0 + ")";
            });

        cell.append("rect")
            .attr("id", function (d) {
                return d.id;
            })
            .attr("width", function (d) {
                return d.x1 - d.x0;
            })
            .attr("height", function (d) {
                return d.y1 - d.y0;
            })
            .attr("fill", function (d) {
                var fillColor;
                if (Number(d.data.size) > Number(fewCommitters)) {
                    fillColor = '#84e9ac';
                } else {
                    fillColor = '#ff1700';
                }
                return fillColor;
            });

        cell.append("clipPath")
            .attr("id", function (d) {
                return "clip-" + d.id.replace("__project__/", "")
            })
            .append("use")
            .attr("xlink:href", function (d) {
                return "#" + d.id.replace("__project__/", "");
            });

        var label = cell.append("text")
            .attr("clip-path", function (d) {
                return d.id.replace("__project__/", "");
            });

        label.append("tspan")
            .attr("x", 4)
            .attr("y", 25)
            .attr("fill", "white")
            .text(function (d) {
                var file = d.data.path.substring(d.data.path.lastIndexOf("/") + 1, d.data.path.lastIndexOf("."));
                file = file.replace("__project__/", "");
                return file;
            });

        label.append("tspan")
            .attr("x", 4)
            .attr("y", 45)
            .attr("fill", "white")
            .text(function (d) {
                return format(d.value);
            });

        cell.append("title")
            .text(function (d) {
                return d.id.replace("__project__/", "") + "\n" + format(d.value);
            });
    }
}
