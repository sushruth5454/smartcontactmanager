const toggleSidebar=() =>{
    if($(".sidebar").is(":visible")){
        console.log("visible")
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }
    else{
        console.log("Notvisible")
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");

    }
}
