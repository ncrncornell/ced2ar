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
	
	var popover = "<i class='fa fa-question-circle helpIcon helpIconB'" 
	+" data-toggle='popover' title='' data-original-title='Editing Group'" 
	+" data-content='Check or uncheck variables to add or remove from this group'></i>";

	$("#results th:first").html(popover);
	$("#results th:first i").popover({ trigger: 'hover'});
	
	sListen();
});

/**
 *Listens for select event s
 */
function sListen(){	
	$(".cc input").change(function(){
		$("#varAddFeedback").html("<i class='fa fa-circle-o-notch fa-spin fa-lg'></i>");
		var row = $(this).parent().parent().parent();
		var url = $("#addVarForm").attr("action");
		var name = $(this).val();
		if($(this).is(":checked")){
			$(row).addClass("selectedRow");
			$.ajax({
				  url: url,
				  type: "POST",
				  data: {"var":name,"add":true},
				  success: function(){
					  console.log("Added variable");
					  varAddFeedback();
			      }				
			});
        }else{
            $(row).removeClass("selectedRow"); 
            $.ajax({
				  url: url,
				  type: "POST",
				  data: {"var":name,"add":false},
				  success: function(){
					  console.log("Removed variable");
					  varAddFeedback();
			      }		
			});
        }	
	});
}

function varAddFeedback(){
	$("#varAddFeedback").html(" <i class='fa fa-check-circle fa-lg'></i> Changes saved");
}