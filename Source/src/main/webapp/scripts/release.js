$(document).ready(function(){
	rListenerForm();
	rListenerClick();	
	rShowNewVersion();
	rClone();
});

function rListenerForm(){
	var hrefBase = $("#releaseLink").attr("href");
	$("#releaseForm").change(function(){
		var href = hrefBase;
		var levels = [];
		$("#releaseForm input:checked").each(function(){
			levels.push($(this).val());
		});		
		href = href + levels.join(",");
		$("#releaseLink").attr("href",href);	
	});
}

function rListenerClick(){
	$("#releaseLink").click(function(){
		var r = this;
		var linkHtml = $(this).html();
		$(this).html("<i class=\"fa fa-spinner fa-spin\"></i>");
		$(this).css("color","#333");
		$(this).css("font-size","1.5em");
		$.ajax({
			cache: false,
	        url : $(this).attr("href"),
	        success : function(data) {
	    		$(r).html(linkHtml);
	    		$(r).css("color","#3886cc");
	    		$(r).css("font-size","1em");
	        }
	    });
	});
}
//TODO fix back issue
function rShowNewVersion(){
	$("input[name=makeNewVersion]").change(function(){
		if(this.checked){
			$("#newVersionLabel").removeClass("hidden");
		}else{
			$("#newVersionLabel").addClass("hidden");
		}
	});
}

function rClone(){
	$("#versionClone").click(function(){
		var vNew = $("input[name=newVersionName]").val();
		$.ajax({
			cache: false,
	        url : $(this).attr("href")+vNew+"/clone",
	        success : function(data) {
	    		console.log('good');
	        },
	        error : function(data, textStatus, errorThrown){
	        	console.log(data.responseText);
	        }
	    });
		return false;
	});
}