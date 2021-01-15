package ru.marina.contactlistviewermvvm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.marina.contactlistviewermvvm.data.db.converter.DateConverter
import ru.marina.contactlistviewermvvm.data.db.converter.TemperamentConverter
import ru.marina.contactlistviewermvvm.data.db.dao.ContactsDao
import ru.marina.contactlistviewermvvm.data.db.entity.ContactDb

@Database(
    entities = [
        ContactDb::class
    ],
    version = 1
)
@TypeConverters(
    TemperamentConverter::class,
    DateConverter::class
)
abstract class AppDb : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
}