import 'package:flutter_eco_mode/flutter_eco_mode.dart';
import 'package:flutter_eco_mode/flutter_eco_mode_platform_interface.dart';

extension FutureEcoRangeExtension on Future<DeviceRange?> {
  Future<String?> getScore() => then((value) =>
      value?.score != null ? "${(value!.score * 100).toInt()}/100" : null);

  Future<String?> getRange() => then((value) => value?.range.name);

  Future<String?> isLowEndDevice() =>
      then((value) => value?.isLowEndDevice.toString());
}
