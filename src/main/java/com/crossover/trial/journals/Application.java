package com.crossover.trial.journals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.ValidationException;
import java.io.File;
import java.util.function.Supplier;

import static com.crossover.trial.journals.javautil.Suppliers.memoize;

@ComponentScan
@EnableAutoConfiguration
public class Application {

	public static final Supplier<String> ROOT = memoize(() ->
			validateRootPath(System.getProperty("upload-dir", System.getProperty("user.home") + "/upload")));

	static String validateRootPath(String rootpath) throws ValidationException {
		if (!new File(rootpath).isDirectory()) {
			throw new ValidationException("ROOT path '" + rootpath + "' is not a valid directory.");
		}
		return rootpath;
	}

	public static void main(String[] args) throws Exception {
		// attempt to validate ROOT path during bootstrapping
		ROOT.get();
		SpringApplication app = new SpringApplicationBuilder(Application.class).build();
		app.run(args);
	}

}