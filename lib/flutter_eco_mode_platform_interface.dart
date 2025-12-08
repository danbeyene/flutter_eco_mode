import 'package:flutter_eco_mode/flutter_eco_mode.dart';
import 'package:flutter_eco_mode/messages.g.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

/// The interface that implementations of flutter_eco_mode must implement.
abstract class FlutterEcoModePlatform extends PlatformInterface {
  /// Constructs a FlutterEcoModePlatform.
  FlutterEcoModePlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterEcoModePlatform _instance = FlutterEcoMode();

  /// The default instance of [FlutterEcoModePlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterEcoMode].
  static FlutterEcoModePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterEcoModePlatform] when
  /// they register themselves.
  static set instance(FlutterEcoModePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }


  // USED: Total memory is used by DevicePerformance
  /// Return the total RAM.
  Future<int?> getTotalMemory();

  // USED: Device range is used by DevicePerformance
  /// Return the eco range.
  Future<DeviceRange?> getDeviceRange();
}

class DeviceRange {
  double score;
  DeviceEcoRange range;
  bool isLowEndDevice;

  DeviceRange({
    required this.score,
    required this.range,
    this.isLowEndDevice = false,
  });
}

enum DeviceEcoRange { lowEnd, midRange, highEnd }
