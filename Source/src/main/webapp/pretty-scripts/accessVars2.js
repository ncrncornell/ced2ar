var baseHandle = null;
var version = null;

$(document).ready(function () {
	
	baseURI = $('#meta_uri').html();
	baseHandle = $("input[name='baseHandle']").val();
	version = $("input[name='version']").val();
	
	varCount = $(".cc").length;
	var sa = $("#results th").first();
	$(sa).html("<label title='Select All'><input id='sa' name='sa' value='true' type='checkbox'></label>");
	saListen();
	sListen();
	toggleDisableVS();
	submitListen();
	saXpathListen();
	sXpathlisten();
});

/**
 * Disables/Enables submit button
 */
function toggleDisableVS(){
	if($("#accessvars input:checkbox:checked").length > 0){
		$("#accessBtn").removeClass("disabled");
	}else{
		$("#accessBtn").addClass("disabled");
	}
}

/**
 *Listens for select all event 
 * @param s
 */
function saListen(){	
	$("#sa").click(function(){	
		if($(this).is(":checked")){
            $(".cc input").prop("checked", true);
            $(".even").addClass("selectedRow");
            $(".odd").addClass("selectedRow");
        }else{
            $(".cc input").prop("checked", false);
            $(".even").removeClass("selectedRow");
            $(".odd").removeClass("selectedRow");
        }
		toggleDisableVS();
	});
}

/**
 * Listens for xpath select all event
 * @param s
 */
function saXpathListen(){
	$("input[name='subpaths_all']").click(function(){	
		if($(this).is(":checked")){
            $("input[name='subpaths']").prop("checked", true);
        }else{
            $("input[name='subpaths']").prop("checked", false);
        }
	});
}

function sXpathlisten(){
	$("input[name='subpaths']").change(function(){
		if($(this).is(":checked")){
			if(!$("input[name='subpaths_all']").is(":checked")){
				if($("input[name='subpaths']").length === $("input[name='subpaths']:checked").length){
					$("input[name='subpaths_all']").prop("checked", true);
				}
			}
        }else{
        	$("input[name='subpaths_all']").prop("checked", false);
        }	
	});
}

/**
 *Listens for select event 
 */
function sListen(){	
	$(".cc input").change(function(){
		var row = $(this).parent().parent().parent();
		if($(this).is(":checked")){
			var curCount = $(".cc :checked").length;
			//If user selects all vars individually, select all should be checked
			if(curCount === varCount){
				$("#sa").prop('checked', true);
			}
			$(row).addClass("selectedRow");
        }else{
        	//If select all is true, and a single item is check, uncheck select all
        	$("#sa").prop('checked', false);
            $(row).removeClass("selectedRow");
        }	
		toggleDisableVS();
	});
}

function submitListen(){
	$("#accessvars").submit(function(){		
		$("#accessBtn").prop("disabled",true);
		$("#accessBtn").after(" <i id='accessLoadingSpinner' class='fa fa-circle-o-notch fa-spin fa-2x'></i>");
		var uri = baseURI+"/edit/codebooks/"+baseHandle+"/v/"+version+"/accessvars2";	
		$("#accessFeedback").empty();		
		
		$.ajax({
	        cache: false,
	        type: "POST",
	        data:$("#accessvars").serialize(),
	        url: uri,
	        success: function(data){      
	        	$("#accessLoadingSpinner").remove();
	        	$("#accessFeedback").html("Access levels updated");
	        	$("#accessBtn").prop("disabled",false);	
	        }, 
	        error: function(data, textStatus, errorThrown){    	        		        	
	        	console.log(data.status);		        	
	        	if(data.status < 400){
	        	//False negative	
	        		$("#accessLoadingSpinner").remove();
	        		$("#accessFeedback").html("Access levels updated");
	        		$("#accessBtn").prop("disabled",false);
	        	}else{
	        	//Error
	        		$("#accessLoadingSpinner").remove();
	        		$("#accessFeedback").html("An error occured, access levels not updated");
	        		$("#accessBtn").prop("disabled",false);
	        	}
	        }
		});	
		
		return false;
	});
	
}