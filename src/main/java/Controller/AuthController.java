package Controller;

import Model.User;
import Repository.MeterRepository;
import Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class AuthController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeterRepository meterRepository;

    private User user;

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

    @PostMapping("/login")
    public String login(String message, Map<String, Object> model)
    {
        model.put("message", message);
        return "login";
    }

    @GetMapping("/registration")
    public String registration()
    {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(User user, Map<String, Object> model)
    {
        if(user != null)
        {
            if(userRepository.findByName(user.getName()) != null)
            {
                model.put("message", "Пользователь с таким логином уже зарегистрированн!");
                return "registration";
            }
            else if(user.getPassword().isEmpty())
            {
                model.put("message", "Пароль не может быть пустым!");
                return "registration";
            }
            else if(user.getName().isEmpty())
            {
                model.put("message", "Имя не может быть пустым!");
                return registration();
            }
            else
            {
                user.setActive(true);
                user.setAuthority(0);
                new User(user, userRepository, meterRepository);
                model.put("message", "Пользователь успешно зарегистрирован!");
                return "login";
            }
        }
        return "registration";
    }

    @GetMapping("/user_cabinet")
    public String user_cabinet()
    {
        return "user_cabinet";
    }

    @PostMapping("/user_cabinet")
    public String user_cabinet(String message, Map<String, Object> model)
    {
        return "user_cabinet";
    }

    @PostMapping("/pass_change")
    public String pass_change(String newpassword, String againpassword, Map<String, Object> model)
    {
        if(!newpassword.equals(againpassword))
        {
            model.put("message","Новый пароль и его подтверждение не совпадают!");
            return "user_cabinet";
        }
        else
        {
            if (newpassword.isEmpty())
            {
                model.put("message", "Новый пароль не может быть пустым!");
                return "user_cabinet";
            }
            try
            {
                user = MainController.checkUser(user, userRepository);
            }
            catch (Exception ex)
            {
                model.put("message", "Произошла внутрення ошибка!");
                return "user_cabinet";
            }
            user.setPassword(newpassword);
            userRepository.saveAndFlush(user);
            model.put("message", "Пароль успешно изменен!");
            return "user_cabinet";
        }
    }

}