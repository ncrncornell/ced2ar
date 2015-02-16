$(document).ready(function () {		
			l = $('#load');
			baseURI = $('#meta_uri').html();
			editPasswordListener();
			bugReportEnableListener();
});

function bugReportEnableListener(){
	/*
	 * When Page loaded first set the bug report fields appropriately
	 */
	var selValue = $('input[id=enableRadio]:checked').val();
	if(selValue=='true')
		$(".bugReportField").show();
	else
		$(".bugReportField").hide();

	/*
	 * When radio button clicked; bug report fields appropriately
	 */
	
	$(".bugReportOn").on("click",function(){
		$(".bugReportField").show();
	});
	$(".bugReportOff").on("click",function(){
		$(".bugReportField").hide();
	});
}


/*Listens for clicking on edit button*/
function editPasswordListener(){	
	$(".editPasswordCell").off("click");
	$(".editPasswordCell").on("click",function(){	
			loadAjax(this.href,true);	
			$(this).off("click");
			$("#info .close").click();
			return false;	
	});
}

/*Makes to load editing contents*/
function loadAjax(href){
	$.ajax({
		cache: false,
        url : href,
        success : function(data) {
        	l.html(data);
        	l.addClass("loadShow");
        	$("body").css("overflow-y", "hidden");
        	$("#wrapper").css("padding-right", "30px");
        	var height = $("#wrapper").height();
        	var windowHeight = $(window).height();
        	if(windowHeight > height){
        		height = windowHeight;
        	}
        	l.css("min-height", height);
        	submitListener();
        }
    });
}

function hideAjax(){
	l.html("");
	l.removeClass("loadShow");
	l.css("min-height",0);
	$("#wrapper").css("padding-right", "15px");
	$("body").css("overflow-y", "scroll");
	editPasswordListener();
	
}

function submitListener(){
	
	notPressed = true;
	$(document).keydown(function(e){
	    if (e.keyCode == 27 && notPressed) { 
	        notPressed = false;
	        hideAjax();        
	     }     
	});
		
	$("#changeBaseXDBButton").click(function(){	   
    	var url =  "processBaseXDBChange?baseXDBUrl=" +$("#baseXDBUrl").val()+"&adminPassword=" + $("#adminPassword").val() + "&readerPassword=" + $("#readerPassword").val()+"&writerPassword="+$("#writerPassword").val();
        $.post( url, function( data,status ) {
        	$('#changeBaseXDBMsg').html(data);
        	});
	});
	
	$("#changePasswordButton").click(function(){	   
		var url =  "processPasswordChange?baseXDBUserId=" +$("#baseXDBUserId").val()+"&currentPassword=" + $("#currentPassword").val() + "&newPassword=" + $("#newPassword").val()+"&confirmPassword="+$("#confirmPassword").val();
        $.post( url, function( data,status ) {
        	$('#changePwdMsg').html(data);
        	});
	});

	$("#closeButton").click(function(){
		hideAjax();   
		return false;

	});
	
	$(".dialogClose").click(function(){
		hideAjax();
		return false;
	});
	
	l.click(function(){
		hideAjax();
	});
		
	$("#loadCoverInner").click(function(event) {
		event.stopPropagation();
	});
}