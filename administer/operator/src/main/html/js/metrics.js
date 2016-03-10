/*
 * Javascript for metric generation on the overview tab
 */


function showTooltip(x, y, contents) {
    $('<div id="tooltip">' + contents + '</div>').css( {
        position: 'absolute',
        display: 'none',
        top: y + 9,
        left: x + 9,
        border: '1px solid #fdd',
        padding: '2px',
        'background-color': '#fee',
        opacity: 0.90
    }).appendTo("body").fadeIn(200);
}


function refreshIngestByDataset() {
	$("#operator-pie-granulesByDataset").empty();
	$("#operator-pie-granulesByDataset").append("<img src='images/spinner.gif'/>");
	$.ajax({
		url: "controllers/statisticsController.php",
		data: {oper: "count_by_dataset"},
		type: "GET",
		success: function(responseObject) {
			$("#operator-pie-granulesByDataset").empty();
			$.plot($("#operator-pie-granulesByDataset") , responseObject, 
			{
				series: {
					pie: {
						show: true,
						combine: {
                    		threshold: 0.005
                		}
                	}
				},
				legend: {show: true},
				grid: {
					hoverable: true,
					clickable: true
				}
			});
			
			var previousSlice = "";
			$("#operator-pie-granulesByDataset").bind("plothover", function(event, pos, obj) {
				if(obj){
					var percent = parseFloat(obj.series.percent).toFixed(2);
					var granules = obj.series.data[0][1];
					var label = obj.series.label;
					$(".operator-pie-info").html("<span style='font-weight:bold;'>"+label+" - "+granules+" granules ("+percent+"%)</span>")
					if($("#tooltip").length <= 0 || previousSlice != label) {
						$("#tooltip").remove();
						previousSlice = label;
						showTooltip(pos.pageX, pos.pageY, label+" - "+granules+" granules ("+percent+"%)");
					}
					else {
						$("#tooltip").css("top", pos.pageY+9);
						$("#tooltip").css("left", pos.pageX+9);
					}
					
				}
				else $("#tooltip").remove();
			});
		},
		error: function() {
			$("#operator-pie-granulesByDataset").empty();
			$("#operator-pie-granulesByDataset").append("<span>SOLR Server Error Occured</span>");
		}
	});
}

function refreshSizeByDataset() {
	$("#operator-pie-sizeByDataset").append("<img src='images/spinner.gif'/>")
	$.ajax({
		url: "controllers/statisticsController.php",
		data: {oper: "size_by_dataset"},
		type: "GET",
		success: function(responseObject) {
			$("#operator-pie-sizeByDataset").empty();
			$.plot($("#operator-pie-sizeByDataset") , responseObject, 
			{
				series: {
					pie: {
						show: true,
						combine: {
                    		threshold: 0.02
                		}
                	}
				},
				legend: {show: true},
				grid: {
					hoverable: true,
					clickable: true
				}
			});
		},
		error: function() {
			
		}
	});
}


var month_lookup = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

function refreshMonthlyIngestion() {
	$("#operator-monthlyIngestion").empty();
	$("#operator-monthlyIngestion").append("<img src='images/spinner.gif'/>")
	$.ajax({
		url: "controllers/statisticsController.php",
		data: {oper: "count_by_month"},
		type: "GET",
		success: function(responseObject) {
			$("#operator-monthlyIngestion").empty();
			$.plot($("#operator-monthlyIngestion"), [responseObject], {
				xaxis : {
					mode : "time",
					minTickSize: [1, "month"],
					//min : (new Date(1999, 1, 1)).getTime(),
					//max : (new Date(2000, 1, 1)).getTime()
				},
				series: {
                   lines: { show: true },
                   points: { show: true }
               },
               grid: { hoverable: true, clickable: true },
			});
			$("#operator-monthlyIngestion").bind("plothover", function (event, pos, item) {
	            if (item) {
	                if (previousPoint != item.dataIndex) {
	                    previousPoint = item.dataIndex;
	                    
	                    $("#tooltip").remove();
	                    var time = new Date(item.datapoint[0]),
	                        y = item.datapoint[1]
	                    
	                    showTooltip(item.pageX, item.pageY,
	                                month_lookup[time.getMonth()] + " "+ time.getFullYear()+": "+y+" granules");
	                }
	            }
	            else {
	                $("#tooltip").remove();
	                previousPoint = null;            
	            }
		    });
		},
		error: function() {
			$("#operator-monthlyIngestion").empty();
			$("#operator-monthlyIngestion").append("<span>SOLR Server Error Occured</span>");
		}
	});	
}

$(function(){

	
	//refreshSizeByDataset();

	
});
