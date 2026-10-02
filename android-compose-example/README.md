# IRext Android APP Example

This project shows how can you develop an Android APP remote controller quickly.

The example app has been rewritten with Jetpack Compose. It demonstrates how to use the IRext Cloud SDK to browse categories, brands, areas/operators and remote indexes, download remote control binary files, and test/send IR commands from a Compose UI.

The key components of the Android remote example include:

## Use The Cloud-SDK to Index Remote
The Cloud SDK calls the Rest API provided by the IRext index service to complete the APP login and
help use indexing targeted remote controller from category to remote index.

To import the Android Cloud SDK, add following line to the build.gradle for the APP

```
implementation 'net.irext.webapi:irext-androidapi:1.5.3'
```

Version `1.5.3` adds the `from` and `count` parameters to the paging APIs, so the app can load index data page by page instead of requesting all data at once.

And then add the meta-data in your AndroidManifest.xml to make the Cloud SDK login to the indexing server for the access token

```xml
<meta-data
    android:name="irext_app_key"
    android:value="c49c3bd5ff6b3efb67d8af2c" />

<meta-data
    android:name="irext_app_secret"
    android:value="194f9cb578c458ace2284f16" />
```

Follow the examples of calling `mApp.mWeAPIs` in corresponding Compose UI flows in order to find the targeted remote index and download the remote control binary file. The paging screens use the new `from` and `count` parameters provided by `irext-androidapi:1.5.3`.

## Jetpack Compose UI

The Android example has been rewritten with Jetpack Compose. The Compose implementation includes screens for category, brand, province/city/operator, remote index and control flows, making the sample easier to extend and maintain with modern Android UI patterns.

A `TestScreen` has also been added for quick validation of a product's IR control codes. After selecting or specifying a target remote/product, you can use this screen to quickly test all available IR remote control codes without going through the full user operation flow each time.

<img width="288" height="640" alt="image" src="https://github.com/user-attachments/assets/83cb9202-f85e-46c6-8e2c-2a20995f615c" />


<img width="288" height="640" alt="image" src="https://github.com/user-attachments/assets/7a36810e-a97e-4ea4-b824-3ab81b9664c7" />

## Use Mobile Phone as Remote Control
After the remote index binary code is downloaded, you can see the remote control panel,
by pressing control buttons, the binary code would be decoded into IR timing series. If you have an Android phone with IR transmitter, 
you can send the 38KHz infra-red waves directly to control the home appliances.

As a reference, you need to integrate the IR decode library, see the shared libraries in jniLibs directory into your project.

By calling API provided by the decode library in order to open and decode remote control binary files into infra-red timing series:

```java
public int irControl(int category, int subCategory, int keyCode) {
    int []decoded;
    StringBuilder debugStr = new StringBuilder();
    ACStatus acStatus = new ACStatus();
    int inputKeyCode = ControlHelper.translateKeyCode(category, keyCode, acStatus);
    decoded = mIRDecode.decodeBinary(inputKeyCode, acStatus);
    ControlHelper.transmitIr(mContext, decoded);
    return 0;
}
```

**Here you need to manage your AC Status in Android application according to user interactions.**


## Working with Arduino Remote Control
There is another example project <a href='https://opensource.irext.net/irext/examples/-/tree/master/arduino-example'>arduino-example</a> which can be co-worked with this
Android APP remote controller.


Well by connecting to the Arduino controller with IP address in the same LAN, the Android APP can send the downloaded remote control binary to it, 
and then pass the remote control command by pressing buttons accordingly. The Arduino remote controller would decode the IR time series instead and send the
38KHz infra-red carrier waves to home appliances.


## Related Links

- [IRext Official Site](https://site.irext.net)
- [IRext Documentation](https://site.irext.net/doc)
- [IRext Open Source Project](https://opensource.irext.net)

## License

Please refer to the [IRext repository](https://opensource.irext.net) for license information.

## Contact Me

If there are any issues with the code, please contact me at [shdm520@gmail.com](mailto:shdm520@gmail.com).
