package Zest.gym.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import Zest.gym.model.AttendanceSheet;
import Zest.gym.model.Diet;
import Zest.gym.model.MembershipDetails;
import Zest.gym.model.User;
import Zest.gym.model.Video;
import Zest.gym.model.MembershipOwned;
import Zest.gym.model.Schedule;
import Zest.gym.model.Trainer;
import Zest.gym.repository.AttendanceRepository;
import Zest.gym.repository.DietRepository;
import Zest.gym.repository.MembershipDetailsRepository;
import Zest.gym.repository.MembershipOwnedRepository;
import Zest.gym.repository.ScheduleRepository;
import Zest.gym.repository.UserRepository;
import Zest.gym.repository.VideoRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;





@Controller
public class LoginSignupContoller {
	@Autowired
	private UserRepository uRepo;
	
	@Autowired
	private AttendanceRepository aRepo;
	
	@Autowired
	private MembershipOwnedRepository mRepo;
	
	@Autowired
	private ScheduleRepository sRepo;
	
	@Autowired
	private MembershipDetailsRepository mbRepo;
	
	@Autowired
	private VideoRepository vRepo;
	
	@Autowired
	private DietRepository dRepo;
	
	
	
	
	
	@GetMapping("/")
	public String LandingPage(HttpSession session, Model model) {
		String userEmail = (String) session.getAttribute("email");

	     // Fetch the user's membership status based on their email
	     List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

	     // List to hold extracted membership details
	     List<Map<String, String>> membershipDetails = new ArrayList<>();

	     // Extract membership name, price, and duration for each membership status
	     for (MembershipOwned membershipOwned : membershipStatus) {
	         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

	         // Create a map to hold extracted name, price, and duration
	         Map<String, String> details = new HashMap<>();
	         details.put("name", extractName(membershipPlan));
	         details.put("price", extractPrice(membershipPlan));
	         details.put("duration", extractDuration(membershipPlan));

	         // Add the details map to the list
	         membershipDetails.add(details);
	     }

	     List<MembershipDetails> md = mbRepo.findAll();
	     model.addAttribute("md", md);
	     // Add the extracted details to the model
	     model.addAttribute("membershipStatus", membershipDetails);
	     return "User/landing.html";
	}
	
	
	@GetMapping("/login")
	public String LoginPage() {
		return "User/login.html";
	}
	
	@GetMapping("/register")
	public String RegistarionPage() {
		return "User/register.html";
	}
	
	@PostMapping("/register")
	public String userRegistration(@ModelAttribute User u, Model model, HttpSession session) {
		Optional<User> existingUser = uRepo.findByEmail(u.getEmail());
		if(existingUser.isPresent()) {
			model.addAttribute("errormessage", "Email already exist! Try new one");
			return "User/index.html";
		}
		
		String hashPwd = DigestUtils.sha3_256Hex(u.getPassword());
		u.setPassword(hashPwd);
		uRepo.save(u);
		model.addAttribute("message", "Signup successful!!");
		String userEmail = (String) session.getAttribute("email");

	     // Fetch the user's membership status based on their email
	     List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

	     // List to hold extracted membership details
	     List<Map<String, String>> membershipDetails = new ArrayList<>();

	     // Extract membership name, price, and duration for each membership status
	     for (MembershipOwned membershipOwned : membershipStatus) {
	         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

	         // Create a map to hold extracted name, price, and duration
	         Map<String, String> details = new HashMap<>();
	         details.put("name", extractName(membershipPlan));
	         details.put("price", extractPrice(membershipPlan));
	         details.put("duration", extractDuration(membershipPlan));

	         // Add the details map to the list
	         membershipDetails.add(details);
	     }
	     
	 

	     // Add the extracted details to the model
	     model.addAttribute("membershipStatus", membershipDetails);
		return "User/index.html";
	}
	
