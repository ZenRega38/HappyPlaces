package com.regadeveloper.happyplaces

import HappyPlaceModel
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.regadeveloper.happyplaces.activities.AddHappyPlaceActivity
import com.regadeveloper.happyplaces.activities.HappyDetailActivity
import com.regadeveloper.happyplaces.callback.SwipeToDeleteCallback
import com.regadeveloper.happyplaces.callback.SwipeToEditCallback
import com.regadeveloper.happyplaces.database.DatabaseHandler
import kotlinx.android.synthetic.main.activity_main.*
import com.regadeveloper.happyplaces.adapter.HappyPlacesAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_happy_place.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()


    }

    private fun getHappyPlacesListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList = dbHandler.getHappyPlaceList()

        if (getHappyPlacesList.size > 0) {
            rv_happy_places_list.visibility = View.VISIBLE
            tv_no_records.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)

        } else {
            rv_happy_places_list.visibility = View.GONE
            tv_no_records.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {
        rv_happy_places_list.layoutManager = LinearLayoutManager(this)
        rv_happy_places_list.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        rv_happy_places_list.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAIL, model)

                startActivity(intent)

            }
        })

        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlacesListFromLocalDB()

            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlacesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }

        }
    }

    companion object {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_PLACE_DETAIL = "extra_place_detail"
    }
}