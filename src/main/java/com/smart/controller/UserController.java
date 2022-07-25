package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    //method for adding common data to response
    //executes before calling every method
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();

        User user = userRepository.getUserByUserName(userName);
        model.addAttribute("user", user);

    }

    @GetMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard");
        return "user/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String addContact(Model model) {
        model.addAttribute("title", "AddContact");
        model.addAttribute("contact", new Contact());
        return "user/addContact";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, Model model, HttpSession session) {
        try {
            User user=userRepository.getUserByUserName(principal.getName());

            //processing and uploading file
            if(file.isEmpty()){
                contact.setImageUrl("defaultImage.png");

            }
            else{
                //upload the file to folder
                contact.setImageUrl(file.getOriginalFilename());

                File saveFile=new ClassPathResource("static/images").getFile();
                Path target=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            }

            user.getContacts().add(contact);
            contact.setUser(user);
            userRepository.save(user);
            model.addAttribute("contact", contact);
            session.setAttribute("message",new Message("Contact Added Succesfuuly", "alert-success"));
            return "user/addContact";

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message",new Message("Something Went Wrong , Contact Not Added", "alert-danger"));
            return "user/addContact";
        }
    }

    //pagination
    //per page 5 contacts
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal){
        model.addAttribute("title","Contacts");
        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);

        //curent page and contacts per page
        Pageable pageable=PageRequest.of(page, 5);
        Page<Contact> contactList=contactRepository.findContactsByUserId(user.getId(), pageable);
        model.addAttribute("contacts",contactList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactList.getTotalPages());

        return "user/show_contacts";
    }

    //showing particular contact
    @GetMapping("/contact/{cId}")
    public String viewContact(@PathVariable("cId") Integer cId, Model model, Principal principal){
        Contact contact=contactRepository.getContactByCId(cId);
        String userName=principal.getName();
        User user=userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId())
            model.addAttribute("contact",contact);
        else
            model.addAttribute("notify","No Contact Found");
        return "user/view_contact";
    }
}
