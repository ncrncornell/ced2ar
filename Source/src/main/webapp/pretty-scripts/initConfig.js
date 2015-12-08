$(document).ready(function () {
	l = $('#load');
	baseURI = $('#meta_uri').html();
	actionButtonListener();
});




function actionButtonListener(){

	var selValue = $('input[id=enableRadio]:checked').val();
	if(selValue=='true')
		$(".bugReportField").show();
	else
		$(".bugReportField").hide();

	
	$(".bugReportOn").on("click",function(){
		$(".bugReportField").show();
	});
	$(".bugReportOff").on("click",function(){
		$(".bugReportField").hide();
	});

	$("#changeReaderPassword").click(function(){
		var label = $("#changeReaderPassword").val();
		if(label == "Change Reader Password"){
			$("#changeReaderPassword").val("Keep Reader Password");
			$("#randomizeReaderPassword").removeAttr('disabled');
			$("#newReaderPassword").removeAttr('disabled');
			$("#confirmReaderPassword").removeAttr('disabled');
			$("#keepReaderPassword").val("false");
		}
		else{
			$("#changeReaderPassword").val("Change Reader Password");
			$("#randomizeReaderPassword").prop('disabled','true');
			$("#newReaderPassword").val('');
			$("#confirmReaderPassword").val('');
			$("#newReaderPassword").prop('disabled','true');
			$("#confirmReaderPassword").prop('disabled','true');
			$("#keepReaderPassword").val("true");
		}
	});

	$("#changeWriterPassword").click(function(){
		var label = $("#changeWriterPassword").val();
		if(label == "Change Writer Password"){
			$("#changeWriterPassword").val("Keep Password");
			$("#randomizeWriterPassword").removeAttr('disabled');
			$("#newWriterPassword").removeAttr('disabled');
			$("#confirmWriterPassword").removeAttr('disabled');
			$("#keepWriterPassword").val("false");
		}
		else{
			$("#changeWriterPassword").val("Change Writer Password");
			$("#randomizeWriterPassword").prop('disabled','true');
			$("#newWriterPassword").prop('disabled','true');
			$("#confirmWriterPassword").prop('disabled','true');
			$("#keepWriterPassword").val("true");
		}
	});

	$("#changeAdminPassword").click(function(){
		var label = $("#changeAdminPassword").val();
		if(label == "Change Admin Password"){
			$("#changeAdminPassword").val("Keep Password");
			$("#randomizeAdminPassword").removeAttr('disabled');
			$("#newAdminPassword").removeAttr('disabled');
			$("#confirmAdminPassword").removeAttr('disabled');
			$("#keepAdminPassword").val("false");
		}
		else{
			$("#changeAdminPassword").val("Change Admin Password");
			$("#randomizeAdminPassword").prop('disabled','true');
			$("#newAdminPassword").prop('disabled','true');
			$("#confirmAdminPassword").prop('disabled','true');
			$("#keepAdminPassword").val("true");
		}
	});
	
	$(".randomizeReaderPassword").click(function(){
		readerPwd = randomString();
		$("#newReaderPassword").val(readerPwd);
		$("#confirmReaderPassword").val(readerPwd);
	});
	
	$(".randomizeWriterPassword").click(function(){
		writerPassword = randomString();
		$("#newWriterPassword").val(writerPassword);
		$("#confirmWriterPassword").val(writerPassword);
	});
	
	$(".randomizeAdminPassword").click(function(){
		adminPassword = randomString();
		$("#newAdminPassword").val(adminPassword);
		$("#confirmAdminPassword").val(adminPassword);
	});

	
function randomString() {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var string_length = 8;
	var randomstring = '';
	for (var i=0; i<string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		randomstring += chars.substring(rnum,rnum+1);
	}
	return randomstring;
}	
	
}


