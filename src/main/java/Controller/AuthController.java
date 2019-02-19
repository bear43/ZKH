package Controller;

import Model.User;
import Repository.MeterRepository;
import Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class AuthController
{
    private final UserRepository userRepository;

    private final MeterRepository meterRepository;

    private User user;

    @Autowired
    public AuthController(UserRepository userRepository, MeterRepository meterRepository)
    {
        this.userRepository = userRepository;
        this.meterRepository = meterRepository;
    }

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
    public String registration(User user, Map<String, Object> model, RedirectAttributes attr)
    {
        if(user != null)
        {
            Pattern p = Pattern.compile("^(7|8|\\+7)\\d{10}$");
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
                return "registration";
            }
            else
            {
                Matcher m = p.matcher(user.getNumber());
                if(m.matches())
                {
                    user.setNumber(user.getNumber());
                    user.setActive(true);
                    user.setAuthority(0);
                    try
                    {
                        new User(user, userRepository);
                    }
                    catch (Exception ex)
                    {
                        model.put("message", "Произошла ошибка при создании пользователя!");
                        return "registration";
                    }
                    attr.addFlashAttribute("message", "Пользователь успешно зарегистрирован!");
                    return "redirect:/login";
                }
                else
                {
                    model.put("message", "Введен неверный номер телефона!");
                    return "registration";
                }
            }
        }
        return "registration";
    }

    @GetMapping("/user_cabinet")
    public String user_cabinet(Map<String, Object> model, RedirectAttributes attr)
    {
        try
        {
            user = MainController.checkUser(user, userRepository, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            model.put("message", "Произошла ошибка! Ваш аккаунт отсутствует в базе данных, пожалуйста, перезайдите");
            return "user_cabinet";
        }
        model.putIfAbsent("user", user);
        if(!user.isNumberActivated())
            model.putIfAbsent("message", "Номер не подтвержден!");
        return "user_cabinet";
    }

    @PostMapping("/user_cabinet")
    public String user_cabinet(String message, Map<String, Object> model)
    {
        return "user_cabinet";
    }

    @PostMapping("/pass_change")
    public String pass_change(String newpassword, String againpassword, Map<String, Object> model, RedirectAttributes attr)
    {
        if(!newpassword.equals(againpassword))
        {
            attr.addFlashAttribute("message","Новый пароль и его подтверждение не совпадают!");
            return "redirect:/user_cabinet";
        }
        else
        {
            if (newpassword.isEmpty())
            {
                attr.addFlashAttribute("message", "Новый пароль не может быть пустым!");
                return "redirect:/user_cabinet";
            }
            try
            {
                user = MainController.checkUser(user, userRepository, true);
            }
            catch (Exception ex)
            {
                attr.addFlashAttribute("message", "Произошла внутрення ошибка!");
                return "redirect:/user_cabinet";
            }
            user.setPassword(newpassword);
            userRepository.saveAndFlush(user);
            attr.addFlashAttribute("message", "Пароль успешно изменен!");
            return "redirect:/user_cabinet";
        }
    }

}