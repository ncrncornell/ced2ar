$(document).ready(function(){
	dmp = new diff_match_patch();
	
    $("#diffTest").on("click", function(){
    	diff();
    });
});

function diff(){
  var original = $("#originalText").val();
  var newText = $("#newText").val();
  
  dmp.Diff_Timeout = 1.0;
  dmp.Diff_EditCost = 10.0;

  var ms_start = (new Date()).getTime();
  var d = dmp.diff_main(newText, original);
  var ms_end = (new Date()).getTime();

  //dmp.diff_cleanupSemantic(d);
  dmp.diff_cleanupEfficiency(d);//If not run, letter by letter replacements show within words
  
  var diffText = dmp.diff_prettyHtml(d);
  $("#outputText").html(diffText);
  //console.log('Time: ' + (ms_end - ms_start) / 1000.0 + 's');
}