package ua.kpi.comsys.IO8206.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FilmDao {

    @Query("SELECT * FROM FilmEntities")
    List<FilmEntities> getAll();

    @Query("SELECT * FROM FilmEntities WHERE SearchRequest = :searchRequest")
    List<FilmEntities> getByRequest(String searchRequest);

    @Query("SELECT * FROM FilmEntities WHERE id = :id")
    FilmEntities getById(long id);

    @Insert
    void insert(FilmEntities film);

    @Update
    void update(FilmEntities film);

    @Delete
    void delete(FilmEntities film);
}

