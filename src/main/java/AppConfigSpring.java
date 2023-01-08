import bill.BillService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import user.UserService;


@Configuration
@Import({PersistenceConfig.class})
@ComponentScan(basePackageClasses = {
        UserService.class,
        BillService.class}
)
public class AppConfigSpring {

    /**
     * To resolve ${} in @Values, you must register a static PropertySourcesPlaceholderConfigurer in either XML or
     * annotation configuration file.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}