package com.codepath.apps.restclienttemplate.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SampleModelDao {

    // @Query annotation requires knowing SQL syntax
    // See http://www.sqltutorial.org/


    // 1. Query object by unique identifier, colon id needs to match (long id)
    @Query("SELECT * FROM SampleModel WHERE id = :id")
    SampleModel byId(long id);

    // 2. pull last 300 entries created for this table
    @Query("SELECT * FROM SampleModel ORDER BY ID DESC LIMIT 300")
    List<SampleModel> recentItems();

    // 3. insert entries into table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(SampleModel... sampleModels);
}
