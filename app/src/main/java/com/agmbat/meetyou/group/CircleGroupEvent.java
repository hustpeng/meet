package com.agmbat.meetyou.group;

import java.util.List;

public class CircleGroupEvent {

    public List<CircleGroup> mCircleGroups;

    public CircleGroupEvent(List<CircleGroup> circleGroups){
        mCircleGroups = circleGroups;
    }

    public List<CircleGroup> getCircleGroups(){
        return mCircleGroups;
    }
}
