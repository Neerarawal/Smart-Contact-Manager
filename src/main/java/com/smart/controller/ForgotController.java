package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
  
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	 private PasswordEncoder passwordEncoder;
	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session)
	{
		//generating OTP of 4 Digit 
	
		int otp=random.nextInt(99999);
		System.out.println("OTP "+otp);
		//write code for send otp to email ...
		String subject="OTP From SCM";
		String message =""
				+"<div style='border:1px solid #e2e2e2; padding:20px '>"
				+"<h1>"
				+"OTP is "
				+"<b>"+otp
				+"</n>"
				+"</h1>"
				+"</div>";
		String to = email;
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag)
		{
			session.setAttribute("myotp", otp); 
			session.setAttribute("email", email);
			
			return "verify_otp";
		}else
		{
			session.setAttribute("message","check your email id !!");
			return "forgot_email_form";
		}
		
		
	}
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
	    // Retrieve OTP from session safely
	    Integer myOtp = (Integer) session.getAttribute("myotp");
	    String email = (String) session.getAttribute("email");

	    // Handle null case to prevent NullPointerException
	    if (myOtp == null) {
	        session.setAttribute("message", "❌ OTP has expired or is invalid. Please try again.");
	        return "forgot_email_form"; // Redirect to OTP entry form
	    }

	    if (myOtp.equals(otp)) { // Use .equals() to compare Integer safely
	        // Retrieve user
	        User user = this.userRepository.getUserByName(email);

	        if (user == null) {
	            session.setAttribute("message", "❌ No User exists with this email");
	            return "forgot_email_form"; // Redirect to email form page
	        } else {
	            return "password_change_form"; // Redirect to password reset form
	        }
	    } else {
	        session.setAttribute("message", "❌ You have entered the wrong OTP!");
	        return "verify_otp"; // Redirect back to OTP verification
	    }
	}

	//change password 
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
	    
	    // Debugging: Print session attributes
	    System.out.println("Session Email: " + session.getAttribute("email"));

	    String email = (String) session.getAttribute("email");

	    // If email is null, redirect back to forgot-password page
	    if (email == null) {
	        session.setAttribute("message", "Session expired! Try resetting password again.");
	        return "redirect:/forgot-password"; // Adjust URL based on your setup
	    }

	    User user = this.userRepository.getUserByName(email);
	    
	    // Check if user exists
	    if (user == null) {
	        session.setAttribute("message", "No user found with this email.");
	        return "redirect:/forgot-password";
	    }

	    // Encode new password and save
	    user.setPassword(this.passwordEncoder.encode(newpassword));
	    this.userRepository.save(user);

	    session.setAttribute("message", "Password changed successfully!");
	    return "redirect:/signin?change=password changed successfully..";
	}


}
