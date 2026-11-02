package org.javastro.panacea.cli;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(name = "panacea", mixinStandardHelpOptions = true,
        description = "Panacea tool management CLI")
public class PanaceaCommand {
}
