package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task; // объект для хранения данных задания
    private int processedTasks = 0; // счетчик обработанных заданий

    public SimpleIntegrator(Task task) {
        this.task = task; // сохраняем переданное задание
    }

    @Override
    public void run() {
        try {
            // выполняем цикл для обработки заданного количества заданий
            for (int i = 0; i < task.getTasksCount(); i++) {
                // объявляем переменные для параметров задания и результата
                double left, right, step, result;

                // синхронизируемся на объекте задания для безопасного доступа
                synchronized (task) {
                    // ждем пока генератор не создаст новое задание
                    while (task.getFunction() == null) {
                        try {
                            // ждем уведомления с таймаутом 50 миллисекунд
                            task.wait(50);
                        } catch (InterruptedException e) {
                            // если поток прервали во время ожидания, завершаем работу
                            return;
                        }
                        // если данные все еще не появились, продолжаем ожидание
                        if (task.getFunction() == null) continue;
                    }

                    // читаем параметры из объекта задания
                    left = task.getLeft();
                    right = task.getRight();
                    step = task.getStep();

                    // вычисляем значение интеграла для заданной функции и параметров
                    result = Functions.Integral(task.getFunction(), left, right, step);

                    // выводим результат вычисления интеграла
                    System.out.printf("Result %f %f %f %f%n", left, right, step, result);
                    processedTasks++; // увеличиваем счетчик обработанных заданий

                    // очищаем задание, сигнализируя о готовности к приему новых данных
                    task.setFunction(null);

                    // уведомляем все ожидающие потоки об освобождении задания
                    task.notifyAll();
                }

                // небольшая пауза между обработкой заданий
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // обработка прерывания потока во время работы
            System.out.println("Поток интегратора был прерван");
        } catch (IllegalArgumentException e) {
            // обработка ошибок вычисления интеграла
            System.out.printf("Ошибка при вычислении интеграла: %s%n", e.getMessage());
        }
    }
}