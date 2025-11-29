package threads;

import functions.Function;
import functions.basic.Log;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private int generatedCount = 0;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        //создаем генератор случайных чисел
        Random random = new Random();

        try {
            //цикл по количеству заданий с проверкой прерывания
            for (int i = 0; i < task.getTasksCount() && !isInterrupted(); i++) {
                //захватываем семафор для записи данных
                semaphore.acquire();
                try {
                    //проверяем, свободно ли задание для новой генерации
                    if (task.getFunction() == null) {
                        //генерируем случайное основание логарифма от 1 до 10
                        double base = 1 + random.nextDouble() * 9;
                        Function logFunction = new Log(base);

                        //генерируем левую границу интегрирования от 0 до 100
                        double left = random.nextDouble() * 100;
                        //генерируем правую границу интегрирования от 100 до 200
                        double right = 100 + random.nextDouble() * 100;
                        //генерируем шаг дискретизации от 0 до 1
                        double step = random.nextDouble();

                        //устанавливаем параметры в объект задания
                        task.setFunction(logFunction);
                        task.setLeft(left);
                        task.setRight(right);
                        task.setStep(step);

                        //выводим сгенерированные параметры
                        System.out.printf("Source %f %f %f%n", left, right, step);
                        //увеличиваем счетчик сгенерированных заданий
                        generatedCount++;
                    }
                } finally {
                    //освобождаем семафор в любом случае
                    semaphore.release();
                }

                try {
                    //небольшая задержка для наглядности работы потоков
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //устанавливаем флаг прерывания при получении исключения
                    interrupt();
                }
            }
        } catch (InterruptedException e) {
            //поток был прерван во время работы с семафором
        }
    }
}