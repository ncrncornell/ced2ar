$(document).ready( function() {
	baseURI = $('#meta_uri').html();
	registerListener();
});

function registerListener(){    
    $("#userComments").submit(function(){
    	var hasErrors = false;
    	$(".inlineError2").remove();
    	$(this).find(".requiredInput").each(function(){
    		var val = $(this).val();
        	if($.trim(val).length == 0)
        	{ 		
        		hasErrors = true;
        		$("<span class='inlineError2'>Required</span>").insertAfter($(this));
        	}
    	});
    	if(hasErrors){
    		return false;
    	}	
	});     
}