package threads;

import functions.Function;

public class Task {
    //ссылка на объект интегрируемой функции
    private Function function;
    //левая граница области интегрирования
    private double left;
    //правая граница области интегрирования
    private double right;
    //шаг дискретизации для интегрирования
    private double step;
    //количество выполняемых заданий
    private int tasksCount;

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }
}