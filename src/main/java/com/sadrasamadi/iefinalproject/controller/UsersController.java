package com.sadrasamadi.iefinalproject.controller;

import com.sadrasamadi.iefinalproject.dto.MessageDto;
import com.sadrasamadi.iefinalproject.model.Message;
import com.sadrasamadi.iefinalproject.model.Participation;
import com.sadrasamadi.iefinalproject.model.User;
import com.sadrasamadi.iefinalproject.repository.MessageRepository;
import com.sadrasamadi.iefinalproject.repository.ParticipationRepository;
import com.sadrasamadi.iefinalproject.repository.UserRepository;
import com.sadrasamadi.iefinalproject.service.StorageService;
import com.sadrasamadi.iefinalproject.util.SecurityUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/users")
public class UsersController {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private StorageService storageService;

    private MessageRepository messageRepository;

    private ParticipationRepository participationRepository;

    @Autowired
    public UsersController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           StorageService storageService,
                           MessageRepository messageRepository,
                           ParticipationRepository participationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
        this.messageRepository = messageRepository;
        this.participationRepository = participationRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid User user,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirect) {
        String email = user.getEmail();
        String password = user.getPassword();
        String matchPassword = user.getMatchPassword();
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        } else if (userRepository.existsByEmail(email)) {
            result.rejectValue("email", "error.email", "حساب کاربری با این ایمیل موجود می باشد");
            model.addAttribute("user", user);
            return "register";
        } else if (!Objects.equals(password, matchPassword)) {
            result.rejectValue("password", "error.password", "کلمه های عبور یکسان نیستند");
            model.addAttribute("user", user);
            return "register";
        } else {
            String encoded = passwordEncoder.encode(password);
            user.setPassword(encoded);
            user.setAvatar("/img/avatar.jpg");
            user.setRole(User.Role.USER);
            userRepository.save(user);
            redirect.addFlashAttribute("success", "حساب کاربری با موفقیت ایجاد شد، لطفا وارد شوید...");
            return "redirect:/users/login";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = SecurityUtility.currentUser();
        model.addAttribute("user", user);
        List<Participation> participations = participationRepository.findAllByUser(user);
        model.addAttribute("participations", participations);
        if (user.getRole() == User.Role.ADMIN) {
            List<Message> messages = messageRepository.findAllByReceiverOrderByDateDesc(user);
            model.addAttribute("messages", messages);
        }
        return "profile";
    }

    @GetMapping("/edit")
    public String edit(Model model) {
        User user = SecurityUtility.currentUser();
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PutMapping
    public String update(@ModelAttribute User user,
                         BindingResult result,
                         @RequestParam MultipartFile file,
                         Model model,
                         RedirectAttributes redirect) {
        User old = SecurityUtility.currentUser();
        if (user.getName().isEmpty())
            result.rejectValue("name", "error.name", "لطفا نام خود را وارد کنید");
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "profile-edit";
        }
        if (file != null && !file.isEmpty()) {
            String avatar = old.getAvatar();
            String name = storageService.save(file);
            old.setAvatar(name);
            storageService.delete(avatar);
        }
        old.setName(user.getName());
        old.setPhone(user.getPhone());
        userRepository.save(old);
        redirect.addFlashAttribute("profileUpdated", "حساب کاربری با موفقیت ویرایش شد");
        return "redirect:/users/profile";
    }

    @ResponseBody
    @PostMapping("/msg")
    public ResponseEntity<Void> msg(@RequestBody MessageDto dto) {
        String text = dto.getText();
        if (text.isEmpty())
            return ResponseEntity.badRequest().build();
        long user = dto.getUser();
        Optional<User> optional = userRepository.findById(user);
        if (!optional.isPresent())
            return ResponseEntity.notFound().build();
        User sender = SecurityUtility.currentUser();
        User receiver = optional.get();
        Message message = new Message();
        message.setDate(new Date());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        messageRepository.save(message);
        return ResponseEntity.ok().build();
    }

}
