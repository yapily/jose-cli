package com.yapily.jose.cli;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class JoseCLIRunner implements CommandLineRunner, ExitCodeGenerator {

    private CommandLine.IFactory factory;    // auto-configured to inject PicocliSpringFactory
    private JOSECommand joseCommand; // your @picocli.CommandLine.Command-annotated class
    private int exitCode;

    // constructor injection
    JoseCLIRunner(CommandLine.IFactory factory, JOSECommand joseCommand) {
        this.factory = factory;
        this.joseCommand = joseCommand;
    }

    @Override
    public void run(String... args) {
        // let picocli parse command line args and run the business logic
        exitCode = new CommandLine(joseCommand, factory)
                .registerConverter(KeyType.class, KeyType::parse)
                .registerConverter(Curve.class, Curve::parse)
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        // let Spring instantiate and inject dependencies
        System.exit(SpringApplication.exit(SpringApplication.run(JoseCLIRunner.class, args)));
    }
}