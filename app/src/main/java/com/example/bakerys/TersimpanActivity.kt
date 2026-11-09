package com.example.bakerys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
class TersimpanActivity : AppCompatActivity() {

    private lateinit var rvTersimpan: RecyclerView
    private lateinit var adapter: TersimpanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tersimpan)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        rvTersimpan = findViewById(R.id.rvTersimpan)
        rvTersimpan.layoutManager = LinearLayoutManager(this)

        adapter = TersimpanAdapter(
            bookmarkedList = RecipeRepository.resepTersimpanList,
            onItemClick = { recipe ->
                val intent = Intent(this, DetailResepActivity::class.java).apply {
                    putExtra("EXTRA_RECIPE_ID", recipe.id)
                    putExtra("EXTRA_JUDUL", recipe.title)
                    recipe.imageResId?.let { putExtra("EXTRA_GAMBAR", it) }
                }
                startActivity(intent)
            },
            onRemoveBookmark = { recipe, position ->
                recipe.isBookmarked = false
                RecipeRepository.resepTersimpanList.removeAll { it.id == recipe.id }
                BookmarkManager.removeBookmark(recipe)
                adapter.removeItemAt(position)
            }
        )
        rvTersimpan.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}