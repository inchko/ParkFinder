package com.inchko.parkfinder.ui.map

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.network.*
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

    @Inject
    lateinit var repoZones: Repository

    @Inject
    lateinit var repoPOI: RepoPOI

    @Inject
    lateinit var repoFz: RepoFavZones

    @Inject
    lateinit var repoService: ServiceRepo


    @Before
    fun init() {
        hiltRule.inject()
    }

    //user test
    @Test
    fun getUser() = runBlocking {
        val o = rep.getUser("test")
        //val p = Objects.isNull(o)
        Assert.assertEquals("pepito calabazas", o.name)
    }

    @Test
    fun getUserFavZones() = runBlocking {
        val o = repoFz.getFavZones("test")
        //val p = Objects.isNull(o)
        Assert.assertEquals("test", o.get(0).id)
    }

    @Test
    fun getUserPOI() = runBlocking {
        val o = repoPOI.getPOI("test")
        //val p = Objects.isNull(o)
        Assert.assertEquals("testAdded location0.14920641446198513", o.get(0).id)
    }

    //Zones test
    @Test
    fun getZones() = runBlocking {
        val o = repoZones.readZonesByLoc(Ubi(20000, 0.0, 0.0))
        Assert.assertEquals("AparcaMotos Besoss", o.get(0).id)
    }

    @Test
    fun getUniqueZone() = runBlocking {
        val o = repoService.readZone("AparcaMotos Besoss")
        Assert.assertEquals(2.192245, o.long)
    }

    //Multiple zone test
    @Test
    fun testFavZone() = runBlocking {
        val testFavZone = FavZone(
            id = "testfavz",
            userID = "test",
            location = "da igual",
            long = "2.5",
            lat = "7.8",
            tipo = 2,
            plazasTotales = 458,
            zoneID = "importa?"
        )
        repoFz.addFavZone(testFavZone)
        val fz = repoFz.getFavZones("test")
        var p: FavZone? = null
        for (f in fz) {
            if (f.plazasTotales == 458)
                p = f;
        }
        if (p != null) {
            p.lat = "111112"
            repoFz.updateFavZones("test", p.id, p)
            val fz2 = repoFz.getFavZones("test")
            for (f in fz2) {
                if (f.plazasTotales == 458)
                    p = f;
            }
            if (p != null) {
                repoFz.deleteFavZones("test", p.id)
            }
            if (p != null) {
                Assert.assertEquals("111112", p.lat)
            }
        } else
            Assert.assertEquals("TO FIND THE ZONE", "It didn't find it")
    }

    @Test
    fun testPOI() = runBlocking {
        val testPOI = POI(
            id = "test",
            lat = "78.5",
            long = "848.5",
            nombre = "POIDETEST",
            userID = "test",
            location = "da igual"
        )
        repoPOI.addPOI(testPOI)

        val pois = repoPOI.getPOI("test")
        var poi: POI? = null
        for (p in pois) {
            if (p.lat == "78.5")
                poi = p
        }
        if (poi != null) {
            poi.long = "666"
            repoPOI.updatePOI(poi.userID, poi.id, poi)
        }
        val pois2 = repoPOI.getPOI("test")
        for (p in pois2) {
            if (p.lat == "78.5")
                poi = p
        }
        if (poi != null) {
            repoPOI.deletePOI("test", poi.id)
            Assert.assertEquals("666", poi.long)
        } else
            Assert.assertEquals("TO FIND THE ZONE", "It didn't find it")


    }

    @Test
    fun testVehicles() = runBlocking {
        val vehicle = Vehicles(
            id = "BRUM BRUM",
            size = 2,
            type = 0,
            model = "TestCar"
        )
        rep.addVehicle("test", vehicle)
        var v = rep.getVehicles("test")
        var tc: Vehicles? = null
        for (car in v) {
            if (car.model == "TestCar")
                tc = car
        }
        if (tc != null) {
            tc.size = 1
            rep.updateVehicle("test", tc.id, tc)
        }
        v = rep.getVehicles("test")
        for (car in v) {
            if (car.model == "TestCar")
                tc = car
        }
        if (tc != null) {
            rep.deleteVehicles("test", tc.id)
            Assert.assertEquals(1, tc.size)
        } else
            Assert.assertEquals("TO FIND THE CAR", "It didn't find it")

    }

}