	@PostMapping("/login")
	public String loginUser(@ModelAttribute User u, Model model, HttpSession session) {
		if (uRepo.existsByEmailAndPassword(u.getEmail(), DigestUtils.sha3_256Hex(u.getPassword()))) {
			String role = uRepo.findRoleByEmail(u.getEmail());
			String username = uRepo.findUsernameByEmail(u.getEmail());
			if (username != null && role.equals("user")) {
				model.addAttribute("email", u.getEmail());
				model.addAttribute("message", "Login successful! Please click on dashborad.");
				session.setAttribute("email", u.getEmail());
				session.setAttribute("username", username);
				System.out.println(session.getAttribute("email"));
				session.setMaxInactiveInterval(1800);
				String userEmail = (String) session.getAttribute("email");

			     // Fetch the user's membership status based on their email
			     List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

			     // List to hold extracted membership details
			     List<Map<String, String>> membershipDetails = new ArrayList<>();

			     // Extract membership name, price, and duration for each membership status
			     for (MembershipOwned membershipOwned : membershipStatus) {
			         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

			         // Create a map to hold extracted name, price, and duration
			         Map<String, String> details = new HashMap<>();
			         details.put("name", extractName(membershipPlan));
			         details.put("price", extractPrice(membershipPlan));
			         details.put("duration", extractDuration(membershipPlan));

			         // Add the details map to the list
			         membershipDetails.add(details);
			     }
			     // Add the extracted details to the model
			     List<MembershipDetails> md = mbRepo.findAll();
			     model.addAttribute("md", md);
			     model.addAttribute("membershipStatus", membershipDetails);
			     return "User/index.html";
			     
			     
			}

			else {
				model.addAttribute("email", u.getEmail());
				model.addAttribute("message", "Login successful! Please click on dashborad.");
				session.setAttribute("email", u.getEmail());
				session.setAttribute("username", username);
				System.out.println(session.getAttribute("email"));
				System.out.println(session.getAttribute("username"));
				session.setMaxInactiveInterval(1800);
				return "Trainer/Tindex.html";
			}
			

		}
		model.addAttribute("errormessage", "Username or password incorrect. Please try again!");
		return "User/login.html";
	}
	
	@GetMapping("logout")
	public String logout(Model model, HttpSession session) {
		
		session.invalidate();
	   List<MembershipDetails> md = mbRepo.findAll();
		model.addAttribute("md", md);
		return "User/index.html";
	}
	
	
	
	@GetMapping("/forgotPwd")
	public String forgotPassword() {
		return "User/forgotPwd.html";
	}
	
	@PostMapping("/forgotPwd")
	public String forgotPasswordEmail(Model model,HttpSession session, @RequestParam("email") String email){
		int otp = new MailSender().sendOtp(email);
		session.setAttribute("otp", otp);
		session.setAttribute("email", email);
		System.out.println(otp + email);
		return "User/otp.html";
	}
	
	
	 @PostMapping("/checkOtp") public String otpChecker(HttpSession
			  session, @RequestParam("otpcode")Integer otpcode){
			  if(session.getAttribute("otp")!=null) {
				  String email = (String) session.getAttribute("email");
			  int otp = (int) session.getAttribute("otp"); 
			  if(otp == otpcode) { 
				  String status="matched"; 
				  session.setAttribute("status", status);
				  System.out.println(session.getAttribute("status"));
				  session.setAttribute("email", email);
				  System.out.println(email);
				  return "User/resetPwd.html";
				  }
			  else{ 
					  String status="mismatch"; 
					  session.setAttribute("status", status);
					  System.out.println(session.getAttribute("status"));
					  return "User/otp.html" ; 
			  }
			  }
			  
			  return "User/forgetPwd.html" ; 
	 }
	 
	 
	 @PostMapping("/newPassword")
	  public String newPassword(@ModelAttribute User u, HttpSession session, Model model, @RequestParam("newPassword") String newPassword){
		  String email =  (String) session.getAttribute("email");
		  if(email != null) {
			  	String hashPwd = DigestUtils.sha3_256Hex(newPassword);
				u.setPassword(hashPwd);
			  	uRepo.updatePasswordByEmail(email, hashPwd);
				new MailSender().sendPasswordChangeMessage(email);
				System.out.println("password changed successfully" + hashPwd);
				session.invalidate();
		  }
		  String userEmail = (String) session.getAttribute("email");

		     // Fetch the user's membership status based on their email
		     List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

		     // List to hold extracted membership details
		     List<Map<String, String>> membershipDetails = new ArrayList<>();

		     // Extract membership name, price, and duration for each membership status
		     for (MembershipOwned membershipOwned : membershipStatus) {
		         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

		         // Create a map to hold extracted name, price, and duration
		         Map<String, String> details = new HashMap<>();
		         details.put("name", extractName(membershipPlan));
		         details.put("price", extractPrice(membershipPlan));
		         details.put("duration", extractDuration(membershipPlan));

		         // Add the details map to the list
		         membershipDetails.add(details);
		     }

		     // Add the extracted details to the model
		     model.addAttribute("membershipStatus", membershipDetails);
		  return "User/landing.html";
		  
		  
	  }
	 
