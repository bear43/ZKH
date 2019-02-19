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
    private final UserRepository userRepository;

    private NotificationChecker notificationChecker;

    @Autowired
    public ApplicationStartupRunner(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception
    {
        Messenger.init();
        NotificationChecker.setUserRepository(userRepository);
        notificationChecker = new NotificationChecker(1);
        notificationChecker.startChecker();
    }
}
