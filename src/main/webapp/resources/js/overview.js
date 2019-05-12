function loadLocByLanguageOverview(data) {
    var rows = data.split(/\n/);

    $("<div>").attr("id", "cardLocByLanguage").addClass("card text-white mb-3 bg-primary").appendTo("#locByLanguage");
    $("<div>").addClass("card-header").text("LOC by Language").appendTo("#cardLocByLanguage");
    $("<div>").attr("id", "cardBodyLocByLanguage").addClass("card-body").appendTo("#cardLocByLanguage");
    for (var i = 0; i<rows.length; i++) {
        var elements = rows[i].split(",");
        $("<p>").addClass("card-text-loc" + i).text(elements[0]).appendTo("#cardBodyLocByLanguage");
        $("<span>").addClass("badge badge-success badge-pill margin-left-8px").text(elements[1]).appendTo(".card-text-loc" + i);
    }
}

function loadNumberOfFilesByLanguageOverview(data) {
    var rows = data.split(/\n/);

    $("<div>").attr("id", "cardNumberOfFilesByLanguage").addClass("card text-white mb-3 bg-success").appendTo("#filesByLanguage");
    $("<div>").addClass("card-header").text("Number Of Files by Language").appendTo("#cardNumberOfFilesByLanguage");
    $("<div>").attr("id", "cardBodyNumberOfFilesByLanguage").addClass("card-body").appendTo("#cardNumberOfFilesByLanguage");
    for (var i = 0; i<rows.length; i++) {
        var elements = rows[i].split(",");
        $("<p>").addClass("card-text-nof" + i).text(elements[0]).appendTo("#cardBodyNumberOfFilesByLanguage");
        $("<span>").addClass("badge badge-primary badge-pill margin-left-8px").text(elements[1]).appendTo(".card-text-nof" + i);
    }
}
