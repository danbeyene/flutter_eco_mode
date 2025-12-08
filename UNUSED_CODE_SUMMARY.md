# Flutter Eco Mode - Unused Code Summary

This document summarizes the methods that have been commented out in the flutter_eco_mode package based on the DevicePerformance usage analysis.

## Analysis Date
Generated based on DevicePerformance code requirements.

## Methods USED by DevicePerformance

Only these methods from flutter_eco_mode are actively used:

1. **`getDeviceRange()`** - Returns device performance classification (high-end, mid-range, low-end)
2. **`getTotalMemory()`** - Returns total RAM available on the device
3. **`getEcoScore()`** - (Internal) Used by getDeviceRange to calculate device performance score

## Methods COMMENTED OUT (Unused)

All other functionality has been commented out as it's not used by DevicePerformance:

### Battery Monitoring
- `getBatteryLevel()` - Current battery percentage
- `getBatteryState()` - Charging/discharging state
- `isBatteryInLowPowerMode()` - Low power mode status
- `batteryLevelEventStream` - Real-time battery level updates
- `batteryStateEventStream` - Real-time battery state updates
- `lowPowerModeEventStream` - Real-time low power mode updates
- `isBatteryEcoMode()` - Combined battery eco mode check
- `isBatteryEcoModeStream` - Real-time eco mode updates

### System Information
- `getPlatformInfo()` - Platform/OS information
- `getThermalState()` - Device thermal state (overheating detection)
- `getProcessorCount()` - CPU core count (DevicePerformance uses `Platform.numberOfProcessors` instead)
- `getFreeMemory()` - Available RAM
- `getTotalStorage()` - Total disk space
- `getFreeStorage()` - Available disk space

### Network Connectivity
- `getConnectivity()` - Current network connection type
- `connectivityStream` - Real-time connectivity updates
- `hasEnoughNetwork()` - Network quality check
- `hasEnoughNetworkStream()` - Real-time network quality updates

## Files Modified

### Dart Library Files
1. **`lib/flutter_eco_mode.dart`**
   - Commented out: All event channel controllers, battery/thermal/connectivity methods
   - Kept active: `getTotalMemory()`, `getDeviceRange()`, and supporting methods

2. **`lib/flutter_eco_mode_platform_interface.dart`**
   - Commented out: All interface methods except `getTotalMemory()` and `getDeviceRange()`

3. **`lib/messages.g.dart`**
   - Auto-generated file (can be regenerated)
   - Only `getTotalMemory()` and `getEcoScore()` are used from EcoModeApi

### Android Native Files
4. **`android/src/main/kotlin/sncf/connect/tech/flutter_eco_mode/FlutterEcoModePlugin.kt`**
   - Commented out: All event channel setup, battery/thermal/connectivity implementations
   - Kept active: `getTotalMemory()`, `getEcoScore()`
   - Note: `getEcoScore()` internally uses processor count and storage for scoring

5. **`android/src/main/kotlin/sncf/connect/tech/flutter_eco_mode/Messages.g.kt`**
   - Commented out: Interface methods except `getTotalMemory()` and `getEcoScore()`

### iOS Native Files
6. **`ios/Classes/FlutterEcoModePlugin.swift`**
   - Commented out: All event stream handlers, battery/thermal/connectivity implementations
   - Kept active: `getTotalMemory()`, `getEcoScore()`, `getProcessorCount()`, `getTotalStorage()`
   - Note: `getProcessorCount()` and `getTotalStorage()` are used internally by `getEcoScore()`

7. **`ios/Classes/Messages.g.swift`**
   - Commented out: Protocol methods except those needed for eco score calculation
   - Kept active: `getTotalMemory()`, `getEcoScore()`, `getProcessorCount()`, `getTotalStorage()`

## Impact on DevicePerformance

The DevicePerformance class will continue to work exactly as before because:
- ✅ `getDeviceRange()` is fully functional
- ✅ `getTotalMemory()` is fully functional
- ✅ `getEcoScore()` is fully functional (used internally by getDeviceRange)
- ✅ All supporting native implementations are kept active

## What DevicePerformance Actually Does

```dart
// DevicePerformance uses only these two methods:
_deviceRange = await _ecoMode.getDeviceRange();  // Gets device tier classification
totalMemory = await ecoMode.getTotalMemory();     // Gets total RAM for classification
```

The classification logic in DevicePerformance primarily relies on:
1. CPU cores (from `Platform.numberOfProcessors` - NOT from flutter_eco_mode)
2. Device range check (from `getDeviceRange()` which uses `getEcoScore()`)
3. Total memory (from `getTotalMemory()`)

## Reverting Changes

If you need to restore any commented functionality:
1. Search for `// UNUSED:` comments in the files
2. Uncomment the sections you need
3. Make sure to uncomment matching methods across Dart, Android, and iOS files

## Notes

- All commented code is marked with `// UNUSED:` prefix
- Event channels and streams are completely commented out since DevicePerformance doesn't use real-time monitoring
- The native implementations retain internal helper methods needed by `getEcoScore()`
- This is a runtime optimization - commented code reduces the active surface area but can be easily restored

## Future Considerations

If DevicePerformance needs additional metrics in the future:
- Uncomment the relevant methods across all platform files
- Ensure the native implementations are also uncommented
- Test on both Android and iOS platforms

