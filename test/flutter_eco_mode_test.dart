import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_eco_mode/flutter_eco_mode.dart';
import 'package:flutter_eco_mode/flutter_eco_mode_platform_interface.dart';
import 'package:flutter_eco_mode/messages.g.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';

class MockEventChannel extends Mock implements EventChannel {}

class MockEcoModeApi extends Mock implements EcoModeApi {}

void main() {
  late EventChannel batteryLevelEventChannel;
  late EventChannel batteryStateEventChannel;
  late EventChannel batteryModeEventChannel;
  late EventChannel connectivityStateEventChannel;
  late EcoModeApi ecoModeApi;

  FlutterEcoMode buildEcoMode() => FlutterEcoMode(
        api: ecoModeApi,
      );

  setUp(() {
    ecoModeApi = MockEcoModeApi();
    batteryLevelEventChannel = MockEventChannel();
    batteryStateEventChannel = MockEventChannel();
    batteryModeEventChannel = MockEventChannel();
    connectivityStateEventChannel = MockEventChannel();
    when(() => batteryLevelEventChannel.receiveBroadcastStream())
        .thenAnswer((_) => Stream<double>.value(100.0));
    when(() => batteryModeEventChannel.receiveBroadcastStream())
        .thenAnswer((_) => Stream<bool>.value(false));
    when(() => connectivityStateEventChannel.receiveBroadcastStream())
        .thenAnswer((_) => Stream<String>.value('{"type": "UNKNOWN"}'));
  });


  group(
    'Device Range getEcoScore',
    () {
      test('should return null when get eco score error', () async {
        when(() => ecoModeApi.getEcoScore())
            .thenAnswer((_) => Future.error('error eco score'));
        expect(await buildEcoMode().getDeviceRange(), null);
      });

      test('should return null when get eco score null', () async {
        when(() => ecoModeApi.getEcoScore())
            .thenAnswer((_) => Future.value(null));
        expect(await buildEcoMode().getDeviceRange(), null);
      });

      test('should return low end device', () async {
        when(() => ecoModeApi.getEcoScore())
            .thenAnswer((_) => Future.value(minScoreLowEndDevice));
        final deviceRange = await buildEcoMode().getDeviceRange();
        expect(deviceRange!.score, minScoreLowEndDevice);
        expect(deviceRange.range, DeviceEcoRange.lowEnd);
        expect(deviceRange.isLowEndDevice, true);
      });

      test('should return mid range device', () async {
        when(() => ecoModeApi.getEcoScore())
            .thenAnswer((_) => Future.value(minScoreMidRangeDevice));
        final deviceRange = await buildEcoMode().getDeviceRange();
        expect(deviceRange!.score, minScoreMidRangeDevice);
        expect(deviceRange.range, DeviceEcoRange.midRange);
        expect(deviceRange.isLowEndDevice, false);
      });

      test('should return high end device', () async {
        when(() => ecoModeApi.getEcoScore())
            .thenAnswer((_) => Future.value(minScoreMidRangeDevice + 0.1));
        final deviceRange = await buildEcoMode().getDeviceRange();
        expect(deviceRange!.score, minScoreMidRangeDevice + 0.1);
        expect(deviceRange.range, DeviceEcoRange.highEnd);
        expect(deviceRange.isLowEndDevice, false);
      });
    },
  );
}
