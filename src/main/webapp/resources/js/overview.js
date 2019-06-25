function loadLocByLanguageOverview(data) {
    var rows = data.split(/\n/);

    $("<div>").attr("id", "cardLocByLanguage").addClass("card card-overview text-white mb-3 bg-primary").appendTo("#locByLanguage");
    $("<h3>").addClass("card-header").text("LOC by Language").css("text-align", "center").appendTo("#cardLocByLanguage");
    $("<div>").attr("id", "cardBodyLocByLanguage").addClass("card-body").css("padding-bottom", "10px").css("width","100%").appendTo("#cardLocByLanguage");
    var max = rows.length;
    if (rows.length > 4) max = 3;
    for (var i = 0; i < max; i++) {
        var elements = rows[i].split(",");
        $("<h5>").addClass("card-text-loc" + i).text(elements[0]).css("padding-bottom", "10px").appendTo("#cardBodyLocByLanguage");
        $("<span>").addClass("badge badge-danger badge-pill margin-left-8px").text(elements[1]).appendTo(".card-text-loc" + i);
    }
}

function loadNumberOfFilesByLanguageOverview(data) {
    var rows = data.split(/\n/);

    $("<div>").attr("id", "cardNumberOfFilesByLanguage").addClass("card card-overview text-white mb-3 bg-secondary").appendTo("#filesByLanguage");
    $("<h3>").addClass("card-header").text("Number Of Files by Language").css("text-align", "center").appendTo("#cardNumberOfFilesByLanguage");
    $("<div>").attr("id", "cardBodyNumberOfFilesByLanguage").addClass("card-body").css("width", "100%").appendTo("#cardNumberOfFilesByLanguage");
    var max = rows.length;
    if (rows.length > 4) max = 3;
    for (var i = 0; i < max; i++) {
        var elements = rows[i].split(",");
        $("<h5>").addClass("card-text-nof" + i).text(elements[0]).css("padding-bottom", "10px").appendTo("#cardBodyNumberOfFilesByLanguage");
        $("<span>").addClass("badge badge-success badge-pill margin-left-8px").text(elements[1]).appendTo(".card-text-nof" + i);
    }
}
