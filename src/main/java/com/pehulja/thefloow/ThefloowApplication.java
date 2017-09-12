package com.pehulja.thefloow;

import java.io.IOException;

import org.mvnsearch.spring.boot.shell.SpringShellApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThefloowApplication {

	public static void main(String[] args) throws IOException
	{
		SpringShellApplication.run(ThefloowApplication.class, args);
	}
}
