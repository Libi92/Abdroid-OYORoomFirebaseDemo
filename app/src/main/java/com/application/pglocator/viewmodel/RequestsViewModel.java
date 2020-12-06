package com.application.pglocator.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.pglocator.model.PGRequest;

public class RequestsViewModel extends ViewModel {
    private final MutableLiveData<PGRequest> requestData = new MutableLiveData<>();

    public void setData(PGRequest pgRequest) {
        requestData.setValue(pgRequest);
    }

    public LiveData<PGRequest> getRequest() {
        return this.requestData;
    }
}
