package com.vulong.unsplashimagesearch.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vulong.unsplashimagesearch.R
import com.vulong.unsplashimagesearch.adapter.UnsplashPhotoAdapter
import com.vulong.unsplashimagesearch.api.ApiManager
import com.vulong.unsplashimagesearch.api.ServiceBuilder
import com.vulong.unsplashimagesearch.data.Photo
import com.vulong.unsplashimagesearch.data.ResponseObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), UnsplashPhotoAdapter.OnItemRecyclerViewClickListener {

    private var mListPhotos = ArrayList<Photo?>()
    private lateinit var unsplashPhotoAdapter: UnsplashPhotoAdapter
    private lateinit var recyclerView: RecyclerView
    private var isLoading = false
    private var isLastPage = false
    private var page = 1
    private var query = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        unsplashPhotoAdapter = UnsplashPhotoAdapter(mListPhotos, this)

        val layout = LinearLayoutManager(this)
//        layout.stackFromEnd = false
//        layout.reverseLayout = true


        recyclerView.layoutManager = layout
        recyclerView.adapter = unsplashPhotoAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading && !isLastPage) {
                    if (
                        linearLayoutManager != null
                        && linearLayoutManager.findLastCompletelyVisibleItemPosition() == mListPhotos.size - 1
                    ) {
                        //bottom of list!
                        isLoading = true
                        loadMore()
                    }
                }

            }
        })


    }


    override fun onClickItemRecyclerView(position: Int) {
        val clickedPhoto = mListPhotos[position]
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("photo_click", clickedPhoto)
        startActivity(intent)

    }


    private fun getDataFromUnsplash(query: String, page: Int) {

        val request = ServiceBuilder.buildService(ApiManager::class.java)
        val call = request.getPhotos(query, page)

        call.enqueue(object : Callback<ResponseObject> {

            override fun onResponse(
                call: Call<ResponseObject>,
                response: Response<ResponseObject>
            ) {

                if (findViewById<TextView>(R.id.text_err).visibility == View.VISIBLE) {
                    findViewById<TextView>(R.id.text_err).visibility = View.GONE
                    findViewById<Button>(R.id.button_try_again).visibility = View.GONE
                }

                val responseListPhotos = response.body()?.results!!

                if (mListPhotos.size == 0) {
                    mListPhotos.addAll(responseListPhotos)
                    unsplashPhotoAdapter.notifyDataSetChanged()
                } else {

                    var lastSize = mListPhotos.size - 1

                    //remove loadmore item
                    if (mListPhotos[lastSize] == null) {
                        mListPhotos.removeAt(lastSize)
                        unsplashPhotoAdapter.notifyItemRemoved(lastSize)
                    }

                    lastSize = mListPhotos.size - 1

                    //append data
                    mListPhotos.addAll(responseListPhotos)
                    unsplashPhotoAdapter.notifyItemRangeInserted(
                        lastSize, mListPhotos.size - lastSize - 1
                    )
                }


                isLoading = false

                //check last page in API
                if (responseListPhotos.size == 0) {
                    isLastPage = true

                    if (mListPhotos.size == 0)
                        Toast.makeText(
                            this@MainActivity,
                            "Not found\nTry another search term",
                            Toast.LENGTH_SHORT
                        ).show()

                    return
                }

            }

            override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Server not responding!", Toast.LENGTH_SHORT)
                    .show()

                this@MainActivity.page--

                findViewById<TextView>(R.id.text_err).visibility = View.VISIBLE
                findViewById<Button>(R.id.button_try_again).visibility = View.VISIBLE
                findViewById<Button>(R.id.button_try_again).setOnClickListener {
                    getDataFromUnsplash(query, this@MainActivity.page++)
                }


            }

        })

    }


    private fun loadMore() {
        //add loadmore item
        mListPhotos.add(null)
        unsplashPhotoAdapter.notifyItemInserted(mListPhotos.size - 1)
        getDataFromUnsplash(query, page++)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_app, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String?): Boolean {

                val inputMethodManager: InputMethodManager = getSystemService(
                    INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchView.applicationWindowToken, 0)
                searchView.clearFocus()

                query = text!!
                mListPhotos.clear()
                unsplashPhotoAdapter.notifyDataSetChanged()
                page = 1
                isLoading = true
                isLastPage = false
                loadMore()

                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }
}