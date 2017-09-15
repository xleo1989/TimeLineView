# TimeLineView
---
***example:***
**xml:**

      <com.x.leo.timelineview.TimeLineView
                android:id="@+id/time_marker"
                android:layout_width="@dimen/dp30"
                android:layout_height="match_parent"
                app:directionStyle="VERTICAL"
                app:markerStyle="circleFilled"
                app:CurrentStatus="Inactive"
                app:drawText="true"
                app:notShowPointLine="true"
                app:circleActiveBigger="@color/color_bottom_tab_light"
                app:circleActive="@color/bottom_tab"
                app:circleInactive="@color/colorPrimaryDark"
                app:radiusInner="@dimen/dp10"
                app:radius="@dimen/dp15"
                app:completeRes="@mipmap/progress_complete"
                app:relativeGravity="CENTER"
                style="@style/text_12dp_white"
                app:strokeColor="@color/colorPrimaryDark"
                app:doAnimation="true"
                app:strokeWidth="@dimen/dp03"/>

**java:**

