<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Charts</title>
    <link rel="shortcut icon" href="<c:url value='/resources/img/favicon.jpg' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/jquery-ui.css' /> ">
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/mapStyle.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/locStyle.css' />">

    <link rel="stylesheet" href="<c:url value='/resources/css/iThing.min.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/fonts/font-awesome-4.7.0/css/font-awesome.min.css' />">
    <link href="<c:url value='/resources/css/fonts.css' />" rel="stylesheet" type="text/css">

</head>
<body class="fixed-nav sticky-footer">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary custom-nav" role="navigation" style="margin-bottom: 0">
    <div class="row">
        <div class="mr-auto pl-5 mt-1">
            <a href="" class="navbar-brand">Code Analyser</a>
        </div>

        <div class="form-inline custom-logout">
            <form id="logout" action="/logout" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input name="submit" type="submit" value="Logout" class="btn btn-secondary my-2 my-sm-0"/>
            </form>
        </div>
    </div>
</nav>

<div>

    <div class="row mt-3 custom-wrapper">
        <t:navTemplate/>
        <div class="col-md-10">
            <div id="dateSelectorSlider"></div>

            <ul class="nav nav-tabs">
                <li class="active">
                    <a class="nav-link" data-toggle="tab" href="#overview" aria-expanded="false">Overview</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#total" aria-expanded="false">Total Changes</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#user" aria-expanded="false">Changes by User</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#add-delete"
                       aria-expanded="false">Additions/Deletions</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#loc" aria-expanded="false">LOC</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#antipatterns" aria-expanded="false">Antipatterns</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#violations" aria-expanded="false">Static Analysis</a>
                </li>

            </ul>
            <div class="tab-content">
                <div class="tab-pane in active" id="overview">
                    <div class="row">
                        <div class="col-sm-4 min-height-220">
                            <div id="locByLanguage"></div>
                        </div>
                        <div class="col-sm-4 min-height-220">
                            <div id="filesByLanguage"></div>
                        </div>
                        <div class="col-sm-4 min-height-220">
                            <div class="card text-white bg-success mb-3">
                                <div class="card-header">Header</div>
                                <div class="card-body">
                                    <h4 class="card-title">Success card title</h4>
                                    <p class="card-text">Some quick example text to build on the card title and make up
                                        the bulk of the card's content.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-4 min-height-220">
                            <div class="card text-white bg-danger mb-3">
                                <div class="card-header">Header</div>
                                <div class="card-body">
                                    <h4 class="card-title">Danger card title</h4>
                                    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4 min-height-220">
                            <div class="card text-white bg-warning mb-3">
                                <div class="card-header">Header</div>
                                <div class="card-body">
                                    <h4 class="card-title">Warning card title</h4>
                                    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4 min-height-220">
                            <div class="card text-white bg-info mb-3">
                                <div class="card-header">Header</div>
                                <div class="card-body">
                                    <h4 class="card-title">Info card title</h4>
                                    <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-pane" id="total">
                    <svg id="sourceTreeCommitsMap" width="1580" height="800"></svg>
                </div>
                <div class="tab-pane" id="user">
                    <svg id="sourceTreeContributorsMap" width="1580" height="800"></svg>
                </div>
                <div class="tab-pane" id="add-delete">
                    <svg id="addRemoveLineChart" width="1550" height="800"></svg>
                    <div id="tooltip-add-remove" class="tooltip" style="opacity:0"></div>
                </div>
                <div class="tab-pane" id="loc">
                    <svg id="locLineChart" width="1550" height="800"></svg>
                    <div id="tooltip-loc" class="tooltip" style="opacity:0"></div>
                </div>
                <div class="tab-pane" id="antipatterns">
                    <div id="antipatternsPage" style="padding-top:1%">
                        <div class="row">
                            <div class="col-2">
                                <div class="nav flex-column nav-pills" id="v-pills-tab" role="tablist" aria-orientation="vertical">
                                    <a class="nav-link" data-toggle="pill" href="#v-pills-spof" role="tab">Single Point of Failure</a>
                                    <a class="nav-link" data-toggle="pill" href="#v-pills-conglomerate" role="tab">Conglomerate</a>
                                    <a class="nav-link" data-toggle="pill" href="#v-pills-mandhchanges" role="tab">Medium and Huge Changes</a>
                                </div>
                            </div>
                            <div class="col-10">
                                <div class="tab-content" id="v-pills-tabContent">
                                    <div class="tab-pane in active" id="v-pills-spof" role="tabpanel">
                                        <div id="spof-description">Lorem ipsum dolor sit amet,
                                            consectetur adipiscing elit. Nunc tincidunt ante at
                                            libero dignissim suscipit. Praesent dignissim velit
                                            ac varius scelerisque. Phasellus turpis augue, semper
                                            eu imperdiet a, efficitur sed ex. Nam sed mollis ipsum.
                                            Donec eu finibus augue. Phasellus convallis auctor nibh
                                            et vestibulum. Mauris gravida enim at ante pellentesque
                                            sagittis eu eu magna. Vivamus aliquet quis orci a laoreet.
                                        </div>
                                        <svg id="single-point-of-failure" width="1290" height="800"></svg>
                                    </div>
                                    <div class="tab-pane" id="v-pills-conglomerate" role="tabpanel">
                                        <c:choose>
                                            <c:when test="${conglomerate == ''}">
                                                <p>No problem found with the <b>Conglomerate antipattern</b></p>
                                            </c:when>
                                            <c:otherwise>
                                                <svg id="conglomerate" width="1180" height="800"></svg>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="tab-pane" id="v-pills-mandhchanges" role="tabpanel">
                                        <c:forEach items="${mediumAndHugeChanges}" var="entry">
                                            <div class="card">
                                                <h3 class="card-header">File <a target="_blank" href="https://github.com/${username}/${repositoryName}/blob/master/${entry.key}">${entry.key}</a></h3>
                                                <c:forEach items="${entry.value}" var="item">

                                                    <div class="card-body">
                                                        <h4 class="card-title"></h4>
                                                        <h6 class="card-subtitle mb-2">
                                                            <c:choose>
                                                                <c:when test="${item.changeSize == 'MAJOR'}">
                                                                    <span style="color: red">${item.changeSize}</span>
                                                                </c:when>
                                                                <c:when test="${item.changeSize == 'MEDIUM'}">
                                                                    <span style="color: orange">${item.changeSize}</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span style="color: greenyellow">${item.changeSize}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                             change by ${item.commiterName}</h6>
                                                        <p class="card-text">Commit on <fmt:formatDate value="${item.commitDate}" type="date" pattern="dd-MMM-yyyy"/></p>
                                                        <p class="card-link">${item.linesChanged} lines changed in that commit</p>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-pane" id="violations">
                    <div id="staticViolationsPage" width="1550" height="800">
                        <c:choose>
                            <c:when test="${fn:length(violationsData) == 0}">
                                You have no static analysis violations.
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${violationsData}" var="violation">
                                    <div class="alert alert-dismissible alert-warning">
                                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                                        <h4 class="alert-heading">${violation.priority}</h4>
                                        <p class="mb-0">
                                        <div><b>${violation.message} </b></div>
                                        <div>${violation.description}</div>

                                        <a href="${violation.externalInfoUrl}" class="alert-link">Check some extra
                                            info</a>.
                                        </p>
                                        <div>File: ${violation.fileName}</div>
                                        <div>Method: ${violation.methodName}</div>
                                        <div>Class: ${violation.className}</div>
                                        <div>Lines between ${violation.beginLine} and ${violation.endLine}</div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<script src="<c:url value='/resources/js/d3.v4.min.js' />"></script>
