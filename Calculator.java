import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Calculator {
    private static final String HISTORY_FILE = "history.txt";

    // Основной метод для вычисления выражения
    public static double evaluate(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", ""); // Удаление пробелов
        return parseExpression(expression);
    }

    // Метод для разбора и вычисления выражения
    private static double parseExpression(String expression) throws Exception {
        // Проверка на модуль числа
        if (expression.matches("\\|-?\\d+\\|")) {
            return Math.abs(Double.parseDouble(expression.replace("|", "")));
        }

        // Определение операций с приоритетом
        List<String> operators = Arrays.asList("^", "*", "/", "//", "%", "+", "-");
        for (String op : operators) {
            String regex = "(-?\\d+(\\.\\d+)?)(\\" + op + ")(-?\\d+(\\.\\d+)?)";
            Matcher matcher = Pattern.compile(regex).matcher(expression);
            if (matcher.find()) {
                double leftOperand = Double.parseDouble(matcher.group(1));
                double rightOperand = Double.parseDouble(matcher.group(4));
                double result = 0;

                switch (op) {
                    case "^":
                        result = Math.pow(leftOperand, rightOperand);
                        break;
                    case "*":
                        result = leftOperand * rightOperand;
                        break;
                    case "/":
                        if (rightOperand == 0) throw new ArithmeticException("Деление на ноль");
                        result = leftOperand / rightOperand;
                        break;
                    case "//":
                        if (rightOperand == 0) throw new ArithmeticException("Деление на ноль");
                        result = (int) (leftOperand / rightOperand);
                        break;
                    case "%":
                        if (rightOperand == 0) throw new ArithmeticException("Деление на ноль");
                        result = leftOperand % rightOperand;
                        break;
                    case "+":
                        result = leftOperand + rightOperand;
                        break;
                    case "-":
                        result = leftOperand - rightOperand;
                        break;
                }

                expression = matcher.replaceFirst(Double.toString(result));
                return parseExpression(expression);
            }
        }

        return Double.parseDouble(expression);
    }

    // Метод для сохранения выражения и результата в файл
    public static void saveToHistory(String expression, double result) throws IOException {
        String entry = expression + " = " + result;
        Files.write(Paths.get(HISTORY_FILE), (entry + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    // Метод для вывода истории
    public static void printHistory() throws IOException {
        if (Files.exists(Paths.get(HISTORY_FILE))) {
            List<String> history = Files.readAllLines(Paths.get(HISTORY_FILE));
            history.forEach(System.out::println);
        } else {
            System.out.println("История пуста.");
        }
    }

    // Основной метод программы
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Введите выражение для вычисления или 'history' для вывода истории, 'exit' для выхода:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            } else if (input.equalsIgnoreCase("history")) {
                try {
                    printHistory();
                } catch (IOException e) {
                    System.err.println("Ошибка чтения истории: " + e.getMessage());
                }
            } else {
                try {
                    double result = evaluate(input);
                    System.out.println("Результат: " + result);
                    saveToHistory(input, result);
                } catch (Exception e) {
                    System.err.println("Ошибка вычисления: " + e.getMessage());
                }
            }
        }
        scanner.close();
    }
}
