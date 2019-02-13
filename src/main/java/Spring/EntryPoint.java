package Spring;

import com.twilio.Twilio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScans({
        @ComponentScan("Controller"),
        @ComponentScan("Config")
})
@EntityScan("Model")
@EnableJpaRepositories("Repository")
public class EntryPoint extends SpringApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EntryPoint.class, args);
    }
}
