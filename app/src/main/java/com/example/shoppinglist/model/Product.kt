package com.example.shoppinglist.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "productTable")
data class Product(

    @ColumnInfo(name = "productQuantity")
    var quantity: Int,

    @ColumnInfo(name = "productName")
    var name: String,

    // TODO stond niet in de opdracht
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null

) : Parcelable