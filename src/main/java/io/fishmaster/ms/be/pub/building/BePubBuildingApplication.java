package io.fishmaster.ms.be.pub.building;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableJpaRepositories(basePackages = "io.fishmaster.ms.be.pub.building.db.jpa.repository")
@EnableMongoRepositories(basePackages = "io.fishmaster.ms.be.pub.building.db.mongo.repository")
@SpringBootApplication
public class BePubBuildingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BePubBuildingApplication.class, args);
	}

}
