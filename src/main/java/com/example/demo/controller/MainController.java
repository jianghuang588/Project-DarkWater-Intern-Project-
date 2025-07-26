package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordDto;
import com.example.demo.dto.UpdateProfileDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import com.example.demo.repository.UserRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.NewsPostDto;
import com.example.demo.service.NewsService;
import java.util.ArrayList;
import java.util.List;

/**
 * Main MVC controller for web pages
 * Handles traditional server-side rendered views with session management
 * Different from REST controllers - returns HTML views instead of JSON
 */
@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsService newsService;



    /**
     * Modified home page - check for splash screen first
     */
    @GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        // Check if splash should be shown
        Cookie[] cookies = request.getCookies();
        boolean splashShown = false;
        boolean splashDisabled = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("splashShown".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                    splashShown = true;
                }
                if ("splashDisabled".equals(cookie.getName()) && "true".equals(cookie.getValue())) {
                    splashDisabled = true;
                }
            }
        }

        // If splash is disabled or was shown recently, go to home
        if (splashDisabled || splashShown) {
            return "redirect:/home";
        }

        // Otherwise show splash
        return "redirect:/splash";
    }

    /**
     * Splash screen - shows periodically based on cookies
     */
    @GetMapping("/splash")
    public String splashPage() {
        return "splash";
    }

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    /**
     * Process login form submission
     * @param username - user's username
     * @param password - user's password
     * @param session - HTTP session to store logged-in user
     * @return redirect to home on success, back to login with error on failure
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        if (userService.validateUser(username, password)) {
            // Store username in session to track logged-in user
            session.setAttribute("username", username);
            return "redirect:/home";
        }
        return "redirect:/login?error";
    }

    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Process registration form
     * Validates email (Gmail only), username format, and password match
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           HttpSession session) {
        // Check passwords match
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password";
        }

        // Validate email format using regex
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "redirect:/register?error=email";
        }

        // Restrict to Gmail accounts only
        if (!email.endsWith("@gmail.com")) {
            return "redirect:/register?error=gmail";
        }

        // Username must be 3-20 chars, alphanumeric + underscore
        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            return "redirect:/register?error=username";
        }

        if (userService.registerUser(username, email, password)) {
            // Auto-login after successful registration
            session.setAttribute("username", username);
            return "redirect:/home";
        }
        return "redirect:/register?error=exists";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);

        // Check if user has any staff role
        boolean isAdmin = false;
        boolean isSupportStaff = false;
        boolean isModerator = false;

        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                UserRole role = user.getRole();

                // DEBUG OUTPUT - ADD THESE LINES
                System.out.println("DEBUG: User " + username + " has role: " + role);
                System.out.println("DEBUG: Available roles: " + java.util.Arrays.toString(UserRole.values()));

                isAdmin = (role == UserRole.ADMIN || role == UserRole.SUPER_ADMIN);

                // DEBUG OUTPUT - ADD THIS LINE
                System.out.println("DEBUG: isAdmin = " + isAdmin);

                isSupportStaff = (role == UserRole.SUPPORT_STAFF);
                isModerator = (role == UserRole.MODERATOR);
            } else {
                System.out.println("DEBUG: User not found in database!");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error checking role: " + e.getMessage());
        }

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isSupportStaff", isSupportStaff);
        model.addAttribute("isModerator", isModerator);

        // Add news data for home page
        try {
            List<NewsPostDto> carouselNews = newsService.getRecentNewsForCarousel();
            model.addAttribute("carouselNews", carouselNews);

            NewsPostDto latestPatch = newsService.getLatestMajorPatchNote();
            model.addAttribute("latestPatch", latestPatch);
        } catch (Exception e) {
            model.addAttribute("carouselNews", new ArrayList<>());
            model.addAttribute("latestPatch", null);
        }

        return "home";
    }



    /**
     * Protected content page
     */
    @GetMapping("/content")
    public String content(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        return "content";
    }

    /**
     * Logout - clear session and redirect to login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Password reset page - displays form with token if provided
     * @param token - reset token from email link (optional)
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        if (token != null) {
            model.addAttribute("token", token);
        }
        return "reset-password";
    }

    /**
     * Forgot password page - request password reset
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    /**
     * Process forgot password request - sends reset email
     */
    @PostMapping("/forgot-password")
    public String forgotPasswordSubmit(@RequestParam String email) {
        try {
            userService.initiatePasswordReset(email);
            return "redirect:/forgot-password?success=true";
        } catch (Exception e) {
            return "redirect:/forgot-password?error=true";
        }
    }

    /**
     * Email verification success page
     */
    @GetMapping("/email-verified")
    public String emailVerified() {
        return "email-verified";
    }

    /**
     * API documentation page
     */
    @GetMapping("/api-docs")
    public String apiDocs() {
        return "api-docs";
    }

    /**
     * Admin dashboard - requires admin role
     * Regular users redirected to home
     */
    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        // Check admin privileges
        if (!userService.isAdmin(username)) {
            return "redirect:/home";
        }
        model.addAttribute("username", username);
        return "admin";
    }

    /**
     * Admin news management page - requires admin role
     */
    @GetMapping("/admin/news")
    public String adminNewsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        // Check admin privileges
        if (!userService.isAdmin(username)) {
            return "redirect:/home";
        }
        model.addAttribute("username", username);
        return "admin-news";
    }

    /**
     * User profile page - displays current user info
     */
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        // Load full user details for display
        model.addAttribute("user", userService.getUserByUsername(username));
        return "profile";
    }

    /**
     * Profile edit page - pre-filled with current user data
     */
    @GetMapping("/profile/edit")
    public String editProfilePage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            UserResponseDto user = userService.getUserByUsername(username);
            model.addAttribute("user", user);
        } catch (Exception e) {
            return "redirect:/profile";
        }

        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String newUsername,
                                @RequestParam String newEmail,
                                HttpSession session) {
        String currentUsername = (String) session.getAttribute("username");
        if (currentUsername == null) {
            return "redirect:/login";
        }

        // Validate email format
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "redirect:/profile/edit?error=Invalid email format";
        }

        // Gmail only restriction
        if (!newEmail.endsWith("@gmail.com")) {
            return "redirect:/profile/edit?error=Only Gmail addresses allowed";
        }

        try {
            UpdateProfileDto dto = new UpdateProfileDto();
            // Keep the same username - don't allow changes
            dto.setUsername(currentUsername);
            dto.setEmail(newEmail);

            userService.updateProfile(currentUsername, dto);

            return "redirect:/profile?success=true";
        } catch (Exception e) {
            return "redirect:/profile/edit?error=" + e.getMessage();
        }
    }

    /**
     * Change password page
     */
    @GetMapping("/profile/change-password")
    public String changePasswordPage(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        return "change-password";
    }

    /**
     * Process password change
     * Validates current password and confirms new password match
     */
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        // Check new passwords match
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/profile/change-password?error=mismatch";
        }

        try {
            ChangePasswordDto dto = new ChangePasswordDto();
            dto.setCurrentPassword(currentPassword);
            dto.setNewPassword(newPassword);
            dto.setConfirmPassword(confirmPassword);

            userService.changePassword(username, dto);
            return "redirect:/profile?passwordChanged=true";
        } catch (Exception e) {
            return "redirect:/profile/change-password?error=invalid";
        }
    }

    /**
     * About Project Darkwater page
     */
    @GetMapping("/about")
    public String aboutPage() {
        return "about";
    }

    /**
     * FAQ page
     */
    @GetMapping("/faq")
    public String faqPage() {
        return "faq";
    }

    /**
     * Interactive Roadmap page
     */
    @GetMapping("/roadmap")
    public String roadmapPage() {
        return "roadmap";
    }

    /**
     * News page - Blog-post style news articles
     */
    @GetMapping("/news")
    public String newsPage() {
        return "news";
    }

    /**
     * Release Notes page - Game updates and patch notes
     */
    @GetMapping("/release-notes")
    public String releaseNotesPage() {
        return "release-notes";
    }

    /**
     * Staff Panel page - for crew members only
     */
    @GetMapping("/staff")
    public String staffPanel(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        // Check if user has staff privileges
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/home";
        }

        UserRole role = user.getRole();
        boolean hasStaffAccess = (role == UserRole.ADMIN ||
                role == UserRole.SUPER_ADMIN ||
                role == UserRole.SUPPORT_STAFF ||
                role == UserRole.MODERATOR);

        if (!hasStaffAccess) {
            return "redirect:/home";
        }

        model.addAttribute("username", username);
        model.addAttribute("userRole", role.toString());
        return "staff-panel";
    }

    /**
     * Apply page - for non-staff users
     */
    @GetMapping("/apply")
    public String applyPage() {
        return "apply";
    }

    /**
     * Play page - Game launcher or download page
     */
    @GetMapping("/play")
    public String playPage() {
        return "play";
    }

    /**
     * Help page
     */
    @GetMapping("/help")
    public String helpPage() {
        return "help";
    }

    /**
     * Settings page - account and app preferences
     */
    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            UserResponseDto user = userService.getUserByUsername(username);
            model.addAttribute("username", username);
            model.addAttribute("user", user);
            return "settings";  // This will look for settings.html template
        } catch (Exception e) {
            System.err.println("Error loading user for settings: " + username + " - " + e.getMessage());
            return "redirect:/profile?error=Could not load settings";
        }
    }
}