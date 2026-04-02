package org.facturepro.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FacturePro Africa — Modular Monolith
 * Architecture : DDD + Clean Architecture + Event-Driven
 * Stack : Spring Boot 3 + PostgreSQL + Redis + JWT
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BackofficeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BackofficeApplication.class, args);
    }
}
