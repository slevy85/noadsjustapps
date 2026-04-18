package com.noadsjustapps.speedometer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapView(
    latitude: Double,
    longitude: Double,
    waypoints: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier
) {
    val hasValidPosition = latitude != 0.0 || longitude != 0.0

    val refs = remember {
        object {
            var mapView: MapView? = null
            var marker: Marker? = null
            var polyline: Polyline? = null
            var hasInitialPosition: Boolean = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> refs.mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> refs.mapView?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            refs.mapView?.onDetach()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(0.0, 0.0))
                    refs.mapView = this
                }
            },
            update = { mapView ->
                val currentPoint = GeoPoint(latitude, longitude)

                if (!refs.hasInitialPosition && hasValidPosition) {
                    mapView.controller.setCenter(currentPoint)
                    mapView.controller.setZoom(15.0)
                    refs.hasInitialPosition = true
                } else if (refs.hasInitialPosition && hasValidPosition) {
                    mapView.controller.animateTo(currentPoint)
                }

                if (hasValidPosition) {
                    val marker = refs.marker ?: Marker(mapView).also { m ->
                        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        m.title = "Current Position"
                        mapView.overlays.add(m)
                        refs.marker = m
                    }
                    marker.position = currentPoint
                } else if (refs.marker != null) {
                    mapView.overlays.remove(refs.marker)
                    refs.marker = null
                    refs.hasInitialPosition = false
                }

                if (waypoints.size >= 2) {
                    val polyline = refs.polyline ?: Polyline(mapView).also { p ->
                        p.outlinePaint.color = android.graphics.Color.argb(200, 33, 150, 243)
                        p.outlinePaint.strokeWidth = 8f
                        mapView.overlays.add(0, p)
                        refs.polyline = p
                    }
                    polyline.setPoints(waypoints.map { (lat, lon) -> GeoPoint(lat, lon) })
                } else if (waypoints.isEmpty() && refs.polyline != null) {
                    mapView.overlays.remove(refs.polyline)
                    refs.polyline = null
                }

                mapView.invalidate()
            }
        )

        if (!hasValidPosition) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE6000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Waiting for GPS\u2026",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
