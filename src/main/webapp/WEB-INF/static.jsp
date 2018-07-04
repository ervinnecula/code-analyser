<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>

        <html>
        <head>
            <title>Static Analysis</title>
            <link rel="shortcut icon" href="/resources/img/favicon.jpg"/>
            <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css' />">
            <link rel="stylesheet" href="<c:url value='/resources/css/style.css' />">


            <link rel="stylesheet" href="<c:url value='/resources/fonts/font-awesome-4.7.0/css/font-awesome.min.css' />">
            <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700,300italic,400italic,700italic"
                  rel="stylesheet" type="text/css">
        </head>
<body>
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
<div class="row mt-3 custom-wrapper">
    <div class="col-md-4 pl-4">
        <ul class="list-group">
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <form action="/repos" method="GET" id="username-form">
                    <input type="hidden" value="${username}" name="username"/>
                    <a href="javascript:{}"
                       onclick="document.getElementById('username-form').submit(); return false;">${username}'s
                        repos</a>

                </form>
            </li>

            <li class="list-group-item d-flex justify-content-between align-items-center">
                Morbi leo risus
            </li>
        </ul>
    </div>
    <div class="col-md-8">
        <c:forEach items="${violations}" var="violation">
            <div class="alert alert-dismissible alert-warning">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <h4 class="alert-heading">${violation.priority}</h4>
            <p class="mb-0">
                <div><b>${violation.message} </b></div>
                <div>${violation.description}</div>

                <a href="${violation.externalInfoUrl}" class="alert-link">Check some extra info</a>.
            </p>
            <div>File: ${violation.fileName}</div>
            <div>Method: ${violation.methodName}</div>
            <div>Class: ${violation.className}</div>
            <div>Lines between ${violation.beginLine} and ${violation.endLine}</div>
            </div>
        </c:forEach>
    </div>
</div>
<script src="<c:url value='/resources/js/jquery-3.2.1.min.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>
</body>
</html>
</title>
</head>
<body>

</body>
</html>
