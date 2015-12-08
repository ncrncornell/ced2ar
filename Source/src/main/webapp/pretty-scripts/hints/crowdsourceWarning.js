$(document).ready(function () {
	if($("#meta_demo").val() === "true"){
		var popupClass="crowdsource-warning";
		var content = "<i class='fa fa-thumbs-o-up fa-lg'></i> Help make CED<sup>2</sup>AR better by reporting bugs and requesting new features <a href='#footer'>below</a>.";
		var msg =  "<div class='alert alert-info "+popupClass+"'>"+"<button type='button' class='close' data-dismiss='alert'>×</button>"+"<p>"+content+"</p>"+"</div>";
		popup(msg,popupClass);
	}
});