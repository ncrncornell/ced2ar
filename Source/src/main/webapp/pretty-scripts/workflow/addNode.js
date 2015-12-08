baseURI = $('#meta_uri').html();

$(document).ready(function(){		
	workflowSubmit();	
});

function workflowSubmit(){
	$("#addNode").submit(function (){
		var errors = false;		

		//Clears feedback
		$(".inputErrorMsg").html("");
		$("#workflowError").html("");
		$("#workflowSuccess").empty();
		
		if(errors){
			$("#workflowError").html("You are missing fields");	
			return false;
		}
			
		var name = $("input[name='name']").val().trim();
		if(name === ""){
			$("#workflowError").html("Name is required");	
			return false;
		}
		
		$.ajax({
	        cache: false,
	        type: "POST",
	        data:$("#addNode").serialize(),
	        url: baseURI+"/edit/workflow/add",
	        success: function(data){       	
	        	showLink(data);
	        }, 
	        error: function(data, textStatus, errorThrown){    
	        	//False negative		        	
	        	console.log(data.status);		        	
	        	if(data.status < 400){
	        		showLink(data);
	        	}else{
	        		$("#workflowSuccess").empty();
		        	$("#workflowError").html(data.responseText);	
	        	}
	        }
		});	
		return false;
	});
	return false;
}

function showLink(data){
	var id = data.responseText;
	var link = "<a href='"+baseURI +"/edit/workflow?start="+id+"'>View on graph</a>";
	$("#workflowError").empty();
	$("#workflowSuccess").html("Added new node. "+link);       
}