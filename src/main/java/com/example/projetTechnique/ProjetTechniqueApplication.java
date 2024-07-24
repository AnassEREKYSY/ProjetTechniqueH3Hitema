package com.example.projetTechnique;

import com.example.projetTechnique.model.Role;
import com.example.projetTechnique.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProjetTechniqueApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ProjetTechniqueApplication.class, args);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		Role adminRole = new Role();
		adminRole.setName("ADMIN");
		roleRepository.save(adminRole);



	}

}
