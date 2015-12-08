$(document).ready(function() {
	l = $('#load');
	baseURI = $('#meta_uri').html();
	toggle();
	toggleText();
	appendBaseURI();
	showSTATAListener();
});

/*Hide part of document without animation*/
function toggle(){
	 $(".toggleContent").hide();
	 $(".toggleHeader").attr("title","Expand");
	 
	  $( ".tcs" ).each(function() {
		  $(this).attr("title","Hide");
		  var parent = this;
		  $(parent).next(".toggleContent").slideToggle(0, function(){	        
			  $(parent).css("background-image","url('"+baseURI+"/images/arrow-d.png')");
		  });
	  });
	 
	  $(".toggleHeader").on("click", function()
	  {
	  	var parent = this;
	    $(parent).next(".toggleContent").slideToggle(0, function(){
	        if($(this).is(':visible')){
	            $(parent).css("background-image","url('"+baseURI+"/images/arrow-d.png')");
	            $(".toggleHeader").attr("title","Hide");
	        } else {
	           $(parent).css("background-image","url('"+baseURI+"/images/arrow-r.png')");
	           $(".toggleHeader").attr("title","Expand");
	        }
	    });
	  });
}

/*JQuery sliding animation for long text*/
function toggleText(){	
	$(".truncExp").on("click", function(){
		var btn = this;
		var pre = $(btn).prev().prev(".truncPre");
		var full = $(btn).prev(".truncFull");
		if($(full).hasClass("hidden")){
		  $(full).removeClass("hidden");
		  $(pre).addClass("hidden");
		  $(btn).html("less");
		}else{
			 $(full).addClass("hidden");
			 $(pre).removeClass("hidden");
			 $(btn).html("...more");
		}
	});
}

/*Appends baseURI to links*/
function appendBaseURI(){
	 $( ".baseURIa" ).each(function() {
		 $(this).attr("href",(baseURI+$(this).attr("href")));
	 });
}

/*Listens for clicking on edit button*/
function showSTATAListener(){	
	$(".showSTATA").off("click");
	$(".showSTATA").on("click",function(){	
			loadAjax(this.href,true);	
			$(this).off("click");
			$("#info .close").click();
			return false;	
	});
}

/*Makes to load editing contents*/
function loadAjax(href){
	$.ajax({
		cache: false,
        url : href,
        success : function(data) {
        	l.html(data);
        	l.addClass("loadShow");
        	$("body").css("overflow-y", "hidden");
        	$("#wrapper").css("padding-right", "30px");
        	var height = $("#wrapper").height();
        	var windowHeight = $(window).height();
        	if(windowHeight > height){
        		height = windowHeight;
        	}
        	l.css("min-height", height);
        	submitListener();
        }
    });
}

function hideAjax(){
	l.html("");
	l.removeClass("loadShow");
	l.css("min-height",0);
	$("#wrapper").css("padding-right", "15px");
	$("body").css("overflow-y", "scroll");
	showSTATAListener();
	
}

function submitListener(){
	
	notPressed = true;
	$(document).keydown(function(e){
	    if (e.keyCode == 27 && notPressed) { 
	        notPressed = false;
	        hideAjax();        
	     }     
	});
		

	$("#closeButton").click(function(){
		hideAjax();   
		return false;

	});
	
	$(".dialogClose").click(function(){
		hideAjax();
		return false;
	});
	
	l.click(function(){
		hideAjax();
	});
		
	$("#loadCoverInner").click(function(event) {
		event.stopPropagation();
	});
}