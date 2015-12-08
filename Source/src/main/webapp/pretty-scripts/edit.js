var _activeField = null;

$(document).ready(function() {
    l = $('#load');
    baseURI = $('#meta_uri').html();
    appendBaseURI();
    editFieldListener();
    schemaDescListener();
});

function editFieldListener() {
    $(".editIcon2").off("click");
    $(".editIcon2").on("click", function() {
        loadAjax(this.href, true);
        $(this).off("click");
        $("#info .close").click();
        return false;
    });
}

function schemaDescListener() {
    $(".schemaDocLink").off("click");
    $(".schemaDocLink").click(function() {
        loadAjax(this.href, false);
        $(this).off("click");
        $("#info .close").click();
        return false;
    });
}

function loadAjax(href, isEdit) {
	var viewH = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
    $.ajax({
        cache: false,
        url: href,
        success: function(data) {
            l.html(data);
            if (isEdit) {
                $("#editForm").attr("action", href)	;
                _activeField = href.replace("edit?","edit-cancel?");
            }
            l.addClass("loadShow");
            $("body").css("overflow-y", "hidden");
            $("#wrapper").css("padding-right", "30px");
            var height = $("#wrapper").height();
            var windowHeight = $(window).height();
            if (windowHeight > height) {
                height = windowHeight;
            }
            l.css("min-height", height);
            $("#loadCoverInner").css("max-height", viewH * 0.90);
            submitListener(isEdit);
        }
    });
}

function submitListener(isEdit) {
	
    notPressed = true;
    $(document).keydown(function(e) {
        if (e.keyCode == 27 && notPressed) {
            notPressed = false;
            hideAjax(isEdit);
        }
        if (e.keyCode == 13) {
         
            hideAjax(isEdit);
        }
        
    });
    if (isEdit) {
        $("#editDiscard").click(function() {
            hideAjax(isEdit);
            return false;
        });
    } else {
        $(".closeWindow").click(function() {
            hideAjax(isEdit);
            return false;
        });
    }
    l.click(function() {
        hideAjax(isEdit);
    });
    $("#loadCoverInner").click(function(event) {
        event.stopPropagation();
    });
    $("#editSave").click(function() {
        var href = $("#editForm").attr("action");
        showSpinner();
        $.ajax({
            type: "POST",
            url: href,
            data: $("#editForm").serialize(),
            success: function(data) {
                location.reload();
                return false;
            },
            error: function(request, textStatus, errorThrown) {
            	var conflictText = request.getResponseHeader("conflictingValue");
            	if(conflictText !== ""){
            		conflictDiff(conflictText);
            	}else{
            		alert("Malformed or invalid content");
            	}
                        
                hideSpinner();
                $("#editSave").unbind();
                submitListener(false);
                return false;
            }
        });
    });
}

function conflictDiff(conflictText){	
	alert("Warning! Your changes conflict with recent edits. Please review these edits before overwriting them.");	
	$(".conflictItem").removeClass("hidden");
	
	var currentContent = "";
	
	if($("#mceu_7").length){
		//Using tinyMCE
		currentContent = tinyMCE.activeEditor.getContent({format : 'raw'});
		//Allows to click save again without making changes
		tinyMCE.activeEditor.isNotDirty = false;

	}else{
		//Not using tinyMCE
		currentContent = $("#editForm input[name='newValue']").val();	
	}
	
	var diff = runDiff(currentContent,conflictText);
	$("#conflictDiff").html(diff);
	console.log(tinyMCE.activeEditor);
	tinyMCE.activeEditor.save_enablewhendirty = false;
	
}

function hideAjax(isEdit) {
    l.html("");
    l.removeClass("loadShow");
    l.css("min-height", 0);
    $("#wrapper").css("padding-right", "15px");
    $("body").css("overflow-y", "scroll");
    if (isEdit) {
        editFieldListener();
        cancelEdit();
    } else {
        schemaDescListener();
    }
}

function cancelEdit(){
	 $.ajax({
         type: "GET",
         url: _activeField,
         data: $("#editForm").serialize(),
         success: function(data) {
       
         },
         error: function(data) {
          
         }
     });
	_activeField = null;
}

function showSpinner() {
    var height = $("#loadCoverInner").height();
    var width = $("#loadCoverInner").width();
    $("#loadCoverInner").prepend("<div id='loadingContent2'><div id='loadingContentInner'><i class='fa fa-circle-o-notch fa-spin'></i></div></div>");
    $("#loadCoverInner").addClass("noSelect");
    $("#loadingContent2").css("height", height);
    $("#loadingContent2").css("width", width);
    $("#editForm").css("opacity", 0.3);
}

function hideSpinner() {
    $("#loadingContent2").remove();
    $("#loadCoverInner").removeClass("noSelect");
    $("#editForm").css("opacity", 1.0);
}

function appendBaseURI() {
    $(".baseURIa").each(function() {
        $(this).attr("href", (baseURI + $(this).attr("href")));
    });
}