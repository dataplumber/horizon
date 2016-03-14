<?php
session_start();

if(!isset($_SESSION['operator-username']) || !file_exists("config/horizondomain.xml")) {
	header('Location: login.php' ) ;
}
else {
  include("controllers/operatorLibrary.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta charset="utf-8" />
		<title>DMAS Operator Tool</title>
		<link type="text/css" href="css/smoothness/jquery-ui-1.8.4.custom.css" rel="Stylesheet" />
		<link type="text/css" href="css/ui.jqgrid.css" rel="Stylesheet" />
		<link rel="stylesheet" href="css/operator.css"/>
		<script type="text/javascript" src="js/jquery/jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="js/jquery/jquery-ui-1.8.4.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery/i18n/grid.locale-en.js"></script>
		<script type="text/javascript" src="js/jquery/jquery.jqGrid.min.js"></script>
		<script type="text/javascript" src="js/flot/jquery.flot.js"></script>
		<script type="text/javascript" src="js/flot/jquery.flot.pie.js"></script>
		<script type="text/javascript">
<?php
    print "                var managers = [";
    $numManagers = count($OPERATOR_MANAGERS);
    foreach($OPERATOR_MANAGERS as $id => $manager) {
        print "{\"name\":\"".$manager['name']."\", \"url\": \"".$manager['url']."\"}";
        if($id < $numManagers-1)
            print ",";
    }
    print "];\n";
?>
		</script>
		<script type="text/javascript" charset="utf-8" src="js/operator.js"></script>
		<!--<script type="text/javascript" charset="utf-8" src="js/zookeeper.js"></script>-->
		<script type="text/javascript" charset="utf-8" src="js/metrics.js"></script>
	</head>
<body>

<div id="operator-container">
<div class="operator-loginInfo">
Logged in as <span style="font-weight:bold"><?php print $_SESSION['operator-username']; ?></span>&nbsp;&nbsp;&nbsp;
<a href="logout.php">Logout</a><br/>
<!--<a href="images/manager_states.jpg" style="color:#fff;">Click here to view the state table diagram</a>-->
</div>

<div id="operator-main">
<div id="operator-top" class="">
DMAS Operator Tool
</div>
<div id="operator-tabs">
	<ul>
		<li><a href="#operator-metrics">Overview</a></li>
		<li><a href="#operator-granuleManagement">Granule/Dataset Management</a></li>
		<li><a href="#operator-storageManagement">Storage/Engine Management</a></li>
		<!--<li><a href="#operator-engineManagement">Engine Listing</a></li>-->
		<?php if($_SESSION['operator-admin']) : ?>
		<li><a href="#operator-userManagement">Utilities</a></li>
		<?php endif; ?>
		<!--<li><a href="#operator-stateTable">State Table Diagram</a></li>-->
		<!--<li><a href="#sigevent">SigEvent</a></li>-->
	</ul>
	<div id="operator-metrics">
		<div class="operator-sidebar">
			<div class="operator-sidebar-section operator-section">
				<h1>System Status</h1>
				<div class="operator-content operator-status-container">
				</div>
			</div>	
			<div class="operator-sidebar-section operator-section operator-summaryInfo ">
				<h1>Summary Of Granules In Manager</h1>
				<div class="operator-content operator-granuleSummary-Tabs">
					<ul>
						<!--<li><a href="#operator-manager1">Manager 1</a></li>
						<li><a href="#operator-manager2">Manager 2</a></li>
						<li><a href="#operator-manager3">Manager 3</a></li>-->
					</ul>
					<!--
					<div id="operator-manager1">
						<div id="operator-summaryInfoArea"></div>
						<button id="operator-refreshSummary">Refresh Summary</button>
					</div>		
					<div id="operator-manager2">
						<div id="operator-summaryInfoArea"></div>
						<button id="operator-refreshSummary">Refresh Summary</button>
					</div>	
					<div id="operator-manager3">
						<div id="operator-summaryInfoArea"></div>
						<button id="operator-refreshSummary">Refresh Summary</button>
					</div>
					-->				
				</div>
				
			</div>	
			<div class="operator-sidebar-section operator-section">
				<h1>Storage Summary</h1>
				<div class="operator-content operator-storage-summary-container">

				</div>
			</div>	
		</div>
		<div class="operator-main-with-sidebar">
			<div class="operator-section">
				<h1>Monthly Ingestion (For Last 30 Months)</h1>
				<div class="operator-content">
					<div id="operator-monthlyIngestion"><button onclick='refreshMonthlyIngestion();'>Generate Statistics</button></div>
				</div>
			</div>	
			<div class="operator-section">
				<h1>Granules By Dataset (All Time)</h1>
				<div class="operator-content">
					<div id="operator-pie-granulesByDataset"><button onclick='refreshIngestByDataset();'>Generate Statistics</button></div>
					<div class="operator-pie-info"></div>
				</div>
			</div>
			<div class="operator-section" id="operator-section-priority">
				<h1>Priority Overview</h1>
				<div class="operator-content operator-priority-tabs">
					<ul></ul>
				</div>
				<div style="clear:both;"></div>
			</div>
		</div>
		<div style="clear:both;"></div>
	</div>
	<div id="operator-granuleManagement" class="operator-section">
		<div id="operator-granuleTabs">
			<ul>
				<!--<li><a href="#operator-granuleFed-1">Manager 1</a></li>
				<li><a href="#operator-granuleFed-1">Manager 2</a></li>
				<li><a href="#operator-granuleFed-1">Manager 3</a></li>-->
			</ul>
			<!--<div id="operator-granuleFed-1">
				<div class="operator-section" style="margin-top:10px;">
					<h1>Granule Status (Times in GMT)</h1>
					<div class="operator-granuleList" class="">
						<table id="operator-granuleGrid"></table>
						<div id="operator-granulePager"></div>
					</div>
				</div>
				<div class="operator-section">
					<h1>Product Management</h1>
					<div class="operator-productList" class="">
						<table id="operator-productGrid"></table>
						<div id="operator-productPager"></div>
					</div>
				</div>
			</div>
            -->
		</div>

	</div>
	<div id="operator-storageManagement">
		<div style="clear:both;"></div>
	</div>
	<?php if($_SESSION['operator-admin']): ?>
	<div id="operator-userManagement">
		<div class="operator-section">
			<h1>Operator Interface Utilities</h1>
			<div class="operator-content">
				<div class="operator-utility-container">
					<button class="operator-utility-button operator-recreate-domain-button skip"><img src="images/view_refresh.png"/></button>
					<span class="operator-utility-text">Recreate the configuration domain file from the default manager currently configured. Page will refresh upon successful recreation of the file.<br/><br/>Note: To set the operator tool to its default state, delete the "horizondomain.xml" file in the config directory.</span>
					<div style="clear:both;"></div>
				</div>
			</div>
		</div>
	</div>
	<?php endif; ?>
	<!--<div id="operator-stateTable">
		<div style="clear:both;"><img src="images/manager_states.jpg" width="100%"/></div>
	</div>-->
</div>
<div id="operator-bottom" class="operator-roundBottom">
</div>

</div> <!-- operator-main -->
<span class="operator-versionInfo">Version 5.1.0</span>
</div> <!-- operator-container -->

<div id="operator-dialog" title="Attention:"></div>
<div id="operator-editGranuleDialog" title="Modify Granules">
	<div id="operator-editGranuleInfo"></div>
	<form>
	<table>
	   <tr>		
		<td>Current State</td>
		<td><select id="operator-editGranuleStateSelect" name="operator-editGranuleStateSelect">
			<option>ABORTED</option>
			<option>PENDING_STORAGE</option>
			<option>PENDING_ARCHIVE_STORAGE</option>
			<option>PENDING</option>
			<option>ASSIGNED</option>
			<option>STAGED</option>
			<option>INVENTORIED</option>
			<option>PENDING_ARCHIVE</option>
			<option>ARCHIVED</option>
		</select></td>	
	   </tr>
	   <tr>
		<td>Current Lock</td>
		<td><select id="operator-editGranuleLockSelect" name="operator-editGranuleLockSelect">
			<option>RESERVED</option>
		</select></td>
	   </tr>
	   <tr>
		<td>Retries (Positive integers only)</td>
		<td><input type="text" id="operator-editGranuleRetries" name="operator-editGranuleRetries" value="" class="text ui-widget-content ui-corner-all" /></td>
	   </tr>
	</table>
	</form>
</div>
<div id="operator-editProductDialog" title="Modify Products">
	<div id="operator-editProductInfo"></div>
	<form>
	<table style="width:100%;">
	   <tr>		
			<td>Operator Defined Latency (in hours)</td>
			<td><input type="text" id="operator-editProductLatency" name="operator-editProductLatency" value="" class="text ui-widget-content ui-corner-all" /></td>
	   </tr>
	   <tr>		
			<td>Priority</td>
			<td>
				<select id="operator-editProductPriority" name="operator-editProductPriority">
					<option>HIGH</option>
					<option>NORMAL</option>
					<option>LOW</option>			
				</select>
			</td>
	   </tr>
	</table>
	</form>
</div>

</body>
</html>
<?php
}
?>
