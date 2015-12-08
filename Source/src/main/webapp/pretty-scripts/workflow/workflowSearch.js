var lastQuery = null;

$(document).ready( function() {
	baseURI = $('#meta_uri').html();			
	searchListen();
});

function workflowSpinner(){
	$("#workflowResults").html("<div id='workflowLoad'><i class='fa fa-spinner fa-spin'></i></div>");
	$("#workflowLoad").css("display", "bloc");	
	$("#workflowLoad").css("margin", "1em auto");
	$("#workflowLoad").css("text-align", "center");
	$("#workflowLoad").css("font-size", "2em");
}

function searchListen(){
	$("#workflowSearch").keyup(function(){
		$("#workflowResults").empty();
		var query = $("#workflowSearch").val().trim();
		if(query.length > 1 && query != lastQuery){
			workflowSpinner();
			search(query);
		}else{
			$("#workflowResults").empty();
		}
	});
}

function search(query){
	lastQuery = query;
	$.ajax({
        cache: false,
        type: "GET",
        ///prov/data/repec/authors?query
        url: baseURI+"/prov/data/workflow?q="+query,
        complete: function(r){    
        	$("#workflowResults").empty();
        	var data = JSON.parse(r.responseText).results[0].data;   
        	for(var i in data){
        		var row = data[i].row;
        		var id = row["0"];
        		var name = row["1"];
        		var nodeType = row["2"];
        		var link = "<a class='redrawLink' href='#"+id+"'>"+name+" ("+nodeType+")</a>";
        		var result = "<tr><td>"+link+"</td><tr/>";
        		$("#workflowResults").append(result);
        	}
        	if(data.length <= 0){
        		var noResults = "<em>Sorry, no matching nodes found</em>";
        		$("#workflowResults").append(noResults);
        	}
        	graphReloadListen();
        }
    });
}

function graphReloadListen(){
	$(".redrawLink").unbind();
	$("a.redrawLink").click(function(){
		var id = $(this).attr("href").substring(1);
		$("#startingNode").val(id);
		redrawGraph();
		return false;
	});	
	$("button.redrawLink").click(function(){
		var id = $(this).val();
		$("#startingNode").val(id);
		redrawGraph();
		return false;
	});	
}