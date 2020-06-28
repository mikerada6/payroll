package rad.irs.payroll.Payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication @EnableSwagger2 public class PayrollApplication {

	public static void main(String[] args) {
		final SpringApplication application = new SpringApplication(PayrollApplication.class);
		application.setWebApplicationType(WebApplicationType.SERVLET);
		application.run(args);
	}

}
