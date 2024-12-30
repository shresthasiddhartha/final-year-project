package Zest.gym.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



import Zest.gym.model.AttendanceSheet;
import Zest.gym.model.Trainer;
import Zest.gym.model.Video;
import Zest.gym.repository.AttendanceRepository;
import Zest.gym.repository.TrainerRepository;
import Zest.gym.repository.VideoRepository;
import jakarta.servlet.http.HttpSession;



@Controller
public class TrainerController {
	
	@Autowired
	private AttendanceRepository aRepo;
	
	@Autowired
	private TrainerRepository tRepo; 
	

	@Autowired
	private VideoRepository vRepo;
	
	
	@GetMapping("t-index")
	public String trainerIndex() {
		return "Trainer/Tindex.html";
	}
	
	@GetMapping("t-class-timetable")
	public String trainerClassTimetable() {
		return "Trainer/Tclass-timetable.html";
	}
	@GetMapping("t-editProfile")
	public String trainerEditProfile() {
		return "Trainer/TeditProfile.html";
	}
	@GetMapping("t-profile")
	public String trainerProfile(HttpSession session, @ModelAttribute Trainer t, Model model) {
	    String email = (String) session.getAttribute("email");
	    Optional<Trainer> tList = tRepo.findByEmail(email);
	    tList.ifPresent(trainer -> model.addAttribute("tList", trainer));  // Only add to model if present
	    return "Trainer/Tprofile.html";
	}

	@GetMapping("/editProfile/{id}")
	public String editProfile(@PathVariable int id, Model model) {
		Optional<Trainer> tList = tRepo.findById(id);
		model.addAttribute("tList", tList);
		return "Trainer/TeditProfile.html";
	}
	
	
	@PostMapping("/saveEditedprofile")
	public String postMethodName(HttpSession session, 
	                              @RequestParam int id,
	                              @RequestParam String name, 
	                              @RequestParam String address, 
	                              @RequestParam String email, 
	                              @RequestParam String contact,  
	                              Model model) {
	    
	    Optional<Trainer> t = tRepo.findById(id);
	    
	    if (t.isPresent()) {  // Ensure the Optional contains a value
	        Trainer trainer = t.get();  // Retrieve the actual Trainer object
	        trainer.setName(name);
	        trainer.setAddress(address);
	        trainer.setContact(contact);
	        trainer.setEmail(email);
	        tRepo.save(trainer);  // Save the updated trainer object
	        
	        // Get the email from the session and find the updated trainer details
	        String semail = (String) session.getAttribute("email");
	        Optional<Trainer> tList = tRepo.findByEmail(semail);
	        
	        model.addAttribute("tList", tList);
	        return "Trainer/Tprofile.html";  // Return the profile page with updated info
	    }

	    // If the trainer with the given id was not found
	    String semail = (String) session.getAttribute("email");
	    Optional<Trainer> tList = tRepo.findByEmail(semail);
	    model.addAttribute("tList", tList);
	    return "Trainer/Tprofile.html";  // Return the profile page if no trainer was found
	}

	@PostMapping("/saveProfile")
	public String saveProfile(@RequestParam("image") MultipartFile Fimage, 
	                          @RequestParam("id") int id, HttpSession session, 
	                          Model model) {
	    Optional<Trainer> trainerOpt = tRepo.findById(id);
	    
	    if (trainerOpt.isPresent()) {
	        Trainer trainer = trainerOpt.get();
	        
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
	                trainer.setImage(imageName);  // You can serve images from the static folder using this URL
	            } catch (IOException e) {
	                // If there was an error uploading, show an error message and redirect back
	                model.addAttribute("error", "Failed to upload image.");
	                String semail = (String) session.getAttribute("email");
	        	    Optional<Trainer> tList = tRepo.findByEmail(semail);
	        	    model.addAttribute("tList", tList);
	                return "Trainer/Tprofile.html";  
	            }
	        }

	        // Save the trainer data (including the updated image path)
	        tRepo.save(trainer);
	        
	        String semail = (String) session.getAttribute("email");
		    Optional<Trainer> tList = tRepo.findByEmail(semail);
		    model.addAttribute("tList", tList);
	        return "Trainer/Tprofile.html";   
	    }

	    // If the trainer with the given id was not found, return an error page or the form
	    model.addAttribute("error", "Trainer not found");
	    String semail = (String) session.getAttribute("email");
	    Optional<Trainer> tList = tRepo.findByEmail(semail);
	    model.addAttribute("tList", tList);
        return "Trainer/Tprofile.html";   
	}

	
	
	
	
	
	@GetMapping("t-video")
	public String trainerVideo(@ModelAttribute Video v, Model model) {
		List<Video> vList = vRepo.findAll();
		model.addAttribute("vList", vList);
		return "Trainer/Tvideo.html";
	}
	
	@GetMapping("t-activities")
	public String trainerActivities() {
		return "Trainer/Tactivities.html";
	}
	
	
	
	@GetMapping("/t-attendance")
	public String trainerAttendancepage(HttpSession session, Model model) {
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


	     } else {
	         model.addAttribute("error", "No user email found in session.");
	     }
		return "Trainer/Tattendance.html";
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

	
	@PostMapping("/t-attendance")
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


	     } else {
	         model.addAttribute("error", "No user email found in session.");
	     }
		return "Trainer/Tattendance.html";
	}
	
	@GetMapping("/addTVideo")
	public String addVideo() {
	
		
		return "Admin/addVideo.html";
		
		
	}
	
	
	@PostMapping("/addTVideo")
	public String addVideoData(@ModelAttribute Video v, Model model) {
		
		vRepo.save(v);
		List<Video> vList = vRepo.findAll();
		model.addAttribute("vList", vList);
		return "Trainer/Tvideo.html";
	}

}
