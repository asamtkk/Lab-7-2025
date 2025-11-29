package threads;

import functions.Functions;
import java.util.concurrent.Semaphore;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;
    private int completedCount = 0;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            //цикл пока не прервали и не обработали все задания
            while (!isInterrupted() && completedCount < task.getTasksCount()) {
                //захватываем семафор для чтения данных
                semaphore.acquire();
                try {
                    //проверяем, есть ли функция для интегрирования
                    if (task.getFunction() != null) {
                        //получаем параметры из объекта задания
                        double left = task.getLeft();
                        double right = task.getRight();
                        double step = task.getStep();
                        //вычисляем значение интеграла
                        double result = Functions.Integral(task.getFunction(), left, right, step);

                        //выводим результат вычисления
                        System.out.printf("Result %f %f %f %f%n", left, right, step, result);

                        //очищаем задание для новых данных
                        task.setFunction(null);
                        //увеличиваем счетчик обработанных заданий
                        completedCount++;
                    }
                } finally {
                    //освобождаем семафор в любом случае
                    semaphore.release();
                }

                try {
                    //небольшая задержка между итерациями
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