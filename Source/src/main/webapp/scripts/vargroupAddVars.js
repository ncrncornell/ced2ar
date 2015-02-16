$(document).ready(function (){
	baseURI = $('#meta_uri').html();
	//Highlights row on load
	$(".cc input").each(function(){
		var row = $(this).parent().parent().parent();
		if($(this).is(":checked")){
			$(row).addClass("selectedRow");
        }else{
            $(row).removeClass("selectedRow");
        }	
	});
	sListen();
});

/**
 *Listens for select event 
 */
function sListen(){	
	$(".cc input").change(function(){
		var row = $(this).parent().parent().parent();
		var url = $("#addVarForm").attr("action");
		var name = $(this).val();
		if($(this).is(":checked")){
			$(row).addClass("selectedRow");
			$.ajax({
				  url: url,
				  type: "POST",
				  data: {"var":name,"add":true}
			});
        }else{
            $(row).removeClass("selectedRow"); 
            $.ajax({
				  url: url,
				  type: "POST",
				  data: {"var":name,"add":false}
			});
        }	
	});
}