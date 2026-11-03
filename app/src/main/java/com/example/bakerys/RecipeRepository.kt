package com.example.bakerys

object RecipeRepository {
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
            imageResId = R.drawable.caramel, // Sesuaikan nama drawable resep caramel kamu
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 3,
            title = "Cookies",
            description = "Cookies cokelat lezat dengan tekstur renyah di luar dan lembut di dalam.",
            imageResId = R.drawable.cookies, // Ganti dengan R.drawable.cookies jika ada
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 4,
            title = "Rainbow Cake",
            description = "Kue lapis warna-warni cantik dengan lapisan krim lembut.",
            imageResId = R.drawable.rainbow, // Ganti dengan R.drawable.rainbow_cake jika ada
            isLiked = false,
            isBookmarked = true
        ),
        Recipe(
            id = 5,
            title = "Chocolate Lava Cake",
            description = "Kue cokelat lezat dengan isian cokelat lumer di bagian tengah.",
            imageResId = R.drawable.lava_cakes, // Ganti dengan R.drawable.chocolate_lava_cake jika ada
            isLiked = false,
            isBookmarked = true
        )
    )

    fun getBookmarkedRecipes(): MutableList<Recipe> {
        return recipeList.filter { it.isBookmarked }.toMutableList()
    }
}