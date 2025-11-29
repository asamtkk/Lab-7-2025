package functions;

// интерфейс фабрики для создания табулированных функций
// позволяет создавать объекты разных типов динамически
public interface TabulatedFunctionFactory {
    // создает функцию по границам и количеству точек
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);
    // создает функцию по границам и массиву значений y
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values);
    // создает функцию по массиву точек
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);
}