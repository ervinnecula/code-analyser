<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    <div class="row" id="top-bar">
        <div class="mr-auto pl-5 mt-1" style="width:100%">
            <a class="navbar-brand" id="brand">Code Analyser</a>
            <a class="navbar-brand" id="central-title">Analysis for <span style="color:white"> ${repoOwner}'s </span> <span style="color: rgb(255, 190, 0)"> ${repositoryName} </span> project</a>
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
            <ul class="nav nav-tabs" role="tablist" id="topTablist">
                <li class="nav-item">
                    <a class="nav-link active" data-toggle="tab" href="#overview" aria-controls="overview" aria-selected="true" role="tab" onclick="hideDateSelector()">
                        Overview
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#total" aria-controls="total" aria-selected="false" role="tab" onclick="showDateSelector()">
                        No. of Total Changes
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#user" aria-controls="user" aria-selected="false" role="tab" onclick="showDateSelector()">
                        No. of Contributors
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#owner" aria-controls="owner" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Owners
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#add-delete" aria-controls="add-delete" aria-selected="false" role="tab" onclick="showDateSelector()">
                        Additions/Deletions
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#loc" aria-controls="loc" aria-selected="false" role="tab" onclick="showDateSelector()">
                        LOC
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#periodoftime" aria-controls="periodoftime" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Period of Time
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#developers" aria-controls="developers" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Developers
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#antipatterns" aria-controls="antipatterns" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Anti-Patterns
                        <span class="badge badge-danger badge-pill badge-custom">${orphanedFiles.size() + mediumAndHugeChanges.size() + fewCommitterPoints + manyCommitterPoints + forgottenPoints}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#staticanalysis" aria-controls="staticanalysis" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Static Analysis
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-toggle="tab" href="#configparams" aria-controls="configparams" aria-selected="false" role="tab" onclick="hideDateSelector()">
                        Configure Parameters
                    </a>
                </li>
            </ul>
            <div class="tab-content" id="mainTabContent">
                <div class="tab-pane active" id="overview" role="tabpanel" aria-labelledby="overview">
                    <div class="row">
                        <div class="col-sm-4">
                            <div id="locByLanguage"></div>
                        </div>
                        <div class="col-sm-4">
                            <div id="filesByLanguage"></div>
                        </div>
                        <div class="col-sm-4">
                            <div class="card card-overview text-white bg-success mb-3">
                                <h3 class="card-header" style="text-align: center">Recent Activity</h3>
                                <div class="card-body" style="width:100%">
                                    <h5 class="card-text" style="padding-bottom:10px">
                                        Files changed: <span class="badge badge-primary badge-pill margin-left-8px">${recentFilesChanged}</span>
                                    </h5>
                                    <h5 class="card-text" style="padding-bottom:10px">
                                        Lines changed: <span class="badge badge-primary badge-pill margin-left-8px">${recentLinesChanged}</span>
                                    </h5>
                                    <h5 class="card-text" style="padding-bottom:10px">
                                        Contributors involved: <span class="badge badge-primary badge-pill margin-left-8px">${recentContributors}</span>
                                    </h5>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-4">
                            <div class="card card-overview text-white bg-danger mb-3">
                                <h3 class="card-header" style="text-align: center">Top 5 Active Contributors by LoC</h3>
                                <div class="card-body">
                                    <c:forEach begin="0" end="4" items="${top5ActiveContributorsLoC}" var="entry">
                                        <h5 class="card-text" style="padding-bottom:10px">${entry.key}:
                                            <span class="badge badge-info badge-pill margin-left-8px">${entry.value}</span>
                                        </h5>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4">
                            <div class="card card-overview text-white bg-warning mb-3">
                                <h4 class="card-header" style="text-align: center">
                                    Top 5 Active Contributors by no. of Files
                                </h4>
                                <div class="card-body">
                                    <c:forEach begin="0" end="4" items="${top5ActiveContributorsFiles}" var="entry">
                                        <h5 class="card-text" style="padding-bottom:10px">${entry.key}:
                                            <span class="badge badge-secondary badge-pill margin-left-8px">${entry.value}</span>
                                        </h5>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-4">
                            <div class="card card-overview text-white bg-info mb-3">
                                <h3 class="card-header" style="text-align: center">Percentage of Involvement</h3>
                                <div class="card-body">
                                    <c:forEach begin="0" end="4" items="${top5InvolvedContributors}" var="entry">
                                        <h5 class="card-text" style="padding-bottom:10px">${entry.key}:
                                            <span class="badge badge-warning badge-pill margin-left-8px">${entry.value} %</span>
                                        </h5>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-pane fade" id="total" role="tabpanel" aria-labelledby="total"></div>
                <div class="tab-pane fade" id="user" role="tabpanel" aria-labelledby="user"></div>
                <div class="tab-pane fade" id="owner" role="tabpanel" aria-labelledby="owner"></div>
                <div class="tab-pane fade" id="add-delete" role="tabpanel" aria-labelledby="add-delete">
                    <div id="tooltip-add-remove" class="tooltip" style="opacity:0"></div>
                </div>
                <div class="tab-pane fade" id="loc" role="tabpanel" aria-labelledby="loc" style="text-align:center">
                    <div id="tooltip-loc" class="tooltip" style="opacity:0"></div>
                </div>
                <div class="tab-pane fade" id="periodoftime" role="tabpanel" aria-labelledby="periodoftime"></div>
                <div class="tab-pane fade" id="developers" role="tabpanel" aria-labelledby="developers">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th scope="col">Username</th>
                                <th scope="col">Number of Commits</th>
                                <th scope="col">Total lines changed</th>
                                <th scope="col">Net contribution LoC</th>
                                <th scope="col">Active Recently</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${authorsData}" var="entry">
                                <tr class="
                                    <c:if test='${entry.value.developerStatus == "INACTIVE"}'>
                                        table-danger
                                    </c:if>
                                    <c:if test='${entry.value.developerStatus == "ACTIVE"}'>
                                        table-success
                                    </c:if>
                                ">
                                    <th scope="row">
                                            ${entry.key}
                                    </th>
                                    <td style="text-align:center">${entry.value.numberOfCommits}</td>
                                    <td style="text-align:center">${entry.value.totalChanges}</td>
                                    <td style="text-align:center">${entry.value.netContribution}</td>
                                    <td style="text-align:center">
                                        <c:if test='${entry.value.developerStatus == "INACTIVE"}'>
                                            <span style="color:red">${entry.value.developerStatus}</span>
                                        </c:if>
                                        <c:if test='${entry.value.developerStatus == "ACTIVE"}'>
                                            <span style="color:green">${entry.value.developerStatus}</span>
                                        </c:if>
                                    </td>
                                </tr>
                             </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="tab-pane fade" id="antipatterns" role="tabpanel" aria-labelledby="antipatterns" style="text-align:center">
                    <div id="antipatternsPage" style="padding-top:1%">
                        <div class="row">
                            <div class="col-2">
                                <div class="nav flex-column nav-pills" id="v-pills-tab" role="tablist"
                                     aria-orientation="vertical">
                                    <a class="nav-link active bg-primary mb-2" data-toggle="pill"  href="#v-pills-aa-spof" role="tab">Single Point of Failure
                                        <c:if test="${fewCommitterPoints != 0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${fewCommitterPoints}</span>
                                        </c:if>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-aa-conglomerate" role="tab">Conglomerate
                                        <c:if test="${manyCommitterPoints != 0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${manyCommitterPoints}</span>
                                        </c:if>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-aa-mandhchanges" role="tab">Medium and Major Changes
                                        <c:if test="${mediumAndHugeChanges.size() != 0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${mediumAndHugeChanges.size()}</span>
                                        </c:if>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-aa-orphaned" role="tab">Orphaned files
                                        <c:if test="${orphanedFiles.size() != 0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${orphanedFiles.size()}</span>
                                        </c:if>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-forgotten" role="tab">Forgotten
                                        <c:if test="${forgottenPoints != 0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${forgottenPoints}</span>
                                        </c:if>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-increasedecreasespikes" role="tab">V Spikes
                                        <c:if test="${increaseDecreaseSpike.size() !=0}">
                                            <span class="badge badge-danger badge-pill badge-custom">${increaseDecreaseSpike.size()}</span>
                                        </c:if>
                                    </a>
                                </div>
                            </div>
                            <div class="col-10">
                                <div class="tab-content" id="v-pills-aa-tabContent">
                                    <div class="tab-pane active" id="v-pills-aa-spof" role="tabpanel">
                                        <div id="spof-description" class="alert alert-info">
                                            Represents those files that have been changed by few developers. These files
                                            usually have one or maybe two committers at most during its lifetime, which
                                            may cause issues inside the project, as shared and distributed knowledge is
                                            vital in any team project.
                                            This situation may cause issues, when the developers that have exclusive
                                            knowledge
                                            over that piece/area of code are sick or leave the project for different
                                            reasons.
                                        </div>
                                    </div>
                                    <div class="tab-pane fade" id="v-pills-aa-conglomerate" role="tabpanel" style="text-align:center">
                                        <div id="conglomerate-description" class="alert alert-info">
                                            Consists on those files that have been changed by a lot of developers. These
                                            files
                                            tend to have tons of committers at most during its lifetime, which
                                            may cause issues inside the project. Each developer features its own
                                            flavour, style
                                            and ideas inside the code. Sometimes these ideas, born from several opinions
                                            end up
                                            creating conflicts, lack of code ownership and problems with code
                                            standardization.
                                        </div>
                                        <c:choose>
                                            <c:when test="${conglomerate == ''}">
                                                <p class="text-success">No problem found with the <b>Conglomerate
                                                    antipattern</b></p>
                                            </c:when>
                                            <c:otherwise>
                                                <svg id="conglomerate"></svg>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="tab-pane fade" id="v-pills-aa-mandhchanges" role="tabpanel" style="text-align:center">
                                        <div class="row">
                                            <c:forEach begin="0" items="${mediumAndHugeChanges}" var="entry">
                                                <div class="card card-mandhchanges">
                                                    <h6 class="card-header">File
                                                        <a target="_blank" href="https://github.com/${username}/${repositoryName}/blob/master/${entry.key}">${entry.key}</a>
                                                    </h6>
                                                    <c:forEach items="${entry.value}" var="item">
                                                        <div class="card-body card-body-padding">
                                                            <h6 class="card-title">
                                                                Commit <b>${item.commitHash}</b> on
                                                                <fmt:formatDate value="${item.commitDate}" type="date" pattern="dd-MMM-yyyy"/>
                                                            </h6>
                                                            <h6 class="card-subtitle mb-2">
                                                                <c:if test="${item.changeSize == 'MAJOR'}">
                                                                    <span style="color: red">${item.changeSize}</span> change by <b>${item.committerName}</b>
                                                                </c:if>
                                                                <c:if test="${item.changeSize == 'MEDIUM'}">
                                                                    <span style="color: orange">${item.changeSize}</span> change by <b>${item.committerName}</b>
                                                                </c:if>
                                                                <p class="card-link">${item.linesChanged} lines changed</p>
                                                            </h6>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <div class="tab-pane fade" id="v-pills-aa-orphaned" role="tabpanel" style="text-align:center">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th scope="col">Owner</th>
                                                    <th scope="col">File</th>
                                                    <th scope="col">Time since last change</th>
                                                    <th scope="col">Orphan</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach items="${orphanedFiles}" var="item">
                                                    <tr class="
                                                        <c:if test='${item.period == "VERY_OLD"}'>
                                                            table-danger
                                                        </c:if>
                                                        <c:if test='${item.period == "OLD"}'>
                                                            table-warning
                                                        </c:if>
                                                    ">
                                                    <td style="text-align:center">${item.owner}</td>
                                                        <td style="text-align:center">${item.filePath}</td>
                                                        <td style="text-align:center">
                                                            <c:if test="${item.period == 'VERY_OLD'}">
                                                                VERY OLD
                                                            </c:if>
                                                            <c:if test="${item.period == 'OLD'}">
                                                                ${item.period}
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <c:if test="${item.period == 'VERY_OLD'}">
                                                                <span style="color: red;"><b>High Chance</b></span>
                                                            </c:if>
                                                            <c:if test="${item.period == 'OLD'}">
                                                                <span style="color: #ff922e;"><b>Possible</b></span>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="tab-pane fade" id="v-pills-forgotten" role="tabpanel" style="text-align:center">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th scope="col">File name</th>
                                                    <th scope="col">Period</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach items="${forgotten}" var="item">
                                                <tr class="
                                                    <c:if test='${item.value == "VERY_OLD"}'>
                                                        table-danger
                                                    </c:if>
                                                    <c:if test='${item.value == "OLD"}'>
                                                        table-warning
                                                    </c:if>
                                                ">
                                                    <td>${item.key}</td>
                                                    <td style="text-align:center">
                                                        <c:if test='${item.value == "VERY_OLD"}'>
                                                            VERY OLD
                                                        </c:if>
                                                        <c:if test='${item.value == "OLD"}'>
                                                            ${item.value}
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="tab-pane fade" id="v-pills-increasedecreasespikes" role="tabpanel" style="text-align:center">
                                        <div class="text-white bg-secondary mb-3">
                                            <div class="card-body">
                                                <h4 class="card-title">V-Spikes Antipattern</h4>
                                                <p class="card-text">
                                                    Detected an increase, decrease, increase spikes (V-Shaped) or a decrease, increase, decrease (Reverse V-Shaped) in terms of lines of code.
                                                    In other words, code-analyser detected a major increase of code, followed by a major decrease of code, followed again by another major increase of code.
                                                    This is a sign of instability.</p>
                                            </div>
                                        </div>
                                        <c:forEach items="${increaseDecreaseSpike}" var="spike">
                                            <div class="mb-3">
                                                <h3 class="card-header">
                                                    <c:if test="${spike.value.get(1).increaseCommit()}">
                                                        Reverse V-Shaped spike series
                                                    </c:if>
                                                    <c:if test="${spike.value.get(1).increaseCommit() == false}">
                                                        V-Shaped spike series
                                                    </c:if>
                                                </h3>
                                            </div>
                                                <div class="row">
                                                    <c:forEach items="${spike.value}" var="commitInSpike">
                                                        <div class="card col">
                                                            <div class="card-header">
                                                                Commit hash <span style="color: #ee8867">${commitInSpike.commitHash}</span>
                                                            </div>
                                                            <div class="card-body">
                                                                <h4 class="card-title">
                                                                    On
                                                                    <fmt:formatDate value="${commitInSpike.commitDate}" type="date" pattern="dd-MMM-yyyy"/>
                                                                    there was a MAJOR
                                                                    <c:if test="${commitInSpike.increaseCommit()}">
                                                                        <span style="color:lightgreen">increase</span>
                                                                    </c:if>
                                                                    <c:if test="${commitInSpike.increaseCommit() == false}">
                                                                        <span style="color:red">decrease</span>
                                                                    </c:if></h4>
                                                                <p class="card-text">
                                                                    Author was <span style="color:rgb(0, 174, 227)">${commitInSpike.committerName}</span>
                                                                </p>
                                                                <p class="card-text">
                                                                    <c:if test="${commitInSpike.increaseCommit()}">
                                                                        <span style="color:lightgreen">Increase commit, where </span>
                                                                    </c:if>
                                                                    <c:if test="${commitInSpike.increaseCommit() == false}">
                                                                        <span style="color:red">Decrease commit, where</span>
                                                                    </c:if>
                                                                        ${commitInSpike.linesChanged} lines of code modified
                                                                </p>
                                                            </div>
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
                <div class="tab-pane fade" id="staticanalysis" role="tabpanel" aria-labelledby="staticanalysis" style="text-align:center">
                    <div id="staticAnalysisPage" style="padding-top:1%">
                        <div class="row">
                            <div class="col-2">
                                <div class="nav flex-column nav-pills" id="v-pills-sa-tab" role="tablist" aria-orientation="vertical">
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-sa-violations" role="tab">
                                        Basic Violations <span class="badge badge-warning">Java Only</span>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-sa-optimizations" role="tab">
                                        Optimizations <span class="badge badge-warning">Java Only</span>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-sa-coupling" role="tab">
                                        Coupling <span class="badge badge-warning">Java Only</span>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-sa-codesize" role="tab">
                                        Code Size <span class="badge badge-warning">Java Only</span>
                                    </a>
                                    <a class="nav-link bg-primary mb-2" data-toggle="pill" href="#v-pills-sa-design" role="tab">
                                        Design <span class="badge badge-warning">Java Only</span></a>
                                </div>
                            </div>
                            <div class="col-10">
                                <div class="tab-content" id="v-pills-sa-tabContent">
                                    <div class="tab-pane in active" id="v-pills-sa-violations">
                                        <div id="staticViolationsPage" width="1550" height="800">
                                            <c:choose>
                                                <c:when test="${fn:length(basicViolations) == 0}">
                                                    <p class="text-success">You have no static analysis violations.</p>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${basicViolations}" var="violation">
                                                        <div class="alert alert-dismissible alert-warning">
                                                            <button type="button" class="close" data-dismiss="alert">
                                                                &times;
                                                            </button>
                                                            <h4 class="alert-heading">${violation.priority}</h4>
                                                            <div class="mb-0">
                                                                <div><b>${violation.message} </b></div>
                                                                <div>${violation.description}</div>
                                                                <a href="${violation.externalInfoUrl}" class="alert-link">
                                                                    Check some extra info.
                                                                </a>
                                                            </div>
                                                            <div>File: ${violation.fileName}</div>
                                                            <div>Method: ${violation.methodName}</div>
                                                            <div>Class: ${violation.className}</div>
                                                            <div>Lines between ${violation.beginLine}
                                                                and ${violation.endLine}</div>
                                                        </div>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="v-pills-sa-optimizations" role="tabpanel">
                                        <div>
                                            <c:choose>
                                                <c:when test="${fn:length(optimizationViolations) == 0}">
                                                    <p class="text-success">You have no optimization suggestions.</p>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${optimizationViolations}" var="optimizationViolation">
                                                        <div class="alert alert-dismissible alert-success">
                                                            <button type="button" class="close" data-dismiss="alert">
                                                                &times;
                                                            </button>
                                                            <h4 class="alert-heading">${optimizationViolation.priority}</h4>
                                                            <div class="mb-0">
                                                                <div><b>${optimizationViolation.message} </b></div>
                                                                <div>${optimizationViolation.description}</div>
                                                                <a href="${optimizationViolation.externalInfoUrl}"
                                                                   class="alert-link">Check some extra
                                                                    info</a>.
                                                                <div style="color:black; font-weight:600">
                                                                    Check file ${optimizationViolation.fileName} in
                                                                    class ${optimizationViolation.className}
                                                                    at method ${optimizationViolation.methodName} lines
                                                                    between
                                                                        ${optimizationViolation.beginLine}
                                                                    and ${optimizationViolation.endLine}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="v-pills-sa-coupling" role="tabpanel">
                                        <c:choose>
                                            <c:when test="${fn:length(couplingViolations) == 0}">
                                                <p class="text-success">Static analysis detected you have no coupling
                                                    issues.</p>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach items="${couplingViolations}" var="couplingViolation">
                                                    <div class="alert alert-dismissible alert-danger">
                                                        <button type="button" class="close" data-dismiss="alert">&times;
                                                        </button>
                                                        <h4 class="alert-heading">${couplingViolation.priority}</h4>
                                                        <div class="mb-0">
                                                            <div><b>${couplingViolation.message} </b></div>
                                                            <div>${couplingViolation.description}</div>
                                                            <a href="${couplingViolation.externalInfoUrl}"
                                                               class="alert-link">Check some extra
                                                                info</a>.
                                                            <div style="color:black; font-weight:600">
                                                                Check file ${couplingViolation.fileName} in
                                                                class ${couplingViolation.className}
                                                                at method ${couplingViolation.methodName} lines between
                                                                    ${couplingViolation.beginLine}
                                                                and ${couplingViolation.endLine}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="tab-pane" id="v-pills-sa-codesize" role="tabpanel">
                                        <div>
                                            <c:choose>
                                                <c:when test="${fn:length(codesizeViolations) == 0}">
                                                    <p class="text-success">Static analysis detected you have no code size problems.</p>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${codesizeViolations}" var="codesizeViolation">
                                                        <div class="alert alert-dismissible alert-dark"
                                                             style="color:#6c6c6c !important;">
                                                            <button type="button" class="close" data-dismiss="alert">
                                                                &times;
                                                            </button>
                                                            <h4 class="alert-heading">${codesizeViolation.priority}</h4>
                                                            <div class="mb-0">
                                                                <div><b>${codesizeViolation.message} </b></div>
                                                                <div>${codesizeViolation.description}</div>
                                                                <a href="${codesizeViolation.externalInfoUrl}"
                                                                   class="alert-link">Check some extra
                                                                    info</a>.
                                                                <div style="color:black; font-weight:600">
                                                                    Check fine ${codesizeViolation.fileName} in
                                                                    class ${codesizeViolation.className}
                                                                    at method ${codesizeViolation.methodName}$ lines between
                                                                        ${codesizeViolation.beginLine}
                                                                    and ${codesizeViolation.endLine}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="v-pills-sa-design" role="tabpanel">
                                        <div>
                                            <c:choose>
                                                <c:when test="${fn:length(designViolations) == 0}">
                                                    <p class="text-success">Static analysis detected you have no design
                                                        problems.</p>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach items="${designViolations}" var="designViolation">
                                                        <div class="alert alert-dismissible alert-primary"
                                                             stlye="color:black !important">
                                                            <button type="button" class="close" data-dismiss="alert">
                                                                &times;
                                                            </button>
                                                            <h4 class="alert-heading">${designViolation.priority}</h4>
                                                            <div class="mb-0">
                                                                <div><b>${designViolation.message} </b></div>
                                                                <div>${designViolation.description}</div>
                                                                <a href="${designViolation.externalInfoUrl}"
                                                                   class="alert-link">Check some extra
                                                                    info</a>.
                                                                <div style="color:black; font-weight:600">
                                                                    Check fine ${designViolation.fileName} in
                                                                    class ${designViolation.className}
                                                                    at method ${designViolation.methodName}$ lines between
                                                                        ${designViolation.beginLine}
                                                                    and ${designViolation.endLine}
                                                                </div>
                                                            </div>
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
                </div>
                <div class="tab-pane fade" id="configparams" role="tabpanel" aria-labelledby="configparams">
                    <form id="update-config-params" action="/update-config" method="POST" >
                        <div class="alert alert-dismissible alert-success">
                            <h4 class="alert-heading">Configure your analysis parameters.</h4>
                            Provide only numeric values for your parameters so that you can addapt and improve your chart, map or graph data.
                        </div>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="repoOwner" value="${repoOwner}"/>
                        <input type="hidden" name="repoName" value="${repoName}"/>
                        <input type="hidden" name="repoGitUrl" value="${repoGitUrl}"/>
                        <div class="row">
                            <div class="col">
                                <div class="form-group">
                                    <label class="col-form-label" for="fewCommittersSize">Few Committers Size</label>
                                    <input class="form-control" type="number" name="fewCommitters" min="1" value="${fewCommitters}" id="fewCommittersSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">Anything below this number it will be considered too few committers for a source file.</small>
                                </div>
                                <div class="form-group">
                                    <label class="col-form-label" for="largeFileSize">Large File Size</label>
                                    <input class="form-control" type="number" name="largeFileSize" min="1" value="${largeFileSize}" id="largeFileSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">Having files with more LOC than this will mean you added a large file.</small>
                                </div>
                                <div class="form-group">
                                    <label class="col-form-label" for="mediumChangeSize">Medium Change Size</label>
                                    <input class="form-control" type="number" name="mediumChangeSize" min="1" value="${mediumChangeSize}" id="mediumChangeSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">Less LOC than Medium Change Size will mean you pushed a small-size commit. Anything above this number but below Major Change Size, will mark the commit as medium-size commit</small>
                                </div>
                                <div>
                                    <input id="submit-update-config" type="submit" value="Update properties for ${username}" class="btn btn-primary my-2 my-sm-0"/>
                                </div>
                            </div>
                            <div class="col">
                                <div class="form-group">
                                    <label class="col-form-label" for="manyCommittersSize">Many Committers Size</label>
                                    <input class="form-control" type="number" name="manyCommitters" min="1" value="${manyCommitters}" id="manyCommittersSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">Having more contributors per file than this will be too many committers.</small>
                                </div>
                                <div class="form-group">
                                    <label class="col-form-label" for="hugeFileSize">Huge File Size</label>
                                    <input class="form-control" type="number" name="hugeFileSize" min="1" value="${hugeFileSize}" id="hugeFileSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">Going above this LOC number in a file will mean you added a huge file.</small>
                                </div>
                                <div class="form-group">
                                    <label class="col-form-label" for="majorChangeSize">Major Change Size</label>
                                    <input class="form-control" type="number" name="majorChangeSize" min="1" value="${majorChangeSize}" id="majorChangeSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small class="form-text text-muted">More LOC than Major Change Size will mean you made a big change</small>
                                </div>
                                <div class="form-group">
                                    <label class="col-form-label" for="periodOfTimeSize">Period of Time</label>
                                    <input class="form-control" type="number" name="periodOfTime" min="1" value="${periodOfTime}" id="periodOfTimeSize" onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
                                    <small  class="form-text text-muted"
                                        >Number of days marking a period. It is CodeAnalyser's way of telling the age of files or activity of contributors inside the project. <br>
                                        Files no older than this number will be marked as RECENT. <br>
                                        Files with age (in days) between [Period Of Time] (included) and 2x[Period Of Time] will be marked as MEDIUM. <br>
                                        Files with age (in days) between 2x[Period Of Time] (included) and 6x[Period Of Time] will be marked as OLD. <br>
                                        Files with age (in days) older than 6x[Period Of Time] (included) will be marked as VERY_OLD.
                                    </small>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>


