$(document).ready(function () {
	baseURI = $('#meta_uri').html();
	surveyMsg();
});
	
/**
 * Creates info splash with survey link
 */
function surveyMsg(){
	var msg =  "<div class='alert alert-success fade in surveySplash'>"
	+"<button type='button' class='close' data-dismiss='alert'>×</button>"
	+"<p>Please help us and take a brief survey:</p>"
	+"<a class='alert-link' target='_blank' href='https://cornell.qualtrics.com/SE/?SID=SV_7a1Wl3aNdVvfkQR'>"
	+"cornell.qualtrics.com/SE/?SID=SV_7a1Wl3aNdVvfkQR</a>";
	+"</div>"
	
	//Checks to make sure current info area is empty
	if(!$("#info").html().trim()){
		//1 in 30 chance the survey pops up
		var r = Math.floor((Math.random() * 30) + 1);
		if(r == 1 && !hasTaken()){
			$("#info").html(msg);
			$("#info").css("left","35%");
			$("#info").css("right","35%");
			$(".surveySplash .alert-link").click(function(){
				setTaken();
			});
		}
	}
}

/**
 *Sets a cookie indicator user has taken survey
 */
function setTaken(){
	var d = new Date();
	var days = 365;
	d.setTime(d.getTime()+(days*24*60*60*1000));
	var expires = "expires="+d.toGMTString();
	document.cookie = "cdrSurvey=true; " + expires;
}

/**
 * Checks cookies to see if survey has been taken
 * @returns
 */
function hasTaken(){
	cname = "cdrSurvey"
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for(var i=0; i<ca.length; i++){
	  var c = ca[i].trim();
	  if (c.indexOf(name)==0) return c.substring(name.length,c.length);
	}
	return false;
}
