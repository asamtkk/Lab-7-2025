package threads;

import functions.Function;
import functions.basic.Log;
import java.util.Random;

public class SimpleGenerator implements Runnable {
    private final Task task; // объект для хранения данных задания
    private int createdTasks = 0; // счетчик созданных заданий

    public SimpleGenerator(Task task) {
        this.task = task; // сохраняем переданное задание
    }

    @Override
    public void run() {
        // создаем генератор случайных чисел для параметров заданий
        Random random = new Random();

        try {
            // выполняем цикл для создания заданного количества заданий
            for (int i = 0; i < task.getTasksCount(); i++) {
                // генерируем случайное основание логарифма от 1 до 10
                double base = 1 + random.nextDouble() * 9;
                // создаем логарифмическую функцию с полученным основанием
                Function logFunc = new Log(base);
                // генерируем левую границу интегрирования от 0 до 100
                double left = random.nextDouble() * 100;
                // генерируем правую границу интегрирования от 100 до 200
                double right = 100 + random.nextDouble() * 100;
                // генерируем шаг дискретизации от 0 до 1
                double step = random.nextDouble();

                // синхронизируемся на объекте задания для безопасного доступа
                synchronized (task) {
                    // ждем пока предыдущее задание не будет обработано интегратором
                    while (task.getFunction() != null) {
                        task.wait(); // освобождаем монитор и переходим в режим ожидания
                    }

                    // устанавливаем сгенерированные параметры в объект задания
                    task.setFunction(logFunc);
                    task.setLeft(left);
                    task.setRight(right);
                    task.setStep(step);

                    // выводим информацию о созданном задании
                    System.out.printf("Source %f %f %f%n", left, right, step);
                    createdTasks++; // увеличиваем счетчик созданных заданий

                    // уведомляем все ожидающие потоки о появлении новых данных
                    task.notifyAll();
                }

                // небольшая пауза для наглядности работы потоков
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // обработка прерывания потока во время работы
            System.out.println("Поток генератора был прерван");
        }
    }
}