package com.abduvosit.devoloper.lugat.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.abduvosit.devoloper.lugat.model.Word

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "words.db"
        private const val DATABASE_VERSION = 3

        private const val TABLE_WORDS = "words"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ENGLISH = "english"
        private const val COLUMN_UZBEK = "uzbek"
        private const val COLUMN_MEMORIZED = "memorized"
        private const val COLUMN_EXAMPLE = "example"
        private const val COLUMN_PART_OF_SPEECH = "part_of_speech"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
        CREATE TABLE $TABLE_WORDS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ENGLISH TEXT,
            $COLUMN_UZBEK TEXT,
            $COLUMN_MEMORIZED INTEGER,
            $COLUMN_EXAMPLE TEXT,
            $COLUMN_PART_OF_SPEECH TEXT
        )
    """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db?.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_PART_OF_SPEECH TEXT")
        }
    }

    fun addWord(word: Word): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ENGLISH, word.english)
            put(COLUMN_UZBEK, word.uzbek)
            put(COLUMN_MEMORIZED, if (word.memorized) 1 else 0)
            put(COLUMN_EXAMPLE, word.example)
            put(COLUMN_PART_OF_SPEECH, word.partOfSpeech)
        }
        return db.insert(TABLE_WORDS, null, values)
    }

    private fun parseCursorToWord(cursor: android.database.Cursor): Word {
        return Word(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            english = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENGLISH)),
            uzbek = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UZBEK)),
            memorized = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEMORIZED)) == 1,
            example = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXAMPLE)),
            partOfSpeech = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PART_OF_SPEECH))
        )
    }

    fun getAllWords(): List<Word> {
        val list = mutableListOf<Word>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WORDS", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(parseCursorToWord(it))
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun getMemorizedWords(): List<Word> {
        val list = mutableListOf<Word>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WORDS WHERE $COLUMN_MEMORIZED = 1", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(parseCursorToWord(it))
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun getNotMemorizedWords(): List<Word> {
        val list = mutableListOf<Word>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_WORDS WHERE $COLUMN_MEMORIZED = 0", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(parseCursorToWord(it))
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun deleteWord(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_WORDS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateMemorized(id: Int, memorized: Boolean): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MEMORIZED, if (memorized) 1 else 0)
        }
        return db.update(TABLE_WORDS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun searchWords(query: String, lang: String): List<Word> {
        val wordList = mutableListOf<Word>()
        val db = readableDatabase

        val columnToSearch = when (lang) {
            "english" -> COLUMN_ENGLISH
            "uzbek" -> COLUMN_UZBEK
            else -> "$COLUMN_ENGLISH || ' ' || $COLUMN_UZBEK"
        }

        val selection = "$columnToSearch LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        val cursor = db.query(
            TABLE_WORDS,
            arrayOf(COLUMN_ID, COLUMN_ENGLISH, COLUMN_UZBEK, COLUMN_EXAMPLE, COLUMN_MEMORIZED, COLUMN_PART_OF_SPEECH),
            selection,
            selectionArgs,
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    wordList.add(parseCursorToWord(it))
                } while (it.moveToNext())
            }
        }

        return wordList
    }
}
