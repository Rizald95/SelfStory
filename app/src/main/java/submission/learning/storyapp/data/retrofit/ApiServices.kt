package submission.learning.storyapp.data.retrofit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import submission.learning.storyapp.data.response.AddNewStoriesResponse
import submission.learning.storyapp.data.response.GetAllStoriesResponse
import submission.learning.storyapp.data.response.LoginUserResponse
import submission.learning.storyapp.data.response.RegisterUserResponse

interface ApiServices {
    @POST("register")
    suspend fun register(

        @Field("name")
        name: String,

        @Field("email")
        email: String,

        @Field("password")
        password: String
    ): RegisterUserResponse

    @POST("login")
    suspend fun login(
        @Field("email")
        email: String,

        @Field("password")
        password: String
    ): LoginUserResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization")
        token: String,

        @Query("page")
        page: Int = 1,

        @Query("size")
        size: Int = 20
    ): GetAllStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): AddNewStoriesResponse

}