package ua.kpi.comsys.IO8206.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PosterDao {
    @Query("SELECT * FROM poster")
    List<PosterEntities> getAll();

    @Query("SELECT * FROM poster WHERE id = :id")
    PosterEntities getById(long id);

    @Query("SELECT * FROM poster WHERE url = :url")
    List<PosterEntities> getByUrl(String url); // здесь в списке всегда должен быть один элемент,
    // List сделан для предотвращения краша, если будет добавлен ещё один элемент

    @Query("SELECT COUNT(*) FROM poster")
    int getDataCount();

    @Insert
    void insert(PosterEntities posterEntities);

    @Update
    void update(PosterEntities posterEntities);

    @Delete
    void delete(PosterEntities posterEntities);
}