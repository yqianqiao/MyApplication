package com.huimee.myapplication


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menu.setOnFloatingActionsMenuUpdateListener(object: FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuCollapsed() {
              Toast.makeText(this@MainActivity,"关闭",Toast.LENGTH_LONG).show()
            }

            override fun onMenuExpanded() {
                Toast.makeText(this@MainActivity,BuildConfig.text,Toast.LENGTH_LONG).show()
            }
        })
    }

}
