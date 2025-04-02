package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal)
	{
	 String userName = principal.getName();
   	 System.out.println("USERNAME" +userName);
   	 
   	 //get the user using username(email) as we can find out name using String userName = principal.getName();
   	 //System.out.println("USERNAME" +userName);
   	    
   	 User user = userRepository.getUserByName(userName);
   	 
   	 System.out.println("USER" +user);
   	 
   	 model.addAttribute("user", user); 
	}
	
	
	//dashboard for home
     @RequestMapping("/index")//that means we can only access it if we give url like this localhost:port_number/user/index
	public String dashboard(Model model,Principal principal)
	{   
    	model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
     
     
     //open add form handler
     @GetMapping("/add-contact")
     public String openAddContactForm(Model model)
     {   
    	 model.addAttribute("title","Add Contact");
    	 model.addAttribute("contact", new Contact());
    	 return "normal/add_contact_form";
     }
     
     //processing  add contact form 
     @PostMapping("/process-contact")
     public String processContact(@ModelAttribute Contact contact,
    		 @RequestParam("profileImage") MultipartFile file,
    		 Principal principal, HttpSession session)
     
     {
    	 try {
    	 String name = principal.getName();
    	 User user = this.userRepository.getUserByName(name);
    	 
    	 //processing and uploading file
    	 if(file.isEmpty())
    	 {
    		 //if the file is empty then try our message 
    		 System.out.println("File is Empty");
    		 contact.setImage("contact1.png");
    		 
    	 }
    	 else
    	 {
    		 //upload the file to folder and update the name to contact
    		 contact.setImage(file.getOriginalFilename());
    		 
    		File saveFile=  new ClassPathResource("static/img").getFile();
    		
    		 Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
    		
    		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    		
    		System.out.println("Image is Uploaded");
    	 }
    	 
    	 user.getContacts().add(contact);
    	 
    	 contact.setUser(user);
    	 
    	 
    	 
    	 
    	 
    	 
    	 this.userRepository.save(user);
    	 
    	 
    	 System.out.println("DATA" +contact);
    	 
    	 System.out.println("Added to Database");
    	 
    	 //message success 
    	 session.setAttribute("message",new Message("Your Contact is added !! Add more..","success"));
    	 
    	 }
    	 
    	 
    	 catch(Exception e)
    	 {
    		 System.out.println("ERROR"+e.getMessage());
    		 e.printStackTrace();
    		 //message error 
    	session.setAttribute("message",new Message("Something went wrong!! Try again...","danger"));
    	 }
    	 
    	 return "normal/add_contact_form";
     }
     
     //show contacts handler
     //per page=7[n]
     //current page =0[page]
     @GetMapping("/show_contacts/{page}")
     public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal)
         {
     
    	 m.addAttribute("title","Show User Contacts");
    	 //here we have to send and show  contact list added by some user  from database
    	 
    	 //so what we do first using Principal principal we will get the user detail from database and ten we will show the list of contacts
//    	  String userName = principal.getName();
//    	  User user = this.userRepository.getUserByName(userName);
//    	  List<Contact> contacts = user.getContacts();//using this we are able to access all contacts
    	  //we can use this method but here we will go with another approach 
    	 //using the below approach we are able to access user by id 
    	 String userName = principal.getName();
    	  User user = this.userRepository.getUserByName(userName);
    	  
    	//Pageable will contain two information 1.currentPage-page 2.Contact Per page 
    	  Pageable pageable = PageRequest.of(page, 3);
    	  
    	 Page<Contact> contacts= this.contactRepository.findContactByUser(user.getId(),pageable);
    	 
    	 m.addAttribute("contacts",contacts);
    	 m.addAttribute("currentPage",page);
    	 
    	 //to show how many pages has been created for as according to our data 
    	 m.addAttribute("totalPages", contacts.getTotalPages());
    	 
    	 
    	 
    	 return "normal/show_contacts";
     }
     
     //showing particular contact details
     @RequestMapping("/{cId}/contact")
     public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal)
     {   
    	 System.out.println("CID " +cId);
    	 
    	 Optional<Contact> contactOptional = this.contactRepository.findById(cId);
    	 Contact contact = contactOptional.get();
    	 
    	 //providing security to id in url
    	 String userName = principal.getName();
    	User user = this.userRepository.getUserByName(userName);
    	
    	 //now we check if uderId and loggedIn user id same or not
    	if(user.getId()==contact.getUser().getId())
    	{
    		model.addAttribute("contact",contact);
    		model.addAttribute("title",contact.getName());
    	}
    	 
    	 return "normal/contact_detail";
     }
       
     
     //Delete contact handler
     @GetMapping("/delete/{cid}") // <-- Add "/user" if necessary
     public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,Principal pricipal) 
     {

    	// Optional<Contact> contactOptional = this.contactRepository.findById(cId);
    	 //Contact contact = contactOptional.get();
    	 System.out.println("CID "+cId);
   	 Contact contact = this.contactRepository.findById(cId).get();
   	   
    	 //check ...Assignment 
//    	 if(user.getId()==contact.getUser().getId())
//     	{
//     		model.addAttribute("contact",contact);
//     		model.addAttribute("title",contact.getName());
//     	}
//    	 //as we have used cascadeFetch All , therefore we are unable to delete contact directly bcz we have done our project mapping based on CASCADE FETCH ALL
    	 //so now we first unlink that contact from contact List and then we will delete that contact
       	 System.out.println("Contact "+contact.getcId());
   	      contact.setUser(null);
//    	 
    	 
    	 
    	 
	//remove image first,use contact.getImage() 
    	 //trying new way by adding orphanRemoval in user.java 
   	
   	    // user.getContacts().remove(contact);
    	 
    	 
    	 this.contactRepository.delete(contact);
    	 System.out.println("DELETED");
    	 session.setAttribute("message", new Message("Contact Deleted Successfully...","success"));
   	 
   	     return "redirect:/user/show_contacts/0";
   	 
     } 
   //open update form handler
     @PostMapping("/update-contact/{cid}")
     public String updateForm(@PathVariable("cid") Integer cid, Model m)
     {
    	 m.addAttribute("title","Update Contact");
    	 
    	 Contact contact = this.contactRepository.findById(cid).get();
    	 
    	 m.addAttribute("contact",contact);
    	 
    	 return "normal/update_form";
     }
    
     //update contact handler
     @RequestMapping(value="/process-update", method = RequestMethod.POST)
     public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal)
     {    
    	 
    	 try {
    		 
    		 //old Contact details
    		 Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
    		 //image 
    		 if(!file.isEmpty())
    		 {
    			 //as he has update the image then we have to rewrite on file 
    			 //first delete old photo and then update new photo
    			 
    			 //delete old photo
    			 
    			 File deleteFile=  new ClassPathResource("static/img").getFile();
    			 File file1= new File(deleteFile, oldContactDetail.getImage());
    			 file1.delete();
    			 
    			 //update new photo
    			 
    			 File saveFile=  new ClassPathResource("static/img").getFile();
    	    		
        		 Path path =Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
        		
        		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        		
        		contact.setImage(file.getOriginalFilename());
    			 //to delete old photo first fetch details of image from old data
    			 
    		 }
    		 else
    		 {
    			 contact.setImage(oldContactDetail.getImage());
    		 }
    		 
    		 User user = this.userRepository.getUserByName(principal.getName());
    		 contact.setUser(user);
    		 this.contactRepository.save(contact);
    		 
    		 session.setAttribute("message", new Message("Your contact is updated...","success"));
    	 }
    	 catch(Exception e)
    	 {
    		e.printStackTrace(); 
    	 }
    	 System.out.println("CONTACT NAME " +contact.getName());
    	 System.out.println("CONTACT ID " +contact.getcId());
    	 return "redirect:/user/"+contact.getcId()+"/contact";
     }
     
     //your profile handler
     @GetMapping("/profile")
     public String yourProfie(Model model)
     {
    	 model.addAttribute("title","Profile Page");
    	 return "normal/profile";
     }
     
     //open settings handler
     @GetMapping("/settings")
     public String openSettings()
     {
    	 return "normal/settings";
     }
  
     //change password handler
     @PostMapping("/change-password")
     public String changePassword(@RequestParam(value = "oldPassword", required = false) String oldPassword,
                                  @RequestParam(value = "newPassword", required = false) String newPassword,
                                  Principal principal, HttpSession session) {

         // 🛑 Validate input fields
         if (oldPassword == null || oldPassword.trim().isEmpty() || 
             newPassword == null || newPassword.trim().isEmpty()) {
             
             System.out.println("❌ ERROR: Missing or empty password fields!");
             session.setAttribute("message", new Message("Both password fields are required!", "danger"));
             return "redirect:/user/settings";
         }

         // Remove leading/trailing spaces
         oldPassword = oldPassword.trim();
         newPassword = newPassword.trim();

         System.out.println("🔹 OLD PASSWORD: " + oldPassword);
         System.out.println("🔹 NEW PASSWORD: " + newPassword);

         // 🛑 Enforce strong password policy (example: at least 6 characters)
         if (newPassword.length() < 6) {
             session.setAttribute("message", new Message("Password must be at least 6 characters long!", "danger"));
             return "redirect:/user/settings";
         }

         // ✅ Get the currently logged-in user
         String userName = principal.getName();
         User currentUser = this.userRepository.getUserByName(userName);

         if (currentUser == null) {
             System.out.println("❌ ERROR: User not found!");
             session.setAttribute("message", new Message("User not found. Please log in again!", "danger"));
             return "redirect:/signin";
         }

         // ✅ Verify old password
         if (!this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
             System.out.println("❌ ERROR: Incorrect old password!");
             session.setAttribute("message", new Message("Incorrect old password!", "danger"));
             return "redirect:/user/settings";
         }

         // ✅ Update the password securely
         currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
         this.userRepository.save(currentUser);

         System.out.println("✅ SUCCESS: Password updated for user: " + userName);
         session.setAttribute("message", new Message("Your password has been changed successfully!", "success"));

         return "redirect:/user/index"; // Redirect to dashboard
     }

}





