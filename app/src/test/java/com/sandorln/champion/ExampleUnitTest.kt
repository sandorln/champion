package com.sandorln.champion

import org.junit.Assert.assertEquals
import org.junit.Test
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
    }
}
