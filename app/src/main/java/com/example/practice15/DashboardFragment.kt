package com.example.practice15

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    lateinit var recycleAdapter: DashboardRecycleAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var bookInfoList=ArrayList<Book>()

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var ratingComparator = Comparator<Book>{book1, book2 ->

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            // sort according to name if rating is same
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerView=view.findViewById(R.id.recyclerDashboard)
        layoutManager=LinearLayoutManager(activity)

        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)

        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"
        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            progressLayout.visibility=View.VISIBLE
            val jsonObjectRequest=object :JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    try {
                        val success=it.getBoolean("success")
                        if(success)
                        {
                            progressLayout.visibility=View.GONE
                            val data=it.getJSONArray("data")
                            for(i in 0 until data.length())
                            {
                                val bookJsonObject=data.getJSONObject(i)
                                val bookObject=Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recycleAdapter= DashboardRecycleAdapter(activity as Context ,bookInfoList)
                                recyclerView.adapter=recycleAdapter
                                recyclerView.layoutManager=layoutManager
                            }

                        }
                        else
                        {
                            Toast.makeText(activity as Context,"Some Error Occurred!!!",Toast.LENGTH_SHORT)
                                .show()
                        }
                    }catch (e:JSONException)
                    {
                        Toast.makeText(activity as Context,"Some Error Occurred!!!",Toast.LENGTH_SHORT)
                            .show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(activity as Context,"Volly  Error Occurred!!!",Toast.LENGTH_SHORT)
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
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setTitle("NO InterNet Connection")
            dialog.setPositiveButton("Open Setting"){text,listener->
                val intent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                activity?.finish()

            }
            dialog.setNegativeButton("Cancel"){text,listener->
                ActivityCompat.finishAffinity(context as Activity)

            }
            dialog.create()
            dialog.show()
        }
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if (id == R.id.action_sort){
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }

        recycleAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}
