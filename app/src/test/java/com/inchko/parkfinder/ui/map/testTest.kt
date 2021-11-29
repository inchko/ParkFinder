package com.inchko.parkfinder.ui.map

import com.inchko.parkfinder.network.RepoUsers
import com.inchko.parkfinder.network.Repository
import dagger.hilt.android.AndroidEntryPoint
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.Objects.isNull
import javax.inject.Inject

@AndroidEntryPoint
class testTest() {

    @Test
    fun isFree() {
        val p = true
        Assert.assertEquals(true, p)
    }

}