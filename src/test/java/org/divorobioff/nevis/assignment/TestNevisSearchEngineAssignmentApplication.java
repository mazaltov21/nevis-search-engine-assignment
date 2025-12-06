package org.divorobioff.nevis.assignment;

import org.springframework.boot.SpringApplication;

public class TestNevisSearchEngineAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.from(SearchEngineApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
