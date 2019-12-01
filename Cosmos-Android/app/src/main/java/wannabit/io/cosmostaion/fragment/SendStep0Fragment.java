package wannabit.io.cosmostaion.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.SendActivity;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.utils.WKey;
import wannabit.io.cosmostaion.utils.WLog;

public class SendStep0Fragment extends BaseFragment implements View.OnClickListener {

    private EditText        mAddressInput;
    private Button          mCancel, mNextBtn;
    private LinearLayout    mBtnQr, mBtnPaste, mBtnHistory;
    private String          scannedText[], sAmount = "";

    public static SendStep0Fragment newInstance(Bundle bundle) {
        SendStep0Fragment fragment = new SendStep0Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_step0, container, false);
        mAddressInput = rootView.findViewById(R.id.receiver_account);
        mNextBtn = rootView.findViewById(R.id.btn_next);
        mCancel = rootView.findViewById(R.id.btn_cancel);

        mBtnQr = rootView.findViewById(R.id.btn_qr);
        mBtnPaste = rootView.findViewById(R.id.btn_paste);
        mBtnHistory = rootView.findViewById(R.id.btn_history);
        mBtnHistory.setVisibility(View.GONE);

        mCancel.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mBtnQr.setOnClickListener(this);
        mBtnPaste.setOnClickListener(this);
        mBtnHistory.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mNextBtn)) {
            String targetAddress = mAddressInput.getText().toString().trim();
            if (getSActivity().mAccount.address.equals(targetAddress)) {
                Toast.makeText(getContext(), R.string.error_self_sending, Toast.LENGTH_SHORT).show();
                return;
            }

            if (getSActivity().mBaseChain.equals(BaseChain.COSMOS_MAIN)) {
                if (targetAddress.startsWith("colors") && WKey.isValidBech32(targetAddress)) {
                    getSActivity().mTagetAddress = targetAddress;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_cosmos_address, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(BaseChain.IRIS_MAIN)) {
                if (targetAddress.startsWith("iaa") && WKey.isValidBech32(targetAddress)) {
                    getSActivity().mTagetAddress = targetAddress;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_iris_address, Toast.LENGTH_SHORT).show();
                }

            } else if (getSActivity().mBaseChain.equals(BaseChain.BNB_MAIN)) {
                if (targetAddress.startsWith("bnb") && WKey.isValidBech32(targetAddress)) {
                    getSActivity().mTagetAddress = targetAddress;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_bnb_address, Toast.LENGTH_SHORT).show();
                }
            } else if (getSActivity().mBaseChain.equals(BaseChain.KAVA_MAIN)) {
                if (targetAddress.startsWith("kava") && WKey.isValidBech32(targetAddress)) {
                    getSActivity().mTagetAddress = targetAddress;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_iov_address, Toast.LENGTH_SHORT).show();
                }
            } else if (getSActivity().mBaseChain.equals(BaseChain.IOV_MAIN)) {
                if (targetAddress.startsWith("iov") && WKey.isValidBech32(targetAddress)) {
                    getSActivity().mTagetAddress = targetAddress;
                    getSActivity().onNextStep();
                } else {
                    Toast.makeText(getContext(), R.string.error_invalid_iov_address, Toast.LENGTH_SHORT).show();
                }
            }


        } else if (v.equals(mCancel)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mBtnQr)) {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();

        } else if (v.equals(mBtnPaste)) {
            ClipboardManager clipboard = (ClipboardManager)getSActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if(clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                String userPaste = clipboard.getPrimaryClip().getItemAt(0).coerceToText(getSActivity()).toString().trim();
                if(TextUtils.isEmpty(userPaste)) {
                    Toast.makeText(getSActivity(), R.string.error_clipboard_no_data, Toast.LENGTH_SHORT).show();
                    return;
                }
                mAddressInput.setText(userPaste);
                mAddressInput.setSelection(mAddressInput.getText().length());

            } else {
                Toast.makeText(getSActivity(), R.string.error_clipboard_no_data, Toast.LENGTH_SHORT).show();
            }


        } else if (v.equals(mBtnHistory)) {
            Toast.makeText(getSActivity(), R.string.error_prepare, Toast.LENGTH_SHORT).show();

        }
    }

    private SendActivity getSActivity() {
        return (SendActivity)getBaseActivity();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                if(result.getContents().contains("/")){
                    scannedText = result.getContents().split("/");
                    sAmount = scannedText[1];
                    getSActivity().mTagetAmount = scannedText[1];

                    if(!scannedText[0].isEmpty()){
                        mAddressInput.setText(scannedText[0]);
                        mAddressInput.setSelection(mAddressInput.getText().length());
                    }
                }else{
                    mAddressInput.setText(result.getContents().trim());
                    mAddressInput.setSelection(mAddressInput.getText().length());
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}