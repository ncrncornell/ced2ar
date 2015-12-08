var newInput = false;

$(document).ready(function(){	
	inputTypeListen();	
	workflowSubmit();
	
});

function inputTypeListen(){
	$("#workflowAdd input[name=inputType]").change(function (){
		if($(this).val() == "existing"){
			$("#inputNew").css("display","none");
			$("#inputExisting").css("display","block");
			$("#inputSelector").chosen();
			newInput = false;
		}else{
			$("#inputNew").css("display","block");
			$("#inputExisting").css("display","none");
			$("#providerSelector").chosen();
			newInput = true;
		}
	});
}

function workflowSubmit(){
	$("#workflowAdd").submit(function (){
		var errors = false;		
		var inputName = $("#inputName").val().trim();
		
		if(!newInput){
			inputName = $("#inputSelector :selected").val();
			$("#inputName").val(inputName);
			$("#inputURI").val("http://cornell.edu");
		}
		
		//Clears error messages for individual fields
		$(".inputErrorMsg").html("");
		$("#inputNew input[type!='radio'][type!='select']").each(function() {	
			if($(this).val().trim() === ""){
				console.log($(this).id);
				errors = true;
				$(this).parent().next(".inputErrorMsg").html("This field is required");
			}
		});
			
		if(errors){
			$("#provError").html("You are missing fields");	
			return false;
		}
		
		var progName = $("#progName").val().trim();
		var outputName = $("#outputName").val().trim();
		/*
		if(outputName === inputName || outputName === progName || progName === inputName){
			$("#provError").html("IDs must be unique");	
			return false;
		}*/
		
		$.ajax({
		        cache: false,
		        type: "POST",
		        data:$("#workflowAdd").serialize(),
		        url: baseURI+"/edit/workflow/add-chain",
		        success: function(data){       	
		    		$("#provError").empty();
		    		$("#provSuccess").html("Successfully added new chain");        	
		    		$("#provShuffle").click();
		        }, 
		        error: function(data, textStatus, errorThrown){       
		        	$("#provSuccess").empty();
		        	$("#provError").html(data.responseText);	
		        }
			});	
			return false;
	});
	return false;
}