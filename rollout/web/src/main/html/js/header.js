/*
* header.js - Contains functions for header manipulation
*/

var infoToggle=true;

function toggleUserInfo() {
	if(infoToggle) {
		dojo.query(".user-info").style("display", "block");
		dojo.query("#banner .user-name").style("background", "#fff");
		dojo.query("#banner .user-name").style("color", "#000");
		infoToggle = false;
	}
	else {
		dojo.query(".user-info").style("display", "none");
		dojo.query("#banner .user-name").style("background", "#333");
		dojo.query("#banner .user-name").style("color", "#fff");
		infoToggle = true;	
	}
	return false;
}

function disableInfoPopup(event) {
	if(!infoToggle && event.target.className != "user-name") {
		dojo.query(".user-info").style("display", "none");
		dojo.query("#banner .user-name").style("background", "#333");
		dojo.query("#banner .user-name").style("color", "#fff");
		infoToggle = true;	
	}
}

dojo.addOnLoad(function(){
	
	dojo.query("body").onclick(disableInfoPopup);
	
});
