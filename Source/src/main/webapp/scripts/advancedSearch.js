$(document).ready(function () {
	//Throw error and isn't need as of bootstrap 3.3.1
	//$( "#advSearch" ).tabs(); 
	var type = new Array("all", "any","none");
	for(var i = 1; i <= 6; i++){
		for(var t in type){
			changeEvent(type[t],i);
		}
	}
});

/*Listen for user input to forms*/
function changeEvent(t, i){
	var field = $('input[name='+t+'-'+i+']');
	setVerbose(t,i);
	field.bind("keyup change autocomplete", function() {
		setVerbose(t,i);
	});
}

/*Clears form and verbose messages*/
function resetForm(){
	$("#viewControls")[0].reset();
	$('#advSearchVerbose * span').empty();
}
/*Set verbose  message upon change*/
function setVerbose(t,i){
	var message = "";
	var field = new Array("Every field ", "Variable name ","Label ","Description ","Concept ","Variable Type ");
	switch(t)
	{
		case "all":
			message = "contains all: ";
			break;
		case "any":
			message = "contains any: ";
			  break;
		case "none":
			message = "doesn't contain: ";
			  break;
	}	
	var f = $('input[name='+t+'-'+i+']');
	var v = $('#'+t+'-'+i+'d');
	if(f.val()){
		v.html("<strong>"+field[i-1] + "</strong>" + message + f.val().toLowerCase());
	}else{
		v.html("");
	}
}