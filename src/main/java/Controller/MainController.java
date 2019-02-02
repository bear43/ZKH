package Controller;

import Model.Meter;
import Model.MeterType;
import Model.User;
import Repository.MeterRepository;
import Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        user = checkUser(user, userRepository);
        model.put("user", user);
        model.put("meters", user.getMeterSet());
        return "main";
    }

    @PostMapping("/update")
    public String update(String type, String value, String cost, Map<String, Object> model)
    {
        try
        {
            Meter in = new Meter(MeterType.valueOf(type), Double.parseDouble(value), Double.parseDouble(cost), null);
            Meter m = user.getMeterByType(in.getType());
            m.copy(in);
            meterRepository.saveAndFlush(m);
        }
        catch(Exception ex)
        {
            model.put("message", "Ошибка: " + ex.getLocalizedMessage());
        }
        return "redirect:/";
    }

    public static User checkUser(User user, UserRepository userRepository) throws Exception
    {
        if(user != null) return user;
        User u = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        if(u == null) throw new Exception("User not found, but already authorized!");
        return u;
    }

}