	 @GetMapping("/about-us")
	 public String aboutUs() {
	 	return "User/about-us.html";
	 }
	 
	 @GetMapping("/team")
	 public String team() {
	 	return "User/team.html";
	 }
	 
	 
	 @GetMapping("/gallery")
	 public String gallery() {
	 	return "User/gallery.html";
	 }
	 
	 @GetMapping("/blog")
	 public String blog() {
	 	return "User/blog.html";
	 }
	 
	 @GetMapping("/404")
	 public String error() {
	 	return "User/404.html";
	 	
	 }
	 
	 @GetMapping("/contact")
	 public String contact() {
	 	return "User/contact.html";
	 	
	 }
	 
	 @GetMapping("/profile")
	 public String profile( HttpSession session, Model model) {
		 String email =  (String) session.getAttribute("email");
		 if(email != null) {
			 Optional<User> uList = uRepo.findByEmail(email);
			 model.addAttribute("uList", uList);
			return "User/profile.html";
		 }
		 List<MembershipDetails> md = mbRepo.findAll();
	     model.addAttribute("md", md);
	     return "User/index.html";
	
	 	
	 }
	 
	 @GetMapping("/editUserProfile/{id}")
		public String editProfile(@PathVariable int id, Model model, HttpSession session) {
		 	String userEmail = (String) session.getAttribute("email");
		 	if(userEmail != null) {
		 		Optional<User> uList = uRepo.findById(id);
				model.addAttribute("uList", uList);
				return "User/userProfileEdit.html";
		 	}
		 	 List<MembershipDetails> md = mbRepo.findAll();
		     model.addAttribute("md", md);
		     return "User/index.html";
		}
	 
	 @GetMapping("/index")
	 public String index(HttpSession session, Model model) {
		 String userEmail = (String) session.getAttribute("email");
		 if(userEmail != null) {
			 List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

		     // List to hold extracted membership details
		     List<Map<String, String>> membershipDetails = new ArrayList<>();

		     // Extract membership name, price, and duration for each membership status
		     for (MembershipOwned membershipOwned : membershipStatus) {
		         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

		         // Create a map to hold extracted name, price, and duration
		         Map<String, String> details = new HashMap<>();
		         details.put("name", extractName(membershipPlan));
		         details.put("price", extractPrice(membershipPlan));
		         details.put("duration", extractDuration(membershipPlan));

		         // Add the details map to the list
		         membershipDetails.add(details);
		     }
		     List<MembershipDetails> md = mbRepo.findAll();
		     model.addAttribute("md", md);

		     // Add the extracted details to the model
		     model.addAttribute("membershipStatus", membershipDetails);
		 	return "User/index.html";
			 
		 }
		 List<MembershipDetails> md = mbRepo.findAll();
	     model.addAttribute("md", md);

	 	return "User/landing.html";
	     
	 }
	 
