$(document).ready(function() {
	showActionList();
});
function showActionList() {
	$(".transition_functions").each(function() {
		getlist($(this).attr("id"));
	});
}
function getlist(issuekey) {
	var aItem = $('#' + issuekey);
	
	$.get('GetTransitionList?issueKey=' + issuekey, function(responseJson) {

		aItem.children().each(
				function() {
					var itemElement = $(this);
					itemElement.attr("style", "display:none;");
					var className = itemElement.attr("class");
					$.each(responseJson, function(index, item) {
						if (index == className && item.id != null) {
							itemElement.attr("style", "display:block;");
							itemElement.attr("href",
									"TransitionIssues?issueKey=" + issuekey
											+ "&transitionId=" + item.id);
							itemElement.unbind( "click" );
							itemElement.click(function(e) {
								e.preventDefault();
								clickOnTransition(this);
								$(this).hide();
							});
						}
					});
				});
	});
}
function clickOnTransition(clickedItem) {
	var aItem = $(clickedItem);

	$.get(aItem.attr("href"), function(responseJson) {
		$.each(responseJson, function(index, item) {
			if (item.result == "sucess") {
				$("#status" + item.issuekey).text(item.status);
				getlist( item.issuekey);
			} 
		});
	});
}