<script src="<c:url value='/resources/js/d3.v4.min.js' />"></script>
<script src="<c:url value='/resources/js/jquery.js' />"></script>
<script src="<c:url value='/resources/js/jquery-ui.min.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>
<script src="<c:url value='/resources/js/chroma.min.js'  />"></script>
<script src="<c:url value='/resources/js/sourceTreeCommitsMap.js' />"></script>
<script src="<c:url value='/resources/js/sourceTreeContributorsMap.js' />"></script>
<script src="<c:url value='/resources/js/sourceTreeOwnersMap.js' />"></script>
<script src="<c:url value='/resources/js/addRemoveLOC.js' />"></script>
<script src="<c:url value='/resources/js/totalLOC.js' />"></script>
<script src="<c:url value='/resources/js/overview.js' />"></script>
<script src="<c:url value='/resources/js/antipatterns-spof.js' />"></script>
<script src="<c:url value='/resources/js/antipatterns-conglomerate.js' />"></script>
<script src="<c:url value='/resources/js/periodOfTime.js' />"></script>
<script src="<c:url value='/resources/js/validationParameters.js' />"></script>
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
    loadSourceTreeCommitsMap(`${username}`, `${repositoryName}`, `${heatMapCommitsData}`, `${startDate}`, `${endDate}`);
    loadSourceTreeContributorsMap(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, `${startDate}`, `${endDate}`);
    loadSourceTreeOwnersMap(`${username}`, `${repositoryName}`, `${heatMapFileOwnersData}`, `${contributorsList}`, `${startDate}`, `${endDate}`);
    loadAddRemoveLineChart(`${addRemoveLinesData}`, `${startDate}`, `${endDate}`);
    loadLocChart(`${locData}`, `${startDate}`, `${endDate}`);
    loadLocByLanguageOverview(`${locByLanguage}`);
    loadNumberOfFilesByLanguageOverview(`${filesByLanguage}`);
    loadSinglePointOfFailure(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, `${fewCommitters}`);
    loadConglomerate(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, `${manyCommitters}`);
    periodOfTime(`${filesAndPeriods}`);

    $("#dateSelectorSlider").bind("valuesChanged", function (e, data) {
        loadSourceTreeCommitsMap(`${username}`, `${repositoryName}`, `${heatMapCommitsData}`, data.values.min, data.values.max);
        loadSourceTreeContributorsMap(`${username}`, `${repositoryName}`, `${heatMapContributorsData}`, data.values.min, data.values.max);
        loadAddRemoveLineChart(`${addRemoveLinesData}`, data.values.min, data.values.max);
        loadLocChart(`${locData}`, data.values.min, data.values.max);
    });

    function hideDateSelector() {
        $("#dateSelectorSlider").css("visibility", "hidden").css("height", "0px").css("margin-bottom", "0px");
    }

    function showDateSelector() {
        $("#dateSelectorSlider").css("visibility", "visible").css("height","30px").css("margin-bottom", "1%");
    }

    function loadDateSelectorSlider(startDate, endDate, callback) {
        if (startDate !== endDate) {
            var startDateSplit = startDate.split('-');
            var endDateSplit = endDate.split('-');

            var start_year = startDateSplit[2];
            var start_month = startDateSplit[1] - 1;
            var start_day = startDateSplit[0];

            var end_year = endDateSplit[2];
            var end_month = endDateSplit[1] - 1;
            var end_day = endDateSplit[0];

            $("#dateSelectorSlider").dateRangeSlider({
                bounds: {
                    min: new Date(start_year, start_month, start_day),
                    max: new Date(end_year, end_month, end_day)
                },
                defaultValues: {
                    min: new Date(start_year, start_month, start_day),
                    max: new Date(end_year, end_month, end_day)
                }
            });
        }
        callback();
    }

    window.onload = function() {
        loadDateSelectorSlider(`${startDate}`, `${endDate}`, hideDateSelector);
    };

    $('#topTablist > .nav-item > .nav-link').on('click', function (e) {
        var selected = $(this.hash);
        if (!selected.hasClass('active')) {
            $('.nav-link.active').removeClass('active');
            $('#mainTabContent > .tab-pane').addClass('fade').removeClass('active').removeClass('in');
            e.target.classList.add('active');
            $(selected).removeClass('fade');
        }
    });

    $('#v-pills-tab > .nav-link').on('click', function (e) {
        var selected = $(this.hash);
        if (!selected.hasClass('active')) {
            $('#v-pills-tab > .nav-link').removeClass('active');
            e.target.classList.add('active');
            $('#v-pills-aa-tabContent > .tab-pane').addClass('fade').removeClass('active').removeClass('in');
            $(selected).removeClass('fade');
        }
    });
</script>
</body>
</html>
