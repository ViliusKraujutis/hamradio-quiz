package com.lt.lrmd.hamradio.quiz.activity

import android.app.ListActivity
import android.content.Context
import android.database.Cursor
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ListView
import androidx.cursoradapter.widget.CursorAdapter
import com.lt.lrmd.hamradio.quiz.Config
import com.lt.lrmd.hamradio.quiz.model.Category
import com.lt.lrmd.hamradio.quiz.model.DataSource

class MainCategoriesActivity : ListActivity() {
//    private val mDataSource = DataSource(this)
//
//    //@Inject
//    private val mApp: App? = null
//
//    //@Inject
//    private val mConfig: Config? = null
//    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
//        Log.d(
//            TAG, "onCreatePanelMenu 0x" + Integer.toHexString(featureId)
//                    + ", ?=" + (featureId == Window.FEATURE_OPTIONS_PANEL)
//        )
//        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
//            val show = onCreateOptionsMenu(menu)
//            Log.d(TAG, "called onCreateOptionsMenu=>$show")
//            return show
//        }
//        return false
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.categories)
//        mApp.initialize(this, object : AppInitializationListener {
//            override fun onAppInitialized() {
//                initListAdapter()
//            }
//        })
//    }
//
//    fun openPdfWindowActivity(pdflangobutton: View?) {
//        val intent = Intent(this, PdfWindowActivity::class.java)
//        startActivity(intent)
//    }
//
//    protected override fun onResume() {
//        super.onResume()
//        initListAdapter() // so that we get the updated high scores
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        getMenuInflater().inflate(R.menu.main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_about -> startActivity(Intent(this, About::class.java))
//            R.id.menu_settings -> startActivity(Intent(this, Settings::class.java))
//            else -> return super.onOptionsItemSelected(item)
//        }
//        return true
//    }
//
//    protected fun getMode(categoryId: Long): Int {
//        return if (mConfig!!.flashcardMode()) Config.MODE_FLASHCARD else Config.MODE_MULTIPLE_CHOICE
//    }
//
//    protected override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
//        startActivity(
//            Intent(this, Quiz::class.java).putExtra(
//                Quiz.CATEGORY_ID_EXTRA, id
//            ).putExtra(
//                Quiz.MODE_EXTRA,
//                getMode(id)
//            )
//        )
//    }
//
//    private fun initListAdapter() {
//        setListAdapter(Adapter(mDataSource.queryCategories()))
//    }
//
//    private fun formatScore(score: Float): CharSequence {
//        return (score * 100).toInt().toString() + "%"
//    }
//
//    private inner class Adapter(cursor: Cursor?) : CursorAdapter(this@Categories, cursor, false) {
//        override fun bindView(view: View, context: Context, cursor: Cursor) {
//            val c = Category(cursor)
//            view.findViewById<View>(R.id.iconFrame).visibility = View.GONE
//            (view.findViewById<View>(R.id.title) as TextView).setText(c.title)
//            val text: TextView = view.findViewById<View>(R.id.text) as TextView
//            if (c.text != null) {
//                text.setText(mApp.getHtmlCache().getHtml(c.text))
//            } else {
//                text.setVisibility(View.GONE)
//            }
//            val info: TextView = view.findViewById<View>(R.id.info) as TextView
//            if (c.hasHighScore() && mConfig!!.highScores()) {
//                info.setText(formatScore(c.highScore))
//            } else {
//                info.setVisibility(View.GONE)
//            }
//        }
//
//        override fun newView(context: Context, c: Cursor, root: ViewGroup): View {
//            return getLayoutInflater().inflate(
//                R.layout.category_list_item,
//                root, false
//            )
//        }
//    }
//
//    companion object {
//        private val TAG = Categories::class.java.simpleName
//    }
}