$(document).ready( function() {
	baseURI = $('#meta_uri').html();
	upload();
	chooseFileType();
	listenObj();
	listenSubmit();
});

/**
 * For adding offline URI
 */
function upload(){
	$(".fileUploadBtn").click(function() {
    	$(this).next(".fileUploadHidden").click();
    	return false;
    });
    
    $(".fileUploadHidden").change(function(){
    	var filename = $(this).val().split('\\').pop();
    	$(this).next(".fileUploadDisplay").val(filename);
	});      
}

/**
 * For selecting online or offline file
 */
function chooseFileType(){
	$("input[name='fileLocSelect']").unbind();
	$("input[name='fileLocSelect']").change(function(){
		if($(this).val() == "online"){
			$("#fileOnline").removeClass("hidden")
			$("#fileOffline").addClass("hidden");
		}else{
			$("#fileOffline").removeClass("hidden")
			$("#fileOnline").addClass("hidden");
		}
	});	
}

/**
 *Listens for change in the form 
 */
function listenObj(){
	$("#provFileAdd").change(function(){
		checkObj();
	});
}

/**
 * Enables or disabled the add button
 */
function checkObj(){
	var errors = "";
	if($("input:checked[name='fileLocSelect']").val() === "online"){
		if($("#objURL").val() === ""){
			return;
			//errors +=" Input URL is required < br/>";
		}else{
			checkFieldComplete();
		}
		
	}else if($("input:checked[name='fileLocSelect']").val() === "offline"){
		if($("#fileOffline .fileUploadHidden").val() === ""){
			return;
			//errors +=" Please select a location for the input file< br/>";
		}else{
			checkFieldComplete();
		}
	}else{
		return;
	}
	//$("#fileAddButton").addClass("disabled");
	return;
}

/**
 * Checks if file type and label is filled out
 */
function checkFieldComplete(){
	if($("#provFileAdd select").val() !== "" && $("#objLabel").val() !== "" 
	&& $("#provFileAdd").val() !== null){
		$("#fileAddButton").removeClass("disabled");
	}else{
		$("#fileAddButton").addClass("disabled");
	}	
}

/**
 * Listens for submit event, makes ajax call
 */
function listenSubmit(){	
	$("#fileAddButton").click(function(){
		$("#provStatus").empty();
		$("#provError").empty();
		$("input[name=objLocal]").val($("input[name=offlineDisplay]").val());	
		submitProvInput();
		return false;
	});
}

function submitProvInput(){
	$(".modal-backdrop").remove();
	$("#duplicateOverride").unbind();
	$.ajax({
        cache: false,
        type: "POST",
        data:$("#provFileAdd").serialize(),
        url: baseURI+"/edit/prov2",
        success: function(data){
        	if(data.status == 200){
        		var id = data.getResponseHeader('objectID');
        		var repText = data.responseText 
        		+ "<br /> <a href='"+baseURI+"+/edit/prov/"+id+"'>See details</a>";
        		$("#provStatus").html(repText);   
        		$("input[name=idOverride]").val("");
        		$("#duplicatePrompt").modal('hide');
        		$("#provFileAdd")[0].reset();
        		return;
        	}
        }, 
        error: function(data, textStatus, errorThrown){
        	var id = data.getResponseHeader('objectID');
        	$("#provStatus").html(""); 
        	//TODO: throws false positive
        	if(data.status === 200){
        		var repText = data.responseText 
        		+ "<br /> <a href='"+baseURI+"/edit/prov/"+id+"'>See details</a>";
        		$("#provStatus").html(repText);    
        		$("input[name=idOverride]").val("");
        		$("#duplicatePrompt").modal('hide');
        		$("#provFileAdd")[0].reset();
        		return;
        	}else if(data.responseText === "duplicateID"){        
        		var msg = "A entity with the same name already exists. Please rename ";
        		msg += "<input name='idOverride' value='"+id+"' type='text'>";	        		
        		$("#duplicateMessage").html(msg);
        		$("#duplicatePrompt").modal('show');
        		$("#duplicateOverride").click(function(){
        			$('#duplicatePrompt').modal('hide');
        			submitProvInput();     			
        			return;
        		});
        	}else{
        		$("#provErrors").html(data.responseText);
        	}
        }
    });	

	return;
}