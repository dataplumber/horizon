var Ajax;

var last_cyc;
var last_auth;
var last_cbox;

if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}


// jQuery UI Dialog    
function init(){
	$('#dialog').dialog({
	    autoOpen: false,
	    width: 400,
	    modal: true,
	    resizable: false,
	    draggable: false,
	    buttons: {
	        "Confirm": function() {
	        	$(this).dialog("close");
	        	$.ajax({type: "POST", 
	              url: "approve", 
	              data: 'cycle=' + last_cycle +'&auth=' + last_auth +'&cb=' + last_cbox+ '&check=' +true, 
	              success: function(msg){ 
	              	var options = '';
	                  for (var i = 0; i < msg.length; i++) {
	                    options += '<option value="' + msg[i].id + '">' + msg[i].name + '</option>';
	                  }
	                  $("select#testSeries").html(options);
	              } 
	           }); 
	        	last_cbox = null;
	        	last_cycle = null;
	        	last_auth = null;
	        	
	        },
	        "Cancel": function() {
	        	//uncheck checkbox
	        	$("#" +last_cycle+"_"+last_auth+"_"+last_cbox).removeAttr("checked"); // uncheck the checkbox or radio
	        	last_cbox = null;
	        	last_cycle = null;
	        	last_auth = null;
	            $(this).dialog("close");
	            
	        }
	    }
	});
	
	$('form#testconfirmJQ').submit(function(e){
	    e.preventDefault();
	
	    $("p#dialog-combo").html($("input#emailJQ").val());
	    $('#dialog').dialog('open');
	});
}

function approveGDR(cycle,cbox,auth){
	var ckd = false;
	if($("#" +cycle+"_"+auth+"_"+cbox).attr("checked") == 'checked'){
		ckd = true;
	}
	
	if($("#" +cycle+"_"+auth+"_CNES").attr("checked") == 'checked' && $("#" +cycle+"_"+auth+"_NASA").attr("checked") == 'checked'){
		
		last_cbox = cbox;
		last_cycle = cycle;
		last_auth = auth;
		
		$("p#dialog-combo").html('Cycle: ' + cycle + ', Author: ' + auth);
	    $('#dialog').dialog('open');
	}else{
	
	$.ajax({type: "POST", 
        url: "approve", 
        data: 'cycle=' + cycle +'&auth=' + auth +'&cb=' + cbox+ '&check=' +ckd, 
        success: function(msg){ 
        	var options = '';
            for (var i = 0; i < msg.length; i++) {
              options += '<option value="' + msg[i].id + '">' + msg[i].name + '</option>';
            }
            $("select#testSeries").html(options);
        } 
     });
	}
}
