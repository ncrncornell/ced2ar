$(document).ready(function () {
		l = $('#load');
		ready();
});

function ready(){
	$("#readingModelink").on("click", function(){
		loadReading(this.href);
		return false;
	});
}

/*Loads reading content into div*/
function loadReading(href){
	$.ajax({
		cache: false,
        url : href,
        success : function(data) {
        	l.html(data);
        	l.addClass("loadShow");
        	$("body").css("overflow-y", "hidden");      	
        	var height = $("#wrapper").height();
        	var windowHeight = $(window).height();        	
        	$("#loadCoverInner").css("height", windowHeight*0.9);	
        	$("#wrapper").css("padding-right", "30px");       
        	if(windowHeight > height){
        		height = windowHeight;
        	}
        	l.css("min-height", height);
        	readingModeListener();
        }
    });
}

/*Listens for prev/next key keys, or escape*/
function readingModeListener(){ 
	
	$(".readBtnActive").on("click",function(){
    	loadReading(this.href);   	
    	return false;
    });
	
	$("#readingModeExit").on("click",function(){
		readingModeExit();	
    	return false;
    });
	
	notPressed = true;		
	$(document).keydown(function(e){
	    if (e.keyCode == 37 && notPressed) {    	
	       loc = $("#buttonPagePrev").attr("href");
	       if(loc){
	    	   notPressed = false;
	    	   loadReading(loc); 
	       }    
	    }
	    
	    if (e.keyCode == 39 && notPressed) { 
	        loc = $("#buttonPageNext").attr("href");
	        if(loc){
	           notPressed = false;
	           loadReading(loc); 
	        }    
	    }      
	    if (e.keyCode == 27 && notPressed) { 
	           notPressed = false;
	           readingModeExit();  
	    }      
	});
	
	l.click(function(){
		readingModeExit();
	});
		
	$("#loadCoverInner").click(function(event) {
		event.stopPropagation();
	});	
}

function readingModeExit(){
	l.html("");
	l.removeClass("loadShow");
	l.css("min-height",0);
	$("#wrapper").css("padding-right", "15px");
	$("body").css("overflow-y", "scroll");
	$(".readBtnActive").off();
	$("#readingModelink").off();
	$("#readingModeExit").off();
	ready();
}