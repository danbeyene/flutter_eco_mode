import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:flutter_eco_mode/flutter_eco_mode_platform_interface.dart';
import 'package:flutter_eco_mode/messages.g.dart';

const double minScoreMidRangeDevice = 0.5;
const double minScoreLowEndDevice = 0.3;

/// An implementation of [FlutterEcoModePlatform] that uses pigeon.
class FlutterEcoMode extends FlutterEcoModePlatform {
  final EcoModeApi _api;
  
  FlutterEcoMode({
    EcoModeApi? api,
  }) : _api = api ?? EcoModeApi();

  // USED: Total memory is used by DevicePerformance
  @override
  Future<int?> getTotalMemory() async {
    return await _api.getTotalMemory();
  }

  // USED: Device range is used by DevicePerformance
  @override
  Future<DeviceRange?> getDeviceRange() async {
    return _api.getEcoScore().then<DeviceRange?>((value) {
      if (value == null) {
        throw Exception('Error while getting eco score');
      }
      final range = _buildRange(value);
      return DeviceRange(
          score: value,
          range: range,
          isLowEndDevice: range == DeviceEcoRange.lowEnd);
    }).onError((error, stackTrace) {
      log(stackTrace.toString(), error: error);
      return null;
    });
  }

  DeviceEcoRange _buildRange(double score) {
    switch (score) {
      case > minScoreMidRangeDevice:
        return DeviceEcoRange.highEnd;
      case > minScoreLowEndDevice:
        return DeviceEcoRange.midRange;
      default:
        return DeviceEcoRange.lowEnd;
    }
  }
}
