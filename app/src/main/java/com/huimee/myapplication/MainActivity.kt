package com.huimee.myapplication


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuCollapsed() {
                Log.e("onMenuCollapsed","bbbb")
            }

            override fun onMenuExpanded() {
                Log.e("onMenuExpanded","bbbb")
            }
        })
    }
}
