package com.judge.myojudge.execution_validations_code.ev_service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CodeRunner {

    private static final int TIMEOUT_SECONDS = 5;

    public String runCode(String language, String code, List<String> inputLines) {
        return switch (language.toLowerCase()) {
            case "python" -> executePythonCode(code, inputLines);
            case "cpp" -> executeCppCode(code, inputLines);
            case "java" -> executeJavaCode(code, inputLines);
            default -> "Unsupported language: " + language;
        };
    }

    private String executePythonCode(String code, List<String> inputLines) {
        return executeProcess(new ProcessBuilder("python3", "-c", code), inputLines);
    }

    private String executeCppCode(String code, List<String> inputLines) {
        Path tempDir = null;
        try {
//            System.out.println(code);
            tempDir = Files.createTempDirectory("cpp_code");
            Path sourceFile = tempDir.resolve("main.cpp");
            Path executable = tempDir.resolve("a.out");

            Files.writeString(sourceFile, code, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            Files.deleteIfExists(executable);

            if (compileCode(new ProcessBuilder("g++", sourceFile.toString(), "-o", executable.toString()), tempDir)) {
                System.out.println("Compilation Failed: " + readErrorLogs(tempDir));
                return "Compilation Failed: " + readErrorLogs(tempDir);
            }

            if (!Files.exists(executable)) {
                return "Error: Executable not created";
            }

            return executeProcess(new ProcessBuilder(executable.toString()), inputLines);
        } catch (IOException e) {
            return "Execution Error: " + e.getMessage();
        }
        finally {
            cleanupDirectory(tempDir);
        }
    }

    private String executeJavaCode(String code, List<String> inputLines) {
        try {
            Path tempDir = Files.createTempDirectory("java_code");
            Path sourceFile = tempDir.resolve("Main.java");

            Files.writeString(sourceFile, code, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            if (compileCode(new ProcessBuilder("javac", sourceFile.toString()), tempDir)) {
                return "Compilation Failed: " + readErrorLogs(tempDir);
            }

            return executeProcess(new ProcessBuilder("java", "-cp", tempDir.toString(), "Main"), inputLines);
        } catch (IOException e) {
            return "Execution Error: " + e.getMessage();
        }
    }

    private boolean compileCode(ProcessBuilder builder, Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("compile_error.log");
        builder.redirectError(logFile.toFile());
        Process process = builder.start();
        try {
            return !process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS) || process.exitValue() != 0;
        } catch (InterruptedException e) {
            process.destroy();

            return true;
        }
    }

    private String readErrorLogs(Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("compile_error.log");
        return Files.exists(logFile) ? Files.readString(logFile) : "No error logs found";
    }

    private String executeProcess(ProcessBuilder builder, List<String> inputLines) {
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                for (String line : inputLines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }

            boolean completed = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                process.destroy();
                System.out.println("Execution Time Out....");
                return "Execution Timed Out";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException | InterruptedException e) {
            return "Execution Error: " + e.getMessage();
        }
    }

    private void cleanupDirectory(Path tempDir) {
        if (tempDir == null) {
            return; // No temp directory to clean up
        }
        try {
            // Traverse the directory and delete files in reverse order (to avoid issues with nested directories)
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder()) // To ensure directories are deleted after files
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (file.exists() && !file.delete()) {
                            // Log if file deletion fails
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    });
        } catch (IOException e) {
            // Handle the exception and log it, instead of ignoring it
            System.err.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace(); // For detailed debugging
        }
    }

}