package com.epam.scanner.config;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.epam.scanner.utils.manager.ConfigurationManager;

@Configuration
@ComponentScan(basePackages = "com.epam.scanner")
public class AppConfig {
	
	
	public final static String SCR_PATH  = ConfigurationManager.getProperty("tdp.path");
	public final static String TDP_LIBS  = ConfigurationManager.getProperty("tdp.libs");
	public final static String LIST_JARS_INPUT = ConfigurationManager.getProperty("list.jars.input");
	public final static String LIST_JARS_OUTPUT = ConfigurationManager.getProperty("list.jars.output");

	
	public final static String DEPENDENCIES = "dependencies[{| {]+(.*|\n|\r)+['|\"]+(1\\.0|4\\.0\\.0)+['|\"]+(.*|\\n|\\r)+?[}]";
	
	@Autowired
	private DataSource dataSource;

	@Bean
	public DataSource dataSource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase dataSource = builder.setType(EmbeddedDatabaseType.DERBY)
				.addScript("create_db.sql")
				.build();

		return dataSource;
	}

	@Bean
	public JdbcTemplate getjdbcTemplate() throws NamingException {
		return new JdbcTemplate(dataSource);
	}

}