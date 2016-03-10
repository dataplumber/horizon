<?php
session_start();

if(isset($_SESSION['operator-username']) && isset($_SESSION['operator-fullname']) && file_exists("config/horizondomain.xml")) {
	header('Location: index.php' ) ;
}
else {
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>DMAS Operator Tool</title>
<link type="text/css" href="css/smoothness/jquery-ui-1.8.4.custom.css" rel="Stylesheet" />
<link rel="stylesheet" href="css/operator.css"></link>

<script type="text/javascript" src="js/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery-ui-1.8.4.custom.min.js"></script>


<script type="text/javascript" charset="utf-8">

function login() {
	var data = {username: $("[name=username]").val(), password: $("[name=password]").val()};
	//console.log(data);
	$.ajax({
		url: "controllers/loginController.php",
		type: "POST",
		data: data,
		success: function(responseObject) {
				if(responseObject.status == "ERROR") {
					$("#operator-loginMessage").text(responseObject.response)
					$("#operator-loginMessage").show("fast");
					setTimeout("$('#operator-loginMessage').hide('fast'); ", 3000);
				}
				else if(responseObject.status == "OK") {
					$("#operator-loginMessage").text(responseObject.response)
					$("#operator-loginMessage").css("color", "blue");
					$("#operator-loginMessage").show("fast");
					setTimeout("window.location.replace('index.php'); ", 1000);
				}
				else {
					$("#operator-loginMessage").text("A server error has occured")
					$("#operator-loginMessage").show("fast");
				}
			},
		error: function(responseObject) {
				$("#operator-loginMessage").text("A server error has occured")
				$("#operator-loginMessage").show("fast");
			}
	});
}

function constructDomain() {
	var username = $("[name=username]").val();
	var password = $("[name=password]").val();
	var url = $("[name=url]").val();
	
	if(username == "" || password == "" || url == "") {
		$("#operator-loginMessage").text("Fill in all fields!")
		$("#operator-loginMessage").show("fast");
		setTimeout("$('#operator-loginMessage').hide('fast'); ", 3000);		
	}
	else {
		var data = {oper: "update", username: username, password: password, url: url};
		$.ajax({
			url: "controllers/domainController.php",
			type: "POST",
			data: data,
			success: function(responseObject) {
					if(responseObject.status == "ERROR") {
						$("#operator-loginMessage").text(responseObject.response)
						$("#operator-loginMessage").show("fast");
						setTimeout("$('#operator-loginMessage').hide('fast'); ", 3000);
					}
					else if(responseObject.status == "OK") {
						$("#operator-loginMessage").text(responseObject.response)
						$("#operator-loginMessage").css("color", "blue");
						$("#operator-loginMessage").show("fast");
						setTimeout("window.location.replace('index.php'); ", 1000);
					}
					else {
						$("#operator-loginMessage").text("A server error has occured")
						$("#operator-loginMessage").show("fast");
					}
				},
			error: function(responseObject) {
					$("#operator-loginMessage").text("A server error has occured. Please check to see a manager is running at the given URL.")
					$("#operator-loginMessage").show("fast");
				}
		});
	}
}

$(function() {
	$("#loginButton").click(login);
	$("#setupButton").click(constructDomain);
});
</script>

</head>

<body>
	
<?php if(file_exists("config/horizondomain.xml")): ?>
<div id="operator-loginContainer">
	<div id="operator-main-login">
		<div id="operator-top-login" class="operator-roundTop">
			DMAS Operator Tool
		</div>
		<div id="operator-login">
			<form method="post" action="" onSubmit="return false;" name="loginForm" id="loginForm">
				<table style="margin:0 auto;">
					<tr>
						<td>Username</td><td>
						<input class="operator-input" type="text" name="username"/>
						</td>
					</tr>
					<tr>
						<td>Password</td><td>
						<input class="operator-input" type="password" name="password"/>
						</td>
					</tr>
					<tr>
						<td colspan=2 style="text-align:center;">
						<button type="submit" id="loginButton">
							Login
						</button>
					</tr>
				</table>
			</form>
			<div id="operator-loginMessage">
				Success!
			</div>
		</div>
		<div id="operator-bottom" class="operator-roundBottom"></div>
	</div>
	<!-- operator-main -->
	<span class="operator-versionInfo">Version 5.0.0d</span>
</div>
<!-- operator-container -->
<?php else: ?>
<!-- Display this to generate the domain file on first login or domain delete -->
<div id="operator-loginContainer" class="operator-setup">
	<div id="operator-main-login" class="operator-setup">
		<div id="operator-top-login" class="operator-roundTop">
			DMAS Operator Tool Setup
		</div>
		<div id="operator-login">
			<div class="operator-welcome">
				<span style='text-align: center; font-weight: bold; display: block; margin-bottom: 10px; font-size: 12px;'>Welcome to the DMAS Operator tool!</span>
				To begin, import a domain file from an instance of manager. Supply your username and password along with the url (hostname with port) of a running manager and the setup will do the rest!</span>
			</div>
			<form method="post" action="" onSubmit="return false;" name="loginForm" id="loginForm">
				<table style="margin:0 auto;">
					<tr>
						<td>Username</td><td>
						<input class="operator-input" type="text" name="username"/>
						</td>
					</tr>
					<tr>
						<td>Password</td><td>
						<input class="operator-input" type="password" name="password"/>
						</td>
					</tr>
					<tr>
						<td>Manager URL</td><td>
						<input class="operator-input" type="text" name="url"/>
						</td>
					</tr>
					<tr>
						<td colspan=2 style="text-align:center;">
						<button type="submit" id="setupButton">
							Create Domain File
						</button>
					</tr>
				</table>
			</form>
			<div id="operator-loginMessage">
				Success!
			</div>
		</div>
		<div id="operator-bottom" class="operator-roundBottom"></div>
	</div>
	<!-- operator-main -->
	<span class="operator-versionInfo">Version 4.0.0</span>
</div>
<?php endif; ?>
</body>
</html>
<?php
}
?>
