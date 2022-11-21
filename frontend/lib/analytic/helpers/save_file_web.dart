import 'dart:async';
import 'dart:convert';
import 'dart:html';

///To save the PDF file on the device
class FileSave {
  static Future<void> fileSave(List<int> bytes, String fileName) async {
    AnchorElement(
        href:
            'data:application/octet-stream;charset=utf-16le;base64,${base64.encode(bytes)}')
      ..setAttribute('download', fileName)
      ..click();
  }
}
