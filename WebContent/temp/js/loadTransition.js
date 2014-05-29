$(document).ready(function() {                           
   $(".transition_functions").each(function(){getlist($(this).attr("id"))});
});

function getlist(issuekey){
	$.get('GetTransitionList?issueKey='+issuekey, function(responseJson) {    
		var aItem = $('#'+issuekey); 
		
		$.each(responseJson, function(index, item) { 
			aItem.children().each(function(){if(this.Attr("class"))});
		   if(index == "closeInfoList"){
			   alert(aItem.children("#closeInfoList").attr("class"));
		   } else {
			   alert(aItem.children("#reopenInfoList").attr("class"));
		   }
		});
    });
}

