package core.dynamo;

import core.IDataStorage;

public interface IDynamo extends IDataStorage {
    public boolean writeRequest(int id);    // запрос на запись в узел id
    public int readRequest(int id);         // запрос на чтение с узла id
    
    public double getAOI(int id);           // получение возраста информации узла id по запросу
}
