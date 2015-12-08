var viewingSubset = false;
var selectedNode = null;
var zoomMin = 0.1;
var zoomMax = 1.5;
var baseURI = $("#meta_uri").html();

$(document).ready(function(){
	$("#graph2").html("<h2>Loading...</h2>");
	graphBuilt = false;
	baseURI = $('#meta_uri').html();
	buildGraph();
	shuffle();
	graphMouseOver();
	
});

function graphMouseOver(){
	$("#graph2").mouseenter(function(){
		$(this).css("border","1px solid #428bca");
	}).mouseleave( function(){
		$(this).css("border","1px solid #ddd");	
	});
}

/**
 * Populates details div with node info
 * @param node
 */
function addDetails(node){
	var id = node.id;
	var dom_id = "node_"+id;
	var title = node.label;
	
	if(id === selectedNode){
		title += " (selected)";
	}
	
	$("#workflowView").append("<fieldset id='"+dom_id+"'></fieldset>");
	$("#"+dom_id).append("<legend>"+title+"</legend>");
	
	$("#"+dom_id).append("<p>ID: "+node.id+"</p>");
	$("#"+dom_id).append("<p>Label: <a href='#' class='bse nodeLabelEdit'>"+node.label+"<i class='fa fa-pencil'></i></a></p>");
	
	switch(parseInt(node.nodeType)){
		case 0:
			$("#"+dom_id).append("<p>Type: Entity</p>");
			break;
		case 1:
			$("#"+dom_id).append("<p>Type: Agent</p>");
			break;
		case 2:
			$("#"+dom_id).append("<p>Type: Activity</p>");
			break;
	}

	$("#"+dom_id).append("<p>URI: <a href='#' class='bse nodeURIEdit'>"+node.uri+"<i class='fa fa-pencil'></i></a></p>");
	$("#"+dom_id).append("<p><a href='"+baseURI+"/edit/prov/"+id+"'>Edit edges</a></p>");
	
	
	//Need to call after dom changes
	$('.nodeLabelEdit').editable({
		//TODO: Get node ID
        type: 'text',
        pk: 'label',
        url: baseURI+'/edit/prov/'+id+'/edit',
        title: 'Edit Label',
        success: function() {
        	redraw();
        }
    });
	
	$('.nodeURIEdit').editable({
		//TODO: Get node ID
        type: 'url',
        pk: 'uri',
        url: baseURI+'/edit/prov/'+id+'/edit',
        title: 'Edit Label',
        success: function() {
        	redraw();
        }
    });
}

