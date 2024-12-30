package Zest.gym.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;


import Zest.gym.model.Diet;
import Zest.gym.model.Trainer;
import Zest.gym.model.MembershipDetails;
import Zest.gym.model.Schedule;
import Zest.gym.model.Video;
import Zest.gym.repository.AttendanceRepository;
import Zest.gym.repository.DietRepository;
import Zest.gym.repository.MembershipDetailsRepository;
import Zest.gym.repository.ScheduleRepository;
import Zest.gym.repository.TrainerRepository;
import Zest.gym.repository.UserRepository;
import Zest.gym.repository.VideoRepository;
import jakarta.servlet.http.HttpSession;


@Controller
public class AdminController {
	@Autowired
	private MembershipDetailsRepository mRepo;
	
	@Autowired
	private TrainerRepository tRepo;
	
	@Autowired
	private VideoRepository vRepo;
	
	@Autowired
	private DietRepository dRepo;
	
	@Autowired
	private UserRepository uRepo;
	
	@Autowired 
	private ScheduleRepository sRepo;
	
	@Autowired
	private AttendanceRepository aRepo;
	
	@GetMapping("/adminDash")
	public String admminDashboard(HttpSession session) {
		if ( session.getAttribute("username")!= null) {
			return "Admin/index.html";
		}
		return "Admin/adminLogin.html";
		
		
	}
	
	
	@GetMapping("/admin")
	public String admminLoginPage() {
		return "Admin/adminLogin.html";
	}
	
	@PostMapping("adminLogin")
	public String adminLogin(HttpSession session, @RequestParam("username") String username, @RequestParam("password") String password) {
		if(username.equals("admin") && password.equals("admin@123")) {
			session.setAttribute("username",username);
			session.setMaxInactiveInterval(1300);
			return "Admin/index.html";
		}
		return "Admin/adminLogin.html";
		
		
	}
	
	@GetMapping("/manageMembership")
	public String addMembership() {
		return "Admin/addMembership.html";
	}
	
	@PostMapping("/addMembership")
	public String addMembershipDetails(@ModelAttribute MembershipDetails m) {
		
		mRepo.save(m);
		
		return "Admin/addMembership.html";
	}
	
	@GetMapping("/addTrainer")
	public String addTrainer() {
		return "Admin/addTrainer.html";
	}
	
	@PostMapping("/addTrainer")
	public String addTrainerData(
	        @ModelAttribute Trainer trainer,
	        @RequestParam("Trainerimage") MultipartFile Fimage, @RequestParam("name") String name, @RequestParam("email") String email,  @RequestParam("contact") String contact, @RequestParam("address") String address ) throws IOException {

		
		
		trainer.setName(name);
        trainer.setEmail(email);
        trainer.setContact(contact);
        trainer.setAddress(address);
        
        String uploadDir = Paths.get("src", "main", "resources", "static", "assets").toString();

	    // Process image
	    if (!Fimage.isEmpty()) {
	        try {
	            // Get the original filename
	            String imageName = StringUtils.cleanPath(Fimage.getOriginalFilename());
	            
	            // Create a path for the image file in the upload directory
	            Path imagePath = Paths.get(uploadDir, imageName);

	            // Create the directory if it doesn't exist
	            Files.createDirectories(imagePath.getParent());

	            // Save the image file
	            Fimage.transferTo(imagePath);

	            // Save the image path to the user object (the URL should be relative for serving)
	            trainer.setImage(imageName);  // You can serve images from static folder using this URL
	        } catch (IOException e) {
//	            model.addAttribute("error", "Failed to upload image.");
	            return "redirect:/addTrainer";  // Return to the form if there was an error
	        }
	    }

        tRepo.save(trainer);
	    
	    return "Admin/addTrainer.html";
	}

	@GetMapping("/addVideo")
	public String addVideo() {
		return "Admin/addVideo.html";
	}
	
	
	@PostMapping("/addVideo")
	public String addVideoData(@ModelAttribute Video v, HttpSession session) {
		session.getAttribute("username");
		System.out.println(session.getAttribute("username"));
		vRepo.save(v);
		return "Admin/addVideo.html";
	}
	
	
	@GetMapping("/manageAttendance")
	public String manageAttendance(Model model) {
		List<Object[]> distinctAttendance = aRepo.findAttendanceByDistinctEmail();

	    // Prepare a model attribute for attendance data
	    List<Map<String, Object>> attendanceData = new ArrayList<>();

	    for (Object[] result : distinctAttendance) {
	        Map<String, Object> data = new HashMap<>();
	        data.put("email", result[0]);  // Email address
	        data.put("attendanceCount", result[1]);  // Attendance count
	        attendanceData.add(data);
	    }

	    // Add data to the model
	    model.addAttribute("attendanceData", attendanceData);
		return "Admin/manageAttendance.html";
	}
	
