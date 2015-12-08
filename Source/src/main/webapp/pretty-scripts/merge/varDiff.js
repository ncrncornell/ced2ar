var dmp = null;
var replacements = {};

$(document).ready(function () {
	dmp = new diff_match_patch();
	showDiffs();	
});

//Flag var for later?
//Disable merging if no changes?

function showDiffs(){
	$("#remoteDetails .merge").each(function(){
		var classNames = $(this).attr("class").split(/\s+/);
		//Import to always make the mergeElement class name last
		var className = classNames[classNames.length-1];
		
		var localElement = $("#localDetails ."+className+" .mergeDisplay");	
		var remoteElement = $("#remoteDetails ."+className+" .mergeDisplay");
		var hasDiff = runDiff(localElement, remoteElement, remoteElement);
		
		if(hasDiff){
			localElement.addClass("remoteChanged");
			remoteElement.addClass("remoteChanged");
		}
	});
	
	$("#localDetails .merge").each(function(){
		var classNames = $(this).attr("class").split(/\s+/);
		//Import to always make the mergeElement class name last
		var className = classNames[classNames.length-1];
		
		var localElement = $("#localDetails ."+className+" .mergeDisplay");	
		var remoteElement = $("#remoteDetails ."+className+" .mergeDisplay");
		var hasDiff = runDiff(localElement, remoteElement, remoteElement);
		
		if(hasDiff && !localElement.hasClass("remoteChanged")){
			localElement.addClass("remoteChanged");
			remoteElement.addClass("remoteChanged");
		}
	});
	
	//Listen for merge events
	mergeListen();
}

/**
 * Compares text between two 
 * @param local
 * @param remote
 * @param display
 */
function runDiff(local,remote,display){	
	var original = local.text().trim().replace(/(\r\n|\n|\r)/gm,"");
	var newText = remote.text().trim().replace(/(\r\n|\n|\r)/gm,"");
  
	if(original === newText){
		return false;
	}
	 
	dmp.Diff_Timeout = 1.0;
	dmp.Diff_EditCost = 10.0;
	
	var d = dmp.diff_main(newText, original);
	 
	//dmp.diff_cleanupSemantic(d);
	dmp.diff_cleanupEfficiency(d);//If not run, letter by letter replacements show within words
	var diffText = dmp.diff_prettyHtml(d);
	
	display.html(diffText);

	if(diffText){
		return true;
	}
	
	return false;
}	