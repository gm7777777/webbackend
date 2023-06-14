package com.gm.webbackend;

import com.gm.webbackend.config.MongoLinkClient;
import com.gm.webbackend.dao.TestDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class WebbackendApplication {
	static{
		MongoLinkClient.getInstance().initClient("mywebdatabase");
	}
	public static void main(String[] args) {
		SpringApplication.run(WebbackendApplication.class, args);
	}
}

