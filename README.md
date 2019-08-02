# Support in-app updates
Keeping your app up-to-date on your users’ devices enables them to try new features, as well as benefit from performance improvements and bug fixes. Although some users enable background updates when their device is connected to an unmetered connection, other users may need to be reminded to update. In-app updates is a Play Core library feature that introduces a new request flow to prompt active users to update your app.

## Flexible Update

![alt text](https://raw.githubusercontent.com/android-covi-team/inappupdate/master/images/Flexible%20Update%20Scenario.png)
![alt text](https://raw.githubusercontent.com/android-covi-team/inappupdate/master/images/in-app-updates-flexible-example.jpg)

A user experience that provides background download and installation with graceful state monitoring. This UX is appropriate when it’s acceptable for the user to use the app while downloading the update. For example, you want to urge users to try a new feature that’s not critical to the core functionality of your app. 


## Immediate Update

![alt text](https://raw.githubusercontent.com/android-covi-team/inappupdate/master/images/Immediate%20Update%20Scenario.png)
![alt text](https://raw.githubusercontent.com/android-covi-team/inappupdate/master/images/in-app-updates-immediate-example.jpg)

A full screen user experience that requires the user to update and restart the app in order to continue using the app. This UX is best for cases where an update is critical for continued use of the app. After a user accepts an immediate update, Google Play handles the update installation and app restart. 

## How to implement to your app

**1. Download**
```
android {
   ...
   
   // For Lambda expression
    compileOptions {
        sourceCompatibility = '1.8'
        targetCompatibility = '1.8'
    }
}

dependencies {
    // In-App Update
    implementation project(':in-app-update')
    implementation 'com.google.android.play:core:1.6.1'
}
```
**2. Create object UpdateManager in onCreate() method**
```
    private UpdateManager mUpdateManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUpdateManager = new UpdateManager(this, // Context
        UpdateManager.UPDATE_IMMEDIATE, // Update Type IMMEDIATE/FLEXIBLE
        this, // Observer
        this); // Callback
    }
```

**3. Implement methods from callback**
```
    @Override
    public void onUpdateNotAvailable() {
       // You're already running on the latest app
    }

    @Override
    public void onFlexibleUpdateFailed() {
       // For FLEXIBLE UPDATE only
       // Update failed -> Notify to user (Show dialog, toast...), no need to close app
    }

    @Override
    public void onCancelledFlexibleUpdate() {
      // For FLEXIBLE UPDATE only
      // Let user continue using app
    }
    @Override
    public void onNewAppIsDownloaded() {
        // For FLEXIBLE UPDATE only
        // New app already downloaded, notify user to install new app
        // Using UpdateManager.installNewApp() to start install new app
    }
    @Override
    public void onImmediateUpdateFailed() {
      // For IMMEDIATE UPDATE only
      // Update failed -> Should close app
    }

    @Override
    public void onCancelledImmediateUpdate() {
        // For IMMEDIATE UPDATE only
        // Close app now
    }
    
```

**4. Added onActivityResult for detect changes during update app**
```
 @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUpdateManager.onActivityForResult(requestCode, resultCode);
    }
```

## Notes
In order to test this library, you have to follow this steps:
  1. Update new app to Google Play Store with already implemented in-app-update library & waiting for review app
      - Version Name: 1.0.0
      - Version Code: 1
  2. When your new app is ready -> Download & install to your phone
  3. Update new app to Google Play Store again (version code is higher than previous app & waiting for review app
      - Version Name: 1.5.0
      - Version Code: 2
  4. After new app is ready -> open current app (Version 1.0.0, Version Code 1)
      - in-app-update library will run & notify your new update

## License
```
Copyright (c) 2019 COVISOFT INCOPORATION

Author : thongpham95

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
