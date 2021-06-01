package com.inchko.parkfinder.ui.map

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inchko.parkfinder.network.RepoUsers
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MapFragmentTest : TestCase() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var rep: RepoUsers

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun ApiZones() = runBlocking {
        val o = rep.getUser("test")
        val p = Objects.isNull(o)
        Assert.assertEquals("pepito calabazas", o.name)
        //Log.e("test", "${o.id}")
    }
}