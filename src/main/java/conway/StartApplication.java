package conway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry Point of this application.
 * 
 * @author Ken
 *
 */
@EnableConfigurationProperties
@SpringBootApplication
public class StartApplication {

	public static void main(String... args) {
		SpringApplication.run(StartApplication.class, args);
	}
}
