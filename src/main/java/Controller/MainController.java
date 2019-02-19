package Controller;

import Model.Meter;
import Util.MeterType;
import Model.User;
import Repository.MeterRepository;
import Repository.PaymentRepository;
import Repository.UserRepository;
import Util.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

@Controller
public class MainController
{
    private final UserRepository userRepository;

    private final MeterRepository meterRepository;

    private final PaymentRepository paymentRepository;

    private User user = null;

    private int activationCode = -1;

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public MainController(UserRepository userRepository, MeterRepository meterRepository, PaymentRepository paymentRepository)
    {
        this.userRepository = userRepository;
        this.meterRepository = meterRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/")
    public String main(Map<String, Object> model) throws Exception
    {
        user = checkUser(user, userRepository, true);
        model.put("user", user);
        model.put("meters", user.getMeterSet());
        return "main";
    }

    @PostMapping("/update")
    public String update(String type, String value, String cost, Map<String, Object> model, RedirectAttributes attr)
    {
        try
        {
            Meter in = new Meter(MeterType.getTypeByTitle(type), Double.parseDouble(value), Double.parseDouble(cost), null);
            Meter m = user.getMeterByType(in.getType());
            m.copy(in);
            meterRepository.saveAndFlush(m);
        }
        catch(Exception ex)
        {
            attr.addFlashAttribute("message", "Ошибка: " + ex.getLocalizedMessage());
        }
        return "redirect:/";
    }

    public static User checkUser(User user, UserRepository userRepository, boolean update) throws Exception
    {
        if(user != null)
        {
            if (update)
                user = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
            return user;
        }
        User u = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        if(u == null) throw new Exception("User not found, but already authorized!");
        return u;
    }

    private String checkUser(boolean update)
    {
        try
        {
            user = checkUser(user, userRepository, update);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Произошла ошибка! Перезайдите, пожалуйста, в аккаунт!";
        }
        return null;
    }

    @GetMapping("/activate_mobile")
    public String activate_mobile(Map<String, Object> model, RedirectAttributes attr)
    {
        String msg = checkUser(true);
        if(msg != null)
        {
            attr.addFlashAttribute("message", msg);
            return "redirect:/user_cabinet";
        }
        if(user.isNumberActivated())
        {
            attr.addFlashAttribute("message", "Номер телефона уже активирован!");
            return "redirect:/user_cabinet";
        }
        activationCode = 10000 + new Random().nextInt(10000);
        try
        {
            Messenger.sendMessage(user.getNumber(), "Код активации: " + activationCode);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            attr.addFlashAttribute("message", "Не удалось отправить сообщение на указаный телефон!");
            return "redirect:/user_cabinet";
        }
        model.put("user", user);
        return "activate_mobile";
    }

    @PostMapping("/activate_mobile")
    public String activate_mobile(String code, Map<String, Object> model, RedirectAttributes attr)
    {
        String msg = checkUser(true);
        if(msg != null)
        {
            attr.addFlashAttribute("message", msg);
            return "redirect:/user_cabinet";
        }
        int localCode;
        try
        {
            localCode = Integer.parseInt(code);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            attr.addFlashAttribute("message", "Вы ввели неверный код активации!");
            return "redirect:/user_cabinet";
        }
        if(activationCode == -1)
        {
            model.put("message", "Произошла ошибка на стороне сервера! Попробуйте перезагрузить страницу!");
        }
        else if(localCode == activationCode)
        {
            user.setNumberActivated(true);
            userRepository.saveAndFlush(user);
            attr.addFlashAttribute("message", "Номер телефона успешно активирован!");
        }
        return "redirect:/user_cabinet";
    }

    @PostMapping("/number_change")
    public String number_change(String newnumber, Map<String, Object> model, RedirectAttributes attr)
    {
        String msg = checkUser(true);
        if(msg != null)
        {
            attr.addFlashAttribute("message", msg);
            return "redirect:/user_cabinet";
        }
        user.setNumber(newnumber);
        user.setNumberActivated(false);
        userRepository.saveAndFlush(user);
        attr.addFlashAttribute("message", "Номер успешно изменен! Теперь его необходимо активировать.");
        return "redirect:/user_cabinet";
    }

    @PostMapping("/set_notification")
    public String set_notification(String date, String repeat, Map<String, Object> model, RedirectAttributes attr)
    {
        if(date != null)
        {
            String msg = checkUser(true);
            if(msg == null)
            {
                try
                {
                    user.setNotificationDate(LocalDate.parse(date, dtf));
                    user.setAutoContinue(repeat.equals("on"));
                    userRepository.saveAndFlush(user);
                    attr.addFlashAttribute("message", "Уведомление успешно включено!");
                }
                catch (Exception ex)
                {
                    attr.addFlashAttribute("message", "Произошла ошибка при распознавании даты");
                    return "redirect:/user_cabinet";
                }
            }
            else
            {
                attr.addFlashAttribute("message", msg);
            }
        }
        return "redirect:/user_cabinet";
    }

    @GetMapping("/drop_notification")
    public String drop_notification(RedirectAttributes attr)
    {
        String msg = checkUser(true);
        if(msg == null)
        {
            user.resetNotificationDate();
            userRepository.saveAndFlush(user);
            attr.addFlashAttribute("message", "Уведомление успешно сброшено!");
        }
        else
        {
            attr.addFlashAttribute("message", msg);
        }
        return "redirect:/user_cabinet";
    }

    @PostMapping("/create_payment")
    public String create_payment(RedirectAttributes attr)
    {
        String msg = checkUser(true);
        if(msg == null)
        {
            try
            {
                user.createPayments();
                user.resetMeters();
            }
            catch (Exception ex)
            {
                attr.addFlashAttribute("message", "Произошла внутренняя ошибка при подтверждении!");
                return "redirect:/";
            }
            userRepository.saveAndFlush(user);
            attr.addFlashAttribute("message", "Оплата успешно подтверждена!");
        }
        else
        {
            attr.addFlashAttribute("message", msg);
        }
        return "redirect:/";
    }

}
