package sncf.connect.tech.flutter_eco_mode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BATTERY_CHANGED
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.BatteryManager.BATTERY_STATUS_CHARGING
import android.os.BatteryManager.BATTERY_STATUS_DISCHARGING
import android.os.BatteryManager.BATTERY_STATUS_FULL
import android.os.BatteryManager.BATTERY_STATUS_NOT_CHARGING
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.PowerManager.THERMAL_STATUS_CRITICAL
import android.os.PowerManager.THERMAL_STATUS_EMERGENCY
import android.os.PowerManager.THERMAL_STATUS_LIGHT
import android.os.PowerManager.THERMAL_STATUS_MODERATE
import android.os.PowerManager.THERMAL_STATUS_NONE
import android.os.PowerManager.THERMAL_STATUS_SEVERE
import android.os.PowerManager.THERMAL_STATUS_SHUTDOWN
import android.os.StatFs
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import listener.BatteryLevelStreamHandler
import listener.BatteryStateStreamHandler
import listener.ConnectivityListener
import listener.PowerModeStreamHandler
import listener.getNetworkType
import listener.getWifiSignalStrength
import sncf.connect.tech.flutter_eco_mode.BatteryState.CHARGING
import sncf.connect.tech.flutter_eco_mode.BatteryState.DISCHARGING
import sncf.connect.tech.flutter_eco_mode.BatteryState.FULL
import sncf.connect.tech.flutter_eco_mode.BatteryState.UNKNOWN
import sncf.connect.tech.flutter_eco_mode.ThermalState.CRITICAL
import sncf.connect.tech.flutter_eco_mode.ThermalState.FAIR
import sncf.connect.tech.flutter_eco_mode.ThermalState.SAFE
import sncf.connect.tech.flutter_eco_mode.ThermalState.SERIOUS


class FlutterEcoModePlugin : FlutterPlugin, EcoModeApi {
    private lateinit var context: Context

    // UNUSED: Event channels not used by DevicePerformance
    // private val lowPowerModeEventChannel = "sncf.connect.tech/battery.isLowPowerMode"
    // private val batteryStateEventChannel = "sncf.connect.tech/battery.state"
    // private val batteryLevelEventChannel = "sncf.connect.tech/battery.level"
    // private val connectivityStateEventChannel = "sncf.connect.tech/connectivity.state"

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        EcoModeApi.setUp(flutterPluginBinding.binaryMessenger, this)
        context = flutterPluginBinding.applicationContext
        
