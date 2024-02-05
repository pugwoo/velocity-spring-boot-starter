package com.kalman03.springboot.velocity;

import ch.qos.logback.classic.Level;
import com.kalman03.springboot.velocity.view.VelocityViewResolver;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.spring.VelocityEngineFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
public class VelocityAutoConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix = "spring.velocity")
	VelocityProperty velocityProperty() {
		return new VelocityProperty();
	}

	@Bean
	VelocityEngineFactoryBean velocityEngineFactoryBean(VelocityProperty velocityProperty) {

		// Set velocity deprecation logger level to error
		Logger logger = LoggerFactory.getLogger(RuntimeConstants.DEFAULT_RUNTIME_LOG_NAME + ".deprecation");
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
			logbackLogger.setLevel(Level.ERROR);
		}

		VelocityEngineFactoryBean bean = new VelocityEngineFactoryBean();
		bean.setResourceLoaderPath(velocityProperty.getResourceLoaderPath());
		Map<String, Object> velocityPropertiesMap = new HashMap<>();
		velocityPropertiesMap.put(Velocity.ENCODING_DEFAULT, "UTF-8");
		velocityPropertiesMap.put(Velocity.INPUT_ENCODING, "UTF-8");
		bean.setVelocityPropertiesMap(velocityPropertiesMap);
		return bean;
	}

	@Bean
	@ConditionalOnMissingBean(name = "velocityViewResolver")
	@ConditionalOnProperty(name = "spring.velocity.enabled", matchIfMissing = true)
	VelocityViewResolver velocityViewResolver(VelocityProperty velocityProperty) {
		String suffix = velocityProperty.getSuffix();
		if (suffix == null) {
			suffix = ".vm";
		}
		VelocityViewResolver resolver = new VelocityViewResolver(null, suffix);
		resolver.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
		return resolver;
	}
}
