package com.example.mizan_android.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.mizan_android.data.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user_table WHERE isLoggedIn = 1 LIMIT 1")
    User getLoggedInUser();

    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();
}
