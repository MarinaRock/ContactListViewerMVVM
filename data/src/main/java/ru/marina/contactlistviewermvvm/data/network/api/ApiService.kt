package ru.marina.contactlistviewermvvm.data.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import ru.marina.contactlistviewermvvm.data.reponse.ContactResponse

interface ApiService {

    @GET("MarinaRock/ContactListViewerMVVM/main/json/{source}")
    fun getContacts(@Path("source") source: String): Single<List<ContactResponse>>
}