$(document).ready(function() {

	createIssue();
	uploadAttachFile();
});
function createIssue() {

	$("#createIssue").submit(
			function(event) {
				$("#warn").text("");
				event.preventDefault();
				var $form = $(this), url = $form.attr("action");
				var departmentValue = $("#department").val();
				var rankValue = $("#rank").val();
				var feeValue = $("#fee").val();
				var summaryValue = $("#summary").val();
				var descriptionValue = $("#description").val();
				var issuetypeValue = $("#issuetype").val();
				if (departmentValue == '' 
						|| summaryValue == '' || descriptionValue == ''
						|| issuetypeValue == '') {
					$("#warn").text("所有字段均不能为空");
					return false;
				}

				var attachList = $("#attachmentList div span");
				var attachNameString = "";
				attachList.each(function(){attachNameString = attachNameString +","+ $(this).text()});
				
				$("#warn").text("Creating Issue");
				$("#createIssueSubmitButton").attr("disabled","disabled");
				
				var posting = $.post(url, {
					department : departmentValue,
					rank : rankValue,
					summary : summaryValue,
					fee : feeValue,
					description : descriptionValue,
					issuetype : issuetypeValue,
					attachNameList: attachNameString
				});
				// Put the results in a div
				posting.done(function(data) {
					if (data == "login") {
						window.location.href = "Login";
					} else {
						$("#warn").text(data);
						$("#createIssueSubmitButton").removeAttr("disabled");
					}
				});
			});
}

function uploadAttachFile() {
	$('#uploadFileInput').change(
			function() {
				var file = this.files[0];
				name = file.name;
				size = file.size;
				type = file.type;

				if (file.name.length < 1) {
					alert("文件不合法");
				} else if (file.size > 50000000) {
					alert("文件太大");
				} else {
					var formData = new FormData($('#uploadFileForm')[0]);
					$.ajax({
						url : 'AjaxUploadFile', // server script to process data
						type : 'POST',
						xhr : function() { // Custom XMLHttpRequest
							var myXhr = $.ajaxSettings.xhr();
							if (myXhr.upload) { // Check if upload property
								// exists
								$("#warn").text("Uploading");
								$("#createIssueSubmitButton").attr("disabled","disabled");
							}
							return myXhr;
						},
						success : completeHandler = function(data, textStatus, jqXHR) {
							if (data.error == "login") {
								window.location.href = "Login";
							} else if (data.success == "success") {
								$("#warn").text("");
								$("#createIssueSubmitButton").removeAttr("disabled");
								$("#attachmentList").append("<div><span style='float: left;'>"+name+"</span><a href='#' onclick='$(this).parent().remove();'>删除</a></div><br />");
							} else{
								alert(data.error);
								$("#warn").text("");
								$("#createIssueSubmitButton").removeAttr("disabled");
							}
						},
						error : errorHandler = function(jqXHR, textStatus, errorThrown) {
							alert(textStatus);
							$("#warn").text("");
							$("#createIssueSubmitButton").removeAttr("disabled");
						},
						// Form data
						data : formData,
						cache : false,
						dataType: 'json',
						contentType : false,
						processData : false
					}, 'json');
				}
			});
}

