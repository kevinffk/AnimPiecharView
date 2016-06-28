/*
 * Copyright (c) 2016.  [597415099@qq.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kv.animpiecharview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.kv.animpiecharview.view.PieCharBean;
import com.kv.animpiecharview.view.PieCharView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-06-24 14:55
 * Description:
 */
public class MainActivity extends Activity implements PieCharView.OnPieCharListener {

    private PieCharView pieCharView;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        pieCharView = (PieCharView) findViewById(R.id.pv);
        tv = (TextView) findViewById(R.id.tv);

        List<PieCharBean> pieCharBeanList = new ArrayList<>();
        pieCharBeanList.add(new PieCharBean(30f, "piece 1"));
        pieCharBeanList.add(new PieCharBean(17f, "piece 2"));
        pieCharBeanList.add(new PieCharBean(15f, "piece 3"));
        pieCharBeanList.add(new PieCharBean(8f, "piece 4"));
        pieCharBeanList.add(new PieCharBean(20f, "piece 5"));
        pieCharBeanList.add(new PieCharBean(10f, "piece 6"));

        pieCharView.setOnPieCharListener(this);
        pieCharView.setData(pieCharBeanList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pieCharView.release();
    }

    @Override
    public void onSelect(PieCharBean pieCharBean) {
        tv.setText(pieCharBean.getName());
    }
}
