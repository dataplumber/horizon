<div class="user-info">
		<?php
			//print "<span style='font-weight:bold;'>".$_SESSION['dmt-username']."</span><br/>";
			if(!$_SESSION['dmt-admin'])		      
				print "Role: Data Engineer";
			else print "Role: Admin";
			print "<br/><div style='padding-top:20px;'><a style='color:#3366CC; text-decoration:none;' href='logout.php'>Logout</a></div>";
		 ?>		
</div>
<div dojoType="dijit.layout.ContentPane" id="banner" region="top">

	<a class="user-name" onclick="toggleUserInfo();"><?php print $_SESSION['dmt-username']; ?></a>
	<div class="short-name">
	<?php			
	if(isset($datasetManifest)) {
		$shortName = getFieldValue($datasetManifest, "dataset_shortName");
		if(strlen($shortName) > 50)
			$shortName = substr($shortName, 0, 50)."...";
		if($shortName != "")
			print $shortName;
	}
	?>
	</div>
	
	<a href="index.php" class="home-link"></a>
	<div class="title">Dataset Manager Tool <span style="font-size:0.7em">v5.1.0</span></div>	
</div>
	<?php 
		/*$shortName = getFieldValue($datasetManifest, "dataset_shortName");
		if(strlen($shortName) > 50)
			$shortName = substr($shortName, 0, 50)."...";
		if($shortName != "")
			print "<div style=\"text-align:left; padding-top:20px; color:#fff\">Dataset: $shortName</div>";*/
	?>
