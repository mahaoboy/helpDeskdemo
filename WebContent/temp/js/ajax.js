
$(document).ready(function(){

  $("#button").click(function(){
	 // $(this).hide();

		    $.get("/TomCat7/GetPara",function(data,status){$("#pagebody").html($('<div/>').html(data).html());});

  });
  $('#dialog').jqm();
});
