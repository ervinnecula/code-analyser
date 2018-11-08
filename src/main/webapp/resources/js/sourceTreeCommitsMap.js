var sourceTreeCommitsSvg = d3.select("#sourceTreeCommitsMap"),
    width = +sourceTreeCommitsSvg.attr("width"),
    height = +sourceTreeCommitsSvg.attr("height");

var colorCommits = d3.scaleOrdinal().range(['#B1DAE8', '#77CDE8', '#50C3E9', '#0278A0','#014C66']);

var format = d3.format(",d");

var treemapCommits = d3.treemap()
    .size([width, height])
    .round(true)
    .padding(1);

function loadSourceTreeCommitsMap(username, repositoryName, data) {
    var array = [];
    var maximumValue = -1;
    var minimumValue = 9999999;

    var rows = data.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        array.push({
            path: elements[0],
            size: elements[1]
        })
    }

    var root = d3.stratify()
        .id(function(d) { return d.path; })
        .parentId(function(d) { return d.path.substring(0, d.path.lastIndexOf("/")); })
        (array)
        .sum(function(d) { if (d.size < minimumValue) { minimumValue = d.size }
                            if (d.size > maximumValue) { maximumValue = d.size }
                            return d.size; })
        .sort(function(a, b) { return b.height - a.height || b.value - a.value; });

    treemapCommits(root);

    var cell = sourceTreeCommitsSvg.selectAll("a")
        .data(root.leaves())
        .enter().append("a")
        .attr("target", "_blank")
        .attr("xlink:href", function(d) { return "https://github.com/" + username + "/" + repositoryName + "/blob/master/" + d.data.path })
        .attr("transform", function(d) { return "translate(" + d.x0 + "," + d.y0 + ")"; });

    cell.append("rect")
        .attr("id", function(d) { return d.id; })
        .attr("width", function(d) { return d.x1 - d.x0; })
        .attr("height", function(d) { return d.y1 - d.y0; })
        .attr("fill", function(d) {
            // Converting values to range 0 - 100
            var newValue = (((d.data.size - minimumValue) * (100)) / (maximumValue - minimumValue));
            var fillColor;
            switch(true) {
                case newValue >= 0 && newValue < 20:
                    fillColor = colorCommits(0);
                    break;
                case newValue >= 20 && newValue < 40:
                    fillColor = colorCommits(1);
                    break;
                case newValue >= 40 && newValue < 60:
                    fillColor = colorCommits(2);
                    break;
                case newValue >= 60 && newValue < 80:
                    fillColor = colorCommits(3);
                    break;
                case newValue >= 80:
                    fillColor = colorCommits(4);
                    break;
            }
            return fillColor;
        });

    cell.append("clipPath")
        .attr("id", function(d) { return "clip-" + d.id; })
        .append("use")
        .attr("xlink:href", function(d) { return "#" + d.id; });

    var label = cell.append("text")
        .attr("clip-path", function(d) { return d.id; });

    label.append("tspan")
        .attr("x", 4)
        .attr("y", 25)
        .attr("fill", "white")
        .text(function(d) { return d.data.path.substring(d.data.path.lastIndexOf("/") + 1, d.data.path.lastIndexOf(".")); });

    label.append("tspan")
        .attr("x", 4)
        .attr("y", 45)
        .attr("fill", "white")
        .text(function(d) { return format(d.value); });

    cell.append("title")
        .text(function(d) { return d.id + "\n" + format(d.value); });
}
