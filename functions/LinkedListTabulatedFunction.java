package functions;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

// класс, реализующий табулированную функцию с использованием двусвязного циклического списка
public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {
    // внутренний класс для узла двусвязного списка
    private static class FunctionNode {
        private FunctionPoint point;    // данные точки, хранящейся в узле
        private FunctionNode prev;      // ссылка на предыдущий узел в списке
        private FunctionNode next;      // ссылка на следующий узел в списке

        // конструктор с точкой
        // создает узел с заданной точкой и null-ссылками
        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head; // голова циклического списка
    private int count;         // количество точек в функции

    //конструктор без параметров (обязательное требование Externalizable)
    public LinkedListTabulatedFunction(){
        initializeList();
    }

    // фабрика для создания объектов LinkedListTabulatedFunction
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }

    // реализация итератора для связного списка
    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next;

            @Override
            public boolean hasNext() {
                // проверяем, не дошли ли до головы (конца списка)
                return currentNode != head;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    // бросаем исключение если элементов больше нет
                    throw new NoSuchElementException("В табличной функции больше нет элементов");
                }
                // создаем копию точки и переходим к следующему узлу
                FunctionPoint point = new FunctionPoint(currentNode.point);
                currentNode = currentNode.next;
                return point;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Операция удаления не поддерживается");
            }
        };
    }

    //метод записи объекта в поток
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        //записываем количество точек
        out.writeInt(count);
        //обходим все узлы списка и записываем координаты точек
        FunctionNode node = head.next;  //начинаем с первого реального узла
        while (node != head) {  //пока не вернемся к голове (циклический список)
            out.writeDouble(node.point.getX());  //x-координата
            out.writeDouble(node.point.getY());  //y-координата
            node = node.next;  //переходим к следующему узлу
        }
    }

    //метод чтения объекта из потока
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //инициализируем пустой список
        initializeList();
        //читаем количество точек
        int pointsCount = in.readInt();
        //читаем координаты и добавляем точки в список
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();  //читаем x-координату
            double y = in.readDouble();  //читаем y-координату

            //создаем новый узел и добавляем его в конец списка
            FunctionNode newNode = addNodeToTail();
            newNode.point = new FunctionPoint(x, y);
        }
    }

    // возвращаем строковое представление ф-ии
    @Override
    public String toString(){
        // создаем строку с открывающей скобкой
        String result = "{";
        // проходим по всем точкам
        FunctionNode current = head.next;
        while (current != head){
            result += "(" + current.point.getX() + "; " + current.point.getY() + ")";
            // если есть следующая точка (не голова), тогда добавляем запятую и пробел
            if (current.next != head){
                result += ", ";
            }
            current = current.next;
        }
        // закрываем скобку и возвращаем результат
        return result + "}";
    }

    // сравниваем табулированную ф-ию с другим объектом на равенство
    @Override
    public boolean equals(Object o){
        // если объект тот же - возвращаем true
        if (this == o) return true;
        // если наш объект не табулированная ф-ия возвращаем false
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction other = (TabulatedFunction) o;

        if (o instanceof LinkedListTabulatedFunction) { // если объект тоже LinkedListTabulatedFunction
            LinkedListTabulatedFunction otherList = (LinkedListTabulatedFunction) o;
            if (this.count != otherList.count){ // сравниваем кол-во точек
                return false;
            }
            //проходим по спискам
            FunctionNode thisNode = this.head.next;
            FunctionNode otherNode = otherList.head.next;
            while (thisNode != this.head){ // проходим по всем точкам списка
                if (!thisNode.point.equals(otherNode.point)){ // сравниваем точки с помощью equals
                    return false;
                }
                //переходим к следующим тчокам в обоих списках
                thisNode = thisNode.next;
                otherNode = otherNode.next;
            }
        }
        else { // если объект другой реализации TabulatedFunction
            if (this.getPointsCount() != other.getPointsCount()) { // сравниваем количество точек
                return false; // если разное количество точек возвращаем false
            }
            FunctionNode currentNode = this.head.next;
            int index = 0; //индекс для доступа к точкам другой функции
            while (currentNode != this.head) { // проходим по всем точкам
                FunctionPoint thisPoint = this.getPoint(index); // получаем точку текущей функции
                FunctionPoint otherPoint = other.getPoint(index); // получаем точку другой функции
                if (!thisPoint.equals(otherPoint)) { // сравниваем точки
                    return false; // если точки не равны возвращаем false
                }
                currentNode = currentNode.next;
                index++;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // начинаем с количества точек, чтобы отличать функции с разным числом точек
        int hash = count;
        // начинаем обход списка с первой реальной точки (после головы)
        FunctionNode current = head.next;
        // проходим по всем точкам
        while (current != head) {
            // вычисляем хэш для каждой точки и объединяем через операцию XOR
            hash ^= current.point.hashCode();
            // переходим к следующей точке в списке
            current = current.next;
        }
        // возвращаем итоговый хэш-код
        return hash;
    }

    @Override
    public Object clone(){
        // создаем временный массив для хранения копий всех точек
        FunctionPoint[] copiedPoints = new FunctionPoint[count];
        // начинаем обход списка с первой реальной точки
        FunctionNode current = head.next;
        int index = 0;
        // проходим по всем точкам исходного списка
        while (current != head) {
            // создаем глубокую копию каждой точки через конструктор копирования
            copiedPoints[index] = new FunctionPoint(current.point);
            // переходим к следующей точке
            current = current.next;
            index++;
        }
        // создаем новый объект функции через конструктор, передавая скопированные точки
        return new LinkedListTabulatedFunction(copiedPoints);
    }

    // метод для сравнения вещественных чисел с учетом погрешности
    private boolean equals(double a, double b) {
        return Math.abs(a - b) < 1e-10;
    }

    // проверка корректности индекса
    // выбрасывает исключение, если индекс выходит за границы списка точек
    private void checkIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (count - 1) + "]");
        }
    }

    // конструктор для получения точек FunctionPoint
    public LinkedListTabulatedFunction(FunctionPoint [] points) throws IllegalArgumentException {
        // проверка, что точек достаточно
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество тчоек должно быть не менее 2");
        }
        // упорядоченность точек по x
        for (int i = 0; i < points.length - 1; i++){
            if(points[i].getX() >= points[i + 1].getX()){
                throw new IllegalArgumentException("Точки должны быть упорядочены по возрастанию x");
            }
        }

        //инициализация пустого списка
        initializeList();

        //добавление точек с созданием копий
        for (FunctionPoint point : points){
            try {
                addPoint(new FunctionPoint(point));
            } catch (InappropriateFunctionPointException e){

            }
        }
    }
    // инициализация пустого циклического списка с головой
    // создаем служебный узел head, который ссылается сам на себя
    // в пустом списке head.next и head.prev указывают на head
    private void initializeList() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        count = 0;
    }

    // получаем узел по индексу
    // последовательно проходим по списку от головы до нужного узла
    private FunctionNode getNode(int index) {
        // проверка корректности индекса
        checkIndex(index);
        // начинаем с первого реального узла (после головы)
        FunctionNode current = head.next;
        // последовательно переходим к следующему узлу index раз
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // добавляем узел в конец списка
    // создаем новый узел и вставляем его перед головой (в циклическом списке)
    private FunctionNode addNodeToTail() {
        // создание нового узла, который будет вставлен между последним узлом и головой
        FunctionNode newNode = new FunctionNode(null);
        // установка ссылок нового узла
        newNode.prev = head.prev;
        newNode.next = head;
        // обновление ссылок соседних узлов
        head.prev.next = newNode;
        head.prev = newNode;
        // увеличение счетчика точек
        count++;

        // возвращаем ссылку на созданный узел
        return newNode;
    }

    // добавляем узел по указанному индексу
    // вставляем новый узел перед узлом с заданным индексом
    private FunctionNode addNodeByIndex(int index) {
        // проверка корректности индекса (допустимы значения от 0 до count)
        if (index < 0 || index > count) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + count + "]");
        }

        // если индекс равен количеству точек, добавляем в конец
        if (index == count) {
            return addNodeToTail();
        }

        // получение узла, перед которым нужно вставить новый узел
        FunctionNode nodeAtIndex = getNode(index);
        // создание нового узла
        FunctionNode newNode = new FunctionNode(null);
        // установка ссылок нового узла
        newNode.prev = nodeAtIndex.prev;
        newNode.next = nodeAtIndex;
        // обновление ссылок соседних узлов
        nodeAtIndex.prev.next = newNode;
        nodeAtIndex.prev = newNode;
        // увеличение счетчика точек
        count++;
        return newNode;
    }

    // удаляем узел по указанному индексу
    // извлекаем узел из списка и обновляем ссылки соседних узлов
    private void deleteNodeByIndex(int index) {
        // проверка корректности индекса
        checkIndex(index);
        // проверка, что после удаления останется минимум 2 точки
        if (count < 3) {
            throw new IllegalStateException("Невозможно удалить точку: количество точек должно быть не менее 3");
        }

        // получение узла для удаления
        FunctionNode nodeToDelete = getNode(index);
        // обход удаляемого узла: предыдущий узел ссылается на следующий и наоборот
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;
        // уменьшение счетчика точек
        count--;
    }

    // конструктор для создания функции с равномерной сеткой точек
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        // проверка корректности входных параметров
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // инициализация пустого списка
        initializeList();

        // вычисление шага между соседними точками
        double step = (rightX - leftX) / (pointsCount - 1);
        // создание точек с координатами x и начальным значением y = 0
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            // добавление точки через метод addPoint для упрощения
            try {
                addPoint(new FunctionPoint(x, 0));
            } catch (InappropriateFunctionPointException e) {
                // не должно происходить, так как точки создаются с уникальными x
            }
        }
    }

    // конструктор для создания функции с равномерной сеткой и заданными значениями y
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        // проверка корректности входных параметров
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // инициализация пустого списка
        initializeList();

        // вычисление шага между соседними точками
        double step = (rightX - leftX) / (values.length - 1);
        // создание точек с координатами x и значениями y из массива values
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            // добавление точки через метод addPoint для упрощения
            try {
                addPoint(new FunctionPoint(x, values[i]));
            } catch (InappropriateFunctionPointException e) {
                // не должно происходить, так как точки создаются с уникальными x
            }
        }
    }

    // получаем левую границу области определения функции
    public double getLeftDomainBorder() {
        // если список пуст, возвращает NaN
        if (count == 0) return Double.NaN;

        // возвращаем x-координату первой точки в списке (после головы)
        return head.next.point.getX();
    }

    // получаем правую границу области определения функции
    public double getRightDomainBorder() {
        // если список пуст, возвращает NaN
        if (count == 0) return Double.NaN;

        // возвращаем x-координату последней точки в списке (перед головой)
        return head.prev.point.getX();
    }

    // вычисляем значение функции в точке x с помощью линейной интерполяции
    public double getFunctionValue(double x) {
        // проверка, находится ли x в области определения функции
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        //проверяем сопвадения
        FunctionNode current = head.next;
        while (current != head){
            if (equals(x, current.point.getX())) {
                return current.point.getY(); //возвращаем y если x совпал
            }
            current = current.next;
        }

        // поиск интервала, в котором находится x
        while (current != head) {
            FunctionNode next = current.next;
            // если next не голова и x находится в текущем интервале [current.x, next.x]
            if (next != head && x >= current.point.getX() && x <= next.point.getX()) {
                double x1 = current.point.getX();
                double x2 = next.point.getX();
                double y1 = current.point.getY();
                double y2 = next.point.getY();
                // линейная интерполяция
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }

        return Double.NaN;
    }

    // возвращаем количество точек в табулированной функции
    public int getPointsCount() {
        return count;
    }

    // получаем точку по указанному индексу
    public FunctionPoint getPoint(int index) {
        // получение узла по индексу и возврат копии его точки
        return new FunctionPoint(getNode(index).point);
    }

    // устанавливаем новую точку по указанному индексу
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        // получение узла по индексу
        FunctionNode node = getNode(index);

        // проверка упорядоченности
        // если есть предыдущая точка, новая x должна быть больше ее x
        if (index > 0 && point.getX() <= node.prev.point.getX()) {
            throw new InappropriateFunctionPointException("Координата x должна быть больше предыдущей точки");
        }
        // если есть следующая точка, новая x должна быть меньше ее x
        if (index < count - 1 && point.getX() >= node.next.point.getX()) {
            throw new InappropriateFunctionPointException("Координата x должна быть меньше следующей точки");
        }

        // установка новой точки
        node.point = new FunctionPoint(point);
    }

    // получаем координату x точки по указанному индексу
    public double getPointX(int index) {
        return getNode(index).point.getX();
    }


    // устанавливаем координату x точки по указанному индексу
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        // получение узла по индексу
        FunctionNode node = getNode(index);

        // проверка упорядоченности
        if (index > 0 && x <= node.prev.point.getX()) {
            throw new InappropriateFunctionPointException("Координата x должна быть больше предыдущей точки");
        }
        if (index < count - 1 && x >= node.next.point.getX()) {
            throw new InappropriateFunctionPointException("Координата x должна быть меньше следующей точки");
        }

        // установка нового значения x
        node.point.setX(x);
    }

    // получаем координату y точки по указанному индексу
    public double getPointY(int index) {
        return getNode(index).point.getY();
    }

    // устанавливаем координату y точки по указанному индексу
    public void setPointY(int index, double y) {
        getNode(index).point.setY(y);
    }

    // удаляем точку по указанному индексу
    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    // добавляем новую точку в табулированную функцию
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // поиск позиции для вставки (точки должны быть упорядочены по x)
        int insertIndex = 0;
        // начинаем с первого реального узла
        FunctionNode current = head.next;
        // ищем позицию, где current.x >= point.x
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            insertIndex++;
        }

        // проверка, что точка с таким x еще не существует
        if (current != head && equals(current.point.getX(), point.getX())) {
            throw new InappropriateFunctionPointException("Точка с координатой x = " + point.getX() + " уже существует");
        }

        // вставка новой точки
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }
}