package com.yapily.jose.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@CommandLine.Command(name = "jose", mixinStandardHelpOptions = true, subcommands = RotateJWKSetsCommand.class)
public class JOSECommand {

}
