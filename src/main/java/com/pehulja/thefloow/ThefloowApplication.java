package com.pehulja.thefloow;

import org.mvnsearch.spring.boot.shell.SpringShellApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class ThefloowApplication {
    private static final Pattern MONGO_CONNECTION_PATTER = Pattern.compile("(.+)\\:(\\d+)");

    public static void main(String[] args) throws IOException {
        if(args.length != 2 || !args[0].equals("-mongo") || args[1].isEmpty()){
            throw new IllegalArgumentException("Invalid startup parameters, should be exactly in format: -mongo [hostname]:[port]");
        }
        Matcher matcher = MONGO_CONNECTION_PATTER.matcher(args[1]);

        if(!matcher.matches()){
            throw new IllegalArgumentException("Invalid mongo connection params, should match exact format: -mongo [hostname]:[port]");
        }
        String [] arguments = new String[2];
        arguments[0] = String.format("--spring.data.mongodb.host=%s", matcher.group(1));
        arguments[1] = String.format("--spring.data.mongodb.port=%s", matcher.group(2));

        SpringShellApplication.run(ThefloowApplication.class, args);
    }
}
