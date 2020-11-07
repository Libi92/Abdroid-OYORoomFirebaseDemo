package com.application.pglocator.db;

import com.application.pglocator.model.PGRequest;

import java.util.List;

public interface PGRequestListener {
    void onGetPGRequest(List<PGRequest> requests);
}
