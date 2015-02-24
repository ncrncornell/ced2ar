/*JQuery sliding animation for long text
 *IMPORTANT - if the contents has elements or sub elements with CSS
 *specifying top or bottom margins, this will break the animation slightly
 *rather than a smooth slide, the slide will have a small but abrupt jump at the end
 *it is hardly noticeable, but annoys me*/
$(document).ready(function() {
  baseURI = $('#meta_uri').html();
  toggle();
  toggleText();
  appendBaseURI();
 
});

/*JQuery sliding animation to hide part of document*/
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
	    $(parent).next(".toggleContent").slideToggle(400, function(){
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