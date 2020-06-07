package com.example.practice15

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycle_dashboard_single_row.*

class DashboardRecycleAdapter(val context: Context,val itemList:ArrayList<Book>):RecyclerView.Adapter<DashboardRecycleAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View):RecyclerView.ViewHolder(view)
    {
        val txtBookName:TextView=view.findViewById(R.id.bookName)
        val txtBookAuthor:TextView=view.findViewById(R.id.bookAuthor)
        val txtBookRating:TextView=view.findViewById(R.id.bookRating)
        val txtBookPrice:TextView=view.findViewById(R.id.bookPrice)
        val llContent:LinearLayout=view.findViewById(R.id.llContent)
        val txtBookImage:ImageView=view.findViewById(R.id.bookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycle_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book=itemList[position]
        holder.txtBookName.text=book.bookName
        holder.txtBookAuthor.text=book.bookAuthor
        holder.txtBookRating.text=book.bookRating
        holder.txtBookPrice.text=book.bookPrice
        //       // holder.txtBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage)
            .error(R.drawable.default_image).into(holder.txtBookImage);
        holder.llContent.setOnClickListener {
            val intent=Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }
}