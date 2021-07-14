package notes.controller;

import notes.entity.SignupEntity;
import notes.entity.UserEntity;
import notes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SignupController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("signupEntity",  new SignupEntity());
        return "signup";
    }

    @PostMapping("/signup")
    public String sendSignupRequest(@ModelAttribute("signupEntity") SignupEntity signupEntity, Model model) {
        UserEntity user = userRepository.findByUsername(signupEntity.getUsername());
        if (user == null) {
            userRepository.save(new UserEntity(signupEntity.getUsername(), bCryptPasswordEncoder.encode(signupEntity.getPassword())));
        } else {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            model.addAttribute("signupEntity", new SignupEntity());
            return "signup";
        }
        return "redirect:signin";
    }

    @GetMapping("/signout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:signin";
    }


}
