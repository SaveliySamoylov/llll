import java.io.*;
import java.util.concurrent.*;

class FileLoader {

    private final static String FILE_NAME = "security.txt";
    private final static String CODES_FILE_NAME = "codes.txt";
    private final static int NUMBER_OF_EMPLOYEES = 10;

    private static Semaphore semaphore = new Semaphore(1);
    private static String[] employees = new String[NUMBER_OF_EMPLOYEES];
    private static int[] codes = new int[NUMBER_OF_EMPLOYEES];

    public static void main(String[] args) throws Exception {
        initializeEmployees();
        createCodesFile();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Введите номер сотрудника: ");
        int employeeIndex = Integer.parseInt(reader.readLine()) - 1;
        System.out.println("Загрузка данных…0%");
        int finalEmployeeIndex = employeeIndex;
        FutureTask<String> loadTask = new FutureTask<>(() -> {
            loadDataFromFile();
            return employees[finalEmployeeIndex];
        });
        new Thread(loadTask).start();
        while (!loadTask.isDone()) {
            System.out.println("Загрузка данных…100%");
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.print("Генерация кода доступа…0%");
            int finalEmployeeIndex1 = employeeIndex;
            FutureTask<Integer> generateTask = new FutureTask<>(() -> {
                generateAccessCode(finalEmployeeIndex1);
                return codes[finalEmployeeIndex1];
            });
            new Thread(generateTask).start();
            while (!generateTask.isDone()) {
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.print("Генерация кода доступа…100%");
            }
            System.out.println();
            System.out.print("Введите номер сотрудника: ");
            employeeIndex = Integer.parseInt(reader.readLine()) - 1;
            System.out.println("Загрузка данных…0%");
            int finalEmployeeIndex2 = employeeIndex;
            loadTask = new FutureTask<>(() -> {
                return employees[finalEmployeeIndex2];
            });
            new Thread(loadTask).start();
        }
        System.out.println("Загрузка данных…100%");
        System.out.println("Сотрудник - " + loadTask.get() + ", код доступа - " + codes[employeeIndex]);
    }

    private static Object generateAccessCode(int employeeIndex) {

        return null;
    }

    private static void initializeEmployees() {
        employees[0] = "Иванов";
        employees[1] = "Петров";
        employees[2] = "Сидоров";
        employees[3] = "Козлов";
        employees[4] = "Новиков";
        employees[5] = "Федоров";
        employees[6] = "Васильев";
        employees[7] = "Алексеев";
        employees[8] = "Дмитриев";
        employees[9] = "Кузнецов";
    }

    private static void createCodesFile() throws IOException, InterruptedException {
        semaphore.acquire();
        File file = new File(CODES_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < NUMBER_OF_EMPLOYEES; i++) {
                writer.println(generateAccessCode(i));
            }
        }
        semaphore.release();
    }

    private static void loadDataFromFile() throws Exception {
        semaphore.acquire();
        File file = new File(FILE_NAME);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i < NUMBER_OF_EMPLOYEES; i++) {
                employees[i] = reader.readLine();
            }
        }
        semaphore.release();
    }
}