	 @PostMapping("/saveUserEditedprofile")
		public String postMethodName(HttpSession session, 
		                              @RequestParam int id,
		                              @RequestParam String name, 
		                              @RequestParam String address, 
		                              @RequestParam String email, 
		                              @RequestParam String contact,  
		                              Model model) {
		 String userEmail = (String) session.getAttribute("email");
		    if(userEmail!=null) {
		    	Optional<User> u = uRepo.findById(id);
			    
			    if (u.isPresent()) {  // Ensure the Optional contains a value
			        User user = u.get();  // Retrieve the actual Trainer object
			        user.setUsername(name);
			        user.setAddress(address);
			        user.setContact(contact);
			        user.setEmail(email);
			        uRepo.save(user);  // Save the updated trainer object
			        
			        // Get the email from the session and find the updated trainer details
			        String semail = (String) session.getAttribute("email");
			        Optional<User> uList = uRepo.findByEmail(semail);
			        
			        model.addAttribute("uList", uList);
			        return "User/profile.html";  // Return the profile page with updated info
			    }

			    // If the trainer with the given id was not found
			    String semail = (String) session.getAttribute("email");
			    Optional<User> uList = uRepo.findByEmail(semail);
			    model.addAttribute("uList", uList);
			    return "User/profile.html";  // Return the profile page if no trainer was found
		    }
		    List<MembershipDetails> md = mbRepo.findAll();
		    model.addAttribute("md", md);

		 	return "User/landing.html";
		    
		    
		    
		}
	 
	 @PostMapping("/saveUserProfile")
		public String saveProfile(@RequestParam("image") MultipartFile Fimage, 
		                          @RequestParam("id") int id, HttpSession session, 
		                          Model model) {
		 
		 	String userEmail = (String) session.getAttribute("email");
		 	if(userEmail!=null) {
		 		Optional<User> userOpt = uRepo.findById(id);
			    
			    if (userOpt.isPresent()) {
			        User user = userOpt.get();
			        
			        String uploadDir = Paths.get("src", "main", "resources", "static", "assets").toString();
			        if (!Fimage.isEmpty()) {
			            try {
			                // Get the original filename
			                String imageName = StringUtils.cleanPath(Fimage.getOriginalFilename());
			                
			                // Define the path to save the image (e.g., in the "uploads" directory)
			                Path imagePath = Paths.get(uploadDir, imageName);

			                // Create the directory if it doesn't exist
			                Files.createDirectories(imagePath.getParent());

			                // Save the image file to the defined path
			                Fimage.transferTo(imagePath);

			                // Save the image name (or relative path) to the trainer object
			                user.setUserimage(imageName);  // You can serve images from the static folder using this URL
			            } catch (IOException e) {
			                // If there was an error uploading, show an error message and redirect back
			                model.addAttribute("error", "Failed to upload image.");
			                String semail = (String) session.getAttribute("email");
			        	    Optional<User> uList = uRepo.findByEmail(semail);
			        	    model.addAttribute("uList", uList);
			                return "User/profile.html";  
			            }
			        }

			        // Save the trainer data (including the updated image path)
			        uRepo.save(user);
			        
			        String semail = (String) session.getAttribute("email");
				    Optional<User> uList = uRepo.findByEmail(semail);
				    model.addAttribute("uList", uList);
			        return "User/profile.html";   
			    }

			    // If the trainer with the given id was not found, return an error page or the form
			    model.addAttribute("error", "User not found");
			    String semail = (String) session.getAttribute("email");
			    Optional<User> uList = uRepo.findByEmail(semail);
			    model.addAttribute("uList", uList);
		        return "User/profile.html";   
		 	}
		 	 List<MembershipDetails> md = mbRepo.findAll();
		     model.addAttribute("md", md);

		 	return "User/landing.html";
		    
		}

		
	 
	 
	 @GetMapping("/membership")
	 public String getMembership(Model model, HttpSession session) {
	     // Get the user email from the session
	     String userEmail = (String) session.getAttribute("email");

	     // Fetch the user's membership status based on their email
	     List<MembershipOwned> membershipStatus = mRepo.findByEmail(userEmail);

	     // List to hold extracted membership details
	     List<Map<String, String>> membershipDetails = new ArrayList<>();

	     // Extract membership name, price, and duration for each membership status
	     for (MembershipOwned membershipOwned : membershipStatus) {
	         String membershipPlan = membershipOwned.getMembershipPlan(); // Assuming this field contains the membership plan details

	         // Create a map to hold extracted name, price, and duration
	         Map<String, String> details = new HashMap<>();
	         details.put("name", extractName(membershipPlan));
	         details.put("price", extractPrice(membershipPlan));
	         details.put("duration", extractDuration(membershipPlan));

	         // Add the details map to the list
	         membershipDetails.add(details);
	     }

	     // Add the extracted details to the model
	     model.addAttribute("membershipStatus", membershipDetails);
	     
	     List<MembershipDetails> md = mbRepo.findAll();
	     model.addAttribute("md", md);

	     // Return the view name
	     return "User/membership";
	 }

