package com.example.pmg302_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.model.Orders;
import com.example.pmg302_project.service.TransferCheckService;
import com.squareup.picasso.Picasso;

public class TransactionActivity extends AppCompatActivity {
    //GetQRCodeByUserIdUseCase getQRCodeByUserIdUseCase;

    //private final CompositeDisposable disposables = new CompositeDisposable();
    private ImageView ivQrCode;
    private TextView tvBankDess,tvBankAmount;
    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish(); // Close the original activity
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        registerReceiver(closeReceiver, new IntentFilter("com.example.pmg302_project.TransactionActivity"));

        ivQrCode=findViewById(R.id.ivQRCode);
        tvBankDess=findViewById(R.id.tvBankDess);
        tvBankAmount=findViewById(R.id.tvBankAmount);
//Du lieu tru file string.xml
        String numberBank = getString(R.string.number_bank);
        String mbbankCode = getString(R.string.mbbankcode);
        String templateQR = getString(R.string.templateQR);

        //String qrcode="https://img.vietqr.io/image/mbbank-0333990577-vmoooJD.png?";


        String ammount=getIntent().getStringExtra("amount");
        Orders order=(Orders)getIntent().getSerializableExtra("orders");
        tvBankDess.setText("Nội dung: "+order.getOrderId());
        tvBankAmount.setText(ammount+" VNĐ");
        ammount="amount="+ammount;
        String codeOrder="addInfo="+order.getOrderId();
        String qrcode="https://img.vietqr.io/image/"+mbbankCode+"-"+numberBank+"-"+templateQR+".jpg?"+ammount+"&"+codeOrder;
        Picasso.get().load(qrcode).into(ivQrCode);

        Intent intent = new Intent(this, TransferCheckService.class);
        intent.putExtra("orders", order); // Pass order code to the service
        startService(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeReceiver); // Unregister the receiver to avoid memory leaks
    }
}