function buildGraph(){
	$("#graph2").html("<h2>Loading...</h2>");

	//Clears details div
	$("#workflowView").html("<h2>Details</h2>");
	if(viewingSubset){
		$("#workflowView").css("visibility","inherit");
	}else{
		$("#workflowView").css("visibility","hidden");
	}
	
	//Resize graph
	
	var hMain = $("#main").height();
	var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0) * 0.7;	
	if(document.documentElement.clientHeight < 900){
		h = h* 0.85; 
	}

	if(h > hMain*0.95) h = hMain*0.95;
	
	$("#graph2").css("height",h+"px");
	$("#graph2").css("cursor","move");
	
	var w = $(graph2).width();
	
	var preSelect = false;
	$("#provShuffle").addClass("disabled");	
	var g = {
        nodes: [],
        edges: []
    };

	var s = new sigma({
	    graph: g,
	    container: 'graph2',
	    renderer: {
	        container: document.getElementById('graph2'),
	        type: 'canvas'
	    },
	    settings:{
	    	doubleClickEnabled:false,
	        sideMargin:3,
	        zoomMax:zoomMax,
	        zoomMin:zoomMin,
	        minNodeSize: 4*(h/475),
	        maxNodeSize: 14*(h/450),
	        defaultLabelColor:"#000",
	        defaultLabelHoverColor:"#eee",
	        defaultHoverLabelBGColor:"#333",
	        labelSizeRatio:"0.75",
	        labelThreshold:10,
	        minEdgeSize:0,
	        maxEdgeSize:0
	    }
	});
	
	var inverted = $("#inverted").val();
	var dataURL = baseURI+'/prov/data2';
	var distance = 2;
	if(selectedNode !== null){
		dataURL += "?roots="+selectedNode;
		if(inverted !== null && inverted !== ""){
			dataURL += "&inverted=true&travelUp=true&d="+distance;
		}
	}else if(inverted !== null && inverted !== ""){
		dataURL += "?inverted=true&d="+distance;
	}
	
	sigma.parsers.json(		
		dataURL, s,
        function(){
        	//var stats = s.graph.HITS();
            var i, nodes = s.graph.nodes(), edges = s.graph.edges(), len = nodes.length;   
            
            for(i = 0; i < edges.length; i++){
            	var edge = edges[i];
            	edge.color = "#ccc";
            	edge.type = "curvedArrow";
            	edge.size = 2;
            }
            
            //TODO: Still trying to determine a good way zoom based off the graph size
            /*
            var zoomResize =  Math.log(len)/Math.LN10
			if(zoomResize > zoomMax) zoomResize = zoomMax;
			if(zoomResize < zoomMin) zoomResize= zoomMin;
			zoom(s,zoomResize);
			*/
            //var edgeSize = 100/len;
            

            for (i = 0; i < len; i++) {
            	var node = nodes[i];
            	var outDegree = s.graph.degree(node.id,"out");
            	var inDegree = s.graph.degree(node.id,"in");

            	//var stat = stats[node.id]
      
                switch(node.nodeType){
                	case 0:
                		node.type = "circle";
                		break;
                	case "0":
                		node.type = "circle";
                		break;
                	case 1:
                		node.type = "square";
                    	break;
                	case "1":
                		node.type = "square";
                    	break;
                	case 2:
                		node.type = "diamond";
                    	break;
                	
                	case "2":
                		node.type = "diamond";
                    	break;
                }
                
                if(node.isCodebook === "true"){
                	node.type = "equilateral";
                	node.label += " (codebook)";
                }
                
                var size = outDegree*3.0;//stat.hub * 550;
                
                node.x = Math.random();
                node.y = Math.random();     
              
                if(node.id == selectedNode){
            		node.color = "#ff4000";
            		if(size === 0) size = 1;
            		node.size = size*4;            		
                }else{
                	if(inDegree > 0 || outDegree > 0){
                		node.color = "#2a6496";
                	}else{
                		node.color = "rgba(42,100,150,.25)";
                	}	
                	node.size = size;               
                }
                         
                if(viewingSubset){
                	addDetails(node);
                }
                
                if($("#nodeFilterList option[value='"+node.id+"']").length < 1){
                	$("#nodeFilterList").append("<option value=\""+node.id+"\">"+node.label+"</option>");
                }
            }	
            
            //loading Text
        	$("#graph2 h2").remove();   
            s.refresh();
            
            $("#nodeCount").remove();
            if(viewingSubset){
            	//Moves selected info to the top
            	$("#node_"+selectedNode).insertAfter("#workflowView h2");
            	
            	//Inserts subset count
            	if(len == 1){
            		$("#provSmall").append("<p id='nodeCount'>Viewing a single unnconnected node</p>");
            	}else{
            		$("#provSmall").append("<p id='nodeCount'>Viewing nodes with in "+distance+" hops of node '"+selectedNode+"' ("+len+" nodes total selected)</p>");
            	}
            }else{
            	$("#provSmall").append("<p id='nodeCount'>Click on a node to view details</p>");
        	}
            
            //Build or cluster
            faConfig = {
            	barnesHutOptimize: true,	
        		barnesHutTheta:0.75,
        		linLogMode: true,
        		adjustSizes: false,
        		edgeWeightInfluence: 20,
        		scalingRatio:4,
        		strongGravityMode: true,
        		gravity: 0,
        		slowDown: 1000	            		 
            }; 

            s.configForceAtlas2(faConfig);
            s.startForceAtlas2();
  
            //Set graph to stop scaling after 750ms
            setTimeout(function(){ 
            	
            	graphBuilt = true;
            	s.stopForceAtlas2(); 
            	s.killForceAtlas2();
            	
            	$("#nodeFilterList").chosen();
            	
            	$("#provShuffle").removeClass("disabled");	
            	//Change cursor on hovering node
            	s.bind('overNode outNode', function(e){
            		if(e.type === "overNode"){
            			$("#graph2").css("cursor","pointer");
            		}else{
            			$("#graph2").css("cursor","move");
            		}
            	});
    
            	var selected;
            	//Toggle highlight on click
            	s.bind('clickNode', function(e){
            		selected = select(s,e,false);	
                });
            	
            	//Rotation Controls
            	var rotating = false;
            	var keyRotateCCW;
            	$("#provRotateCCW").mousedown(function(){
            	    if(!rotating){    	
            	    	rotating = true;
            	    	keyRotateCCW = setInterval(function() {
        					rotate(s,0.01);
        				}, 5);
            	    }
            	}).mouseup(function() {
        				clearInterval(keyRotateCCW);
        				keyRotateCCW = null;
        				rotating = false;
            	}).mouseout(function() {
    				clearInterval(keyRotateCCW);
    				keyRotateCCW = null;
    				rotating = false;
            	});
            	
            	var keyRotateCW;
            	$("#provRotateCW").mousedown(function(){
            	    if(!rotating){   
            	    	rotating = true;
            	    	keyRotateCW = setInterval(function() {
        					rotate(s,-0.01);
        				}, 5);
            	    }
            	}).mouseup(function() {
        				clearInterval(keyRotateCW);
        				keyRotateCW = null;
        				rotating = false;
            	}).mouseout(function() {
    				clearInterval(keyRotateCW);
    				keyRotateCW = null;
    				rotating = false;
            	});    	
            	
            	//Zoom Controls
            	var zooming = false;
            	var zoomingInterval;
            	$('#provZoomIn').mousedown(function(){
            		 if(!zooming){   
            			 zooming = true;
            			 zoomingInterval = setInterval(function() {
            				 zoom(s,0.98);
         				}, 5);
             	    }
            	}).mouseup(function() {
    				clearInterval(zoomingInterval);
    				zoomingInterval = null;
    				zooming = false;
            	}).mouseout(function() {
    				clearInterval(zoomingInterval);
    				zoomingInterval = null;
    				zooming = false;
            	});       
            	
            	$('#provZoomOut').mousedown(function(){
           		if(!zooming){   
           			 zooming = true;
           			 zoomingInterval = setInterval(function() {
           				 	zoom(s,1.02);
        				}, 5);
            	    }
	           	}).mouseup(function() {
	   				clearInterval(zoomingInterval);
	   				zoomingInterval = null;
	   				zooming = false;
	           	}).mouseout(function() {
	   				clearInterval(zoomingInterval);
	   				zoomingInterval = null;
	   				zooming = false;
	           	});       
            	
            }, 750);
        }
	);
}
/**
 * Zooms on camera focus
 * @param s - graph object
 * @param d - percent change to make from 1.0
 */