	 // Extract name from the membershipPlan (before the first '-')
	 private String extractName(String membershipPlan) {
	     int endIndex = membershipPlan.indexOf('-');
	     if (endIndex > 0) {
	         return membershipPlan.substring(0, endIndex).trim(); // Extract name before the first '-'
	     }
	     return membershipPlan.trim(); // Return the whole string if no '-' is found
	 }

	 // Extract price from the membershipPlan (between '$' and '-')
	 private String extractPrice(String membershipPlan) {
	     int startIndex = membershipPlan.lastIndexOf('$') + 1;
	     int endIndex = membershipPlan.indexOf('-', startIndex);
	     if (startIndex < endIndex && startIndex >= 0) {
	         return membershipPlan.substring(startIndex, endIndex).trim(); // Extract price between '$' and '-'
	     }
	     return ""; // Return empty string if extraction fails
	 }

	 private String extractDuration(String membershipPlan) {
		    // Start from the last character
		    int startIndex = membershipPlan.length() - 1;

		    // Find the position of the first '-' from the last character
		    int endIndex = membershipPlan.lastIndexOf('-', startIndex);

		    // Check if valid indices were found
		    if (startIndex > 0 && endIndex > 0 && endIndex < startIndex) {
		        return membershipPlan.substring(endIndex + 1, startIndex).trim(); // Extract the duration from the end till '-'
		    }
		    return ""; // Return empty string if extraction fails
		}



	 @GetMapping("/membershipForm")
	 public String membershipForm(Model model, HttpSession session) {
		 String userEmail = (String) session.getAttribute("email");
		 if(userEmail != null) {
		 	List<MembershipDetails> membershipDetailList = mbRepo.findAll();
			model.addAttribute("membershipDetailList", membershipDetailList);
				
		 	return "User/membershipForm.html";
		 }
		 List<MembershipDetails> md = mbRepo.findAll();
	     model.addAttribute("md", md);

	 	return "User/landing.html";
	     
		 
		
	 }
	 
	 @PostMapping("/membershipForm")
	 public String membershipFormData(@ModelAttribute MembershipOwned m, Model model) {
		mRepo.save(m);
		List<MembershipDetails> membershipDetailList = mbRepo.findAll();
		model.addAttribute("membershipDetailList", membershipDetailList);
	 	return "User/membershipForm.html";
	 }
	 
