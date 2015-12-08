//http://vitalets.github.io/x-editable/docs.html
$(document).ready(function() {
    l = $('#load');
    baseURI = $('#meta_uri').html();
    provID = $('#provID').val();
    $('#provLabel').editable({
        type: 'text',
        pk: 'label',
        url: provID+'/edit',
        title: 'Edit Label'
    });
    $('#provURI').editable({
        type: 'url',
        pk: 'uri',
        url: provID+'/edit',
        title: 'Edit URI'
    });
    $('#provDate').editable({
        type: 'text',
        pk: 'date',
        url: provID+'/edit',
        title: 'Edit Date'
    });
    $('.edgeDelete').editable({
        type: 'text',
        pk: 'delete'
    });
    
    $('#nodeDelete').editable({
        type: 'text',
        pk: 'deleteN'
    });
    
    
    $('.edgeDelete').click(function(){
    	$('.editable-submit').html("Yes");
    	$('.editable-cancel').html("No");
    	
    	//next(".class") and arg isn't working
    	var popover = $(this).next();
    	var source = $(this).next().next().val();
    	var target = $(this).next().next().next().val();
    	var type = $(this).next().next().next().next().val();
    	var submit = $(popover).find(".editable-submit");
    	var cancel = $(popover).find(".editable-cancel");
    	var parent = $(this).parent();
    	
    	edgeDeleteListen(submit,cancel,source,target,type,parent);
    });
    
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
function edgeDeleteListen(s,c,src,tgt,typ,par){
	$(s).unbind();
	$(s).click(function(){
		var path = baseURI+"/edit/prov/edgeremove?src="+src;
		path += "&tgt="+tgt+"&typ="+typ;
		$.ajax({
	        cache: false,
	        type: "POST",
	        url: path,
	        success: function(){
	        	$(par).next().remove();
	        	$(par).remove();
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
function nodeDeleteListen(s,c){
	$(s).unbind();
	$(s).click(function(){
		
		var path = baseURI+"/erest/prov/nodes/"+provID;
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