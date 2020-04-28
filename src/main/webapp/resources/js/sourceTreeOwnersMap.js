var sourceTreeOwnersSvg = d3.select("#owner")
    .append("svg")
    .attr("height", "100%")
    .attr("width", "100%")
    .attr("preserveAspectRatio", "xMidYMid")
    .attr("class", "pt-2");

var widthOwners = $("#owner").width() * 0.97;
var heightOwners = $("#total").height() * 1.40;

function loadSourceTreeOwnersMap(username, repositoryName, data, contributorsList, startDate, endDate) {

    var contributors = contributorsList.split(",");

    var colors = chroma.scale(['#fafa6e','#2A4858'])
        .mode('lch').colors(contributors.length);

    var treemapOwners = d3.treemap()
        .size([widthOwners, heightOwners])
        .round(true)
        .padding(1);

    var array = [];

    var startDateObj = startDate;
    var endDateObj = endDate;

    if (startDate instanceof Date === false && endDate instanceof Date === false) {
        var startDateSplit = startDate.split("-");
        var endDateSplit = endDate.split("-");
        startDateObj = new Date(startDateSplit[2], Number(startDateSplit[1]) - 1, startDateSplit[0], 0);
        endDateObj = new Date(endDateSplit[2], Number(endDateSplit[1]) - 1, endDateSplit[0], 0);
    }
    sourceTreeOwnersSvg.selectAll("*").remove();

    var rows = data.split(/\n/);
    for (var i = 0; i < rows.length; i++) {
        var elements = rows[i].split(",");
        var splitDate = elements[1].split("-");
        var currentDateObj = new Date(Number(splitDate[0]), splitDate[1] - 1, Number(splitDate[2]));

        if (startDateObj <= currentDateObj && currentDateObj <= endDateObj || elements[1] === 'null' || elements[2] === 'null') {
            array.push({
                path: elements[0],
                owner: elements[2]
            });
        }
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
            return +contributors.indexOf(d.owner);
        })
        .sort(function (a, b) {
            return b.height - a.height || b.value - a.value;
        });

    treemapOwners(root);

    var cell = sourceTreeOwnersSvg.selectAll("a")
        .data(root.leaves())
        .enter().append("a")
        .attr("target", "_blank")
        .attr("xlink:href", function (d) {
            return "https://github.com/" + username + "/" + repositoryName + "/blob/master/" + d.data.path.replace("__project__/", "")
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
            return colors[contributors.indexOf(d.data.owner)];
        });

    cell.append("clipPath")
        .attr("id", function (d) {
            return "clip-" + d.id.replace("__project__/", "");
        })
        .append("use")
        .attr("xlink:href", function (d) {
            return "#" + d.id.replace("__project__/", "");
        });

    var label = cell.append("text")
        .attr("clip-path", function (d) {
            return d.id.replace("__project__/", "")
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
            return contributors[contributors.indexOf(d.data.owner)];
        });

    cell.append("title")
        .text(function (d) {
            return d.id.replace("__project__/", "") + "\n" + d.data.owner;
        });
}
