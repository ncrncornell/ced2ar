var sideFilterSize = "col-xs-4 col-sm-3 col-md-2";

$(document).ready(function () {
	baseURI = $('#meta_uri').html();
	$("#sidebar").attr('class', sideFilterSize);
	$("#main").attr('class', 'col-xs-6 col-sm-8');
	f = $('#filterCodebook');
	c = $('#filterCompare');
	loadFilter();
	loadCompare();
	compareCheck();
	changeCompare();
	filterHide();
	changeResultSize();
	clearFilter();
});

function clearFilter(){
	$("#clearFilter").unbind();
	$("#clearFilter").click(function(){
		$("#ff :checkbox").prop("checked", false);	
		$("#ff :checkbox").change();
		return false;
	});
}

/**
 * Controls the toggle state of the codebook filter
 */
function toggleFilter(){
	 $(".filterVersion").hide();
	 $(".fts").show();
	 $(".filterToggle .fti").html("<i class='fa fa-plus-square'></i>");
	 $(".filterToggle .fti").attr("title","Show details");
	 
	 $(".filterToggle").each(function(){
		 if($(this).next().hasClass("fts")){
			 $(".fti",this).attr("title","Hide details");
			 $(".fti",this).html("<i class='fa fa-minus-square'></i>"); 
		 }
	 });	 
	 
	 $(".fti", ".filterToggle").on("click", function()
	 {
		var baseHandle = $(this).next().attr("class");
		var toggle = this;
	   $(".filterVersion."+baseHandle).slideToggle(0, function(){
			if($(this).is(':visible')){
				$(this).addClass("fts");
				$(toggle).attr("title","Hide details");
				$(toggle).html("<i class='fa fa-minus-square'></i>");
				$(".filterShow."+baseHandle).val(baseHandle);
			} else {			
				$(this).removeClass("fts");
				$(toggle).attr("title","Show details");
				$(toggle).html("<i class='fa fa-plus-square'></i>");
				$(".filterShow."+baseHandle).removeAttr("value");
			}
	   });
	   $("input[name=updateT]").val("1");
	   //Saves state of toggling even if filters are not submitted
	   $.ajax( {
			  cache: false, 
		      url: baseURI+"/filterCodebook",
		      data:  $("#ff").serialize(),
		      async: false
		}); 
	 }); 
}

/**
 *Listens for show dropdown to change 
 */
function changeResultSize(){
	$("#limitResults").change(function() {
		if ($('#results').length){
			$("#viewControls").submit();
	    }
	});

	$(".browseLetter").click(function() {
		l = $(this).text();
		$("#viewControls input[name=a]").val(l);
		$("#viewControls").submit();
		return false;
	});	
};

/**
 *Listens for checking or unchecking of codebook filter 
 */
function changeFilter(){	
	$("#ff :checkbox").change(function(){	
		if($(this).attr("name") == "cb"){
			$("input[name='updateV']").val("1");
		}else if($(this).attr("name") == "ctb"){
			$("input[name='updateB']").val("1");
		}
		loadFilter();
		if($("#results").length){
			$("#viewControls").submit();
		}else{
			  loadVerbose();	
			  changeFilter();
		}
	});
}

/**
 *Reloads verbose filter on refresh 
 */
function loadVerbose(){
	$.ajax( {
		  cache: false, 
	      url: baseURI+"/filterVerbose",
	      async: false,
	      success: function(data){
	    	  $("#filterCodebookVerbose").empty();
	    	  $("#filterCodebookVerbose").html(data);
	    	  clearFilter();
	      }	  
	} );
}

/**
 *Makes an ajax call to the sidebar filter itself 
 */
function loadFilter(){
	$.ajax( {
		  cache: false, 
	      url: baseURI+"/filterCodebook",
	      data:  $("#ff").serializeArray(),
	      async: false,
	      success: function(data){
	    	  f.html(data);
	    	  toggleFilter();//For new filter, remove if not in use
	    	  changeFilter();
	    	  $('.helpIcon').popover({ trigger: 'hover'});
	    	  $(".ttf").tooltip({placement : 'right'});
	      }	  
	} );
}

/**
 *Listens for compare check box to be clicked 
 */
