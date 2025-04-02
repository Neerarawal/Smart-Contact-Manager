console.log("This is Script file")

const toggleSidebar = () => {
	if($(".sidebar").is(":visible")){
		//true, here we chek if sidebar is visible , we will close it
		$(".sidebar").css("display", "none");//we have close the sidebar now we also have to move 
		// our private dashboard into left so that it take full space
	
			$(".content").css("margin-left","0%"); 
	}else{
		//false, here if sidebar is not visible , we have to show it 
		$(".sidebar").css("display", "block");//when we show our dashboard than it will take only 20% width or 
		// as you want but not full as our right dashboard 
        $(".content").css("margin-left","20%"); 	
		
	}
};


const search=()=>{
	//console.log("search...");
	
	let query = $("#search-input").val();
	
	if(query==""){
		$(".search-result").hide();
	}else{
		console.log(query);
		
		//sending request to server
		
		let url=`http://localhost:8080/search/${query}`;
		
		fetch(url)
		.then((response)=>{
			return response.json();
		})
		.then((data)=>{
			//accessing data
			
			
			let text=`<div class='list-group'>`
			
			 data.forEach((contact)=> {
				text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`;
				
			 });
			 
			text+=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
			
		});
		
		
	}
};