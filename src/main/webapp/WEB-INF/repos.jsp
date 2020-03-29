<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
<head>
    <title>Repos</title>
    <link rel="shortcut icon" href="/resources/img/favicon.jpg"/>
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css' />">
    <link rel="stylesheet" href="<c:url value='/resources/css/style.css' />">


    <link rel="stylesheet" href="<c:url value='/resources/fonts/font-awesome-4.7.0/css/font-awesome.min.css' />">
    <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700,300italic,400italic,700italic"
          rel="stylesheet" type="text/css">

</head>
<body class="fixed-nav sticky-footer">
<nav class="navbar navbar-expand-lg navbar-dark bg-primary custom-nav" role="navigation" style="margin-bottom: 0">
    <div class="row">
        <div class="mr-auto pl-5 mt-1">
            <a class="navbar-brand">Code Analyser</a>
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
            <h2>Repos available for ${username}</h2>
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th scope="col">Repository Name</th>
                        <th scope="col">Url</th>
                        <th scope="col">Language</th>
                        <th scope="col"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${repoNameHtmlGitUrlsBeans}" var="repoNameHtmlGitUrlsBean">
                        <tr>
                            <form action="/analysis" method="POST">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input type="hidden" name="username" value="${username}"/>
                                <th scope="row">
                                    <input type="hidden" value="${repoNameHtmlGitUrlsBean.repoName}" name="repoName"/>
                                        ${repoNameHtmlGitUrlsBean.repoName}
                                </th>
                                <input type="hidden" value="${repoNameHtmlGitUrlsBean.repoGitUrl}" name="repoGitUrl"/>
                                <td>
                                    <input type="hidden" value="${repoNameHtmlGitUrlsBean.repoHtmlUrl}" name="repoHtmlUrl"/>
                                    <a target="_blank" href="${repoNameHtmlGitUrlsBean.repoHtmlUrl}">${repoNameHtmlGitUrlsBean.repoHtmlUrl}</a>
                                </td>
                                <td>
                                    <input type="hidden" value="${repoNameHtmlGitUrlsBean.repoLanguage}" name="repoLanguage"/> ${repoNameHtmlGitUrlsBean.repoLanguage}
                                </td>
                                <td>
                                    <input type="submit" value="Analyse  ${repoNameHtmlGitUrlsBean.repoName}" class="btn btn-primary"/>
                                </td>
                            </form>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="<c:url value='/resources/js/jquery.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>

</body>
</html>
