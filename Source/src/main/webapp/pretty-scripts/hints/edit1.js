$(document).ready(function () {
	if($("#meta_demo").val() === "true"){
		var popupClass="hint-editing";
		var msg =  "<div class='alert alert-info "+popupClass+"'>"+"<button type='button' class='close' data-dismiss='alert'>×</button>"+"<p>You can edit metadata! Click the <i class='fa fa-fa fa-sign-in'></i> to try it out.</p>"+"</div>";
		popup(msg,popupClass);
	}
});