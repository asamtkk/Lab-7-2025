package functions;

public class FunctionPoint {
    // приватные поля для храннения координат точки
    private double x;
    private double y;

    //конструктор с параметрами - создает точку с заданными координатами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // конструктор копирования
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // конструктор по умолчанию
    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    // геттер для получения координаты X точки
    public double getX() {
        return x;
    }

    // сеттер для установки координаты X точки
    public void setX(double x) {
        this.x = x;
    }

    // геттер для получения координаты Y точки
    public double getY() {
        return y;
    }

    // сеттер для установки координаты Y точки
    public void setY(double y) {
        this.y = y;
    }


    //переопределение метода для текстового описания точки
    @Override
    public String toString() {
        return "(" + this.x + ";" + this.y + ")";
    }

    //переопределение метода для сравнения с теекущей точкой
    @Override
    public boolean equals(Object o) {
        //проверка на ссылочное равенство
        if (this == o) return true;
        //проверка на совместимость классов
        if (o == null || getClass() != o.getClass()) return false;

        //сравниваем координаты x и y для корректного сравнения double
        return Math.abs(this.x - ((FunctionPoint) o).x) < 1e-10 &&
                Math.abs(this.y - ((FunctionPoint) o).y) < 1e-10;
    }

    //переопределение метода для вычисления хэш-кода тчоки
    @Override
    public int hashCode() {
        //преобразование double в long
        long x_bits = Double.doubleToLongBits(this.x);
        long y_bits = Double.doubleToLongBits(this.y);
        //разбиваем 64бит long на два 32бит int
        // ^ операция XOR между старшими и младшими 32 битами
        int x_hash = (int) (x_bits ^ (x_bits >>> 32));
        int y_hash = (int) (y_bits ^ (y_bits >>> 32));
        //объединяем хэш координаты через XOR
        return x_hash ^ y_hash;
    }

    //переопределение метода для создания копии точки
    @Override
    public Object clone() {
        //создание нового объекта с теми же координатами
        return new FunctionPoint(this.x, this.y);
    }
}