	 //Activities
	 @GetMapping("/class-timetable")
	 public String getClassTimetable(Model model) {
	     List<Schedule> schedules = sRepo.findSchedulesByDaysAndTimeSlots();

	     // Filter the schedules for each day and time slot
	     Map<String, Schedule> filteredSchedules = new HashMap<>();
	     for (Schedule schedule : schedules) {
	         String key = schedule.getDay() + "-" + schedule.getTimeSlot();
	         filteredSchedules.put(key, schedule);
	     }

	     model.addAttribute("filteredSchedules", filteredSchedules);

	     // Log the schedules for debugging
	     if (schedules != null && !schedules.isEmpty()) {
	         System.out.println("Schedules for specific days and time slots:");
	         for (Schedule schedule : schedules) {
	             System.out.println("Day: " + schedule.getDay() +
	                                ", Time Slot: " + schedule.getTimeSlot() +
	                                ", Trainer: " + schedule.getTrainer() +
	                                ", Activity: " + schedule.getActivity());
	         }
	     } else {
	         System.out.println("No schedules found for the specified days and time slots.");
	     }

	     return "User/class-timetable";
	 }

	 @GetMapping("/bmi-calculator")
	 public String bmiCalculator() {
	 	return "User/bmi-calculator.html";
	 }
	 
	 @GetMapping("/activities")
	 public String activities() {
	 	return "User/activities.html";
	 }
	 
	 //Workout
	 @GetMapping("/video")
	 public String video(Model model) {
		List<Video> vList = vRepo.findAll();
		model.addAttribute("vList", vList);
	 	return "User/video.html";
	 }
	 
	 @GetMapping("/diet")
	 public String deit(Model model) {
		 List<Diet> dietList = dRepo.findAll();
		 model.addAttribute("dietList", dietList);
	 	return "User/diet.html";
	 }
	 
	 @GetMapping("/attendance")
	 public String trainerAttendancePage(HttpSession session, Model model) {
	     // Get the current user's email from the session
	     String userEmail = (String) session.getAttribute("email");

	     if (userEmail != null) {
	         // Fetch today's attendance record
	         Optional<AttendanceSheet> attendanceOptional = aRepo.findByDateAndEmail(LocalDate.now(), userEmail);

	         if (attendanceOptional.isPresent()) {
	             // Attendance exists for today
	             model.addAttribute("attendanceStatus", "Present");
	             System.out.println("Present");
	         } else {
	             // No attendance record for today
	             model.addAttribute("attendanceStatus", "Absent");
	             System.out.println("Absent");
	         }

	         // Check if attendance exists for today
	         AttendanceSheet attendanceSheet = attendanceOptional.orElse(null);

	         LocalDate currentDate = LocalDate.now();
	         int totalDaysInMonth = currentDate.lengthOfMonth(); // Dynamically calculates the days in the current month
	         model.addAttribute("totalDaysInMonth", totalDaysInMonth);
	         System.out.println(totalDaysInMonth);

	         Long totalPresent = aRepo.countCurrentMonthByEmail(userEmail);
	         model.addAttribute("presentDays", totalPresent);

	         // Fetch all attendance records for this user
	         model.addAttribute("attendanceList", aRepo.findByEmail(userEmail));

	         // Fetch distinct days from the date column for the specific email
	         List<Integer> distinctDays = aRepo.findDistinctDaysByEmail(userEmail);
	         model.addAttribute("distinctDays", distinctDays);
	         System.out.println(distinctDays);

	         // Pass attendance status to the model
	         model.addAttribute("attendanceSheet", attendanceSheet);
	         
	         int streak = calculateStreak(distinctDays);
	         model.addAttribute("streak", streak);
	         System.out.println("Current Streak: " + streak);
	         
	         
	         int daysPassed = currentDate.getDayOfMonth(); // Total days up to today
	         model.addAttribute("totalDaysInMonth", totalDaysInMonth);
	         model.addAttribute("daysPassed", daysPassed);

	         // Calculate absent days
	         int totalAbsentDays = daysPassed - totalPresent.intValue();
	         model.addAttribute("absentDays", totalAbsentDays);
	         System.out.println(totalAbsentDays);
	         
	         return "User/userAttendance.html";


	     } else {
	         model.addAttribute("error", "No user email found in session.");
	         List<MembershipDetails> md = mbRepo.findAll();
		     model.addAttribute("md", md);

	 		return "User/landing.html";
		     
	     }
	    
	 }

