/*Changes number of results to show if results are present */
$(document).ready(function () {
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
	
});