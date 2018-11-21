<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Charts</title>
    <link rel="shortcut icon" href="/resources/img/favicon.jpg"/>
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/mapStyle.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/locStyle.css' />">

    <link rel="stylesheet" href="<c:url value='/resources/fonts/font-awesome-4.7.0/css/font-awesome.min.css' />">
    <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700,300italic,400italic,700italic"
          rel="stylesheet" type="text/css">

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
                    <a class="nav-link" data-toggle="tab" href="#loc" aria-expanded="false">LOC over time</a>
                </li>
                <li>
                    <a class="nav-link" data-toggle="tab" href="#antipatterns" aria-expanded="false">Antipatterns</a>
                </li>
            </ul>
            <div class="tab-content">
                <div class="tab-pane in active" id="overview">
                    <div>time period selector</div>
                    <div>linesofcode</div>
                    <div>number of files</div>
                </div>
                <div class="tab-pane" id="total">
                    <svg id="sourceTreeCommitsMap" width="1580" height="800"></svg>
                </div>
                <div class="tab-pane" id="user">
                    <svg id="sourceTreeContributorsMap" width="1580" height="800"></svg>
                </div>
                <div class="tab-pane" id="loc">
                    <svg id="locLineChart" width="1550" height="770"></svg>
                    <div id="tooltip" class="tooltip" style="opacity:0"></div>
                </div>
                <div class="tab-pane" id="antipatterns">
                    <div id="antipatternsPage" width="1550" height="770"></div>
                </div>
            </div>
        </div>
    </div>
</div>


<script src="https://d3js.org/d3.v4.min.js"></script>
<script src="<c:url value='/resources/js/sourceTreeCommitsMap.js' />"></script>
<script src="<c:url value='/resources/js/locLineChart.js' />"></script>
<script src="<c:url value='/resources/js/sourceTreeContributorsMap.js' />"></script>
<script>
    loadSourceTreeCommitsMap(`${username}`, `${repositoryName}`, `${heatMapCommitsData}`);
    loadLocLineChart(`${locData}`);
    loadSourceTreeContributorsMap(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`);
</script>

<script src="<c:url value='/resources/js/jquery-3.2.1.min.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>

</body>
</html>
