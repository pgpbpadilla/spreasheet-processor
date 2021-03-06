package org.pgpb.cli;

import org.apache.commons.cli.*;
import org.pgpb.evaluation.SpreadsheetEvaluator;
import org.pgpb.evaluation.ExpressionSpreadsheetEvaluator;
import org.pgpb.spreadsheet.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class Cli {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cli.class);
    private final String[] args;
    private final Options options = new Options();

    public Cli(String[] args) {

        this.args = args;

        options.addOption("h", "help", false, "show help.");
        options.addOption("f", "file", true, "The path to the input file.");

    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                help();
            }

            if (cmd.hasOption("file")) {
                evaluateSpreadsheet(cmd);
            } else {
                help();
            }

        } catch (ParseException e) {
            LOGGER.error("Failed to parse command line properties");
            help();
        }
    }

    private void evaluateSpreadsheet(CommandLine cmd) {
        String filePath = cmd.getOptionValue("f");
        Spreadsheet sheet;

        try(Stream<String> contents = Files.lines(Paths.get(filePath))){
            sheet = Spreadsheet.fromTsvLines(contents.collect(toList()));
            SpreadsheetEvaluator evaluator = new ExpressionSpreadsheetEvaluator();
            evaluator.toTSVLines(sheet).forEach(System.out::println);
        } catch (IOException e) {
            LOGGER.error("Could not read input file: " + filePath);
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
    }
}