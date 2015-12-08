$(document).ready( function() {
	baseURI = $('#meta_uri').html();
	checkForm();
});

function checkForm(){
	$("#createGroup").submit(function(){
    	var hasErrors = false;
    	$(".inlineError").remove();
    	$(".inlineError2").remove();
    	$(this).find(".requiredInput").each(function(){
    		var val = $(this).val();
        	if($.trim(val).length === 0){ 		
        		hasErrors = true;
        		if($(this).hasClass("form-control")){
        			$("<span class='inlineError2'>Required</span>").insertAfter($(this));
        		}else{
        			$("<span class='inlineError'>Required</span>").insertAfter($(this));
        		}
        	}
    	});
    	if(hasErrors){
    		return false;
    	}	
	});
}