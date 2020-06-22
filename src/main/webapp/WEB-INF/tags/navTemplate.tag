<%@tag description="Side Nav Template" pageEncoding="UTF-8"%>

<div class="col-md-2">
    <ul class="list-group">
        <li class="list-group-item d-flex justify-content-between align-items-center">
            <form action="/repos" method="GET" id="username-form">
                <input type="hidden" value="${repoOwner}" name="repoOwner"/>
                <a href="javascript:{}"
                   onclick="document.getElementById('username-form').submit(); return false;">${repoOwner}'s
                    repos</a>
            </form>
        </li>

        <li class="list-group-item d-flex justify-content-between align-items-center">
            <form action="/main" method="GET" id="return-to-main-form">
                <a href="#" onclick="document.getElementById('return-to-main-form').submit();">Choose another user</a>
            </form>
        </li>
    </ul>
</div>