<script src="<c:url value='/resources/js/sourceTreeCommitsMap.js' />"></script>
<script src="<c:url value='/resources/js/addRemoveLOC.js' />"></script>
<script src="<c:url value='/resources/js/totalLOC.js' />"></script>
<script src="<c:url value='/resources/js/sourceTreeContributorsMap.js' />"></script>
<script src="<c:url value='/resources/js/overview.js' />"></script>
<script src="<c:url value='/resources/js/antipatterns-spof.js' />"></script>
<script src="<c:url value='/resources/js/antipatterns-conglomerate.js' />"></script>
<script src="<c:url value='/resources/js/jquery.js' />"></script>
<script src="<c:url value='/resources/js/jquery-ui.min.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>

<!-- JQ STUFF -->
<script src="<c:url value='/resources/js/jQRangeSliderMouseTouch.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRangeSliderDraggable.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRangeSliderBar.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRangeSliderHandle.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRangeSliderLabel.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRangeSlider.min.js' />"></script>
<script src="<c:url value='/resources/js/jQDateRangeSliderHandle.min.js' />"></script>
<script src="<c:url value='/resources/js/jQDateRangeSlider.min.js' />"></script>
<script src="<c:url value='/resources/js/jQRuler.min.js' />"></script>

<script>
    setStartEndDates(`${startDate}`, `${endDate}`);
    loadSourceTreeCommitsMap(`${username}`, `${repositoryName}`, `${heatMapCommitsData}`, `${startDate}`, `${endDate}`);
    loadSourceTreeContributorsMap(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, `${startDate}`, `${endDate}`);
    loadAddRemoveLineChart(`${addRemoveLinesData}`, `${startDate}`, `${endDate}`);
    loadLocChart(`${locData}`, `${startDate}`, `${endDate}`);
    loadLocByLanguageOverview(`${locByLanguage}`);
    loadNumberOfFilesByLanguageOverview(`${filesByLanguage}`);
    loadSinglePointOfFailure(`${username}`, `${repositoryName}`, `${singlePointOfFailure}`, `${startDate}`, `${endDate}`);
    loadConglomerate(`${username}`, `${repositoryName}`, `${conglomerate}`, `${startDate}`, `${endDate}`);

    $("#dateSelectorSlider").bind("valuesChanged", function (e, data) {
        loadSourceTreeCommitsMap(`${username}`, `${repositoryName}`, `${heatMapCommitsData}`, data.values.min, data.values.max);
        loadSourceTreeContributorsMap(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, data.values.min, data.values.max);
        loadAddRemoveLineChart(`${addRemoveLinesData}`, data.values.min, data.values.max);
        loadLocChart(`${locData}`, data.values.min, data.values.max);
        loadSinglePointOfFailure(`${username}`, `${repositoryName}`, `${singlePointOfFailure}`, data.values.min, data.values.max);
        loadConglomerate(`${username}`, `${repositoryName}`, `${conglomerate}`, data.values.min, data.values.max);
    });

    function setStartEndDates(startDate, endDate) {

        var startDateSplit = startDate.split('-');
        var endDateSplit = endDate.split('-');

        $("#dateSelectorSlider").dateRangeSlider({
            bounds: {
                min: new Date(startDateSplit[2], startDateSplit[1] - 1, startDateSplit[0]),
                max: new Date(endDateSplit[2], endDateSplit[1] - 1, endDateSplit[0])
            },
            defaultValues: {
                min: new Date(startDateSplit[2], startDateSplit[1] - 1, startDateSplit[0]),
                max: new Date(endDateSplit[2], endDateSplit[1] - 1, endDateSplit[0])
            }
        });
    }
</script>
</body>
</html>
