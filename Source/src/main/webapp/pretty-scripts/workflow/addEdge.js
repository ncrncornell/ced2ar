baseURI = $('#meta_uri').html();

var source = $("select[name='source']");
var target = $("select[name='target']");
var type = $("select[name='type']");	
var typeError = $("#typeError");
var submit = $("#addEdge input[type='submit']");
var preview =$("#triplePreview");
var error = $("#edgeError");
var success = $("#edgeSuccess");

$(document).ready(function(){	
	loadSelector(source,"","");
	loadSelector(target,"","");
	submit.prop('disabled', true);
	selectorChange();
	isComplete();
	addEdge();
});

function loadSelector(selector,t,query){
	$.ajax({
		url: baseURI+"/prov/data/workflow?l=1000&t="+t+"&q="+query,
		dataType: "json",
		//beforeSend: function(){selector.empty();},
		success: function(r) {
			var data = r.results[0].data;   
			for(var i in data){
				var row = data[i].row;
				var id = row["0"];
				var name = row["1"];
				var nodeType = row["2"];
				var text = t === "" ? name + " ("+ nodeType+")" : name;
				var option = "<option value='"+id+"."+nodeType+"'>"+text+"</option>";
				selector.append(option);	
			}
			selector.chosen({
				placeholder_text_single: "Select a node",
			});   
		}
	});	 
}

function selectorChange(){
	source.change(function(){
		loadPreds();
	});
	target.change(function(){
		loadPreds();
	});
}

function loadPreds(){
	type.empty();
	typeError.empty();
	
	var s = source.val();
	var t = target.val();
	if(s !== "" && t !== ""){
		//0 = ID, 1 = Node type
		s = String(s).split(".");
		t = String(t).split(".");
		switch(s[1]){
			case "Dataset":
				switch(t[1]){
					case "Dataset":
						//Nothing exists
						noPredicate(s[1],t[1]);
					break;
					case "Program":
						type.append("<option value='Used_by'>used by</option>");
					break;
					case "Provider":
						//Nothing exists
						noPredicate(s[1],t[1]);
					break;
				}
			break;
			case "Program":
				switch(t[1]){
					case "Dataset":
						type.append("<option value='Produced'>produced</option>");
					break;
					case "Program":
						//Add?
						//type.append("<option value='Modified_from'>modified from</option>");
						noPredicate(s[1],t[1]);
					break;
					case "Provider":
						//Nothing exists
						noPredicate(s[1],t[1]);
					break;
				}
			break;
			case "Provider":
				switch(t[1]){
					case "Dataset":
						type.append("<option value='Provides'>provides</option>");
					break;
					case "Program":
						//Nothing exists
						noPredicate(s[1],t[1]);
					break;
					case "Provider":
						//Add?
						//type.append("<option value='Associated_with'>"+Associated_with+"</option>");
						noPredicate(s[1],t[1]);
					break;
				}
			break;
		}
		type.change();
	}
}

function noPredicate(s,t){
	s = s.toLowerCase();
	t = t.toLowerCase();
	typeError.append("Sorry, no relationship exists from a "+s+" to a "+t);
}

function isComplete(){	
	type.change(function(){	
		preview.empty();
		if(type.val() !== null){
			submit.prop('disabled', false);
			writePreview();
		}else{
			submit.prop('disabled', true);
		}
	});
}

function writePreview(){	
	var sName = source.next('.chosen-container-single').children(".chosen-single").children("span").html();
	var tName = target.next('.chosen-container-single').children(".chosen-single").children("span").html();
	var pred = type.children("option:selected").text();
	preview.html([sName,pred,tName].join(" "));
}

function addEdge(){
	$("#addEdge").submit(function(){
		error.empty();
		success.empty();
		$.ajax({
	        cache: false,
	        type: "POST",
	        data:$("#addEdge").serialize(),
	        url: baseURI+"/edit/workflow/edge",
	        success: function(data){       	
	        	successMsg(data);
	        }, 
	        error: function(data, textStatus, errorThrown){    
	        	//False negative		        		        	
	        	if(data.status < 400){
	        		successMsg(data);
	        	}else{
	        	
		        	error.html(data.responseText);	
	        	}
	        }
		});
		return false;
	});
}

function successMsg(){
	var id = String(source.val()).split(".")[0];
	var href = baseURI + "/edit/workflow/n/"+id;
	var link = "<a href='"+href+"'>View source node.</a>";
	success.html("Added new edge. " + link);
}