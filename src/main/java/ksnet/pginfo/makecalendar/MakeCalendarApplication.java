package ksnet.pginfo.makecalendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MakeCalendarApplication {

	public static void main(String[] args) {
		SpringApplication app =	new SpringApplication(MakeCalendarApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

}
