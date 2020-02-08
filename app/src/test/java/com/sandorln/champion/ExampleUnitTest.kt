package com.sandorln.champion

import com.sandorln.champion.api.LolApiClient
import com.sandorln.champion.api.data.LolVersion
import com.sandorln.champion.api.response.LolDataServiceResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val countDownLatch = CountDownLatch(1)

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun retrofit2Connection() {

        LolApiClient
            .getService()
            .getVersion()
            .enqueue(object : Callback<LolVersion> {
                override fun onResponse(call: Call<LolVersion>, response: Response<LolVersion>) {
                    LolApiClient
                        .getService()
                        .getAllChampion(response.body()!!.lvCategory.cvChampion)
                        .enqueue(object : Callback<LolDataServiceResponse> {
                            override fun onResponse(call: Call<LolDataServiceResponse>, response: Response<LolDataServiceResponse>) {
                                println(response.body()!!)
                            }

                            override fun onFailure(call: Call<LolDataServiceResponse>, t: Throwable) {
                                countDownLatch.countDown()
                            }
                        })
                }

                override fun onFailure(call: Call<LolVersion>, t: Throwable) {
                    countDownLatch.countDown()
                }
            })

        countDownLatch.await()
    }
}