function changeCompare(){		
	$("input[name='cc']").each(function(){
		if($(this).is(':checked')){
			$(this).attr("title","Remove from compare");
		}
	});
	
	$("input[name='cc']").change(function(){
		if($(this).is(':checked')){
			$(this).attr("title","Remove from compare");
			loadCompare();
		}else{
			$(this).attr("title","Add to compare");
			removeCompare2(this.value);
		}
	});
}

/*Makes ajax call to fetch new filter*/
function loadCompare(){
	$.ajax( {
		  cache: false, 
	      url: baseURI+"/filterCompare",
	      data:  $("#rf").serializeArray(),
	      async: false,
	      success: function(data){
	    	  c.html(data);
	      }	  
	} );
}

/**
 *Clears compare filter and resets checkboxes 
 */
function resetCompare(){
	$("#rf")[0].reset();
	$.ajax( {
		  cache: false, 
	      url: baseURI+"/filterCompareReset",
	      async: false,
	      success: function(data){
	    	  c.html(data);
	      }	  
	} );
}

/**
 *Removes specific  variable from comparision view and unchecks box 
 */
function removeCompare(ref){
		var data = $(ref).next().val();
		var checked = $("[value='"+data+"']");
		checked.prop('checked', false);
		$.ajax( {
			  cache: false, 
		      url: baseURI+"/filterCompareRemove",
		      data:  "rm="+data,
		      async: false,
		      success: function(data){
		    	  c.html(data);
		      }	  
		} );
		return false;
}

/**
 *Removes specific variable from comparasion view when box is unchecked 
 */
function removeCompare2(data){
		var checked = $("input[value='"+data+"'][name='cv']");
		checked.prop('checked', false);
		$.ajax( {
			  cache: false, 
			  url: baseURI+"/filterCompareRemove",
		      data:  "rm="+data,
		      async: false,
		      success: function(data){
		    	  c.html(data);
		      }	  
		} );
		return false;
}

/**
 *Checks compare checkboxes upon page load if they are currently in the session
 *For example, reloading the page would ordinarily clear the checkboxes
 */
function compareCheck(){
	$("input[name='cv']").each(function(){
		var data = this.value;
		var checked = $("[value='"+data+"'][name='cc']");
		checked.prop('checked', true);		
	});
}

/**
 *Hides filter
 */
function filterHide(){	
	$("#filterHide").attr("title","Hide sidebar");
	if (getCookie("smallDisplay") == "true") {
	   $("#sidebar").attr("class","col-xs-0");
	   $("#sidebar").css("display","none");
	   $("#filterHide").attr("title","Show sidebar");
	   $("#filterHide").css("background-image","url('"+baseURI+"/images/arrow-r.png')");
	   $("#filterHide").addClass("filterHighlight");
	  
	   $("#main").css("left","12px");
	}

	$("#filterHide").on("click", function()
	  {
	    $("#sidebar").toggle(0, function(){
		    if($("#sidebar").is(':visible')){
		    	setCookie("smallDisplay","false");
		    	$("#sidebar").attr("class",sideFilterSize);
		    	$("#sidebar").css("display","inline-block");
		    	$("#filterHide").attr("title","Hide sidebar");
		    	$("#filterHide").css("background-image","url('"+baseURI+"/images/arrow-l.png')");
		        $("#filterHide").removeClass("filterHighlight");
		        $("#main").css("left","");
		    } else {
		    	   setCookie("smallDisplay","true");
		    	   $("#sidebar").css("display","none");
		    	   $("#sidebarOuter").attr("class","col-xs-0");
		    	   $("#filterHide").attr("title","Show sidebar");
				   $("#filterHide").css("background-image","url('"+baseURI+"/images/arrow-r.png')");
				   $("#filterHide").addClass("filterHighlight");
				   $("#main").css("left","12px");
		    	}
		    });
	  });
}

/**
 *Thanks to http://www.w3schools.com/js/js_cookies.asp
 */
function setCookie(cname,cvalue,exdays){
	var d = new Date();
	d.setTime(d.getTime()+(exdays*24*60*60*1000));
	var expires = "expires="+d.toGMTString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}

/**
 *Thanks to http://www.w3schools.com/js/js_cookies.asp
 */
function getCookie(cname){
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++) 
	  {
	  var c = ca[i].trim();
	  if (c.indexOf(name)==0) return c.substring(name.length,c.length);
	  }
	return "";
}