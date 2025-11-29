package functions;

import java.io.*;
import java.lang.reflect.Constructor;

public class TabulatedFunctions {
    // статическое поле для хранения текущей фабрики
    // по умолчанию используется фабрика массива
    private static TabulatedFunctionFactory factory = new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    // метод для замены фабрики в runtime
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    // методы создания функций через фабрику
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    //приватный конструктор - запрещает создание экземпляров класса
    private TabulatedFunctions() {
    }

    // методы создания функций через рефлексию
    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, double leftX, double rightX, int pointsCount) {
        try {
            // проверяем, что класс реализует TabulatedFunction
            if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
                throw new IllegalArgumentException("Класс должен реализовывать интерфейс TabulatedFunction");
            }

            // ищем конструктор с параметрами (double, double, int)
            Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, int.class);

            // создаем объект через рефлексию
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, pointsCount);

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, double leftX, double rightX, double[] values) {
        try {
            // проверяем, что класс реализует TabulatedFunction
            if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
                throw new IllegalArgumentException("Класс должен реализовывать интерфейс TabulatedFunction");
            }

            // ищем конструктор с параметрами (double, double, double[])
            Constructor<?> constructor = functionClass.getConstructor(double.class, double.class, double[].class);

            // создаем объект через рефлексию
            return (TabulatedFunction) constructor.newInstance(leftX, rightX, values);

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<?> functionClass, FunctionPoint[] points) {
        try {
            // проверяем, что класс реализует TabulatedFunction
            if (!TabulatedFunction.class.isAssignableFrom(functionClass)) {
                throw new IllegalArgumentException("Класс должен реализовывать интерфейс TabulatedFunction");
            }

            // ищем конструктор с параметрами (FunctionPoint[])
            Constructor<?> constructor = functionClass.getConstructor(FunctionPoint[].class);

            // создаем объект через рефлексию
            return (TabulatedFunction) constructor.newInstance((Object) points);

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка при создании объекта через рефлексию", e);
        }
    }

    // перегруженный метод табуляции с рефлексией
    public static TabulatedFunction tabulate(Class<?> functionClass, Function function, double leftX, double rightX, int pointsCount) {
        // стандартные проверки параметров
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табуляции выходят за область определения");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }

        // создаем табулированную функцию через рефлексию
        TabulatedFunction tabulatedFunc = createTabulatedFunction(functionClass, leftX, rightX, pointsCount);

        // заполняем значения y
        for (int i = 0; i < pointsCount; i++) {
            double x = tabulatedFunc.getPointX(i);
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }
        return tabulatedFunc;
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        //проверка на количество точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        //проверка, находятся ли границы в области определения
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табуляции выходят за область определения");
        }
        //проверка корректности границ
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        //создаем табулированную функцию
        TabulatedFunction tabulatedFunc = createTabulatedFunction(leftX, rightX, pointsCount);
        //заполняем значения y, вычисляя их из исходной функции
        for (int i = 0; i < pointsCount; i++) {
            double x = tabulatedFunc.getPointX(i);
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }
        return tabulatedFunc;
    }

    //выводим табулированную функцию в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        //создаем поток-обертку
        DataOutputStream dataOut = new DataOutputStream(out);
        //записываем количество точек функции
        dataOut.writeInt(function.getPointsCount());
        //последовательно записываем координаты всех точек функции
        for (int i = 0; i < function.getPointsCount(); i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }
        //сбрасываем буфер, обеспечивая запись всех данных в поток
        //не закрываем поток, так как он может использоваться вызывающим кодом дальше
        dataOut.flush();
    }

    //выводим табулированную функцию из байтового потока
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        //создаем поток-обертку
        DataInputStream dataIn = new DataInputStream(in);
        //читаем количество точек функции
        int pointsCount = dataIn.readInt();
        //создаем массивы для хранения координат точек
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        //последовательно читаем координаты всех точек из потока
        for (int i = 0; i < pointsCount; i++) {
            xValues[i] = dataIn.readDouble();
            yValues[i] = dataIn.readDouble();
        }
        //создаем массив объектов FunctionPoint из прочитанных координат
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
        //создаем и возвращаем табулированную функцию
        return createTabulatedFunction(points);
    }

    //записываем табулированную функцию в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException{
        PrintWriter writer = new PrintWriter(out);

        //записываем количество точек
        writer.print(function.getPointsCount());
        writer.print(" ");
        //записываем координаты всех точек через пробел
        for(int i = 0; i < function.getPointsCount(); i++){
            writer.print(function.getPointX(i));
            writer.print(" ");
            writer.print(function.getPointY(i));
            writer.print(" ");
        }
        //не закрываем поток, а сбрасываем буфер, так как он может использоваться дальше
        writer.flush();
    }
    //читаем табулированную функцию из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException{
        //StreamTokenizer для чтения чисел
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        //читаем количество точек
        tokenizer.nextToken(); //переходим к следующему токену (числу)
        int pointsCount = (int) tokenizer.nval; //читаем числовое значение
        //создаем массивы для координат
        double[] xValues = new double[pointsCount];
        double[] yValues = new double[pointsCount];
        //читаем координаты всех точек
        for(int i = 0; i < pointsCount; i++){
            //переходим к следуещему токену в потоке, который должен быть числом
            //не делаем проверки, так как считаем, что данные в потоке записаны корректно
            tokenizer.nextToken();
            //сохраняем числовое значение токена как x-координату текущей точки
            xValues[i] = tokenizer.nval;
            //аналогично с y-координатой
            tokenizer.nextToken();
            yValues[i] = tokenizer.nval;
        }
        //создаем функцию из массива точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for(int i = 0; i < pointsCount; i++){
            points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
        return createTabulatedFunction(points);
    }
}
