package com.example.practice15

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: Toolbar
    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        navigationView=findViewById(R.id.navigationView)
        frameLayout=findViewById(R.id.frameLayout)
        toolbar=findViewById(R.id.toolbar)
        setUpToolBar()
        openDrawer()
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem!=null)
            {
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
            when(it.itemId)
            {
                R.id.dashboard->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,DashboardFragment())
                        .commit()
                    supportActionBar?.title="DashBoard"
                    drawerLayout.closeDrawers()
                }
                R.id.favorite->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,FavouriteFragment())
                        .commit()
                    supportActionBar?.title="Favourite"
                    drawerLayout.closeDrawers()
                }
                R.id.profile->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,ProfileFragment())
                        .commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.about->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout,AboutFragment())
                        .commit()
                    supportActionBar?.title="About"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolBar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Tool Bar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId
        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openDrawer()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout,DashboardFragment())
            .commit()
        supportActionBar?.title="Dashboard"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag)
        {
            !is DashboardFragment ->openDrawer()
            else ->super.onBackPressed()
        }
    }
}
