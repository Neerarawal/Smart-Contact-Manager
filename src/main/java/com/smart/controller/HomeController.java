package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	@Autowired
	private UserRepository userRepository;
	
	
	
	    @RequestMapping("/")
       public String home(Model model)
       {
	    	model.addAttribute("title","Home-Smart Contact Manager");
    	   return "home";
       }
	   //About Mappinng  
	    @RequestMapping("/about")
	       public String about(Model model)
	       {
		    	model.addAttribute("title","About-Smart Contact Manager");
	    	   return "about";
	       }
	    
	     //for signup page 
	    @GetMapping({"/signup", "/signup/"})
	    public String signup(Model model) {
	        model.addAttribute("title", "Register - Smart Contact Manager");
	        model.addAttribute("user", new User());
	        return "signup";
	    }

	    
	    
    //Handler for registering user 
	    @PostMapping("/do_register")
	    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
	                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
	                               Model model, RedirectAttributes redirectAttributes) {
	        try {
	            System.out.println("Register User Method Called");

	            if (!agreement) {
	                System.out.println("You have not agreed to the terms and conditions");
	                redirectAttributes.addFlashAttribute("message", new Message("You must agree to terms and conditions!", "alert-danger"));
	                return "redirect:/signup"; // ✅ Use redirect to persist the message
	            }

	            if (result1.hasErrors()) {
	                System.out.println("Validation Errors: " + result1.toString());
	                model.addAttribute("user", user);
	                return "signup";
	            }

	            // Set default user properties
	            user.setRole("ROLE_USER");
	            user.setEnabled(true);
	            user.setImageUrl("default.png");
	            user.setPassword(passwordEncoder.encode(user.getPassword()));

	            System.out.println("Saving user: " + user);
	            User result = this.userRepository.save(user);
	            System.out.println("User saved successfully: " + result);

	            // ✅ Use RedirectAttributes to persist message after redirection
	            redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered!", "alert-success"));

	            return "redirect:/signup"; // ✅ Redirect to clear form fields and avoid re-submission issues

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Exception: " + e.getMessage());

	            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong! " + e.getMessage(), "alert-danger"));
	            return "redirect:/signup"; // ✅ Redirect to persist error messages
	        }
	    }

	    
	    //handler for custom login
	    @GetMapping("/signin")
	    public String customLogin(Model model)
	    {  
	    	model.addAttribute("title", "Login Page");
	    	return "login";
	    }
	    
	    
	   
	    	
	    	
	    	
	    	
	    
}
