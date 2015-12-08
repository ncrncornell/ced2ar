$(document).ready(function () {
	baseURI = $('#meta_uri').html();

});
	
/**
 * Creates info splash with a popup msg
 */
function popup(msg,popupClass){
	//Checks to make sure current info area is empty
	if(!$("#info").html().trim()){	
		if(!hasTaken(popupClass)){
			$("#info").html(msg);
			$("."+popupClass+" button").click(function(){
				setTaken(popupClass);
			});
		}
	}
}

/**
 *Sets a cookie indicator user has taken survey
 */
function setTaken(popupClass){
	var d = new Date();
	var days = 365;
	d.setTime(d.getTime()+(days*24*60*60*1000));
	var expires = "expires="+d.toGMTString();
	document.cookie = "cdr-"+popupClass+"=true; " + expires;
}

/**
 * Checks cookies to see if survey has been taken
 * @returns
 */
function hasTaken(popupClass){
	var cname = "cdr-"+popupClass;
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++){
	  var c = ca[i].trim();
	  if (c.indexOf(name)===0) return c.substring(name.length,c.length);
	}
	return false;
}
