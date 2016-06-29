# Animation Piechar View
android animation piechar view, can fling and when stop in a pie area, it will adjust the pie angle to aim the center position of the pie area.

# Screenshot

![image](https://github.com/kevinffk/AnimPiecharView/blob/master/AnimPiecharView/demo.gif) 

#Usage

##Step1 
####add animation piechar view into layout xml.
```xml
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.kv.animpiecharview.view.PieCharView
        android:id="@+id/pv"
        android:layout_margin="20dp"
        android:layout_centerInParent="true"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    <TextView
        android:id="@+id/tv"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="30dp"
        android:textSize="25sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</RelativeLayout>
```

##Step2
####create a data list and add items into it.
```java
List<PieCharBean> pieCharBeanList = new ArrayList<>();
pieCharBeanList.add(new PieCharBean(30f, "piece 1"));
pieCharBeanList.add(new PieCharBean(17f, "piece 2"));
pieCharBeanList.add(new PieCharBean(15f, "piece 3"));
pieCharBeanList.add(new PieCharBean(8f, "piece 4"));
pieCharBeanList.add(new PieCharBean(20f, "piece 5"));
pieCharBeanList.add(new PieCharBean(10f, "piece 6"));

pieCharView.setData(pieCharBeanList);
```
###Tips
* sum of the items percentage should equals 100 (sum of percentage == 100)
* the size of items should no more than 15 (pieCharBeanList.size() <= 15)

##Step3
####register an animation piechar view listener
```java
pieCharView.setOnPieCharListener(this); // register listener
```
```java
@Override
public void onSelect(PieCharBean pieCharBean) { //listener callback method
    tv.setText(pieCharBean.getName());
}
```

##Step4
####Recycle when exit UI Activity
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    pieCharView.release();
}
```

##Done
any questions and welcome to send email for me:) 
kevinffk@126.com

#License
```
Copyright (c) 2016 kevinffk

Licensed under the Apache License, Version 2.0

```


