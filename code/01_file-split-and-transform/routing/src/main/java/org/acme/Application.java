package org.acme;

import org.acme.routes.CustomerRoute;
import org.springframework.boot.SpringApplication;


import org.apache.camel.Main;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String... args) {
        SpringApplication.run(org.acme.Application.class, args);
    }

//    public void run(String... args) throws Exception {
//        Main main = new Main();
//        main.addRouteBuilder(new CustomerRoute());
//        main.run();
//    }

}
