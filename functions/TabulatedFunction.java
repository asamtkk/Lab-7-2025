package functions;

//расширяем базовый интерфейс Function доп. методами
// добавлен интерфейс Iterable для поддержки for-each циклов
public interface TabulatedFunction extends Function, Cloneable, Iterable<FunctionPoint> {

    // возвращаем количество точек табуляции
    int getPointsCount();

    // получаем точку по указанному индексу
    FunctionPoint getPoint(int index);

    // устанавливаем новую точку по указанному индексу
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;

    // получаем координату x точки по указанному индексу
    double getPointX(int index);

    // устанавливаем координату x точки по указанному индексу
    void setPointX(int index, double x) throws InappropriateFunctionPointException;

    // получаем координату y точки по указанному индексу
    double getPointY(int index);

    // устанавливаем координату y точки по указанному индексу
    void setPointY(int index, double y);

    // удаляем точку по указанному индексу
    void deletePoint(int index);

    // добавляем новую точку в табулированную функцию
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

    Object clone();
}