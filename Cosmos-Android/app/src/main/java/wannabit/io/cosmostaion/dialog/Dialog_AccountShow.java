package wannabit.io.cosmostaion.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;

import static android.content.Context.WINDOW_SERVICE;

public class Dialog_AccountShow extends DialogFragment {

    String TAG = "GenerateQRCode";
    EditText edtAmount;
    private ImageView mQr;
    private TextView mTitle, mAddress;
    private Button btn_Share, btn_Receive;

    String inputValue, QRTextValue;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    public static Dialog_AccountShow newInstance(Bundle bundle) {
        Dialog_AccountShow frag = new Dialog_AccountShow();
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_account_show_new, null);

        btn_Share = view.findViewById(R.id.btn_nega);
        btn_Receive = view.findViewById(R.id.btn_posi);
        mTitle = view.findViewById(R.id.wallet_name);
        mAddress = view.findViewById(R.id.wallet_address_tv);
        mQr = view.findViewById(R.id.wallet_address_qr);
        edtAmount = view.findViewById(R.id.edt_amount);

        final String address = getArguments().getString("address");

        mTitle.setText(getArguments().getString("title"));
        mAddress.setText(address);

        btn_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtAmount.getText().toString().trim().isEmpty()){
                    edtAmount.setError("Required!");
                    return;
                }
                ((BaseActivity) getActivity()).onShareType(getArguments().getString("address"));
                getDialog().dismiss();
            }
        });

        btn_Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputValue = edtAmount.getText().toString().trim();

                QRTextValue = address +"/"+ inputValue;

                if (QRTextValue.length() > 0) {
                    WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            QRTextValue, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        mQr.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }
                } else {
                    edtAmount.setError("Required!");
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        return builder.create();
    }

}
