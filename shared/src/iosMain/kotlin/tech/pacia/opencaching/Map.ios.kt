@file:OptIn(ExperimentalForeignApi::class)

package tech.pacia.opencaching

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSLog
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UICoordinateSpaceProtocol
import platform.darwin.NSObject
import tech.pacia.opencaching.data.BoundingBox
import tech.pacia.opencaching.data.Geocache
import tech.pacia.opencaching.data.Location
import kotlin.time.Duration


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun Map(
    modifier: Modifier,
    center: Location,
    caches: List<Geocache>,
    onMapBoundsChange: (BoundingBox?) -> Unit,
) {
    val annotations = caches.map {
        MKPointAnnotation(
            coordinate = CLLocationCoordinate2DMake(
                it.location.latitude,
                it.location.longitude
            ),
            title = it.name,
            subtitle = it.code,
        )

    }

    val mapViewDelegate = remember {
        MapViewDelegate(
            onMapBoundsChange = onMapBoundsChange
        )
    }

    val mkMapView = remember {
        val mapView = MKMapView()
        mapView.delegate = mapViewDelegate
        mapView
    }

    UIKitView(
        modifier = modifier,
        factory = { mkMapView },
        update = {
            it.addAnnotations(annotations)

            it.addAnnotation(
                MKPointAnnotation(
                    CLLocationCoordinate2DMake(
                        center.latitude,
                        center.longitude
                    ),
                )
            )

            it.setRegion(
                MKCoordinateRegionMakeWithDistance(
                    centerCoordinate = CLLocationCoordinate2DMake(
                        center.latitude,
                        center.longitude
                    ),
                    latitudinalMeters = 10_000.0,
                    longitudinalMeters = 10_000.0
                ),
            )

            onMapBoundsChange(it.boundingBox())
        }
    )
}

class MapViewDelegate(private val onMapBoundsChange: (BoundingBox?) -> Unit) : NSObject(),
    MKMapViewDelegateProtocol {

    override fun mapView(mapView: MKMapView, regionDidChangeAnimated: Boolean) {
        debugLog("MapViewDelegate", "regionDidChangeAnimated: ${mapView.boundingBox()}")
    }

    override fun mapViewDidChangeVisibleRegion(mapView: MKMapView) {
        // onMapBoundsChange(mapView.boundingBox())
        debugLog("MapViewDelegate", "mapViewDidChangeVisibleRegion: ${mapView.boundingBox()}")
    }
}


@OptIn(ExperimentalForeignApi::class)
fun MKMapView.boundingBox(): BoundingBox {
//    val center = this.region.useContents {
//        debugLog("Inside useContents", "lat: ${center.latitude}, lng: ${center.longitude}")
//        this.center
//    }
//
//    debugLog("OUTSIDE useContents", "lat: ${center.latitude}, lng: ${center.longitude}")

    // Be careful - don't return objects from useContents or dragons will appear

    val latitude = region.useContents { center.latitude }
    val longitude = region.useContents { center.longitude }
    val latitudeDelta = region.useContents { span.latitudeDelta }
    val longitudeDelta = region.useContents { span.longitudeDelta }

    val northWestCorner = CLLocationCoordinate2DMake(
        latitude = latitude + latitudeDelta / 2,
        longitude = longitude - longitudeDelta / 2
    )

    val southEastCorner = CLLocationCoordinate2DMake(
        latitude = latitude - latitudeDelta / 2,
        longitude = longitude + longitudeDelta / 2,
    )

    return BoundingBox(
        north = northWestCorner.useContents { this.latitude },
        east = southEastCorner.useContents { this.longitude },
        south = southEastCorner.useContents { this.latitude },
        west = northWestCorner.useContents { this.longitude },
    )
}
