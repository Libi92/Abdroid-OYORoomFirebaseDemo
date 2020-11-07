package com.application.pglocator.db;

import com.application.pglocator.model.PGRoom;

import java.util.List;

public interface PGListener {
    void onGetPG(List<PGRoom> rooms);
}
