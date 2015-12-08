$(document).ready(function () {
	startListen();
});

function startListen(){
	$(".inlineLoad").on("click",function(){
		$(this).addClass("disabled");
		$(this).after(" <i class='fa fa-circle-o-notch fa-spin fa-2x'></i>");
	});
}