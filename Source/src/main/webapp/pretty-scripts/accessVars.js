$(document).ready(function () {
	varCount = $(".cc").length;
	var sa = $("#results th").first();
	$(sa).html("<label title='Select All'><input id='sa' name='sa' value='true' type='checkbox'></label>");
	saListen();
	sListen();
	toggleDisableVS();
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
 */
function saListen(s){	
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