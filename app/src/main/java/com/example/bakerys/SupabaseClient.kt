package com.example.bakerys

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val instance = createSupabaseClient(
        supabaseUrl = "https://raqjgojlvrimbxfvlejs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJhcWpnb2psdnJpbWJ4ZnZsZWpzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTM2MjY4MDYsImV4cCI6MjEwOTIwMjgwNn0.R6e-ilJ74zUgoxvrQO8CZiQ1RAtOcBZ1Lr7jHky2rH4"
    ) {
        install(Postgrest)
        install(Auth)
    }

}
