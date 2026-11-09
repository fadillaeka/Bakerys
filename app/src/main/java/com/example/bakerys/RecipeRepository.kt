package com.example.bakerys

object RecipeRepository {
    // List resep utama (Jelajah)
    val recipeList: MutableList<Recipe> = mutableListOf(
        Recipe(
            id = 1,
            title = "No-Bake Pumpkin Cheesecake Bars",
            description = "Resep No-Bake Pumpkin Cheesecake Bars ini terdiri dari lapisan dasar biskuit graham dengan isian cheesecake labu yang lembut dan krim kocok manis.",
            imageResId = R.drawable.no_bake_pumpkin_cheesecake_bars,
            isLiked = true,
            isBookmarked = true
        ),
        Recipe(
            id = 2,
            title = "Caramel Apple Magic Fridge Cake",
            description = "Dibuat dengan menyusun biskuit atau biskuit graham secara berlapis dengan adonan krim, lalu didinginkan di kulkas hingga tekstur biskuitnya melunak dan menyatu seperti kue spons.",
            imageResId = R.drawable.caramel,
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 3,
            title = "Cookies",
            description = "Cookies cokelat lezat dengan tekstur renyah di luar dan lembut di dalam.",
            imageResId = R.drawable.cookies,
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 4,
            title = "Rainbow Cake",
            description = "Kue lapis warna-warni cantik dengan lapisan krim lembut.",
            imageResId = R.drawable.rainbow,
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 5,
            title = "Chocolate Lava Cake",
            description = "Kue cokelat lezat dengan isian cokelat lumer di bagian tengah.",
            imageResId = R.drawable.lava_cakes,
            isLiked = false,
            isBookmarked = true
        )
    )

    // List khusus resep tersimpan yang disinkronkan dari recipeList awal
    val resepTersimpanList: MutableList<Recipe> = recipeList.filter { it.isBookmarked }.toMutableList()

    // List khusus draft
    val draftList: MutableList<Recipe> = mutableListOf()

    // Fungsi untuk menambah/menghapus resep dari daftar tersimpan (Bookmark Toggle)
    fun toggleSimpanResep(recipe: Recipe): Boolean {
        val index = recipeList.indexOfFirst { it.id == recipe.id }
        val isCurrentlyBookmarked = if (index != -1) recipeList[index].isBookmarked else false

        return if (isCurrentlyBookmarked) {
            // Jika sudah disimpan -> Hapus dari simpanan
            if (index != -1) recipeList[index].isBookmarked = false
            resepTersimpanList.removeAll { it.id == recipe.id }
            false // Status baru: tidak disimpan
        } else {
            // Jika belum disimpan -> Tambahkan ke simpanan
            if (index != -1) recipeList[index].isBookmarked = true
            recipe.isBookmarked = true
            if (resepTersimpanList.none { it.id == recipe.id }) {
                resepTersimpanList.add(0, recipe)
            }
            true // Status baru: disimpan
        }
    }

    fun getBookmarkedRecipes(): MutableList<Recipe> {
        return resepTersimpanList
    }

    fun tambahPostingan(recipe: Recipe) {
        recipeList.add(0, recipe) // Masukkan postingan baru ke paling atas Jelajah
    }

    fun tambahDraft(recipe: Recipe) {
        draftList.add(0, recipe)
    }
}