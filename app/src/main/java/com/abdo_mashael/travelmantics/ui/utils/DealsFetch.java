package com.abdo_mashael.travelmantics.ui.utils;

import com.abdo_mashael.travelmantics.data.UserListItem;

import java.util.List;

public interface DealsFetch{
    void dealsReady(List<UserListItem> listItems, Boolean isRemove, int removedPosition);
}
