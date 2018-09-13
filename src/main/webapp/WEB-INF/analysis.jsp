<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Analysis</title>
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
    <div class="col-md-2 pl-4">
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
    <div class="col-md-10">
        <div class="container">
            <div class="row">
                <div class="col">
                    <form action="/commits" method="GET" id="commits-form">
                        <input type="hidden" value="${repositoryName}" name="repositoryName">
                        <input type="hidden" value="${username}" name="username">
                        <a href="javascript:{}"
                           onclick="document.getElementById('commits-form').submit(); return false;">Analyze commits</a>
                    </form>
                </div>
                <div class="col" class="alert alert-dismissible alert-warning">
                    You have ${violationsCount} static analysis violations.
                    <form action="/static" method="GET">
                        <input type="hidden" value="${repositoryName}" name="repositoryName">
                        <input type="hidden" value="${username}" name="username">
                        <button type="submit">
                            View them in detail.
                        </button>
                    </form>
            </div>
            </div>
        </div>
    </div>
</div>
<script src="<c:url value='/resources/js/jquery-3.2.1.min.js'/>"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
</body>
</html>
