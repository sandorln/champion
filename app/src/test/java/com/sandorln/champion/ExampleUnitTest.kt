package com.sandorln.champion

import com.sandorln.champion.api.LolDataService
import org.junit.Test

import org.junit.Assert.*
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

        LolDataService.getAllChampion(
            { successMsg ->
                println("가져온 값 : $successMsg")
                countDownLatch.countDown()
            },
            { errorMsg ->
                println("에러 발생 : $errorMsg")
                countDownLatch.countDown()
            }
        )

        countDownLatch.await()
    }
}
