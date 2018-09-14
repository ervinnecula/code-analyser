<%@tag description="Side Nav Template" pageEncoding="UTF-8"%>

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