
package sncf.connect.tech.flutter_eco_mode

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import io.flutter.embedding.engine.plugins.FlutterPlugin

class FlutterEcoModePlugin : FlutterPlugin, EcoModeApi {
    private lateinit var context: Context

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        EcoModeApi.setUp(flutterPluginBinding.binaryMessenger, this)
        context = flutterPluginBinding.applicationContext
    }


    override fun onDetachedFromEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        EcoModeApi.setUp(flutterPluginBinding.binaryMessenger, null)
    }

    // USED: Total memory is used by DevicePerformance
    override fun getTotalMemory(): Long {
        return Runtime.getRuntime().totalMemory()
    }

    // USED: getEcoScore is used internally by getDeviceRange
    override fun getEcoScore(): Double {
        val nbrParams = 4.0
        var score = nbrParams

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) score--
        if (getTotalMemory() <= 1_000_000_000) score--
        
        // Note: Using processor count internally in score calculation
        val processorCount = Runtime.getRuntime().availableProcessors().toLong()
        if (processorCount <= 2) score--
        
        // Note: Using total storage internally in score calculation
        val totalStorage = try {
            val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
            val blockSizeLong = statFs.blockSizeLong
            val totalBlocksLong = statFs.blockCountLong
            blockSizeLong * totalBlocksLong
        } catch (e: Exception) {
            0L
        }
        if (totalStorage <= 16_000_000_000) score--

        return score / nbrParams
    }
}


