package balancetalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "https://api.balancetalk.kro.kr", description = "Default Server URL")})
@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BalanceTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalanceTalkApplication.class, args);
	}

}