	@GetMapping("/manageDiet")
	public String manageDiet(Model model) {
		
		List<Diet> dList = dRepo.findAll();
		model.addAttribute("dList", dList);
		return "Admin/manageDiet.html";
	}
	
	
	@GetMapping("/manageTrainer")
	public String manageTraine(Model model) {
		List<Trainer> tList = tRepo.findAll();
		model.addAttribute("tList", tList);
		
		return "Admin/manageTrainer.html";
	}
	
	@GetMapping("/manageUser")
	public String manageUser(Model model) {
		List<Zest.gym.model.User> users = uRepo.findAll(); // Fetch all users from the database
	    model.addAttribute("users", users); // Add the user list to the model
		return "Admin/manageUser.html";
	}
	
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable int id, Model model) {
	    uRepo.deleteById(id); // Delete user by ID
	    List<Zest.gym.model.User> users = uRepo.findAll(); // Fetch all users from the database
	    model.addAttribute("users", users);
	    return "redirect:/manageUser"; // Redirect to the user management page
	}
	
	@GetMapping("/deleteDiet/{id}")
	public String deleteDiet(@PathVariable int id, Model model) {
	    dRepo.deleteById(id); // Delete user by ID
		List<Diet> dList = dRepo.findAll();
		model.addAttribute("dList", dList);
	    return "redirect:/manageDiet"; // Redirect to the user management page
	}
	
	@GetMapping("/deleteTrainer/{id}")
	public String deleteTrainer(@PathVariable int id, Model model) {
	    tRepo.deleteById(id); // Delete user by ID
	    List<Trainer> tList = tRepo.findAll();
		model.addAttribute("tList", tList);
	    return "redirect:/manageTrainer"; // Redirect to the user management page
	}
	
	@GetMapping("/deleteVideo/{id}")
	public String deleteVideo(@PathVariable int id, Model model) {
	    vRepo.deleteById(id); // Delete user by ID
	    List<Video> vList = vRepo.findAll();
		model.addAttribute("vList", vList);
	    return "redirect:/manageVideo"; // Redirect to the user management page
	}
	
	@PostMapping("/updateRole")
	public String updateUserRole(@RequestParam("id") int id, @RequestParam("role") String role, Model model) {
	    Optional<Zest.gym.model.User> optionalUser = uRepo.findById(id); // Find the user by ID
	    if (optionalUser.isPresent()) {
	        Zest.gym.model.User user = optionalUser.get();
	        user.setRole(role); // Update the role
	        List<Zest.gym.model.User> users = uRepo.findAll(); // Fetch all users from the database
		    model.addAttribute("users", users);
	        uRepo.save(user); // Save the changes
	    }
	    return "redirect:/manageUser"; // Redirect back to the manage user page
	}


	@GetMapping("/manageVideo")
	public String manageVideo(Model model) {
		
		List<Video> vList = vRepo.findAll();
		model.addAttribute("vList", vList);
		return "Admin/manageVideo.html";
	}
	
	@GetMapping("/addDiet")
	public String addDiet() {
		return "Admin/addDiet.html";
	}
	
	@PostMapping("/addDiet")
	public String addDietData(@ModelAttribute Diet d, @RequestParam("dietImage") MultipartFile image) {
		String uploadDir = Paths.get("src", "main", "resources", "static", "assets").toString();

	    // Process image
	    if (!image.isEmpty()) {
	        try {
	            // Get the original filename
	            String imageName = StringUtils.cleanPath(image.getOriginalFilename());
	            
	            // Create a path for the image file in the upload directory
	            Path imagePath = Paths.get(uploadDir, imageName);

	            // Create the directory if it doesn't exist
	            Files.createDirectories(imagePath.getParent());

	            // Save the image file
	            image.transferTo(imagePath);

	            // Save the image path to the user object (the URL should be relative for serving)
	            d.setImage(imageName);  // You can serve images from static folder using this URL
	        } catch (IOException e) {
//	            model.addAttribute("error", "Failed to upload image.");
	            return "redirect:/addTrainer";  // Return to the form if there was an error
	        }
	    }

	    dRepo.save(d);
		return "Admin/addDiet.html";
	}
	
	
	
	
	
	@GetMapping("/manageSchedule")
	 public String addSchedule(Model model) {
	
	 List<Trainer> trainers = tRepo.findAll();
     model.addAttribute("trainers", trainers);
	 	return "Admin/addSchedule.html";
	 }
	 
	 @PostMapping("/addSchedule")
	 public String addScheduleData(@ModelAttribute Schedule s, Model model) {
	 	sRepo.save(s);
	 	 List<Trainer> trainers = tRepo.findAll();
	     model.addAttribute("trainers", trainers);
		return "Admin/addSchedule.html";
	 }
	 
	 @GetMapping("adminLogout")
	 public String adminLogout(HttpSession session) {
	 	session.invalidate();
	 	
	 	return "Admin/adminLogin.html";
	 }
	 
	
	
	

}
