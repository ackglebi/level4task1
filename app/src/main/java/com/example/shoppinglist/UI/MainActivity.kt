package com.example.shoppinglist.UI

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.model.Product
import com.example.shoppinglist.R
import com.example.shoppinglist.database.ProductRepository

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository
    private var products = arrayListOf<Product>()
    private var productAdapter =
        ProductAdapter(products)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        productRepository =
            ProductRepository(this)
        initViews()
        fab.setOnClickListener { addProducts()}
    }

    private fun initViews() {

        // recylerview setup
        rvList.layoutManager = LinearLayoutManager(this@MainActivity,
            RecyclerView.VERTICAL, false)
        rvList.adapter = productAdapter
        rvList.addItemDecoration(DividerItemDecoration(this@MainActivity,
            RecyclerView.VERTICAL))

        // get data from database
        getShoppingListFromDatabase()

        createItemTouchHelper().attachToRecyclerView(rvList)
    }

    /**
     * check validation of input fields
     */
    private fun validateFields() : Boolean {
        return if (etProduct.text.toString().isNotBlank() &&
            etQuantity.text.toString().isNotBlank()) {
            true
        } else {
            Toast.makeText(this, "Fill in all of the fields", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * adds product to database
     */
    private fun addProducts() {

        if (validateFields()) {
            CoroutineScope(Dispatchers.Main).launch {

                // create product
                val product = Product(
                    quantity = etQuantity.text.toString().toInt(),
                    name = etProduct.text.toString()
                )

                // add product to database
                withContext(Dispatchers.IO) {
                    productRepository.insertProduct(product)
                }

                // refresh product lsit
                getShoppingListFromDatabase()
            }
        }
    }

    /**
     * Gets all products from database
     */
    private fun getShoppingListFromDatabase() {
        CoroutineScope(Dispatchers.Main).launch {
            val shoppingList = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }

            // TODO misschien zit er een fout in de addAll products/shoppinglist
            this@MainActivity.products.clear()
            this@MainActivity.products.addAll(shoppingList)
            this@MainActivity.productAdapter.notifyDataSetChanged()
        }
    }

    private fun createItemTouchHelper() : ItemTouchHelper {

        // enables left and right swipe
        val callback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // position product and product
                val position = viewHolder.adapterPosition
                val productToDelete = products[position]

                // delete the product
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        productRepository.deleteProduct(productToDelete)
                    }
                    // refresh products
                    getShoppingListFromDatabase()
                }
            }
        }

        return ItemTouchHelper(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun deleteShoppingList() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                productRepository.deleteAllProducts()
            }

            getShoppingListFromDatabase()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_shopping_list -> {
                deleteShoppingList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
