<?php
session_start();

if(isset($_SESSION['dmt-username'])) {
	header('Location: index.php' ) ;
}
else {

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
	"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>Dataset Manager Tool</title>

	<!--<link rel="stylesheet" href="css/dataset.css"></link>-->
	<style type="text/css">
@import "js/dojo/resources/dojo.css";
@import "js/dijit/themes/claro/claro.css";

html, body{	
	width: 100%;	/* make the body expand to fill the visible window */
	height: auto;
	/*overflow: hidden;	/* erase window level scrollbars */
	padding: 0 0 0 0;
	margin: 0px 0 0px 0;
	font: 10pt Arial,Myriad,Tahoma,Verdana,sans-serif;
	background-color: #000000
}


.login-window{
	width:300px;
	height: 200px;
	padding: 10px;
	background: white;
	text-align: center;
	margin: 80px auto;
	border-radius: 6px 6px 6px 6px;
	box-shadow: 0px 2px 11px #fff;
	/*display: block;*/
}

.login-title{
	font-size: 2em;
	font-weight: bold;
	/*padding: 10px, 10px, 10px, 10px;*/
}

.conatiner {
	width:300px;
}

.login-row {
	text-align: left;
	display: inline;
	width: 300px;
}

.login-field{
	float:left:
	width: 200px;
	height: 30px;
}

.login-label{
	float:left;
	width: 100px;
	height: 30px;
}
	</style>
	<script type="text/javascript" src="js/dojo/dojo.js" charset="utf-8" djConfig="parseOnLoad: true"></script>
	<script type="text/javascript" charset="utf-8">
	      dojo.require("dojo.parser");
	      dojo.require("dijit.form.Button");
	      dojo.require("dijit.form.Form");
	      dojo.require("dijit.form.ValidationTextBox");

		function login() {
			dojo.xhrPost({
				form:dojo.byId("loginForm"),
				url:"controllers/login-controller.php",
				handleAs: "json",
				load: function(responseObject, ioArgs) {
					if(responseObject.status == "ERROR")
						dojo.byId("loginMessage").innerHTML = responseObject.response;
					else if(responseObject.status == "OK") {
						dojo.byId("loginMessage").innerHTML = responseObject.response;
						setTimeout("window.location.replace('index.php'); ", 1000);
					}
					else {
						dojo.byId("loginMessage").innerHTML = "A server error has occured";
					}
					return responseObject;
				},
				error: function(responseObject, ioArgs) {
					dojo.byId("loginMessage").innerHTML = "A server error has occured";
					return responseObject;
				}
			});
		}
	</script>
</head>

<body class="claro" role="application">
	<div class="login-window">
		<div class="container">
			<span align="center" class="login-title"> Dataset Manager Tool</span>
		</div>
		<div class="conainer" style="height:20px"></div>
		<form method="post" action="" onSubmit="return false;" dojoType="dijit.form.Form" name="loginForm" id="loginForm">
		<div class="container">
			<div class="login-label">Username</div>
			<div class="login-field"><input type="text" dojoType="dijit.form.ValidationTextBox" name="username" style="width: 180px; float:left;" required="true" invalidMessage="Required"/></div>
		</div>
		<div class="container">
			<div class="login-label">Password</div>
			<div class="login-field"><input type="password" dojoType="dijit.form.ValidationTextBox" name="password" style="width: 180px; float:left;" required="true" invalidMessage="Required"/></div>
		</div>
		<div class="container">
			<button type="submit" onClick="login" dojoType="dijit.form.Button">Login</button>
		</div>
		</form>
		<div id="loginMessage" style="color:red"></div>
	</div>
</body>

<?php
}
?>
