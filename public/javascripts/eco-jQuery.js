$(document).ready(function(){
  	
  	//chosen activation
  	$(".chzn-select").chosen();

  	$("ul li").click(function() {
		$("ul li").removeClass('active');
		$(this).addClass('active');
	});

});