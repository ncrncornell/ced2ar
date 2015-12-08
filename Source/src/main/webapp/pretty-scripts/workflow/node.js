//http://vitalets.github.io/x-editable/docs.html
$(document).ready(function() {
    l = $('#load');
    baseURI = $('#meta_uri').html();
    nodeID = $('#nodeID').val();
    
    getScore();
   
    codebooks = [];
    if ($("#nodeHandle").length){
	    //Loads codebooks into handle dropdown
		$.ajax({
			url: baseURI+"/rest/codebooks",
			headers: {
				"id-type":"json2",
				"accept":"application/json"	
			}
		}).done(function(data) {
	
			codebooks = data;
			  $('#nodeHandle').editable({
			   	   type: 'select',
			   	   pk: 'handle',
			   	   url: nodeID,
			   	   title: 'Choose Metadata',
			   	   source:codebooks,
			   	   success: function(response, newValue) {
		   			   h = newValue.split(".");
		   			   $("#docLink").attr("href",baseURI+"/codebooks/"+h[0]+"/v/"+h[1]);			   			   			   		   
			   	   }
			   });
		});
    }
    
    $('#nodeURI').editable({
        type: 'url',
        pk: 'uri',
        url: nodeID,
        title: 'Edit URI',
        success: function(response, newValue) {	
        	getScore();
        }
    });
    
    if ($("#nodeNotes").length){
	    $('#nodeNotes').editable({
		   type: 'textarea',
	       pk: 'notes',
	       url: nodeID,
	       title: 'Edit Notes',
	       success: function(response, newValue) {	
	        	getScore();
	        }
	    });
    }
    
    if ($("#nodeDisplayName").length){
	    $('#nodeDisplayName').editable({
		   type: 'text',
	       pk: 'displayName',
	       url: nodeID,
	       title: 'Edit Display Name'
	    });
    }
    
	if ($("#nodeAuthor").length){
	    $('#nodeAuthor').editable({
	 	   type: 'text',
	        pk: 'author',
	        url: nodeID,
	        title: 'Edit Author',
	        success: function(response, newValue) {	
	        	getScore();
	        }
	    });
    }
	
	if ($("#nodeDOI").length){
	    $('#nodeDOI').editable({
	  	   type: 'text',
	         pk: 'doi',
	         url: nodeID,
	         title: 'Edit DOI',
	         success: function(response, newValue) {	
	         	getScore();
	         }
	    });
	}
	
    $('.edgeDelete').editable({
        type: 'text',
        pk: 'delete',
        title:'Delete this edge?',
        success: function(response, newValue) {	
        	getScore();
        }
    });
    
    //TODO: Implement?
    /*
    $('#nodeDelete').editable({
        type: 'text',
        pk: 'deleteN'
    });*/
    
    
    $('.edgeDelete').click(function(){
    	$('.editable-submit').html("Yes");
    	$('.editable-cancel').html("No");

    	var popover = $(this).next();
    	var id = $(this).val();

    	var submit = $(popover).find(".editable-submit");
    	var cancel = $(popover).find(".editable-cancel");
    	var parent = $(this).parent();
    	
    	edgeDeleteListen(submit,cancel,id,parent);
    });
    
    
  //TODO: Implement for neo4j workflow
    $('#nodeDelete').click(function(){
    	$('.editable-submit').html("Yes");
    	$('.editable-cancel').html("No");
    	
    	var popover = $(this).next();
    	var source = $(this).next().next().val();
    	var submit = $(popover).find(".editable-submit");
    	var cancel = $(popover).find(".editable-cancel");
    	source = $(this).next().next().val();    	
    	
    	nodeDeleteListen(submit,cancel);
    }); 
    
});

/**
 * Listens for user to confirm edge deletion
 * @param s
 * @param c
 * @param src
 * @param tgt
 * @param typ
 */
function edgeDeleteListen(s,c,id,p){
	$(s).unbind();
	$(s).click(function(){
		$.ajax({
	        cache: false,
	        type: "DELETE",
	        url: baseURI+"/edit/workflow/e/"+id,
	        complete: function(){
	        	$(p).remove();
	        }
	    });
		return;
	});
	$(c).click(function(){
		return true;
	});
}

/**
 * Listens for user to confirm node deletion
 * @param s
 * @param c
 * @param src
 * @param tgt
 * @param typ
 */
//TODO: Implement?
/*
function nodeDeleteListen(s,c){
	$(s).unbind();
	$(s).click(function(){
		$(s).parent().children("a").remove();
		
		var path = baseURI+"/erest/prov/nodes/"+nodeID;
		$.ajax({
	        cache: false,
	        type: "DELETE",
	        url: path,
	        complete: function(){
	        	var url = baseURI+"/prov2";    
	        	$(location).attr('href',url);
	        }
	    });	
		return;
	});
	$(c).click(function(){
		return true;
	});
}
*/

function getScore(){
	$.ajax({
		url: baseURI+"/edit/workflow/n/"+nodeID+"/score",
		dataType: "json",
		success: function(data,status,request) {
			loadScore(data,request.getResponseHeader('message'));
		}
		
	});	 
}

function loadScore(data,msg){
	$("#totalScore").html(data + "% complete "+msg);	
}