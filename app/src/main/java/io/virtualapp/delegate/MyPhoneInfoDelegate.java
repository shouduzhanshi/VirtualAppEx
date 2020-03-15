package io.virtualapp.delegate;


import com.lody.virtual.client.hook.delegate.PhoneInfoDelegate;

import timber.log.Timber;


/**
 * Fake the Device ID.
 */
public class MyPhoneInfoDelegate implements PhoneInfoDelegate {

    @Override
    public String getDeviceId(String oldDeviceId, int userId) {
        Timber.e("getDeviceId "+oldDeviceId);
        return oldDeviceId;
    }

    @Override
    public String getBluetoothAddress(String oldAddress, int userId) {
        Timber.e("getBluetoothAddress "+oldAddress);
        return oldAddress;
    }

    @Override
    public String getMacAddress(String oldAddress, int userId) {
        Timber.e("getMacAddress "+oldAddress);
        return oldAddress;
    }
}
