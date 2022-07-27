<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Utah Openid demo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <style>
        body {
            padding-top: 5px; /* 60px to make the container go all the way to the bottom of the topbar */
        }
        .navbar {
            width: 350px;
        }
        .list {
            list-style-type: none;
            padding-left: 0;
        }
        .list li {
            padding: 10px;
            border-bottom: 1px solid #dddddd;
        }
        .list li:last-child {
            border: 0;
        }
        .list li:nth-child(odd) {
            background-color: #bdf6e9;
        }
        .container {
            color: green;
            font-size: 50px;
            text-align: center;
        }
    </style>
</head>

<body>

<!-- 
				<sec:authorize access="hasRole('ROLE_AGENCY')">LFA ONLY</sec:authorize>
				<sec:authorize access="hasRole('ROLE_LFA')">LFA ONLY</sec:authorize>
				<sec:authorize access="hasRole('ROLE_ADMIN')">ADMIN ONLY</sec:authorize>
				<sec:authorize access="hasRole('ROLE_GOPB')">GOPB ONLY</sec:authorize>
 -->

<div class="navbar">
    <ul class="list">
        <li>Email: ${user.umdEmail}</li>
        <li>Role: ${user.authorities}</li>
        <li><a href='secure.html'>Admin Page</a></li>
    </ul>
</div>


<div class="container">
      LOGIN SUCCESSFUL
</div> <!-- /container -->


<script type="text/javascript">
	//alert('hello');
</script>

<jsp:include page="/version.html"></jsp:include>
</body>
</html>
