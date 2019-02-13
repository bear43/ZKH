package Config;

import Repository.UserRepository;
import Util.Messenger;
import Util.NotificationChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner
{
    @Autowired
    private UserRepository userRepository;

    private NotificationChecker notificationChecker;

    @Override
    public void run(String... args) throws Exception
    {
        Messenger.init();
        NotificationChecker.setUserRepository(userRepository);
        notificationChecker = new NotificationChecker(1);
        notificationChecker.startChecker();
    }
}
