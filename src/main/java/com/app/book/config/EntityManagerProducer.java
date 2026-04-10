package com.app.book.config;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Properties;

@ApplicationScoped
public class EntityManagerProducer {

    private final EntityManagerFactory emf;

    // default to dev if not set
    public EntityManagerProducer() {
        var env = System.getProperty("app.env", "dev");
        var config = Config.builder()
                .sources(ConfigSources.classpath("application.yaml"), ConfigSources.classpath("application-" + env + ".yaml"))
                .build();

        var props = new Properties(5);
        var dbConfig = config.get("db");
        dbConfig.get("url").asString().ifPresent(v -> props.put("jakarta.persistence.jdbc.url", v));
        dbConfig.get("username").asString().ifPresent(v -> props.put("jakarta.persistence.jdbc.user", v));
        dbConfig.get("password").asString().ifPresent(v -> props.put("jakarta.persistence.jdbc.password", v));
        dbConfig.get("driver").asString().ifPresent(v -> props.put("jakarta.persistence.jdbc.driver", v));
        dbConfig.get("dialect").asString().ifPresent(v -> props.put("hibernate.dialect", v));

        emf = Persistence.createEntityManagerFactory("booksPU", props);
    }

    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }
}