function zoom(s,d){
	var x = s.camera.x;
	var y = s.camera.y;
	var a = s.camera.a;
	var r = s.camera.ratio;
	
	if(r * d <= zoomMax && r * d >= zoomMin) r = r * d;
	s.camera.goTo({x:x,y:y,angle:a,ratio:r});
}

/**
 *Draws the graph
 */
function shuffle(){
	$("#nodeFilterList").change(function(){
		if($("#nodeFilterList").val() != '--Show All Nodes--' && $("#nodeFilterList").val() !== null){	
			viewingSubset = true;
			selectedNode = $("#nodeFilterList").val();
		}else{
			viewingSubset = false;
			selectedNode = null;
		}
		redraw();		
	});

	$("#provShuffle").click(function(){
		redraw();		
	});
}

/**
 * Core method to redraw the graph
 */
function redraw(){
	$("#graph2").empty();
	$("#graph2Info").empty();
	$("#graph2Info").css("padding","0em");
	buildGraph();	
}

/**
 * Rotates the graph
 * @param sigma  s - graph instance
 * @param double delta - amount to rotate
 */
function rotate(s,delta){
	var cA = s.camera.angle;
	var cR = s.camera.ratio;
	var cX = s.camera.x;
	var cY = s.camera.y;
	var moveRatio = 150;
	
	if(cY > 1){
		cY -= cY/moveRatio;
	}else if(cY < -1){
		cY -= cY/moveRatio;
	}else{
		cY = 0;
	}
	
	if(cX > 1){
		cX -= cX/moveRatio;
	}else if(cX < -1){
		cX -= cX/moveRatio;
	}else{
		cX = 0;
	}
		
	s.camera.goTo({
      x: cX,
      y: cY,
      angle: cA + delta,
      ratio: cR
    });
}

/**
 * Moves camera to a node
 * @param s
 * @param node
 */
function moveToNode(s,node){
	var zoom = node.size / 7.0;
	if(zoom > 1){
		zoom = 1;
	}else if(zoom < 0.5){
		zoom = 0.5;
	}
	s.camera.goTo({x:node['read_cam0:x'],y:node['read_cam0:y'],ratio:zoom});
	//console.log(zoom);
	return;
}

/**
 * Highlights a node and edges 
 * @param sigma  s - graph instance
 * @param event e - event from click
 * @param string id - id to select
 */
function select(s,e,r){
	$("#nodeCount").remove();

 	var nodes = s.graph.nodes();
	var edges = s.graph.edges();
 	var id = e.data.node.id;
 	viewingSubset = !viewingSubset;
 	 	
 	if(selectedNode == id && !viewingSubset){
 		//Deselect by clicking on the selected node
 		selectedNode = null;
 		$("#nodeFilterList option:first").attr("selected","selected");
 	}else{
 		if(selectedNode !== id){
 	 		//Node was selected, and user selected different node
 	 		selectedNode = id;
 	 		viewingSubset = true;
 	 	}else{
 	 		//Nothing selected, and user clicked on a node
 	 		selectedNode = id;
 	 	}
 		
 		$("#nodeFilterList option").each(function(){
 	        if($(this).val() == id){
 	            $(this).attr("selected","selected");    
 	        }
 	    });
 	}
 
	redraw();
	return null;
}