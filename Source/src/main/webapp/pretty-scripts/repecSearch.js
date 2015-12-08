var lastQuery = null;

$(document).ready( function() {
	baseURI = $('#meta_uri').html();			
	searchListen();
});

function searchListen(){
	$("#authorSearch").keyup(function(){
		$("#authorResults").empty();
		var query = $("#authorSearch").val().trim();
		if(query.length > 1 && query != lastQuery){
			search(query);
		}else{
			$("#authorResults").empty();
		}
	});
}

function search(query){
	lastQuery = query;
	$.ajax({
        cache: false,
        type: "GET",
        url: baseURI+"/prov/data/repec/authors?query="+query,
        complete: function(r){    
        	$("#authorResults").empty();
        	var data = JSON.parse(r.responseText).results[0].data;   
        	for(var i in data){
        		var row = data[i].row;
        		var id = row["0"];
        		var name = row["1"];
        		var link = "<a class='redrawLink' href='#"+id+"'>"+name+"</a>";
        		var result = "<tr><td>"+link+"</td><tr/>";
        		$("#authorResults").append(result);
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