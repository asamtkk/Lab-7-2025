package functions;

import functions.meta.*;

public class Functions {
    //приватный конструктор - запрещает создание экземпляров класса
    private Functions(){
    }

    //метод, возвращающий значение интеграла функции
    public static double Integral(Function function, double left, double right, double step) throws IllegalArgumentException {
        //проверка интервала на выход из границ
        if (function.getLeftDomainBorder() > left || function.getRightDomainBorder() < right)
            throw new IllegalArgumentException("Интервал интегрирования выходит за границы области определения");
        //проверка корректности границ
        if (left >= right)
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");

        double val = 0; //переменная для хранения суммы значений интеграла
        double current = left; //начинаем с левой границы
        //проходим от left до right
        while (current < right){
            //определяем правую границу
            double next = Math.min(current + step, right); //берем либо полный шаг либо последний шаг(неполный)
            //вычисляем значение ф-ии на левой и правой границах
            double func1 = function.getFunctionValue(current);
            double func2 = function.getFunctionValue(next);
            //вычисляем площадь трапеции на текущем участке
            val += (func1 + func2) * (next - current) / 2;
            current = next; //переходим к следующему участку
            }
        return val;
        }
    //функция сдвига
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }
    //функция масштабирования
    public static Function scale(Function f, double scaleX, double scaleY){
        return new Scale(f, scaleX, scaleY);
    }
    //функция возведения в степень
    public static Function power(Function f, double power){
        return new Power(f, power);
    }
    //функция суммы
    public static Function sum(Function f1, Function f2){
        return new Sum(f1, f2);
    }
    //функция произведения
    public static Function mult(Function f1, Function f2){
        return new Mult(f1, f2);
    }
    //композиция функций
    public static Function composition(Function f1, Function f2){
        return new Composition(f1, f2);
    }
}

