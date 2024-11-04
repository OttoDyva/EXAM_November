package dat.daos;

import java.util.List;

public interface IDAO<T> {
    T create(T t);
    T getById(Integer integer);
    List<T> getAll();
    T update(Integer integer, T t);
    void delete(Integer integer);
}
