import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/messages.g.dart',
  dartOptions: DartOptions(),
  kotlinOut:
      'android/src/main/kotlin/sncf/connect/tech/flutter_eco_mode/Messages.g.kt',
  kotlinOptions: KotlinOptions(package: 'sncf.connect.tech.flutter_eco_mode'),
  swiftOut: 'ios/Classes/Messages.g.swift',
  swiftOptions: SwiftOptions(),
  dartPackageName: 'flutter_eco_mode',
))
@HostApi()
abstract class EcoModeApi {
  int getTotalMemory();

  double? getEcoScore();
}
