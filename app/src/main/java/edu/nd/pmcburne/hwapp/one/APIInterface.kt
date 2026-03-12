package edu.nd.pmcburne.hwapp.one

import edu.nd.pmcburne.hwapp.one.responseObjects.APIResponse
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.LocalDate

interface APIInterface {
    @GET("basketball-{gender}/d1/{yyyy}/{mm}/{dd}")
    suspend fun getGames(
        @Path("gender") gender: String,
        @Path("yyyy") year: String,
        @Path("mm") month: String,
        @Path("dd") day: String
    ): APIResponse
}