	 private int calculateStreak(List<Integer> distinctDays) {
		    if (distinctDays == null || distinctDays.isEmpty()) {
		        return 0; // No attendance records
		    }

		    // Sort the list in ascending order
		    Collections.sort(distinctDays);

		    // Initialize streak counter
		    int streak = 1; // At least one day (if list is not empty)

		    // Traverse the list from the second last element to the start
		    for (int i = distinctDays.size() - 1; i > 0; i--) {
		        // Check if the current day and the previous day differ by 1
		        if (distinctDays.get(i) - distinctDays.get(i - 1) == 1) {
		            streak++;
		        } else {
		            // Break the streak if there's a gap
		            break;
		        }
		    }

		    return streak;
		}

		
	@PostMapping("/attendance")
	public String postMethodName(@ModelAttribute AttendanceSheet a, HttpSession session, Model model) {
		if(session != null) {
		String email = (String) session.getAttribute("email");
		a.setDate(LocalDate.now());
		a.setEmail(email);
		aRepo.save(a);
		}
		
		 String userEmail = (String) session.getAttribute("email");

	     if (userEmail != null) {
	         // Fetch today's attendance record
	         Optional<AttendanceSheet> attendanceOptional = aRepo.findByDateAndEmail(LocalDate.now(), userEmail);

	         if (attendanceOptional.isPresent()) {
	             // Attendance exists for today
	             model.addAttribute("attendanceStatus", "Present");
	             System.out.println("Present");
	         } else {
	             // No attendance record for today
	             model.addAttribute("attendanceStatus", "Absent");
	             System.out.println("Absent");
	         }

	         // Check if attendance exists for today
	         AttendanceSheet attendanceSheet = attendanceOptional.orElse(null);

	         LocalDate currentDate = LocalDate.now();
	         int totalDaysInMonth = currentDate.lengthOfMonth(); // Dynamically calculates the days in the current month
	         model.addAttribute("totalDaysInMonth", totalDaysInMonth);
	         System.out.println(totalDaysInMonth);

	         Long totalPresent = aRepo.countCurrentMonthByEmail(userEmail);
	         model.addAttribute("presentDays", totalPresent);

	         // Fetch all attendance records for this user
	         model.addAttribute("attendanceList", aRepo.findByEmail(userEmail));

	         // Fetch distinct days from the date column for the specific email
	         List<Integer> distinctDays = aRepo.findDistinctDaysByEmail(userEmail);
	         model.addAttribute("distinctDays", distinctDays);
	         System.out.println(distinctDays);

	         // Pass attendance status to the model
	         model.addAttribute("attendanceSheet", attendanceSheet);
	         
	         int streak = calculateStreak(distinctDays);
	         model.addAttribute("streak", streak);
	         System.out.println("Current Streak: " + streak);
	         
	         
	         int daysPassed = currentDate.getDayOfMonth(); // Total days up to today
	         model.addAttribute("totalDaysInMonth", totalDaysInMonth);
	         model.addAttribute("daysPassed", daysPassed);

	         // Calculate absent days
	         int totalAbsentDays = daysPassed - totalPresent.intValue();
	         model.addAttribute("absentDays", totalAbsentDays);
	         System.out.println(totalAbsentDays);
	         return "User/userAttendance.html";


	     } else {
	         model.addAttribute("error", "No user email found in session.");
	         List<MembershipDetails> md = mbRepo.findAll();
		     model.addAttribute("md", md);

	 		return "User/landing.html";
	     }

		
	}
		
	 
	 
	 

	 
	 
	 
	 
	 
	 
	 
	 

	
	
	
	
	
	
	
	
	
	

}
