$(document).ready(function () {		
			l = $('#load');
			baseURI = $('#meta_uri').html();
			actionButtonListener();
});



function actionButtonListener(){	
	
	$(".conflictButton").off("click");
	$(".conflictButton").on("click",function(){	
			loadAjax(this.href,true);	
			$(this).off("click");
			$("#info .close").click();
			return false;	
	});
	
	$(".pushToRemoteButton").off("click");
	$(".pushToRemoteButton").on("click",function(){	
			loadAjax(this.href,true);	
			$(this).off("click");
			$("#info .close").click();
			return false;	
	});
	
	$(".pullFromRemoteButton").off("click");
	$(".pullFromRemoteButton").on("click",function(){	
		loadAjax(this.href,true);	
		$(this).off("click");
		$("#info .close").click();
		return false;	
	});

	$(".ingestIntoBasexButton").off("click");
	$(".ingestIntoBasexButton").on("click",function(){
		loadAjax(this.href,true);	
		$(this).off("click");
		$("#info .close").click();
		return false;	
	});
	$(".replaceInvalidRemoteButton").off("click");
	$(".replaceInvalidRemoteButton").on("click",function(){
		loadAjax(this.href,true);	
		$(this).off("click");
		$("#info .close").click();
		return false;	
	});

}


function loadAjax(href){
    $.ajax({
    	type: "POST",
        url: href,
        success: function() {
        	alert("success");
            location.reload();
        },
    	error: function(){
    		alert("error");
            location.reload();
    	}
    });
}




