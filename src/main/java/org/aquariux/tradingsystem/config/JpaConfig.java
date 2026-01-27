package org.aquariux.tradingsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "org.aquariux.tradingsystem")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
}
