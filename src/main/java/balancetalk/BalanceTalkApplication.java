package balancetalk;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@EnableJpaAuditing
@SpringBootApplication
public class BalanceTalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalanceTalkApplication.class, args);
	}

}
