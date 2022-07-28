package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.GeneratedValue;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("user",new User());
        return "signup";
    }
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("title","login");
        return "login";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(name = "agreed", defaultValue = "false") boolean agreed, @RequestParam("imageUrl") MultipartFile file, Model model, HttpSession session){
        try {
            if(!agreed){
                System.out.println("Accept erms and conditions");
                throw new Exception("Please accept terms and conditions");
            }
            if(result.hasErrors()){
                System.out.println(result);
                model.addAttribute("user", user);
                return "signup";
            }
            if(file.isEmpty()){
                user.setImageUrl("defaultImage.png");
            }
            else{
                user.setImageUrl(file.getOriginalFilename());

                File saveFile=new ClassPathResource("static/images").getFile();
                Path target= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            session.setAttribute("message", new Message("Succesffuly registered ","alert-success"));
            model.addAttribute("user",new User());
            return "signup";
        }catch (Exception e){
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
            return "signup";

        }
    }
}
