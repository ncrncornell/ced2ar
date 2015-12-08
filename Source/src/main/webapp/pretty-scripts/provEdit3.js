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
			newInput = true;
		}
	});
}

function workflowSubmit(){
	$("#workflowAdd").submit(function (){
		var errors = false;		
		var inputID = $("#inputID").val().trim();
		
		if(!newInput){
			inputID = $("#inputSelector :selected").val();
			$("#inputName").val(inputID);
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
		
		var progID = $("#progID").val().trim();
		var outputID = $("#outputID").val().trim();
		
		if(outputID === inputID || outputID === progID || progID === inputID){
			$("#provError").html("IDs must be unique");	
			return false;
		}
		
		$.ajax({
		        cache: false,
		        type: "POST",
		        data:$("#workflowAdd").serialize(),
		        url: baseURI+"/edit/prov3",
		        success: function(data){       	
		    		$("#provError").empty();
		    		$("#provSuccess").html("Graph successfully generated <a href='/ced2ar-web/prov2'>View Graph</a>");        	
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