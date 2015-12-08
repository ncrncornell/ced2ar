$(document).ready(function () {
	var search = false;
	//If type search
	if($("#viewControls input[name=q]")){
		search = true;
	}
	
	$("#viewControls").submit(function() {
		//Will not run search and show spinner if query is empty
		var input =$("#viewControls input[name=q]");
		if(search && input.length &&  input.val().trim() === ""){
			return false;
		}else{
			showSpinner();
		}
    });  
	
	$(".sortable a").click(function (){
		showSpinner();
	});
	
	//For clicking on the navigation buttons
	$(".pagelinks .btn-group .btn").click(function(){
		showSpinner();
	});
});

/*Pops up CSS loading animation*/
function showSpinner(){
	$("#footer").css("opacity", "0.2");
	$("#load").html("<div id='loadingContent'><i class='fa fa-circle-o-notch fa-spin fa-4x'></i></div>");
	$("#load").css("position", "absolute");
	$("#load").css("display", "block");
	$("#load").css("margin", "auto");
	$("#load").css("min-height", $("html").height());
	$("#load").css("width", "100%");
	$("#load").css("background", "rgba(255,255,255,.8)");
	$("#load").css("z-index", "10");
	$("#load").css("font-style", "italic");
	$("#load").css("text-align", "center");
	
}

/*Hides of CSS loading animation*/
function hideSpinner(){
	$("#footer").css("opacity", "1");
	$("#load").html("");
	$("#load").css("display", "none");
}

/*Hides or displays loading animation*/
function toggleSpinner(){
	if($("#meta_codebook").is(':empty') && $("#filterCodebook").is(':empty'))
	{
		showSpinner();
	}else{
		hideSpinner();
	}	
}