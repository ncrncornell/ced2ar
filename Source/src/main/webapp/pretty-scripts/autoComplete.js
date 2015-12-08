$(document).ready( function() {
	baseURI = $('#meta_uri').html();
});

/**
 * Loads autocomplete list for variables in a codebook
 * @param varList
 */
function loadVarAC(varList){
	varList.initialize();
	$('#addVar .typeahead').typeahead(null, {
		  name: 'var-list',
		  displayKey: 'name',
		  source: varList.ttAdapter(),
		  templates: {
		    empty: [
		      '<div class="empty-message">No matches found</div>'
		    ].join('\n'),
		    suggestion: Handlebars.compile('<p class="autoCompVar"><strong>{{name}}</strong> – {{label}}</p>')
		  }
	});	
}