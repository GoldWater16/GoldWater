# Eclipse Memory Analyzer：使用-data参数异常的解决方案

启动Eclipse Memory Analyzer(即MAT)程序报如下异常：

```java
eclipse.buildId=unknown
java.version=1.8.0_141
java.vendor=Oracle Corporation
BootLoader constants: OS=macosx, ARCH=x86_64, WS=cocoa, NL=zh_CN
Framework arguments:  --data /Users/lcp/Documents/mat -keyring /Users/lcp/.eclipse_keyring
Command-line arguments:  -os macosx -ws cocoa -arch x86_64 --data /Users/lcp/Documents/mat -keyring /Users/lcp/.eclipse_keyring

!ENTRY org.eclipse.osgi 4 0 2019-10-17 15:02:50.158
!MESSAGE Application error
!STACK 1
java.lang.IllegalStateException: The platform metadata area could not be written: /private/var/folders/sm/ss8nr3kj5_36d8tq_867mj9c0000gn/T/AppTranslocation/741D64CC-BF78-4406-A40C-6284FE0279D6/d/mat-1.app/Contents/MacOS/workspace/.metadata.  By default the platform writes its content
under the current working directory when the platform is launched.  Use the -data parameter to
specify a different content area for the platform.
        at org.eclipse.core.internal.runtime.DataArea.assertLocationInitialized(DataArea.java:70)
        at org.eclipse.core.internal.runtime.DataArea.getStateLocation(DataArea.java:138)
        at org.eclipse.core.internal.preferences.InstancePreferences.getBaseLocation(InstancePreferences.java:44)
        at org.eclipse.core.internal.preferences.InstancePreferences.initializeChildren(InstancePreferences.java:209)
        at org.eclipse.core.internal.preferences.InstancePreferences.<init>(InstancePreferences.java:59)
        at org.eclipse.core.internal.preferences.InstancePreferences.internalCreate(InstancePreferences.java:220)
        at org.eclipse.core.internal.preferences.EclipsePreferences.create(EclipsePreferences.java:349)
        at org.eclipse.core.internal.preferences.EclipsePreferences.create(EclipsePreferences.java:337)
        at org.eclipse.core.internal.preferences.PreferencesService.createNode(PreferencesService.java:393)
        at org.eclipse.core.internal.preferences.RootPreferences.getChild(RootPreferences.java:60)
        at org.eclipse.core.internal.preferences.RootPreferences.getNode(RootPreferences.java:95)
        at org.eclipse.core.internal.preferences.RootPreferences.node(RootPreferences.java:84)
        at org.eclipse.core.internal.preferences.AbstractScope.getNode(AbstractScope.java:38)
        at org.eclipse.core.runtime.preferences.InstanceScope.getNode(InstanceScope.java:77)
        at org.eclipse.ui.preferences.ScopedPreferenceStore.getStorePreferences(ScopedPreferenceStore.java:225)
        at org.eclipse.ui.preferences.ScopedPreferenceStore.<init>(ScopedPreferenceStore.java:132)
        at org.eclipse.ui.plugin.AbstractUIPlugin.getPreferenceStore(AbstractUIPlugin.java:287)
        at org.eclipse.ui.internal.Workbench.lambda$3(Workbench.java:609)
        at org.eclipse.core.databinding.observable.Realm.runWithDefault(Realm.java:336)
        at org.eclipse.ui.internal.Workbench.createAndRunWorkbench(Workbench.java:597)
        at org.eclipse.ui.PlatformUI.createAndRunWorkbench(PlatformUI.java:148)
        at org.eclipse.mat.ui.rcp.Application.start(Application.java:26)
        at org.eclipse.equinox.internal.app.EclipseAppHandle.run(EclipseAppHandle.java:196)
        at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.runApplication(EclipseAppLauncher.java:134)
        at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.start(EclipseAppLauncher.java:104)
        at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:388)
        at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:243)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.eclipse.equinox.launcher.Main.invokeFramework(Main.java:656)
        at org.eclipse.equinox.launcher.Main.basicRun(Main.java:592)
        at org.eclipse.equinox.launcher.Main.run(Main.java:1498)xxxxxxxxxx eclipse.buildId=unknownjava.version=1.8.0_141java.vendor=Oracle CorporationBootLoader constants: OS=macosx, ARCH=x86_64, WS=cocoa, NL=zh_CNFramework arguments:  --data /Users/lcp/Documents/mat -keyring /Users/lcp/.eclipse_keyringCommand-line arguments:  -os macosx -ws cocoa -arch x86_64 --data /Users/lcp/Documents/mat -keyring /Users/lcp/.eclipse_keyring!ENTRY org.eclipse.osgi 4 0 2019-10-17 15:02:50.158!MESSAGE Application error!STACK 1java.lang.IllegalStateException: The platform metadata area could not be written: /private/var/folders/sm/ss8nr3kj5_36d8tq_867mj9c0000gn/T/AppTranslocation/741D64CC-BF78-4406-A40C-6284FE0279D6/d/mat-1.app/Contents/MacOS/workspace/.metadata.  By default the platform writes its contentunder the current working directory when the platform is launched.  Use the -data parameter tospecify a different content area for the platform.        at org.eclipse.core.internal.runtime.DataArea.assertLocationInitialized(DataArea.java:70)        at org.eclipse.core.internal.runtime.DataArea.getStateLocation(DataArea.java:138)        at org.eclipse.core.internal.preferences.InstancePreferences.getBaseLocation(InstancePreferences.java:44)        at org.eclipse.core.internal.preferences.InstancePreferences.initializeChildren(InstancePreferences.java:209)        at org.eclipse.core.internal.preferences.InstancePreferences.<init>(InstancePreferences.java:59)        at org.eclipse.core.internal.preferences.InstancePreferences.internalCreate(InstancePreferences.java:220)        at org.eclipse.core.internal.preferences.EclipsePreferences.create(EclipsePreferences.java:349)        at org.eclipse.core.internal.preferences.EclipsePreferences.create(EclipsePreferences.java:337)        at org.eclipse.core.internal.preferences.PreferencesService.createNode(PreferencesService.java:393)        at org.eclipse.core.internal.preferences.RootPreferences.getChild(RootPreferences.java:60)        at org.eclipse.core.internal.preferences.RootPreferences.getNode(RootPreferences.java:95)        at org.eclipse.core.internal.preferences.RootPreferences.node(RootPreferences.java:84)        at org.eclipse.core.internal.preferences.AbstractScope.getNode(AbstractScope.java:38)        at org.eclipse.core.runtime.preferences.InstanceScope.getNode(InstanceScope.java:77)        at org.eclipse.ui.preferences.ScopedPreferenceStore.getStorePreferences(ScopedPreferenceStore.java:225)        at org.eclipse.ui.preferences.ScopedPreferenceStore.<init>(ScopedPreferenceStore.java:132)        at org.eclipse.ui.plugin.AbstractUIPlugin.getPreferenceStore(AbstractUIPlugin.java:287)        at org.eclipse.ui.internal.Workbench.lambda$3(Workbench.java:609)        at org.eclipse.core.databinding.observable.Realm.runWithDefault(Realm.java:336)        at org.eclipse.ui.internal.Workbench.createAndRunWorkbench(Workbench.java:597)        at org.eclipse.ui.PlatformUI.createAndRunWorkbench(PlatformUI.java:148)        at org.eclipse.mat.ui.rcp.Application.start(Application.java:26)        at org.eclipse.equinox.internal.app.EclipseAppHandle.run(EclipseAppHandle.java:196)        at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.runApplication(EclipseAppLauncher.java:134)        at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.start(EclipseAppLauncher.java:104)        at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:388)        at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:243)        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)        at java.lang.reflect.Method.invoke(Method.java:498)        at org.eclipse.equinox.launcher.Main.invokeFramework(Main.java:656)        at org.eclipse.equinox.launcher.Main.basicRun(Main.java:592)        at org.eclipse.equinox.launcher.Main.run(Main.java:1498)java.lang.IllegalStateException: The platform metadata area could not be written: /private/var/folders/9q/zhpkyd3s4y9d5t1nv_5hszww0000gp/T/AppTranslocation/DF264CA5-4EEF-4916-A3FA-881B111294E5/d/mat.app/Contents/MacOS/work/.metadata.  By default the platform writes its contentunder the current working directory when the platform is launched.  Use the -data parameter tospecify a different content area for the platform.java
```

#### 解决方案：

&emsp;&emsp;打开MemoryAnalyzer.ini，添加一个参数即可解决问题，例如我是mac电脑，打开路径为：`sudo vim ~/Downloads/mat.app/Contents/Eclipse/MemoryAnalyzer.ini`

##### 添加参数：

```java
-data
/Users/lcp/Documents/mat
```

##### 完整配置：

```java
-startup
../Eclipse/plugins/org.eclipse.equinox.launcher_1.5.0.v20180512-1130.jar
-data
/Users/lcp/Documents/mat
--launcher.library
../Eclipse/plugins/org.eclipse.equinox.launcher.cocoa.macosx.x86_64_1.1.700.v20180518-1200
-vmargs
-Xmx1024m
-Dorg.eclipse.swt.internal.carbon.smallFonts
-XstartOnFirstThread
```

