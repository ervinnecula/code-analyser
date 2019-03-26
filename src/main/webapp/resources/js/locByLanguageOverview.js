function loadLocByLanguageOverview(data) {
    var rows = data.split(/\n/);

    $("<div>").attr("id", "cardLocByLanguage").addClass("card text-white mb-3 bg-primary").appendTo("#locByLanguage");
    $("<div>").addClass("card-header").text("LOC by Language").appendTo("#cardLocByLanguage");
    $("<div>").attr("id", "cardBodyLocByLanguage").addClass("card-body").appendTo("#cardLocByLanguage");
    for (var i = 0; i<rows.length; i++) {
        var elements = rows[i].split(",");
        $("<p>").addClass("card-text" + i).text(elements[0]).appendTo("#cardBodyLocByLanguage");
        $("<span>").addClass("badge badge-success badge-pill margin-left-8px").text(elements[1]).appendTo(".card-text" + i);
    }
}