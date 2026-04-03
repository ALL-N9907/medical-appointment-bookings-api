package edu.unimag.medical;

import org.springframework.boot.SpringApplication;

public class TestMedicalApplication {

	public static void main(String[] args) {
		SpringApplication.from(MedicalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
