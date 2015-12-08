$(document).ready(function () {
	if($("#meta_demo").val() === "true"){
		var popupClass="hint-editing2";
		var msg =  "<div class='alert alert-info "+popupClass+"'>"+"<button type='button' class='close' data-dismiss='alert'>×</button>"+"<p>Now click on the <i class='fa fa-pencil'></i>'s next to a specific field you want to edit</p>"+"</div>";
		popup(msg,popupClass);
	}
});