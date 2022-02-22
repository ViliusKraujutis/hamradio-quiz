package com.lt.lrmd.hamradio.quiz.model

import android.database.Cursor
import com.lt.lrmd.hamradio.quiz.model.DataSource.CategoryColumns
import android.os.Parcelable
import android.os.Parcel

class Category : CategoryColumns, Parcelable {
    var id: Long
        private set
    var title: String?
        private set
    var text: String?
        private set
    var icon: String?
        private set
    var highScore: Float
        private set
    var mode: Int
        private set

    constructor(c: Cursor) {
        id = c.getLong(CategoryColumns._ID_INDEX)
        title = c.getString(CategoryColumns.TITLE_INDEX)
        text = c.getString(CategoryColumns.TEXT_INDEX)
        icon = c.getString(CategoryColumns.ICON_INDEX)
        mode = c.getInt(CategoryColumns.MODE_INDEX)
        highScore = c.getFloat(CategoryColumns.HIGHSCORE_INDEX)
    }

    fun hasHighScore(): Boolean {
        return highScore >= 0
    }

    /*
	 * Parcelable implementation
	 */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(text)
        dest.writeString(icon)
        dest.writeFloat(highScore)
        dest.writeInt(mode)
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(source: Parcel) {
        id = source.readLong()
        title = source.readString()
        text = source.readString()
        icon = source.readString()
        highScore = source.readFloat()
        mode = source.readInt()
    }

    override fun toString(): String {
        return ("Category [mId=" + id + ", mTitle=" + title + ", mText="
                + text + ", mIcon=" + icon + ",mMode=" + mode + "]")
    }

//    companion object {
//        val CREATOR: Parcelable.Creator<Category> = object : Parcelable.Creator<Category?> {
//            override fun newArray(size: Int): Array<Category?> {
//                return arrayOfNulls(size)
//            }
//
//            override fun createFromParcel(source: Parcel): Category? {
//                return Category(source)
//            }
//        }
//    }
}