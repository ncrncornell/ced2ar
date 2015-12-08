$(document).ready( function() {
	baseURI = $('#meta_uri').html();
	
	//Shows specific tab on page reload
	var url = document.location.toString();
	if (url.match('#')) {
	    $('.nav-tabs a[href=#'+url.split('#')[1]+']').tab('show') ;
	} 

	$('.nav-tabs a').on('shown', function (e) {
	    window.location.hash = e.target.hash;
	});
	
	indexListener();
	upload();
	pdfListen();
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

function indexListener(){
	$(".indexForm").attr("action",baseURI+"/edit/index");
	$(".indexForm").change(function(){
		var form = this;
		$.ajax({
			  type: "POST",
			  cache: false, 
		      url: baseURI+"/edit/index",
		      data:  $(this).serialize(),
		      async: true,
		      success: function(response){}	  
		} );
	});
}

function pdfListen(){
	var pdfGenRunning = false;
	$("#newPDFs").click(function(){	
		if(!pdfGenRunning){
			pdfGenRunning = true;		
			var l = this;
			var linkHtml = $(this).html();
			$(this).html("<i class=\"fa fa-spinner fa-spin\"></i>");
			$(this).css("color","#333");
			$(this).css("font-size","1.5em");
			$(this).css("cursor","not-allowed");
			$.ajax({
				cache: false,
		        url : baseURI+"/edit/codebooks/generatepdf",
		        success : function(data) {
		    		$(l).html(linkHtml);
		    		$(l).css("color","#3886cc");
		    		$(l).css("font-size","1em");
		    		$(l).css("cursor","pointer");
		    		pdfGenRunning = false;
		        },
		        error:function(data) {
		    		$(l).html(linkHtml);
		    		$(l).css("color","#3886cc");
		    		$(l).css("font-size","1em");
		    		$(l).css("cursor","pointer");
		    		pdfGenRunning = false;
		        },
				
		    });
		}
		return false;
	});
}