        // UNUSED: Event channel setup not needed by DevicePerformance
        // EventChannel(
        //     flutterPluginBinding.binaryMessenger,
        //     lowPowerModeEventChannel,
        // ).setStreamHandler(PowerModeStreamHandler(context))
        // EventChannel(
        //     flutterPluginBinding.binaryMessenger,
        //     batteryStateEventChannel,
        // ).setStreamHandler(BatteryStateStreamHandler(context))
        // EventChannel(
        //     flutterPluginBinding.binaryMessenger,
        //     batteryLevelEventChannel,
        // ).setStreamHandler(BatteryLevelStreamHandler(context))
        // EventChannel(
        //     flutterPluginBinding.binaryMessenger,
        //     connectivityStateEventChannel,
        // ).setStreamHandler(
        //     ConnectivityListener(
        //         context
        //     )
        // )
    }


    override fun onDetachedFromEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        EcoModeApi.setUp(flutterPluginBinding.binaryMessenger, null)
    }

    // UNUSED: Platform info not used by DevicePerformance
    // override fun getPlatformInfo(): String {
    //     val release: String = Build.VERSION.RELEASE
    //     val device: String = Build.DEVICE
    //     val hardware: String = Build.HARDWARE
    //     val product: String = Build.PRODUCT
    //     val type: String = Build.TYPE
    //     return "Android - $release - $device - $hardware - $product - $type"
    // }

    // UNUSED: Battery level not used by DevicePerformance
    // override fun getBatteryLevel(): Double = getBatteryLevel(getBatteryStatus())

    // private fun getBatteryLevel(intent: Intent?): Double = intent?.let {
    //     val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    //     val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    //     level * 100 / scale.toDouble()
    // } ?: 0.0

    // UNUSED: Battery state not used by DevicePerformance
    // override fun getBatteryState(): BatteryState {
    //     val batteryStatus = getBatteryStatus()
    //     val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    //     return convertBatteryState(status)
    // }

    // UNUSED: Battery low power mode not used by DevicePerformance
    // override fun isBatteryInLowPowerMode(): Boolean {
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    //         val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    //         return powerManager.isPowerSaveMode
    //     }
    //     return false // can't return null so we return false for older versions
    // }

    // UNUSED: Thermal state not used by DevicePerformance
    // override fun getThermalState(): ThermalState {
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    //         val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    //         return convertThermalState(powerManager.currentThermalStatus)
    //     }
    //     return ThermalState.UNKNOWN
    // }

    // UNUSED: Processor count not used (DevicePerformance uses Platform.numberOfProcessors)
    // override fun getProcessorCount(): Long {
    //     return Runtime.getRuntime().availableProcessors().toLong()
    //     // TODO: check if it returns the total of cores
    // }

    // USED: Total memory is used by DevicePerformance
    override fun getTotalMemory(): Long {
        return Runtime.getRuntime().totalMemory()
    }

    // UNUSED: Free memory not used by DevicePerformance
    // override fun getFreeMemory(): Long {
    //     return Runtime.getRuntime().freeMemory()
    // }

    // UNUSED: Storage methods not used by DevicePerformance
    // override fun getTotalStorage(): Long {
    //     val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
    //     val blockSizeLong = statFs.blockSizeLong
    //     val totalBlocksLong = statFs.blockCountLong
    //     return blockSizeLong * totalBlocksLong
    // }

    // override fun getFreeStorage(): Long {
    //     val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
    //     val blockSizeLong = statFs.blockSizeLong
    //     val availableBlocksLong = statFs.availableBlocksLong
    //     return blockSizeLong * availableBlocksLong
    // }

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

    // UNUSED: Connectivity not used by DevicePerformance
    // @RequiresApi(Build.VERSION_CODES.R)
    // override fun getConnectivity(): Connectivity =
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    //         val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    //         val network = connectivityManager.activeNetwork
    //         val telephonyManager = context.getSystemService(TelephonyManager::class.java)
    //         val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    //         Connectivity(type = connectivityManager.getNetworkType(
    //             telephonyManager = telephonyManager,
    //             networkCapabilities = networkCapabilities
    //         ), wifiSignalStrength = networkCapabilities?.getWifiSignalStrength()?.toLong())
    //     } else {
    //         Connectivity(type = ConnectivityType.UNKNOWN)
    //     }

    // UNUSED: Battery status helper not needed
    // private fun getBatteryStatus(): Intent? {
    //     return IntentFilter(ACTION_BATTERY_CHANGED).let { intentFilter ->
    //         context.registerReceiver(null, intentFilter)
    //     }
    // }

    // UNUSED: Battery state converter not needed
    // private fun convertBatteryState(state: Int): BatteryState {
    //     return when (state) {
    //         BATTERY_STATUS_CHARGING -> CHARGING
    //         BATTERY_STATUS_FULL -> FULL
    //         BATTERY_STATUS_DISCHARGING, BATTERY_STATUS_NOT_CHARGING -> DISCHARGING
    //         else -> UNKNOWN
    //     }
    // }

    // UNUSED: Thermal state converter not needed
    // private fun convertThermalState(state: Int): ThermalState {
    //     return when (state) {
    //         THERMAL_STATUS_NONE -> SAFE
    //         THERMAL_STATUS_MODERATE, THERMAL_STATUS_LIGHT -> FAIR
    //         THERMAL_STATUS_SEVERE -> SERIOUS
    //         THERMAL_STATUS_CRITICAL, THERMAL_STATUS_EMERGENCY, THERMAL_STATUS_SHUTDOWN -> CRITICAL
    //         else -> ThermalState.UNKNOWN
    //     }
    // }
}


