package Controller;

import Model.User;
import Repository.MeterRepository;
import Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class MainController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeterRepository meterRepository;

    private User user;

    @GetMapping("/")
    public String main(Map<String, Object> model) throws Exception
    {
        user =checkUser(user, userRepository);
        model.put("user", user);
        return "main";
    }

    public static User checkUser(User user, UserRepository userRepository) throws Exception
    {
        if(user != null) return user;
        User u = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        if(u == null) throw new Exception("User not found, but already authorized!");
        return u;
    }

}
