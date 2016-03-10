// CONFIG FILE
//
//
// MODIFY THE FOLLOWING VALUES TO CONFIGURE
// Note: BASE_URL is the base directory for the sigevent server
//	All other values are locations for particular sig event API calls
//

//var BASE_URL = "http://lanina.jpl.nasa.gov:8100/sigevent/";  	//for running with a proxy server from anywhere
//var BASE_URL = "http://localhost:8100/sigevent/"; 	//for running locally
var BASE_URL = "/sigevent/"				//for running with grails (default)

var EVENTS = "events/";
var NOTIFIES = "notifies/";
var GROUPS = "groups/";
var USERS = "users/";
var LIST = "list?format=DOJO_JSON&";
var SEARCH = "search?format=DOJO_JSON&";
var CREATE = "create?format=JSON&";
var DELETE = "delete?format=JSON&";
var UPDATE = "update?format=JSON&";
var AUTH = "auth?format=JSON&";
var USER_UPDATE = "update?format=JSON";
var USER_SHOW = "show?format=JSON";
var CATEGORY_SHOW = "showByCategory?format=JSON";
