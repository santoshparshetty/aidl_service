package com.example.client_aidl_emi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import server_package.EMIInterface;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText principalAmountEdit, downPaymentEdit, interestRateEdit, loanTermEdit;
    private Button calculateEMIButton, clearButton;
    private TextView emiResult;

    private EMIInterface emiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        principalAmountEdit = findViewById(R.id.principalEdit);
        downPaymentEdit = findViewById(R.id.downPaymentEdit);
        interestRateEdit = findViewById(R.id.interestEdit);
        loanTermEdit = findViewById(R.id.loanTermEdit);
        emiResult = findViewById(R.id.emiResult);

        calculateEMIButton = findViewById(R.id.calculateEMIButton);
        clearButton = findViewById(R.id.clearButton);

        calculateEMIButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);


        bindToAIDLService();
    }

    private void bindToAIDLService() {

        Intent aidlServiceIntent = new Intent("connection_to_aidl_service");


        bindService(convertImplicitIntentToExplicitIntent(aidlServiceIntent, this),
                serviceConnectionObject, BIND_AUTO_CREATE);

    }

    ServiceConnection serviceConnectionObject = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            emiInterface = server_package.EMIInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent,
                                                               Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName,
                serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    public void onClick(View v) {
        double principalAmount = Double.parseDouble(principalAmountEdit.getText().toString());
        double downPayment = Double.parseDouble(downPaymentEdit.getText().toString());
        double interestRate = Double.parseDouble(interestRateEdit.getText().toString());
        double loanTerm = Double.parseDouble(loanTermEdit.getText().toString());
        switch (v.getId()) {
            case R.id.calculateEMIButton:
                calculateMethod(principalAmount, downPayment, interestRate, loanTerm);
                break;
            case R.id.clearButton:
                clearMethod();
                break;
            default:
                Log.i("Error", "Default Case");
        }
    }

//    private double calculateEMI(double principalAmount, double downPayment, double interestRate,
//                                double loanTerm) {
//        double emiFinal;
//        principalAmount = principalAmount - downPayment;
//        interestRate = interestRate / (12 * 100);
//        emiFinal = principalAmount * (interestRate * Math.pow((1 + interestRate), loanTerm))
//                / (Math.pow((1 + interestRate), loanTerm) - 1);
//        emiFinal = emiFinal / 12;
//        System.out.println("----------Final EMI : " + emiFinal + "  ---------");
//        return emiFinal;
//    }

    private void calculateMethod(double principalAmount, double downPayment, double interestRate,
                                 double loanTerm) {
        if (String.valueOf(principalAmount).isEmpty() ||
                String.valueOf(downPaymentEdit).isEmpty() ||
                String.valueOf(interestRateEdit).isEmpty() ||
                String.valueOf(loanTermEdit).isEmpty()) {

            Toast.makeText(this, "Enter all the values", Toast.LENGTH_SHORT).show();
        } else {
            try {
                double emiFinal = emiInterface.calculateEMI(principalAmount, downPayment,
                        interestRate, loanTerm);
                emiResult.setText(String.valueOf(emiFinal));
            } catch (RemoteException re) {
                re.printStackTrace();
            }
        }
    }

    private void clearMethod() {
        principalAmountEdit.setText(null);
        interestRateEdit.setText(null);
        downPaymentEdit.setText(null);
        loanTermEdit.setText(null);
        emiResult.setText(null);
    }
}