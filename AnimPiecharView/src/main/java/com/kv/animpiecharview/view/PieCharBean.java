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

package com.kv.animpiecharview.view;


public class PieCharBean {

    public static final int FLAG_NO_MATCH = -361;
    
    private float mPercentage = 0f;
    
    private String mName;
    
    private float mAngleSize = 0f;
    
    private float mAngleStart = 0f;
    
    private float mAngleEnd = 0f;
    
    private float mAnimAngleSize = 0f;
    
    public PieCharBean(float percentage, String name) {
        this.mPercentage = percentage;
        this.mName = name;
    }

    public float getPercentage() {
        return mPercentage;
    }

    public void setPercentage(float percentage) {
        this.mPercentage = percentage;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public float getAngleSize() {
        return mAngleSize;
    }

    public void setAngleSize(float angleSize) {
        this.mAngleSize = angleSize;
    }

    public float getAngleStart() {
        return mAngleStart;
    }

    public void setAngleStart(float angleStart) {
        this.mAngleStart = angleStart;
    }

    public float getAngleEnd() {
        return mAngleEnd;
    }

    public void setAngleEnd(float angleEnd) {
        this.mAngleEnd = angleEnd;
    }
    
    public float getAnimAngleSize() {
        return mAnimAngleSize;
    }

    public void setAnimAngleSize(float animAngleSize) {
        this.mAnimAngleSize = animAngleSize;
    }

    /**
     * is contain select
     * @param startAngle
     * @param selectAngle
     * @return the middle angle
     */
    public float isContainSelect(float startAngle, float selectAngle) { // contain start
        float posModeStartAngle = getPosMode360(startAngle + mAngleStart);
        float posModeEndAngle = getPosMode360(startAngle + mAngleEnd);
//        Log.e("xx", "posStar=" + posModeStartAngle + " posEnd=" + posModeEndAngle + " select=" + selectAngle);
        if (posModeStartAngle < posModeEndAngle) {
            if (selectAngle < posModeEndAngle && selectAngle >= posModeStartAngle) {
                return (posModeEndAngle + posModeStartAngle)/2; 
            } 
        } else if (posModeEndAngle < posModeStartAngle) {
            if (selectAngle >= posModeStartAngle || selectAngle < posModeEndAngle) {
                float factEndAngle = posModeEndAngle - 360;
                return (factEndAngle + posModeStartAngle) /2;
            }
        }
        return FLAG_NO_MATCH;
    }

    /**
     * Get Position mode 360.
     * @param num
     * @return
     */
    private float getPosMode360(float num) {
        float modeNum = num % 360;
        if (modeNum < 0) {
            modeNum += 360;
        }
        return modeNum;
    }
}
