$(document).ready(function() {
	objType = -1;
	subType = -1;
	objLabel = "";
	subLabel = "";
	
	baseURI = $('#meta_uri').html();
	enableObjSelect();
	enableSubSelect();
	listenPred();
	changePred();
	listenSubmit();
	
	//Deselects radio when going back a page
	$("input[name='objSelect']").prop('checked', false);
	$("input[name='subSelect']").prop('checked', false);
	
	var psObject = $("#psObject").val();
	if(psObject !== ""){
		preSelectObj(psObject);
	}
	var psSubject = $("#psSubject").val();
	if(psSubject !== ""){
		preSelectSub(psSubject);
	}
});

function preSelectObj(ps){
	ps += "#";
	$("#provObjExisting option[value^='"+ps+"']").prop('selected', true);
	$("#provObjExisting").removeClass("hidden");
	$("input[name='objSelect'][value='existingNode']").prop("checked", "checked");
	$("#psObject").val("");
	return;
}

function preSelectSub(ps){
	ps += "#";
	$("#provSubExisting option[value^='"+ps+"']").prop('selected', true);
	$("#provSubExisting").removeClass("hidden");
	$("input[name='subSelect'][value='existingNode']").prop("checked", "checked");
	$("#psSubject").val("");
	return;
}

/**
 *Chooses which object form to display
 *User can pick an existing object, or create a new one
 */
function enableObjSelect(){
	$("input[name='objSelect']").unbind();
	$("input[name='objSelect']").change(function(){
		console.log("val "+$(this).val());
		if($(this).val() == "existingNode"){
			$("#provObjExisting").removeClass("hidden");
			$("#provObjNew").addClass("hidden");
		}else{
			$("#provObjNew").removeClass("hidden");
			$("#provObjExisting").addClass("hidden");
		}
		enableObjSelect();
	});	
}

/**
 *Chooses which subject form to display
 *User can pick an existing subject, or create a new one
 */
function enableSubSelect(){
	$("input[name='subSelect']").unbind();
	$("input[name='subSelect']").change(function(){
		if($(this).val() == "existingNode"){
			$("#provSubExisting").removeClass("hidden");
			$("#provSubNew").addClass("hidden");
		}else{
			$("#provSubNew").removeClass("hidden");
			$("#provSubExisting").addClass("hidden");
		}
		enableSubSelect();
	});	
}

/**
 *Listens for agents in object/subject forms, and calls predicate
 */
function listenPred(){
	$("#provForm").change(function(){
		selectPred();
	});
}

/**
 *Returns list of possible predicates based off object and subject
 */
function selectPred(){
	
	objType = -1;
	subType = -1;
	objLabel = "";
	subLabel = "";
	
	if($("input:checked[name='objSelect']").val() == "existingNode"){
		var curVal = $("#provObjExisting select").val();
		if(curVal !== null){
			objType = curVal.split("#")[1];	
			objLabel = $("#provObjExisting :selected").text();
		}
		//console.log("object exists");
	}else if($("input:checked[name='objSelect']").val() == "newNode"){
		objType = $("#provObjNew select").val();	
		objLabel = $("#objLabel").val();
		//console.log("object is new");
	}	
	//console.log("object type is " + objType);
	
	if($("input:checked[name='subSelect']").val() == "existingNode"){
		var curVal2 = $("#provSubExisting select").val();
		if(curVal2 !== null){
			subType = curVal2.split("#")[1];	
			subLabel =  $("#provSubExisting :selected").text();
		}
		//console.log("subject exists");
	}else if($("input:checked[name='subSelect']").val() == "newNode"){
		subType = $("#provSubNew select[name='provSubClass']").val();	
		subLabel = $("#subLabel").val();
		//console.log("subject is new");
	}
	//console.log("subject type is " + subType); 
	
	if(objType >= 0 && subType >= 0){
		$("#provPreviewObj").html(objLabel);
		$("#provPreviewSub").html(subLabel);
		$("#provAddButton").removeClass("disabled");
		$("#provAddButton").removeAttr("disabled");
		switch(objType){
			//Obj is entity
			case "0":
				//console.log("object is an entity");
				loadPred(subType); 
			break;
			
			//Obj is agent	
			case "1":
				switch(subType){
				case "0":
					loadPred(4); 
				break;
				case "1":
					loadPred(3); 
				break;
				case "2":
					loadPred(5); 
				break;
			}
				
			break;
			
			//Obj is activity
			case "2":
				//console.log("object is an activity");
				switch(subType){
					case "0":
						loadPred(6); 
					break;
					case "1":
						loadPred(7); 
					break;
					case "2":
						loadPred(8); 
					break;
				}
			break;
		}
	}else{
		$("#provAddButton").addClass("disabled");
		$("#provAddButton").attr("disabled",true);
		$("#provPreviewObj").empty();
		$("#provPreviewSub").empty();
		$("#provPreviewPred").empty();
	}
	return;
}

function loadPred(i){
	console.log("Loading predicates for condition "+i);
	var preds = JSON.parse($("#provEditPred input[type='hidden']").val())[i];
	$("#provEditPred select").empty();
	$("#provEditPred select").removeAttr("disabled");
	$("#provEditPred select").removeClass("disabled");	
	
	preds.forEach(function(pred){ 
		$("#provEditPred select").append("<option value='"+pred.id+"'>"+pred.label+"</option>");
	});
	$("#provPreviewPred").html($("#provEditPred :selected").text());
	return;
}

function changePred(){
	$("#provEditPred select").change(function(){
		$("#provPreviewPred").html($("#provEditPred :selected").text());
	});
	return;
}

function listenSubmit(){
	$("#provForm").submit(function(){
		$("#provErrors").empty();
		
		var errors = "";
	
		if(objType === -1){
			errors +="Object type is required <br /> ";
		}
		
		if(subType === -1){
			errors +="Subject type is required <br />";		
		}
		
		if($("input:checked[name='objSelect']").val() == "newNode"){	
			var objID = $("#objID").val();
			if(objID === ""){
				errors +="Object ID is required <br />";	
			}
			if(objLabel === ""){
				errors +="Object label is required <br />";	
			}
			if($("#objURI").val() === ""){
				errors +="Object URI is required <br />";	
			}
		}
		
		if($("input:checked[name='subSelect']").val() == "newNode"){
			var subID = $("#subID").val();
			if(subID === ""){
				errors +="Subject ID is required <br />";	
			}
			if(subLabel === ""){
				errors +="Subject label is required <br />";	
			}
			if($("#subURI").val() === ""){
				errors +="Subject URI is required <br />";	
			}	
		}
		
		if(errors !== ""){
			$("#provErrors").html(errors);
		}else{
			$("#provErrors").empty();
			$("#provStatus").empty();
			$.ajax({
		        cache: false,
		        type: "POST",
		        data:$("#provForm").serialize(),
		        url: baseURI+"/edit/prov",
		        success: function(data){
		        	if(data.status === 200){
		        		var rep = data.responseText;
		        		rep += "<br /><a href='"+baseURI+"/prov2'>View on graph</a>";//TODO:Args to select edge
		        		$("#provStatus").html(rep);      
		        	}
		        }, 
		        error: function(data){
		        	//TODO: throws false positive
		        	if(data.status === 200){
		        		var rep = data.responseText;
		        		rep += "<br /><a href='"+baseURI+"/prov2'>View on graph</a>";
		        		$("#provStatus").html(rep);        
		        	}else{
		        		$("#provErrors").html(data.responseText);
		        	}
		        }
		    });
		}
	
		return false;
	});
}