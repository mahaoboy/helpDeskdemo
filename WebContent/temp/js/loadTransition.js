$(document).ready(function() {
	showActionList();
	var txt2 = $("#txt2");
	txt2.focus(function(e){$(this).select();})
	
	$("#dialog-form").dialog({
		title: "不通过原因",
		autoOpen : false,
		modal : true,
		height: 400,
		width: 500,
		buttons : {
			"Ok" : function() {
				var href = $("#href");
				var txt2 = $("#txt2");
				clickOnTransition(href.val(), txt2.val());
				$(this).dialog("close");
			},
			"Cancel" : function() {
				$(this).dialog("close");
			}
		}
	});


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
							itemElement.unbind("click");
							itemElement.click(function(e) {
								e.preventDefault();
								if ($(this).attr('class') == "reopenInfoList") {
									var href = $("#href");
									href.val($(this).attr("href"));
									$("#dialog-form").dialog("open");
								} else {
									clickOnTransition($(this).attr("href"), "");
									$(this).hide();
								}
							});
						}
					});
				});
	});
}
function clickOnTransition(href, inputString) {
	$.get(href+"&reasonDetail="+encodeURI(encodeURI(inputString)), function(responseJson) {
		responseJson=JSON.parse(responseJson);
		$.each(responseJson, function(index, item) {
			if (item.result == "sucess") {
				$("#status" + item.issuekey).text(item.status);
				getlist(item.issuekey);
				$("#reason" + item.issuekey).text(item.reasonDetailString);
			}
		});
	});
}