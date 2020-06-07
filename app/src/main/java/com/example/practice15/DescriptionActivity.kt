package com.example.practice15

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var bookName:TextView
    lateinit var bookAuthor:TextView
    lateinit var bookRating:TextView
    lateinit var bookPrice:TextView
    lateinit var bookImage:ImageView
    lateinit var btnFav:Button
    lateinit var bookDesc:TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    var bookid:String?="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        bookName=findViewById(R.id.bookName)
        bookAuthor=findViewById(R.id.bookAuthor)
        bookRating=findViewById(R.id.bookRating)
        bookPrice=findViewById(R.id.bookPrice)
        bookImage=findViewById(R.id.bookImage)
        btnFav=findViewById(R.id.btnFav)
        bookDesc=findViewById(R.id.bookDescription)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)
        progressLayout.visibility=View.VISIBLE
        progressBar.visibility=View.VISIBLE
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Book Details"

        if(intent!=null)
        {
            bookid=intent.getStringExtra("book_id")
        }
        else
        {
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Error Occurred!!!",Toast.LENGTH_SHORT)
                .show()
        }
        if(bookid=="100")
        {
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Error Occurred!!!",Toast.LENGTH_SHORT)
                .show()
        }
        val queue=Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"

        val jsonParam=JSONObject()
        jsonParam.put("book_id",bookid)

        if(ConnectionManager().checkConnectivity(this@DescriptionActivity))
        {
            val jsonObjectRequest=object :JsonObjectRequest(Request.Method.POST,url,jsonParam,
                Response.Listener {

                    try {
                        val success=it.getBoolean("success")
                        if(success)
                        {
                            val jsonObject=it.getJSONObject("book_data")
                            progressLayout.visibility=View.GONE
                            val bookImageUrl=jsonObject.getString("image")
                            Picasso.get().load(jsonObject.getString("image"))
                                .error(R.drawable.default_image).into(bookImage);
                            bookName.text=jsonObject.getString("name")
                            bookAuthor.text=jsonObject.getString("author")
                            bookRating.text=jsonObject.getString("rating")
                            bookPrice.text=jsonObject.getString("price")
                            bookDesc.text=jsonObject.getString("description")
                            val bookEntity=BookEntity(
                                bookid?.toInt() as Int,
                                bookName.text.toString(),
                                bookAuthor.text.toString(),
                                bookRating.text.toString(),
                                bookPrice.text.toString(),
                                bookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav=checkFav.get()
                            if(isFav)
                            {
                              btnFav.text="Remove From Favorites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavorites)
                                btnFav.setBackgroundColor(favColor)
                            }
                            else
                            {
                                btnFav.text="Add To Favorite"
                                val noFavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                btnFav.setBackgroundColor(noFavColor)
                            }
                            btnFav.setOnClickListener {
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get())
                                {
                                    val asyns=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result=asyns.get()
                                    if(result)
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Book Added to Favourites",
                                            Toast.LENGTH_SHORT).show()
                                        btnFav.text="Remove From Favorites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavorites)
                                        btnFav.setBackgroundColor(favColor)
                                    }
                                    else
                                    {
                                       Toast.makeText(this@DescriptionActivity,"Some Error Occurred!!",
                                           Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else
                                {
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()
                                    if(result)
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Book Removed From Favourites",
                                            Toast.LENGTH_SHORT).show()
                                        btnFav.text="Add to  Favorites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        btnFav.setBackgroundColor(favColor)
                                    }
                                    else
                                    {
                                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred!!",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }catch (e:JSONException)
                    {
                        Toast.makeText(this@DescriptionActivity,"Some Error Occurred!!!",Toast.LENGTH_SHORT)
                            .show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this@DescriptionActivity,"Volly Error Occurred!!!",Toast.LENGTH_SHORT)
                        .show()
                })
            {
                override fun getHeaders(): MutableMap<String, String> {

                    val headers=HashMap<String,String>()
                    headers["content-type"]="application/json"
                    headers["token"]="879d3c08e52d7d"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else
        {
            val dialog= AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setTitle("NO InterNet Connection")
            dialog.setPositiveButton("Open Setting"){text,listener->
                val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                finish()

            }
            dialog.setNegativeButton("Cancel"){text,listener->
                ActivityCompat.finishAffinity(this@DescriptionActivity)

            }
            dialog.create()
            dialog.show()
        }
    }
    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode: Int):AsyncTask<Void,Void,Boolean>()
    {
        val db=Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode)
            {
                1->{
                val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }
                2->
                {
                   db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->
                {
                  db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}
