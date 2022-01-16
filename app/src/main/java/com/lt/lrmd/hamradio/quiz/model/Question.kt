package com.lt.lrmd.hamradio.quiz.model

import android.database.Cursor
import com.lt.lrmd.hamradio.quiz.model.DataSource.QuestionColumns
import android.os.Parcelable
import android.os.Parcel
import com.lt.lrmd.hamradio.quiz.model.Question
import com.lt.lrmd.hamradio.quiz.util.Util
import java.util.*

class Question : QuestionColumns, Parcelable {
    var id: Long
        private set

    /**
     * The text of the question
     *
     * @return the text
     */
    var text: String?
        private set
    var image: String?
        private set
    var audio: String?
        private set

    /**
     * Choices to present to the user.
     *
     * @return the choices
     */
    var choices: Array<String>?
        private set

    /**
     * The index of the correct answer in [.getChoices].
     *
     * @return the index
     */
    var answer: Int
        private set

    /**
     * Create a question from the database
     */
    constructor(c: Cursor) {
        text = c.getString(QuestionColumns.TEXT_INDEX)
        image = c.getString(QuestionColumns.IMAGE_INDEX)
        audio = c.getString(QuestionColumns.AUDIO_INDEX)
        choices = Util.deserialize(c.getBlob(QuestionColumns.CHOICES_INDEX)) as Array<String>
        answer = c.getInt(QuestionColumns.ANSWER_INDEX)
        id = c.getLong(QuestionColumns._ID_INDEX)
    }

    override fun toString(): String {
        return ("Question [mText=" + text + ", mImage=" + image + ", mAudio="
                + audio + ", mChoices=" + Arrays.toString(choices)
                + ", mAnswer=" + answer + "]")
    }

    private constructor(parcel: Parcel) {
        id = parcel.readLong()
        text = parcel.readString()
        image = parcel.readString()
        audio = parcel.readString()
        choices = parcel.createStringArray()
        answer = parcel.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(text)
        dest.writeString(image)
        dest.writeString(audio)
        dest.writeStringArray(choices)
        dest.writeInt(answer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        /*
	 * Parcelable implementation
	 */
        val CREATOR: Parcelable.Creator<Question> = object : Parcelable.Creator<Question?> {
            override fun createFromParcel(source: Parcel): Question? {
                return Question(source)
            }

            override fun newArray(size: Int): Array<Question?> {
                return arrayOfNulls(size)
            }
        }
    }
}