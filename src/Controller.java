import models.Bind;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.io.*;

public class Controller {
    private Object model;
    private String[] columnNames;
    private Map<String, double[]> scriptResults;
    private Map<String, Field> boundFields;

    public Controller(String modelName) {
        modelName = modelName.replace(".java", "");

        try {
            Class<?> modelClass = Class.forName("models." + modelName);
            model = modelClass.getDeclaredConstructor().newInstance();
            boundFields = new HashMap<>();
            Arrays.stream(modelClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Bind.class))
                    .forEach(field -> {
                        field.setAccessible(true);  // Make private fields accessible
                        boundFields.put(field.getName(), field);
                    });
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create model: " + modelName, e);
        }
        scriptResults = new HashMap<>();
    }

    public Controller readDataFrom(String fname) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("LATA")) {
                    String[] years = line.split("\\s+");
                    int numYears = years.length - 1;

                    Field llField = boundFields.get("LL");
                    if (llField != null) {
                        llField.setAccessible(true);
                        llField.setInt(model, numYears);
                    }

                    columnNames = new String[numYears + 1];
                    columnNames[0] = "Variable";
                    System.arraycopy(years, 1, columnNames, 1, numYears);
                } else if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    String varName = parts[0];

                    Field field = boundFields.get(varName);
                    if (field != null) {
                        field.setAccessible(true);

                        Field llField = boundFields.get("LL");
                        llField.setAccessible(true);
                        int ll = llField.getInt(model);

                        double[] values = new double[ll];
                        for (int i = 1; i < parts.length && i - 1 < ll; i++) {
                            values[i - 1] = Double.parseDouble(parts[i]);
                        }

                        double[] extendedValues = extendValues(values, ll);
                        field.set(model, extendedValues);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading data file: " + fname, e);
        }
        return this;
    }

    private double[] extendValues(double[] values, int length) {
        double[] extended = new double[length];
        System.arraycopy(values, 0, extended, 0, values.length);
        if (values.length < length) {
            for (int i = values.length; i < length; i++) {
                extended[i] = values[values.length - 1];
            }
        }
        return extended;
    }

    public Controller runModel() {
        try {
            model.getClass().getMethod("run").invoke(model);
        } catch (Exception e) {
            throw new RuntimeException("Error running model", e);
        }
        return this;
    }

    public double[] runScriptFile(String scriptPath) {
        try {
            String scriptContent = new String(Files.readAllBytes(new File(scriptPath).toPath()));
            return runScript(scriptContent);
        } catch (IOException e) {
            throw new RuntimeException("Error reading script file: " + scriptPath, e);
        }
    }

    public double[] runScript(String scriptContent) {
        try {
            Binding binding = new Binding();

            for (Map.Entry<String, Field> entry : boundFields.entrySet()) {
                entry.getValue().setAccessible(true);
                binding.setVariable(entry.getKey(), entry.getValue().get(model));
            }

            for (Map.Entry<String, double[]> entry : scriptResults.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }

            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(scriptContent);

            Map<String, Object> variables = binding.getVariables();
            double[] lastResult = null;

            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String varName = entry.getKey();
                Object value = entry.getValue();

                if (boundFields.containsKey(varName) ||
                        (varName.length() == 1 && Character.isLowerCase(varName.charAt(0)))) {
                    continue;
                }

                if (value instanceof double[]) {
                    double[] resultArray = (double[]) value;
                    scriptResults.put(varName, resultArray);
                    lastResult = resultArray;
                }
            }

            return lastResult;
        } catch (Exception e) {
            throw new RuntimeException("Error running script", e);
        }
    }

    public String getResultsAsTsv() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(String.join("\t", columnNames)).append("\n");

            for (Map.Entry<String, Field> entry : boundFields.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue().get(model);
                if (value instanceof double[]) {
                    appendArray(sb, name, (double[]) value);
                }
            }
            for (Map.Entry<String, double[]> entry : scriptResults.entrySet()) {
                appendArray(sb, entry.getKey(), entry.getValue());
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating TSV", e);
        }
    }

    private void appendArray(StringBuilder sb, String name, double[] values) {
        sb.append(name);
        for (double value : values) {
            sb.append("\t").append(formatNumber(value));
        }
        sb.append("\n");
    }

    private static String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(number);
    }
}