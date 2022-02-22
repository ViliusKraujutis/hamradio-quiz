package com.lt.lrmd.hamradio.quiz.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.lt.lrmd.hamradio.quiz.util.Util
import java.io.File
import java.util.*

/**
 * Provides access to the database.
 */
class DataSource constructor(private val context: Context) {
    interface CategoryColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "category"
            const val TITLE_COLUMN = "title"
            const val TEXT_COLUMN = "text"
            const val ICON_COLUMN = "icon"
            const val HIGHSCORE_COLUMN = "highscore"
            const val MODE_COLUMN = "mode"
            val COLUMNS = arrayOf(
                BaseColumns._ID, TITLE_COLUMN,
                TEXT_COLUMN, ICON_COLUMN, HIGHSCORE_COLUMN, MODE_COLUMN
            )
            const val _ID_INDEX = 0
            const val TITLE_INDEX = 1
            const val TEXT_INDEX = 2
            const val ICON_INDEX = 3
            const val HIGHSCORE_INDEX = 4
            const val MODE_INDEX = 5
            val CREATE_SQL = ("CREATE TABLE " + TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TITLE_COLUMN + " TEXT,"
                    + TEXT_COLUMN + " TEXT,"
                    + ICON_COLUMN + " TEXT,"
                    + HIGHSCORE_COLUMN + " REAL,"
                    + MODE_COLUMN + " INTEGER)")
        }
    }

    interface QuestionColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "question"
            const val CATEGORY_ID_COLUMN = "category_id"
            const val TEXT_COLUMN = "text"
            const val AUDIO_COLUMN = "audio"
            const val IMAGE_COLUMN = "image"
            const val CHOICES_COLUMN = "choices"
            const val ANSWER_COLUMN = "answer"
            val COLUMNS = arrayOf(
                BaseColumns._ID, CATEGORY_ID_COLUMN,
                TEXT_COLUMN, AUDIO_COLUMN, IMAGE_COLUMN, CHOICES_COLUMN,
                ANSWER_COLUMN
            )
            const val _ID_INDEX = 0
            const val CATEGORY_ID_INDEX = 1
            const val TEXT_INDEX = 2
            const val AUDIO_INDEX = 3
            const val IMAGE_INDEX = 4
            const val CHOICES_INDEX = 5
            const val ANSWER_INDEX = 6
            val CREATE_SQL = ("CREATE TABLE " + TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CATEGORY_ID_COLUMN + " INTEGER,"
                    + TEXT_COLUMN + " TEXT,"
                    + AUDIO_COLUMN + " TEXT,"
                    + IMAGE_COLUMN + " TEXT,"
                    + CHOICES_COLUMN + " BLOB,"
                    + ANSWER_COLUMN + " INTEGER)")
        }
    }

    private val openHelper: OpenHelper

    private class OpenHelper(context: Context?) :
        SQLiteOpenHelper(context, "quiz.db", null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CategoryColumns.CREATE_SQL)
            db.execSQL(QuestionColumns.CREATE_SQL)
            db.execSQL("CREATE TABLE metadata (apkLastModified INTEGER)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE " + CategoryColumns.TABLE_NAME)
            db.execSQL("DROP TABLE " + QuestionColumns.TABLE_NAME)
            db.execSQL("DROP TABLE metadata")
            onCreate(db)
        }
    }

    fun reset() {
        openHelper.onUpgrade(openHelper.getWritableDatabase(), 0, 1)
        updateApkLastModified()
    }

    val realApkLastModified: Long
        get() = File(context.packageCodePath).lastModified()
    val isDatabaseCreated: Boolean
        get() = realApkLastModified <= apkLastModified
    var apkLastModified: Long
        get() {
            val c = read().query("metadata")
            return if (c != null && c.moveToFirst()) {
                c.getLong(c.getColumnIndex("apkLastModified"))
            } else 0
        }
        set(lastModified) {
            write().delete("metadata", "1")
            val cv = ContentValues()
            cv.put("apkLastModified", lastModified)
            write().insert("metadata", cv)
        }

    fun updateApkLastModified() {
        apkLastModified = realApkLastModified
    }

    fun updateCategory(id: Long, title: String?, text: String?, icon: String?, mode: Int) {
        val cv = ContentValues()
        cv.put(CategoryColumns.TITLE_COLUMN, title)
        cv.put(CategoryColumns.TEXT_COLUMN, text)
        cv.put(CategoryColumns.ICON_COLUMN, icon)
        cv.put(CategoryColumns.MODE_COLUMN, mode)
        write().update(CategoryColumns.TABLE_NAME, "_id=$id", cv)
    }

    fun addCategory(): Long {
        val cv = ContentValues()
        cv.put(CategoryColumns.HIGHSCORE_COLUMN, -1f)
        return write().insert(CategoryColumns.TABLE_NAME, cv)
    }

    fun addQuestion(
        categoryId: Long, text: String?, image: String?,
        audio: String?, choices: List<String>, answer: Int
    ): Long {
        return addQuestion(
            categoryId, text, image, audio,
            choices.toTypedArray(), answer
        )
    }

    fun addQuestion(
        categoryId: Long, text: String?, image: String?,
        audio: String?, choices: Array<String>?, answer: Int
    ): Long {
        val cv = ContentValues()
        cv.put(QuestionColumns.CATEGORY_ID_COLUMN, categoryId)
        cv.put(QuestionColumns.TEXT_COLUMN, text)
        cv.put(QuestionColumns.IMAGE_COLUMN, image)
        cv.put(QuestionColumns.AUDIO_COLUMN, audio)
        cv.put(QuestionColumns.CHOICES_COLUMN, Util.serialize(choices))
        cv.put(QuestionColumns.ANSWER_COLUMN, answer)
        return write().insert(QuestionColumns.TABLE_NAME, cv)
    }

    fun saveScore(categoryId: Long, highScore: Float): Boolean {
        val cv = ContentValues()
        cv.put(CategoryColumns.HIGHSCORE_COLUMN, highScore)
        return 0 != write().update(
            CategoryColumns.TABLE_NAME,
            CategoryColumns.HIGHSCORE_COLUMN + "<" + highScore + " AND "
                    + BaseColumns._ID + "=" + categoryId, cv
        )
    }

    fun queryCategories(): Cursor {
        return read()
            .query(CategoryColumns.TABLE_NAME, CategoryColumns.COLUMNS)
    }

    fun getCategory(categoryId: Long): Category? {
        val c = read().query(
            CategoryColumns.TABLE_NAME, CategoryColumns.COLUMNS,
            BaseColumns._ID + "=" + categoryId
        )
        var category: Category? = null
        if (c.moveToFirst()) {
            category = Category(c)
        }
        c.close()
        return category
    }

    fun getCategoryMode(categoryId: Long): Int {
        return getCategory(categoryId)!!.mode
    }

    fun queryQuestions(categoryId: Long): Cursor {
        return read().query(
            QuestionColumns.TABLE_NAME,
            QuestionColumns.COLUMNS,
            QuestionColumns.CATEGORY_ID_COLUMN + "=" + categoryId
        )
    }

    fun createQuiz(categoryId: Long, numQuestions: Int, seed: Long): Array<Question?> {
        var numQuestions = numQuestions
        val c = read().query(
            QuestionColumns.TABLE_NAME,
            QuestionColumns.COLUMNS,
            QuestionColumns.CATEGORY_ID_COLUMN + "=" + categoryId
        )
        val total = c.count
        if (total < numQuestions) numQuestions = total

        // This is from Knuth 3.4.2, algorithm S. It selects
        // each question with equal probability, and surprisingly,
        // the cursor never runs off the end. Cool, huh?
        var t = 0 // record index
        var m = 0 // records selected so far
        val r = Random(seed)
        val qs = arrayOfNulls<Question>(numQuestions)
        while (m < numQuestions) {
            if (r.nextFloat() * (total - t) < numQuestions - m) {
                qs[m++] = Question(c)
            }
            t++
            c.moveToNext()
        }
        // Give the questions a shuffle
        for (i in 0 until numQuestions) {
            val j = r.nextInt(numQuestions)
            val tmp = qs[i]
            qs[i] = qs[j]
            qs[j] = tmp
        }
        return qs
    }

    fun read(): DatabaseWrapper {
        return DatabaseWrapper(openHelper.getReadableDatabase())
    }

    fun write(): DatabaseWrapper {
        return DatabaseWrapper(openHelper.getWritableDatabase())
    }

    class DatabaseWrapper(db: SQLiteDatabase) {
        val db: SQLiteDatabase
        fun query(
            tableName: String?, columns: Array<String>?,
            selection: String?, selectionArgs: Array<String?>?, groupBy: String?,
            having: String?, orderBy: String?
        ): Cursor {
            return db.query(
                tableName, columns, selection, selectionArgs,
                groupBy, having, orderBy
            )
        }

        @JvmOverloads
        fun query(
            table: String?, columns: Array<String>? = null, selection: String? = null,
            selectionArgs: Array<String?>? = null, groupBy: String? = null, having: String? =
                null
        ): Cursor {
            return query(
                table, columns, selection, selectionArgs, groupBy,
                having, null
            )
        }

        fun insert(table: String?, cv: ContentValues?): Long {
            return db.insert(table, "title", cv)
        }

        fun update(table: String?, selection: String?, cv: ContentValues?): Int {
            return db.update(table, cv, selection, null)
        }

        @JvmOverloads
        fun delete(table: String?, selection: String?, selectionArgs: Array<String?>? = null): Int {
            return db.delete(table, selection, selectionArgs)
        }

        init {
            this.db = db
        }
    }

    companion object {
        // increasing this value will force the database to be reloaded.  Decreasing
        // it will cause an error.
        const val DB_VERSION = 5
    }

    init {
        openHelper = OpenHelper(context)
    }
}