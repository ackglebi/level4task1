package com.example.shoppinglist.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.shoppinglist.model.Product

@Dao
interface ProductDao {

    /**
     * suspend fun  = for the coroutines. These methods can't be run
     * from the main method.
     */
    @Query("SELECT * FROM productTable")
    suspend fun getAllProducts() : List<Product>

    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM productTable")
    suspend fun deleteAllProducts()
}