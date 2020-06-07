package com.example.practice15

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT *FROM BOOKS")
    fun getAllBooks():List<BookEntity>

    @Query("SELECT *FROM BOOKS WHERE book_id=:bookId")
    fun getBookById(bookId:String):BookEntity
}