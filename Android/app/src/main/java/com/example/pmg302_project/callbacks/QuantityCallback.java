package com.example.pmg302_project.callbacks;

public interface QuantityCallback {
    void onQuantityReceived(int quantity);
    void onError(Exception e);
}
