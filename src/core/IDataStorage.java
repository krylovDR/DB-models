package core;

// интерфейс 
public interface IDataStorage {
    public void doWrite();      // метод моделирования записи в системе
    public int doRead();        // метод моделирования чтения в системе

    public String toString();   // метод вывода состояния системы
}
