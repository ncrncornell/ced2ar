$(document).ready(function(){
	dmp = new diff_match_patch();
});

function runDiff(){
  var original = $("#masterValue").html();
  var newText = $("input[name='currentValue']").val();
  
  if(original.trim() === ""){
	  return;
  }else if(original === newText){
	  $("#outputDiff").html("<em>No changes made</em>");
	  return;
  }
    
  dmp.Diff_Timeout = 1.0;
  dmp.Diff_EditCost = 10.0;

  var d = dmp.diff_main(original, newText);
   
  //dmp.diff_cleanupSemantic(d);
  dmp.diff_cleanupEfficiency(d);//If not run, letter by letter replacements show within words
  var diffText = dmp.diff_prettyHtml(d);
 
  $("#outputDiff").html(diffText);
}	