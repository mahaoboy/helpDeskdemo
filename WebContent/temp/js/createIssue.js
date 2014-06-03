$(document).ready(function() {

	createIssue();
});
function createIssue() {

	$("#createIssue").submit(
			function(event) {
				$("#warn").text("");
				event.preventDefault();
				var $form = $(this), url = $form.attr("action");
				var departmentValue = $("#department").val();
				var informationValue = $("#information").val();
				var summaryValue = $("#summary").val();
				var descriptionValue = $("#description").val();
				var issuetypeValue = $("#issuetype").val();
				
				
				if (departmentValue == '' || informationValue == ''
						|| summaryValue == '' || descriptionValue == ''
						|| issuetypeValue == '') {
					$("#warn").text("所有字段均不能为空");
					return false;
				}

				var posting = $.post(url, {
					department : departmentValue,
					information : informationValue,
					summary : summaryValue,
					description : descriptionValue,
					issuetype : issuetypeValue
				});
				// Put the results in a div
				posting.done(function(data) {
					if (data == "login") {
						window.location.href = "Login";
					} else {
						$("#warn").text(data);
					}
				});
			});
}


function uploadAttachFile(){
	$(':file').change(function(){
	    var file = this.files[0];
	    name = file.name;
	    size = file.size;
	    type = file.type;

	    if(file.name.length < 1) {
	    	alert("File is not valid");
	    }
	    else if(file.size > 50000) {
	        alert("File is to big");
	    }
	    else { 
	        $(':submit').click(function(){
	            var formData = new FormData($('#uploadFileForm')[0]);
	            $.ajax({
	                url: 'script',  //server script to process data
	                type: 'POST',
	                xhr: function() {  // Custom XMLHttpRequest
	                    var myXhr = $.ajaxSettings.xhr();
	                    if(myXhr.upload){ // Check if upload property exists
	                        myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
	                    }
	                    return myXhr;
	                },
	                success: completeHandler = function(data) {
	                	alert("ok");
	                },
	                error: errorHandler = function() {
	                    alert("error");
	                },
	                // Form data
	                data: formData,
	                cache: false,
	                contentType: false,
	                processData: false
	            }, 'json');
	        });
	    }
	});
}

function progressHandlingFunction(e){
    if(e.lengthComputable){
        $('progress').attr({value:e.loaded,max:e.total});
    }
}
