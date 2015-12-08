var dmp = null;
var changedElements = [];
var replacements = {};

$(document).ready(function () {
	dmp = new diff_match_patch();
	showDiffs();	
});

//TODO:Flag var for later?
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
			addMergeButton(className);
			localElement.addClass("remoteChanged");
			remoteElement.addClass("remoteChanged");
			changedElements.push(className);
			
			//Attempting to horizontally align fields, not sure if worth doing
			/*
			if(className === "mergeETxt"){
				remoteElement.css("margin-top","2.6em");
				localElement.css("height",Math.max(remoteElement.height()+20,remoteElement.height()));
			}
			*/
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
	saveListen();
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
	
	var d = dmp.diff_main(original, newText);
	 
	//dmp.diff_cleanupSemantic(d);
	dmp.diff_cleanupEfficiency(d);//If not run, letter by letter replacements show within words
	var diffText = dmp.diff_prettyHtml(d);
	
	display.html(diffText);

	if(diffText){
		return true;
	}
	
	return false;
}	

/**
 * Adds merge buttons to both the local and remote views
 * @param className
 */
function addMergeButton(className){
	$("#localDetails ."+className).prepend("<button class='btn mergeBtnL'>" +
	"<i class='fa fa-square-o '></i> Use original</button>");
	$("#localDetails ."+className).prepend("<button class='btn mergeBtnR'>" +
	"<i class='fa fa-square-o '></i> Use crowdsourced</button>");
}

/**
 * Listens for merge events
 */
function mergeListen(){
	$("button.mergeBtnR").click(function(){

		var classNames = $(this).parent().attr("class").split(/\s+/);
		
		//Import to always make the mergeElement class name last
		var className = classNames[classNames.length-1].trim();
		
		var replacement = $("#remoteDetails ."+className+" .mergeOriginal").html();
		var target = $("#localDetails ."+className+" .mergeDisplay");
		
		var index = $.inArray(className, changedElements);
		if(index > -1){
			changedElements.splice(index, 1);
		}
		
		replacements[className] = replacement.trim();
		
		$(this).children("i").attr("class","fa fa-check-square-o");
		
		$("#localDetails ."+className+" .mergeBtnL i").attr("class","fa fa-square-o");
		$("#localDetails ."+className+" .mergeBtnL").css("background-color","#ddd");
		$("#localDetails ."+className+" .mergeBtnL").prop("disabled",false);

		flashText(target,replacement,this,true);
	});
	
	$("button.mergeBtnL").click(function(){
		var classNames = $(this).parent().attr("class").split(/\s+/);
		
		//Import to always make the mergeElement class name last
		var className = classNames[classNames.length-1].trim();
		var replacement = $("#localDetails ."+className+" .mergeOriginal").html();
		var target = $(this).next();

		var index = $.inArray(className, changedElements);
		if(index > -1){
			changedElements.splice(index, 1);
		}
		
		replacements[className] = replacement.trim();
		
		$(this).children("i").attr("class","fa fa-check-square-o");
		
		$("#localDetails ."+className+" .mergeBtnR i").attr("class","fa fa-square-o");
		$("#localDetails ."+className+" .mergeBtnR").css("background-color","#ddd");
		$("#localDetails ."+className+" .mergeBtnR").prop("disabled",false);
		
		var current = target.html();
		flashText(target,replacement,this,replacement !== current);
	});
}

/**
 * Replaces text with an animation
 * TODO: Figure out how to work with rich text
 */
function flashText(target,replacement,btn,flash){
	var fadeTime = flash ? 300 : 0;
	target.fadeOut(fadeTime,function(){
		target.empty();
		target.html(replacement);
		target.fadeIn(fadeTime);	
		$(btn).css("background-color","green");		
		$(btn).prop("disabled",true); 
	});	
}

function saveListen(){	
	$("#saveMerge").click(function(){
		$("mergeFeedback").empty();	
		if(changedElements.length > 0){			
			//TODO: JQuery shake?			
			$("#mergeFeedback").html("<span class='error'>You must select an option for all fields. Choose either crowdsourced or original</span>");	

		}else{	
			$("#mergeFeedback").html("<span class='success'><i class='fa fa-spinner fa-spin'></i> Merging changes</span>");
			$("#saveMerge").prop("disabled",true);
			$.ajax({
		        cache: false,
		        async: true,
		        type: "POST",
		        data: {"replacements":JSON.stringify(replacements)},
		        url: window.location,
		        complete: function(r){
		        	$("#mergeFeedback").html("<span class='success'>Saved</span>");	
		        } 
		    });	
		}
	});
}