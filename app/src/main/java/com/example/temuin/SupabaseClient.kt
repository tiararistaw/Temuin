
package com.example.temuin

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://orzckzuljvzngpzgjkhk.supabase.co",
        supabaseKey = "sb_publishable_h2UBWd8Q4G2dumhy71W7MA_QoBl_NB9"
    ) {
        install(Auth) {
            scheme = "temuin"
            host = "auth"
        }

        install(Postgrest)
    }
}
