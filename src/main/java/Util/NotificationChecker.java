package Util;

import Model.User;
import Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationChecker
{
    /**
     * Хранилище пользователей
     */
    private static UserRepository userRepository;

    /**
     * Сколько загружать из БД экземпляров User
     */
    private static int ELEMENTS_ON_PAGE_COUNT = 10;

    /**
     * Время в минутах - таймер проверки всех пользователей для рассылки
     */
    private long checkPeriod;//Minutes

    /**
     * Текст уведомления
     */
    private static final String NOTIFICATION_MESSAGE = "Напоминание об оплате за услуги ЖКХ. Чтобы отключить эти уведомления перейдите в личный кабинет.";

    /**
     * Поток, в котором выполняется проверка пользователей
     */
    private Thread checkThread;

    /**
     * Метод проверки
     */
    private void check()
    {
        final long totalCount = userRepository.count();
        int totalPages = (int)(totalCount/ELEMENTS_ON_PAGE_COUNT);
        int remainder = (int)(totalCount % ELEMENTS_ON_PAGE_COUNT);
        int currentPage = 0;
        int limit = ELEMENTS_ON_PAGE_COUNT;
        if(remainder > 0)
            totalPages++;
        while(currentPage < totalPages)
        {
            if(totalPages-currentPage == 1 && (remainder > 0)) limit = remainder;
            Page<User> users = userRepository.findAll(PageRequest.of(currentPage, limit));
            for (User user : users)
            {
                if (user.isNumberActivated() && user.isNotificationDateNow())
                {
                    try
                    {
                        Messenger.sendMessage(user.getNumber(), NOTIFICATION_MESSAGE);
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            currentPage++;
        }
    }

    /**
     * Метод запуска проверки
     * @throws Exception Если хранилище указывает в никуда - null reference
     */
    public void startChecker() throws Exception
    {
        if(userRepository == null) throw new Exception("User repository is null! Cannot start checker");
        checkThread = new Thread(() ->
                new Timer().schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        check();
                    }
                }, checkPeriod * 60000, checkPeriod * 60000));
        checkThread.start();
    }

    /**
     * Конструктор с временем проверки
     * @param checkPeriod Время проверки в минутах
     */
    public NotificationChecker(int checkPeriod)
    {
        this.checkPeriod = checkPeriod;
    }

    public NotificationChecker()
    {
    }

    /**
     *
     * @return Возврашает объект хранилища пользователей
     */
    public static UserRepository getUserRepository()
    {
        return userRepository;
    }

    /**
     * Устанавливает экземпляр хранилища пользователей
     * @param userRepository Экземпляр хранилища
     */
    public static void setUserRepository(UserRepository userRepository)
    {
        NotificationChecker.userRepository = userRepository;
    }

    public long getCheckPeriod()
    {
        return checkPeriod;
    }

    public void setCheckPeriod(long checkPeriod)
    {
        this.checkPeriod = checkPeriod;
    }

    public Thread getCheckThread()
    {
        return checkThread;
    }

    public void setCheckThread(Thread checkThread)
    {
        this.checkThread = checkThread;
    }
}
