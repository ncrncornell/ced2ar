$(document).ready( function() {
	upload();
});

function upload(){
	$(".fileUploadBtn").click(function() {
    	$(this).next(".fileUploadHidden").click();
    	return false;
    });
    
    $(".fileUploadHidden").change(function(){
    	var filename = $(this).val().split('\\').pop();
    	$(this).next(".fileUploadDisplay").val(filename);
	}); 
    
    $("form").submit(function(){
    	var hasErrors = false;
    	$(".inlineError").remove();
    	$(".inlineError2").remove();
    	$(this).find(".requiredInput").each(function(){
    		var val = $(this).val();
        	if($.trim(val).length === 0)
        	{ 		
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