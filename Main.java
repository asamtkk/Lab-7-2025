import functions.*;
import threads.*;
import java.util.Random;
import functions.basic.*;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        System.out.println("\n=== Задание 1: Проверка итераторов ===");

        // проверяем работу итератора для ArrayTabulatedFunction
        TabulatedFunction arrF = new ArrayTabulatedFunction(0, 10, 8);
        for (FunctionPoint p : arrF){
            System.out.println(p);  // используем for-each цикл
        }

        System.out.println("\n=== Задание 2: Проверка фабрик ===");

        // тестируем смену фабрик во время выполнения
        Function f = new Cos();
        TabulatedFunction tf;
        // создаем функцию через фабрику по умолчанию (Array)
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass()); // должен быть ArrayTabulatedFunction
        // меняем фабрику на LinkedList
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass()); // должен быть LinkedListTabulatedFunction
        // возвращаем фабрику Array обратно
        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println(tf.getClass()); // снова ArrayTabulatedFunction

        System.out.println("\n=== Задание 3: Проверка рефлексии ===");
        // тестируем создание объектов через рефлексию

        TabulatedFunction rf; // переиспользуем переменную для разных тестов

        rf = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(rf.getClass());
        System.out.println(rf);

        rf = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(rf.getClass());
        System.out.println(rf);

        rf = TabulatedFunctions.createTabulatedFunction(LinkedListTabulatedFunction.class, new FunctionPoint[] {
                        new FunctionPoint(0, 0),
                        new FunctionPoint(10, 10)
                }
        );
        System.out.println(rf.getClass());
        System.out.println(rf);

        rf = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println(rf.getClass());
        System.out.println(rf);

        System.out.println("\n=== Задание 1: Интегрирование экспоненциальной функции ===");

        //создаем объект экспоненциальной функции
        Function exponential = new Exp();

        //задаем границы интегрирования: от 0 до 1
        final double lowerBound = 0.0;
        final double upperBound = 1.0;

        //вычисляем точное теоретическое значение интеграла e^x от 0 до 1
        final double exactResult = Math.E - 1;
        System.out.printf("Точное значение интеграла: %.10f%n", exactResult);

        //начинаем поиск подходящего шага дискретизации
        double discretizationStep = 1.0;
        double computedResult;
        double error;

        System.out.println("\nПоиск требуемого шага дискретизации:");

        //уменьшаем шаг до достижения требуемой точности
        do {
            discretizationStep *= 0.5;
            //вычисляем интеграл с текущим шагом
            computedResult = Functions.Integral(exponential, lowerBound, upperBound, discretizationStep);
            //вычисляем абсолютную погрешность
            error = Math.abs(computedResult - exactResult);
            //выводим информацию о текущей итерации
            System.out.printf("Шаг: %12.10f, Вычислено: %12.10f, Погрешность: %8.2e%n",
                    discretizationStep, computedResult, error);
        } while (error > 1e-7);

        //выводим итоговый результат
        System.out.printf("%nИтог: шаг = %12.10f обеспечивает требуемую точность%n",
                discretizationStep);
        System.out.println("\n=== Задание 2: nonThread ===");
        nonThread();
        System.out.println("\n=== Задание 3: Простая многопоточная версия ===");
        simpleThreads();
        System.out.println("\n=== Задание 4: Усовершенствованная многопоточная версия ===");
        complicatedThreads();
    }



    public static void nonThread() {
        //создаем объект задания
        Task task = new Task();
        //устанавливаем количество выполняемых заданий
        task.setTasksCount(100);

        //создаем генератор случайных чисел
        Random random = new Random();

        //выполняем заданное количество заданий
        for (int i = 0; i < task.getTasksCount(); i++) {
            //создаем логарифмическую функцию со случайным основанием от 1 до 10
            double base = 1 + random.nextDouble() * 9;
            Function logFunction = new Log(base);
            task.setFunction(logFunction);

            //устанавливаем левую границу области интегрирования (от 0 до 100)
            double left = random.nextDouble() * 100;
            task.setLeft(left);

            //устанавливаем правую границу области интегрирования (от 100 до 200)
            double right = 100 + random.nextDouble() * 100;
            task.setRight(right);

            //устанавливаем шаг дискретизации (от 0 до 1)
            double step = random.nextDouble();
            task.setStep(step);

            //выводим сообщение с параметрами задания
            System.out.printf("Source %f %f %f%n", left, right, step);

            try {
                //вычисляем значение интеграла для параметров из объекта задания
                double result = Functions.Integral(task.getFunction(), task.getLeft(), task.getRight(), task.getStep());

                //выводим сообщение с результатом интегрирования
                System.out.printf("Result %f %f %f %f%n", left, right, step, result);
            } catch (IllegalArgumentException e) {
                //обрабатываем исключение при некорректных параметрах
                System.out.printf("Exception: %s%n", e.getMessage());
            }
        }
    }

    public static void simpleThreads() {
        //создаем объект задания
        Task task = new Task();
        //устанавливаем количество выполняемых заданий
        task.setTasksCount(100);

        //создаем потоки
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));

        //экспериментируем с приоритетами (по заданию)
        generatorThread.setPriority(Thread.MIN_PRIORITY);
        integratorThread.setPriority(Thread.MAX_PRIORITY);

        //запускаем потоки
        generatorThread.start();
        integratorThread.start();

        try {
            //ожидаем завершения потоков
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            System.out.println("Основной поток был прерван");
        }

        System.out.println("Задание 3 завершено");
    }

    public static void complicatedThreads() {
        //создаем объект задания
        Task task = new Task();
        //устанавливаем количество выполняемых заданий
        task.setTasksCount(100);

        //создаем семафор с одним разрешением (одноместный)
        Semaphore semaphore = new Semaphore(1);

        //создаем потоки с передачей семафора
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        //запускаем потоки
        generator.start();
        integrator.start();

        try {
            //основной поток ждет 50 миллисекунд
            Thread.sleep(50);

            //прерываем потоки после 50 мс
            generator.interrupt();
            integrator.interrupt();

            //ожидаем завершения потоков
            generator.join();
            integrator.join();

        } catch (InterruptedException e) {
            System.out.println("Основной поток был прерван");
        }

        System.out.println("Задание 4 завершено");
    }
}
