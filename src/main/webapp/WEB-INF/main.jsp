<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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
<div class="row mt-3 custom-wrapper">
    <div class="container">
        <div class="row">
            <div class="col"></div>

            <div class="col align-self-center">
                <h1 class="custom-center">Type in a GitHub user</h1>
                <div class="container">

                    <form class="form-signin" name="f" action="/repos" method="GET">
                        <input type="text" name="username" id="username" class="form-control mt-3 ${isInvalid}" placeholder="GitHub user name" required autofocus>
                        <div style="color:red">${errorMessage}</div>

                        <button class="btn btn-lg btn-primary btn-block btn-signin mt-3" type="submit">
                            Get repos
                        </button>
                    </form>
                </div>
            </div>

            <div class="col"></div>
        </div>
    </div>
</div>
<script src="<c:url value='/resources/js/jquery.js' />"></script>
<script src="<c:url value='/resources/js/bootstrap.min.js'  />"></script>
</body>
</html>