package com.example.server_aidl_emi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;

import androidx.annotation.Nullable;

import server_package.EMIInterface;

public class MyService extends Service {

    public MyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stubObject;
    }

    EMIInterface.Stub stubObject = new EMIInterface.Stub() {
        @Override
        public double calculateEMI(double principalAmount, double downPayment, double interestRate, double loanTerm) throws RemoteException {
            double emiFinal;
            principalAmount = principalAmount - downPayment;
            interestRate = interestRate / (12 * 100);
            emiFinal = principalAmount * (interestRate * Math.pow((1 + interestRate), loanTerm))
                    / (Math.pow((1 + interestRate), loanTerm) - 1);
            emiFinal = emiFinal / 12;
            System.out.println("-------- Server -Final EMI : " + emiFinal + "  - ");

            return emiFinal;


        }
    };

}