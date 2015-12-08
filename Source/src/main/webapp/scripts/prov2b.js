function graphMouseOver(){$("#graph2").mouseenter(function(){$(this).css("border","1px solid #428bca")}).mouseleave(function(){$(this).css("border","1px solid #ddd")})}function addDetails(e){var o=e.id,t="node_"+o,a=e.label;switch(o===selectedNode&&(a+=" (selected)"),$("#workflowView").append("<fieldset id='"+t+"'></fieldset>"),$("#"+t).append("<legend>"+a+"</legend>"),$("#"+t).append("<p>ID: "+e.id+"</p>"),$("#"+t).append("<p>Label: <a href='#' class='bse nodeLabelEdit'>"+e.label+"<i class='fa fa-pencil'></i></a></p>"),parseInt(e.nodeType)){case 0:$("#"+t).append("<p>Type: Entity</p>");break;case 1:$("#"+t).append("<p>Type: Agent</p>");break;case 2:$("#"+t).append("<p>Type: Activity</p>")}$("#"+t).append("<p>URI: <a href='#' class='bse nodeURIEdit'>"+e.uri+"<i class='fa fa-pencil'></i></a></p>"),$("#"+t).append("<p><a href='"+baseURI+"/edit/prov/"+o+"'>Edit edges</a></p>"),$(".nodeLabelEdit").editable({type:"text",pk:"label",url:baseURI+"/edit/prov/"+o+"/edit",title:"Edit Label",success:function(){redraw()}}),$(".nodeURIEdit").editable({type:"url",pk:"uri",url:baseURI+"/edit/prov/"+o+"/edit",title:"Edit Label",success:function(){redraw()}})}function buildGraph(){$("#graph2").html("<h2>Loading...</h2>"),$("#workflowView").html("<h2>Details</h2>"),viewingSubset?$("#workflowView").css("visibility","inherit"):$("#workflowView").css("visibility","hidden");var e=$("#main").height(),o=.7*Math.max(document.documentElement.clientHeight,window.innerHeight||0);document.documentElement.clientHeight<900&&(o=.85*o),o>.95*e&&(o=.95*e),$("#graph2").css("height",o+"px"),$("#graph2").css("cursor","move");$(graph2).width();$("#provShuffle").addClass("disabled");var t={nodes:[],edges:[]},a=new sigma({graph:t,container:"graph2",renderer:{container:document.getElementById("graph2"),type:"canvas"},settings:{doubleClickEnabled:!1,sideMargin:3,zoomMax:zoomMax,zoomMin:zoomMin,minNodeSize:4*(o/475),maxNodeSize:14*(o/450),defaultLabelColor:"#000",defaultLabelHoverColor:"#eee",defaultHoverLabelBGColor:"#333",labelSizeRatio:"0.75",labelThreshold:10,minEdgeSize:0,maxEdgeSize:0}}),n=$("#inverted").val(),i=baseURI+"/prov/data2",r=2;null!==selectedNode?(i+="?roots="+selectedNode,null!==n&&""!==n&&(i+="&inverted=true&travelUp=true&d="+r)):null!==n&&""!==n&&(i+="?inverted=true&d="+r),sigma.parsers.json(i,a,function(){var e,o=a.graph.nodes(),t=a.graph.edges(),n=o.length;for(e=0;e<t.length;e++){var i=t[e];i.color="#ccc",i.type="curvedArrow",i.size=2}for(e=0;n>e;e++){var l=o[e],d=a.graph.degree(l.id,"out"),s=a.graph.degree(l.id,"in");switch(l.nodeType){case 0:l.type="circle";break;case"0":l.type="circle";break;case 1:l.type="square";break;case"1":l.type="square";break;case 2:l.type="diamond";break;case"2":l.type="diamond"}"true"===l.isCodebook&&(l.type="equilateral",l.label+=" (codebook)");var c=3*d;l.x=Math.random(),l.y=Math.random(),l.id==selectedNode?(l.color="#ff4000",0===c&&(c=1),l.size=4*c):(s>0||d>0?l.color="#2a6496":l.color="rgba(42,100,150,.25)",l.size=c),viewingSubset&&addDetails(l),$("#nodeFilterList option[value='"+l.id+"']").length<1&&$("#nodeFilterList").append('<option value="'+l.id+'">'+l.label+"</option>")}$("#graph2 h2").remove(),a.refresh(),$("#nodeCount").remove(),viewingSubset?($("#node_"+selectedNode).insertAfter("#workflowView h2"),1==n?$("#provSmall").append("<p id='nodeCount'>Viewing a single unnconnected node</p>"):$("#provSmall").append("<p id='nodeCount'>Viewing nodes with in "+r+" hops of node '"+selectedNode+"' ("+n+" nodes total selected)</p>")):$("#provSmall").append("<p id='nodeCount'>Click on a node to view details</p>"),faConfig={barnesHutOptimize:!0,barnesHutTheta:.75,linLogMode:!0,adjustSizes:!1,edgeWeightInfluence:20,scalingRatio:4,strongGravityMode:!0,gravity:0,slowDown:1e3},a.configForceAtlas2(faConfig),a.startForceAtlas2(),setTimeout(function(){graphBuilt=!0,a.stopForceAtlas2(),a.killForceAtlas2(),$("#nodeFilterList").chosen(),$("#provShuffle").removeClass("disabled"),a.bind("overNode outNode",function(e){"overNode"===e.type?$("#graph2").css("cursor","pointer"):$("#graph2").css("cursor","move")});var e;a.bind("clickNode",function(o){e=select(a,o,!1)});var o,t=!1;$("#provRotateCCW").mousedown(function(){t||(t=!0,o=setInterval(function(){rotate(a,.01)},5))}).mouseup(function(){clearInterval(o),o=null,t=!1}).mouseout(function(){clearInterval(o),o=null,t=!1});var n;$("#provRotateCW").mousedown(function(){t||(t=!0,n=setInterval(function(){rotate(a,-.01)},5))}).mouseup(function(){clearInterval(n),n=null,t=!1}).mouseout(function(){clearInterval(n),n=null,t=!1});var i,r=!1;$("#provZoomIn").mousedown(function(){r||(r=!0,i=setInterval(function(){zoom(a,.98)},5))}).mouseup(function(){clearInterval(i),i=null,r=!1}).mouseout(function(){clearInterval(i),i=null,r=!1}),$("#provZoomOut").mousedown(function(){r||(r=!0,i=setInterval(function(){zoom(a,1.02)},5))}).mouseup(function(){clearInterval(i),i=null,r=!1}).mouseout(function(){clearInterval(i),i=null,r=!1})},750)})}function zoom(e,o){var t=e.camera.x,a=e.camera.y,n=e.camera.a,i=e.camera.ratio;zoomMax>=i*o&&i*o>=zoomMin&&(i*=o),e.camera.goTo({x:t,y:a,angle:n,ratio:i})}function shuffle(){$("#nodeFilterList").change(function(){"--Show All Nodes--"!=$("#nodeFilterList").val()&&null!==$("#nodeFilterList").val()?(viewingSubset=!0,selectedNode=$("#nodeFilterList").val()):(viewingSubset=!1,selectedNode=null),redraw()}),$("#provShuffle").click(function(){redraw()})}function redraw(){$("#graph2").empty(),$("#graph2Info").empty(),$("#graph2Info").css("padding","0em"),buildGraph()}function rotate(e,o){var t=e.camera.angle,a=e.camera.ratio,n=e.camera.x,i=e.camera.y,r=150;i>1?i-=i/r:-1>i?i-=i/r:i=0,n>1?n-=n/r:-1>n?n-=n/r:n=0,e.camera.goTo({x:n,y:i,angle:t+o,ratio:a})}function moveToNode(e,o){var t=o.size/7;t>1?t=1:.5>t&&(t=.5),e.camera.goTo({x:o["read_cam0:x"],y:o["read_cam0:y"],ratio:t})}function select(e,o,t){$("#nodeCount").remove();var a=(e.graph.nodes(),e.graph.edges(),o.data.node.id);return viewingSubset=!viewingSubset,selectedNode!=a||viewingSubset?(selectedNode!==a?(selectedNode=a,viewingSubset=!0):selectedNode=a,$("#nodeFilterList option").each(function(){$(this).val()==a&&$(this).attr("selected","selected")})):(selectedNode=null,$("#nodeFilterList option:first").attr("selected","selected")),redraw(),null}var viewingSubset=!1,selectedNode=null,zoomMin=.1,zoomMax=1.5,baseURI=$("#meta_uri").html();$(document).ready(function(){$("#graph2").html("<h2>Loading...</h2>"),graphBuilt=!1,baseURI=$("#meta_uri").html(),buildGraph(),shuffle(),graphMouseOver()});