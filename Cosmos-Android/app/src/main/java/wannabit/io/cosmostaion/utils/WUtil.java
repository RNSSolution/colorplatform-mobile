package wannabit.io.cosmostaion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.common.BitMatrix;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseChain;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dao.Balance;
import wannabit.io.cosmostaion.dao.BnbToken;
import wannabit.io.cosmostaion.dao.BondingState;
import wannabit.io.cosmostaion.dao.IovToken;
import wannabit.io.cosmostaion.dao.IrisToken;
import wannabit.io.cosmostaion.dao.Reward;
import wannabit.io.cosmostaion.dao.UnBondingState;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.model.type.IrisProposal;
import wannabit.io.cosmostaion.model.type.IrisVote;
import wannabit.io.cosmostaion.model.type.Proposal;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.network.res.ResBnbAccountInfo;
import wannabit.io.cosmostaion.network.res.ResBnbTic;
import wannabit.io.cosmostaion.network.res.ResIovBalance;
import wannabit.io.cosmostaion.network.res.ResLcdAccountInfo;
import wannabit.io.cosmostaion.network.res.ResLcdBonding;
import wannabit.io.cosmostaion.network.res.ResLcdIrisReward;
import wannabit.io.cosmostaion.network.res.ResLcdKavaAccountInfo;
import wannabit.io.cosmostaion.network.res.ResLcdProposalVoted;
import wannabit.io.cosmostaion.network.res.ResLcdUnBonding;

import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_ATOM;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_BNB;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_IOV;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_IRIS;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_IRIS_ATTO;
import static wannabit.io.cosmostaion.base.BaseConstant.COSMOS_KAVA;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_BasicProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_CommunityTaxUsageProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_ParameterProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_PlainTextProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_SoftwareUpgradeProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_SystemHaltProposal;
import static wannabit.io.cosmostaion.base.BaseConstant.IRIS_PROPOAL_TYPE_TokenAdditionProposal;

public class WUtil {

    public static Account getAccountFromLcd(long id, ResLcdAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        if (lcd.result != null && lcd.height != null) {
            if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                    lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT_LEGACY) ||
                    lcd.result.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
                result.address = lcd.result.value.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.account_number);
                return result;
            } else {
                result.address = lcd.result.value.BaseVestingAccount.BaseAccount.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.BaseVestingAccount.BaseAccount.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.BaseVestingAccount.BaseAccount.account_number);
                return result;
            }
        }
        if (lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT_LEGACY) ||
                lcd.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
            result.address = lcd.value.address;
            result.sequenceNumber = Integer.parseInt(lcd.value.sequence);
            result.accountNumber = Integer.parseInt(lcd.value.account_number);
            return result;
        } else {
            result.address = lcd.value.BaseVestingAccount.BaseAccount.address;
            result.sequenceNumber = Integer.parseInt(lcd.value.BaseVestingAccount.BaseAccount.sequence);
            result.accountNumber = Integer.parseInt(lcd.value.BaseVestingAccount.BaseAccount.account_number);
            return result;
        }
    }

    public static Account getAccountFromBnbLcd(long id, ResBnbAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        result.address = lcd.address;
        result.sequenceNumber = Integer.parseInt(lcd.sequence);
        result.accountNumber = Integer.parseInt(lcd.account_number);
        return result;
    }

    public static Account getAccountFromKavaLcd(long id, ResLcdKavaAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        if (lcd.result != null && lcd.height != null) {
            if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT)) {
                result.address = lcd.result.value.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.account_number);

            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_VESTING_ACCOUNT)) {
                result.address = lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.account_number);

            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_P_VESTING_ACCOUNT)) {
                result.address = lcd.result.value.BaseVestingAccount.BaseAccount.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.BaseVestingAccount.BaseAccount.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.BaseVestingAccount.BaseAccount.account_number);

            }
        }
        return result;
    }

    public static ArrayList<Balance> getBalancesFromLcd(long accountId, ResLcdAccountInfo lcd) {
        long time = System.currentTimeMillis();
        ArrayList<Balance> result = new ArrayList<>();
        if (lcd.result != null && lcd.height != null) {
            if(lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                    lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT_LEGACY) ||
                    lcd.result.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
                if (lcd.result.value.coins != null && lcd.result.value.coins.size() > 0){
                    for (Coin coin : lcd.result.value.coins) {
                        Balance temp = new Balance();
                        temp.accountId = accountId;
                        temp.symbol = coin.denom;
                        temp.balance = new BigDecimal(coin.amount);
                        temp.fetchTime = time;
                        result.add(temp);
                    }
                }
                return result;
            } else {
                if (lcd.result.value.BaseVestingAccount.BaseAccount.coins != null && lcd.result.value.BaseVestingAccount.BaseAccount.coins.size() > 0){
                    for(Coin coin : lcd.result.value.BaseVestingAccount.BaseAccount.coins) {
                        Balance temp = new Balance();
                        temp.accountId = accountId;
                        temp.symbol = coin.denom;
                        temp.balance = new BigDecimal(coin.amount);
                        temp.fetchTime = time;
                        result.add(temp);
                    }
                }
                return result;
            }
        }
        if(lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT) ||
                lcd.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT_LEGACY) ||
                lcd.type.equals(BaseConstant.IRIS_BANK_TYPE_ACCOUNT)) {
            if (lcd.value.coins != null && lcd.value.coins.size() > 0){
                for(Coin coin : lcd.value.coins) {
                    Balance temp = new Balance();
                    temp.accountId = accountId;
                    temp.symbol = coin.denom;
                    temp.balance = new BigDecimal(coin.amount);
                    temp.fetchTime = time;
                    result.add(temp);
                }
            }
            return result;
        } else {
            if (lcd.value.BaseVestingAccount.BaseAccount.coins != null && lcd.value.BaseVestingAccount.BaseAccount.coins.size() > 0){
                for(Coin coin : lcd.value.BaseVestingAccount.BaseAccount.coins) {
                    Balance temp = new Balance();
                    temp.accountId = accountId;
                    temp.symbol = coin.denom;
                    temp.balance = new BigDecimal(coin.amount);
                    temp.fetchTime = time;
                    result.add(temp);
                }
            }
            return result;
        }
    }

    public static ArrayList<Balance> getBalancesFromBnbLcd(long accountId, ResBnbAccountInfo lcd) {
        long time = System.currentTimeMillis();
        ArrayList<Balance> result = new ArrayList<>();
        if (lcd.balances != null && lcd.balances.size() > 0) {
            for(ResBnbAccountInfo.BnbBalance coin : lcd.balances) {
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = coin.symbol;
                temp.balance = new BigDecimal(coin.free);
                temp.locked = new BigDecimal(coin.locked);
                temp.frozen = new BigDecimal(coin.frozen);
                temp.fetchTime = time;
                result.add(temp);
            }
        }
        return result;
    }

    public static ArrayList<Balance> getBalancesFromKavaLcd(long accountId, ResLcdKavaAccountInfo lcd) {
        long time = System.currentTimeMillis();
        ArrayList<Balance> result = new ArrayList<>();
        if (lcd.result != null && lcd.height != null) {
            if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_ACCOUNT)) {
                if (lcd.result.value.coins != null && lcd.result.value.coins.size() > 0) {
                    for (Coin coin : lcd.result.value.coins) {
                        Balance temp = new Balance();
                        temp.accountId = accountId;
                        temp.symbol = coin.denom;
                        temp.balance = new BigDecimal(coin.amount);
                        temp.fetchTime = time;
                        result.add(temp);
                    }
                }
                return result;

            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_VESTING_ACCOUNT)) {
//                //TODO 1 year after re-calculate logic
                BigDecimal totalVesting = BigDecimal.ZERO;
                BigDecimal totalDelegateVseting = BigDecimal.ZERO;
                BigDecimal dpVesting = BigDecimal.ZERO;
                BigDecimal dpBalance = BigDecimal.ZERO;

                for (int i = 0 ; i < lcd.result.value.vesting_period_progress.size(); i ++) {
                    if (lcd.result.value.vesting_period_progress.get(i).period_complete == false &&
                            lcd.result.value.vesting_period_progress.get(i).vesting_successful == false) {
                        totalVesting = totalVesting.add(new BigDecimal(lcd.result.value.PeriodicVestingAccount.vesting_periods.get(i).amount.get(0).amount));
                    }
                }

                for (Coin delegated:lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.delegated_vesting) {
                    totalDelegateVseting = totalDelegateVseting.add(new BigDecimal(delegated.amount));
                }

                if (totalVesting.compareTo(BigDecimal.ZERO) > 0) {
                    dpVesting = totalVesting.subtract(totalDelegateVseting);
                }

                if (lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.coins != null &&
                        lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.coins.size() > 0) {
                    for (Coin coin:lcd.result.value.PeriodicVestingAccount.BaseVestingAccount.BaseAccount.coins) {
                        dpBalance = dpBalance.add(new BigDecimal(coin.amount));
                    }
                }

                dpBalance = dpBalance.add(totalDelegateVseting).subtract(totalVesting);
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = COSMOS_KAVA;
                temp.balance = dpBalance;
                temp.locked = totalVesting;
                temp.fetchTime = time;
                result.add(temp);

                WLog.w(BaseConstant.COSMOS_AUTH_TYPE_VESTING_ACCOUNT);
                WLog.w("totalVesting " + totalVesting);
                WLog.w("totalDelegateVseting " + totalDelegateVseting);
                WLog.w("dpVesting " + dpVesting);
                WLog.w("dpBalance " + dpBalance);



            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_P_VESTING_ACCOUNT)) {
//                //TODO 1 year after re-calculate logic
                BigDecimal totalVesting = BigDecimal.ZERO;
                BigDecimal totalDelegateVseting = BigDecimal.ZERO;
                BigDecimal dpVesting = BigDecimal.ZERO;
                BigDecimal dpBalance = BigDecimal.ZERO;

                for (int i = 0 ; i < lcd.result.value.vesting_periods.size(); i ++) {
                    totalVesting = totalVesting.add(new BigDecimal(lcd.result.value.vesting_periods.get(i).amount.get(0).amount));
                }

                for (Coin delegated:lcd.result.value.BaseVestingAccount.delegated_vesting) {
                    totalDelegateVseting = totalDelegateVseting.add(new BigDecimal(delegated.amount));
                }

                if (totalVesting.compareTo(BigDecimal.ZERO) > 0) {
                    dpVesting = totalVesting.subtract(totalDelegateVseting);
                }


                if (lcd.result.value.BaseVestingAccount.BaseAccount.coins != null &&
                        lcd.result.value.BaseVestingAccount.BaseAccount.coins.size() > 0) {
                    for (Coin coin:lcd.result.value.BaseVestingAccount.BaseAccount.coins) {
                        dpBalance = dpBalance.add(new BigDecimal(coin.amount));
                    }
                }

                dpBalance = dpBalance.add(totalDelegateVseting).subtract(totalVesting);
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = COSMOS_KAVA;
                temp.balance = dpBalance;
                temp.locked = totalVesting;
                temp.fetchTime = time;
                result.add(temp);

                WLog.w(BaseConstant.COSMOS_AUTH_TYPE_P_VESTING_ACCOUNT);
                WLog.w("totalVesting " + totalVesting);
                WLog.w("totalDelegateVseting " + totalDelegateVseting);
                WLog.w("dpVesting " + dpVesting);
                WLog.w("dpBalance " + dpBalance);

            }
        }
        return result;
    }

    public static ArrayList<Balance> getIovBalances(long accountId, ResIovBalance rest) {
        long time = System.currentTimeMillis();
        ArrayList<Balance> result = new ArrayList<>();
        if (rest.balance != null && rest.balance.size() > 0) {
            for(ResIovBalance.IovBalance coin : rest.balance) {
                Balance temp = new Balance();
                temp.accountId = accountId;
                temp.symbol = coin.tokenTicker;
                temp.balance = new BigDecimal(coin.quantity);
                temp.fetchTime = time;
                result.add(temp);
            }
        }
        return result;
    }

    public static Balance getTokenBalance(ArrayList<Balance> list, String symbol) {
        for (Balance balance:list) {
            if (balance.symbol.equals(symbol)) {
                return balance;
            }
        }
        return null;
    }


    public static ArrayList<BondingState> getBondingFromLcds(long accountId, ArrayList<ResLcdBonding> list, BaseChain chain) {
        long time = System.currentTimeMillis();
        ArrayList<BondingState> result = new ArrayList<>();
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            for(ResLcdBonding val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                BondingState temp = new BondingState(accountId, valAddress, new BigDecimal(val.shares), time);
                result.add(temp);
            }

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            for(ResLcdBonding val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                BondingState temp = new BondingState(accountId, valAddress, new BigDecimal(val.shares).movePointRight(18), time);
                result.add(temp);
            }
        }

        return result;
    }

    public static BondingState getBondingFromLcd(long accountId, ResLcdBonding lcd, BaseChain chain) {
        String valAddress = "";
        if(!TextUtils.isEmpty(lcd.validator_addr))
            valAddress = lcd.validator_addr;
        if(!TextUtils.isEmpty(lcd.validator_address))
            valAddress = lcd.validator_address;

        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            return new BondingState(accountId, valAddress, new BigDecimal(lcd.shares), System.currentTimeMillis());

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            return new BondingState(accountId, valAddress, new BigDecimal(lcd.shares).movePointRight(18), System.currentTimeMillis());

        } else if (chain.equals(BaseChain.KAVA_MAIN)) {
            return new BondingState(accountId, valAddress, new BigDecimal(lcd.shares), System.currentTimeMillis());

        }
        return null;
    }

    public static ArrayList<UnBondingState> getUnbondingFromLcds(Context c, BaseChain chain, long accountId, ArrayList<ResLcdUnBonding> list) {
        long time = System.currentTimeMillis();
        ArrayList<UnBondingState> result = new ArrayList<>();
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            for(ResLcdUnBonding val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                for(ResLcdUnBonding.Entry entry:val.entries) {
                    UnBondingState temp = new UnBondingState(
                            accountId,
                            valAddress,
                            entry.creation_height,
                            WUtil.cosmosTimetoLocalLong(c, entry.completion_time),
                            new BigDecimal(entry.getinitial_balance()),
                            new BigDecimal(entry.getbalance()),
                            time
                    );
                    result.add(temp);
                }
            }

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            for(ResLcdUnBonding val : list) {
                String valAddress = "";
                if(!TextUtils.isEmpty(val.validator_addr))
                    valAddress = val.validator_addr;
                if(!TextUtils.isEmpty(val.validator_address))
                    valAddress = val.validator_address;

                UnBondingState temp = new UnBondingState(
                        accountId,
                        valAddress,
                        val.creation_height,
                        WUtil.cosmosTimetoLocalLong(c, val.min_time),
                        new BigDecimal(val.initial_balance.replace("iris","")).movePointRight(18),
                        new BigDecimal(val.balance.replace("iris","")).movePointRight(18),
                        time
                );
                result.add(temp);
            }
        }
        return result;
    }

    //TODO check multi unbonding with one validator
    //TOOD check Chain Type need??
    public static ArrayList<UnBondingState> getUnbondingFromLcd(Context c, long accountId, ResLcdUnBonding lcd) {
        long time = System.currentTimeMillis();
        ArrayList<UnBondingState> result = new ArrayList<>();
        for(ResLcdUnBonding.Entry entry:lcd.entries) {
            String valAddress = "";
            if(!TextUtils.isEmpty(lcd.validator_addr))
                valAddress = lcd.validator_addr;
            if(!TextUtils.isEmpty(lcd.validator_address))
                valAddress = lcd.validator_address;

            UnBondingState temp = new UnBondingState(
                    accountId,
                    valAddress,
                    entry.creation_height,
                    WUtil.cosmosTimetoLocalLong(c, entry.completion_time),
                    new BigDecimal(entry.getinitial_balance()),
                    new BigDecimal(entry.getbalance()),
                    time
            );
            result.add(temp);
        }
        return result;
    }

    public static int getVoterType(ArrayList<ResLcdProposalVoted> votes, String type) {
        int result = 0;
        for(ResLcdProposalVoted vote:votes) {
            if (vote.option.equals(type)) {
                result = result + 1;
            }
        }

        return result;
    }



    public static String prettyPrinter(Object object) {
        String result = "";
        try {
            result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            result = "Print json error";
        }
        return result;
    }


    public static boolean checkPasscodePattern(String pincode) {
        if(pincode.length() != 5)
            return false;
        String regex = "^\\d{4}+[A-Z]{1}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pincode);
        boolean isNormal = m.matches();
        return isNormal;
    }


    public static long cosmosTimetoLocalLong(Context c, String rawValue) {
        try {
            SimpleDateFormat cosmosFormat = new SimpleDateFormat(c.getString(R.string.str_block_time_format));
            cosmosFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return cosmosFormat.parse(rawValue).getTime();
        } catch (Exception e) {

        }
        return 0;
    }


    public static Gson getPresentor(){
//        return new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        return new GsonBuilder().disableHtmlEscaping().create();
    }



    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static int[] Bytearray2intarray(byte[] barray) {
        int[] iarray = new int[barray.length];
        int i = 0;
        for (byte b : barray)
            iarray[i++] = b & 0xff;
        return iarray;
    }

    public static String BytearryToDecimalString(byte[] barray) {
        String result = "";
        int[] iarray = new int[barray.length];
        int i = 0;
        for (byte b : barray) {
            iarray[i++] = b & 0xff;
            result = result + " " + (b & 0xff);
        }
        return result;
    }

    public static byte[] integerToBytes(BigInteger s, int length) {
        byte[] bytes = s.toByteArray();

        if (length < bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
            return tmp;
        } else if (length > bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
            return tmp;
        }
        return bytes;
    }

    public static String str2Hex(String bin) {
        char[] digital = "0123456789abcdef".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

    public static String hexToStr(String hex) {
        String digital = "0123456789abcdef";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return new String(bytes);
    }



    //TODO for ssh ignore test
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }


    /**
     * Sorts
     */
    public static void onSortByValidatorName(ArrayList<Validator> validators) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;
                return o1.description.moniker.compareTo(o2.description.moniker);
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByValidatorPower(ArrayList<Validator> validators) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                if (Double.parseDouble(o1.tokens) > Double.parseDouble(o2.tokens)) return -1;
                else if (Double.parseDouble(o1.tokens) < Double.parseDouble(o2.tokens)) return 1;
                else return 0;
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByDelegate(final long userId, ArrayList<Validator> validators, final BaseData dao) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                BigDecimal bondingO1 = BigDecimal.ZERO;
                BigDecimal bondingO2 = BigDecimal.ZERO;
                if(dao.onSelectBondingState(userId, o1.operator_address) != null &&
                        dao.onSelectBondingState(userId, o1.operator_address).getBondingAmount(o1) != null) {
                    bondingO1  = dao.onSelectBondingState(userId, o1.operator_address).getBondingAmount(o1) ;
                }
                if(dao.onSelectBondingState(userId, o2.operator_address) != null &&
                        dao.onSelectBondingState(userId, o2.operator_address).getBondingAmount(o2)  != null) {
                    bondingO2  = dao.onSelectBondingState(userId, o2.operator_address).getBondingAmount(o2) ;
                }
                return bondingO2.compareTo(bondingO1);

            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByReward(ArrayList<Validator> validators, final ArrayList<Reward> rewards, String denom) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                BigDecimal rewardO1 = WDp.getValidatorReward(rewards, o1.operator_address, denom);
                BigDecimal rewardO2 = WDp.getValidatorReward(rewards, o2.operator_address, denom);
                return rewardO2.compareTo(rewardO1);
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortByOnlyReward(ArrayList<Validator> validators, final ArrayList<Reward> rewards, String denom) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                BigDecimal rewardO1 = WDp.getValidatorReward(rewards, o1.operator_address, denom);
                BigDecimal rewardO2 = WDp.getValidatorReward(rewards, o2.operator_address, denom);
                return rewardO2.compareTo(rewardO1);
            }
        });
    }

    public static void onSortingByCommission(ArrayList<Validator> validators, final BaseChain chain) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;
                if (chain.equals(BaseChain.COSMOS_MAIN)) {
                    if (Float.parseFloat(o1.commission.rate) > Float.parseFloat(o2.commission.rate)) return 1;
                    else if (Float.parseFloat(o1.commission.rate) < Float.parseFloat(o2.commission.rate)) return -1;
                    else return 0;
                } else if (chain.equals(BaseChain.KAVA_MAIN)){
                    if (Float.parseFloat(o1.commission.commission_rates.rate) > Float.parseFloat(o2.commission.commission_rates.rate)) return 1;
                    else if (Float.parseFloat(o1.commission.commission_rates.rate) < Float.parseFloat(o2.commission.commission_rates.rate)) return -1;
                    else return 0;
                }
                return 0;
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortingProposal(ArrayList<Proposal> proposals) {
        Collections.sort(proposals, new Comparator<Proposal>() {
            @Override
            public int compare(Proposal o1, Proposal o2) {
                if (Integer.parseInt(o1.id) < Integer.parseInt(o2.id)) return 1;
                else if (Integer.parseInt(o1.id) > Integer.parseInt(o2.id)) return -1;
                else return 0;

            }
        });
    }

    public static void onSortingIrisProposal(ArrayList<IrisProposal> proposals) {
        Collections.sort(proposals, new Comparator<IrisProposal>() {
            @Override
            public int compare(IrisProposal o1, IrisProposal o2) {
                if (Integer.parseInt(o1.value.BasicProposal.proposal_id) < Integer.parseInt(o2.value.BasicProposal.proposal_id)) return 1;
                else if (Integer.parseInt(o1.value.BasicProposal.proposal_id) > Integer.parseInt(o2.value.BasicProposal.proposal_id)) return -1;
                else return 0;

            }
        });
    }


    public static void onSortIrisByReward(ArrayList<Validator> validators, final ResLcdIrisReward reward) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if(o1.description.moniker.equals("Cosmostation")) return -1;
                if(o2.description.moniker.equals("Cosmostation")) return 1;

                BigDecimal rewardO1 = reward.getPerValReward(o1.operator_address);
                BigDecimal rewardO2 = reward.getPerValReward(o2.operator_address);
                return rewardO2.compareTo(rewardO1);
            }
        });
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {
                if (o1.jailed && !o2.jailed) return 1;
                else if (!o1.jailed && o2.jailed) return -1;
                else return 0;
            }
        });
    }

    public static void onSortIrisOnlyByReward(ArrayList<Validator> validators, final ResLcdIrisReward reward) {
        Collections.sort(validators, new Comparator<Validator>() {
            @Override
            public int compare(Validator o1, Validator o2) {

                BigDecimal rewardO1 = reward.getPerValReward(o1.operator_address);
                BigDecimal rewardO2 = reward.getPerValReward(o2.operator_address);
                return rewardO2.compareTo(rewardO1);
            }
        });
    }

    public static void onSortingTokenByAmount(ArrayList<Balance> balances, final BaseChain chain) {
        Collections.sort(balances, new Comparator<Balance>() {
            @Override
            public int compare(Balance o1, Balance o2) {
                if (chain.equals(BaseChain.COSMOS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_ATOM)) return -1;
                    if(o2.symbol.equals(COSMOS_ATOM)) return 1;

                } else if (chain.equals(BaseChain.IRIS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_IRIS_ATTO)) return -1;
                    if(o2.symbol.equals(COSMOS_IRIS_ATTO)) return 1;

                } else if (chain.equals(BaseChain.BNB_MAIN)) {
                    if(o1.symbol.equals(COSMOS_BNB)) return -1;
                    if(o2.symbol.equals(COSMOS_BNB)) return 1;
                }
                return o2.balance.compareTo(o1.balance);
            }
        });
    }

    public static void onSortingTokenByName(ArrayList<Balance> balances, final BaseChain chain) {
        Collections.sort(balances, new Comparator<Balance>() {
            @Override
            public int compare(Balance o1, Balance o2) {
                if (chain.equals(BaseChain.COSMOS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_ATOM)) return -1;
                    if(o2.symbol.equals(COSMOS_ATOM)) return 1;

                } else if (chain.equals(BaseChain.IRIS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_IRIS_ATTO)) return -1;
                    if(o2.symbol.equals(COSMOS_IRIS_ATTO)) return 1;

                } else if (chain.equals(BaseChain.BNB_MAIN)) {
                    if(o1.symbol.equals(COSMOS_BNB)) return -1;
                    if(o2.symbol.equals(COSMOS_BNB)) return 1;
                }
                return o1.symbol.compareTo(o2.symbol);
            }
        });
    }

    public static void onSortingTokenByValue(ArrayList<Balance> balances, final BaseChain chain, HashMap<String, ResBnbTic> tics) {
        Collections.sort(balances, new Comparator<Balance>() {
            @Override
            public int compare(Balance o1, Balance o2) {
                if (chain.equals(BaseChain.COSMOS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_ATOM)) return -1;
                    if(o2.symbol.equals(COSMOS_ATOM)) return 1;

                } else if (chain.equals(BaseChain.IRIS_MAIN)) {
                    if(o1.symbol.equals(COSMOS_IRIS_ATTO)) return -1;
                    if(o2.symbol.equals(COSMOS_IRIS_ATTO)) return 1;

                } else if (chain.equals(BaseChain.BNB_MAIN)) {
                    if(o1.symbol.equals(COSMOS_BNB)) return -1;
                    if(o2.symbol.equals(COSMOS_BNB)) return 1;
                }

                ResBnbTic tic1 = tics.get(WUtil.getBnbTicSymbol(o1.symbol));
                ResBnbTic tic2 = tics.get(WUtil.getBnbTicSymbol(o2.symbol));
                if (tic1 != null && tic2 != null) {
                    BigDecimal o1Amount = o1.exchangeToBnbAmount(tic1);
                    BigDecimal o2Amount = o2.exchangeToBnbAmount(tic2);
                    return o2Amount.compareTo(o1Amount);
                } else {
                    return 0;
                }
            }
        });
    }


    public static void onSortingAccount(ArrayList<Account> accounts) {
        Collections.sort(accounts, new Comparator<Account>() {
            @Override
            public int compare(Account o1, Account o2) {
                if (o1.sortOrder > o2.sortOrder) return 1;
                else if (o1.sortOrder < o2.sortOrder) return -1;
                else return 0;

            }
        });
    }

    public static ArrayList<Validator> getIrisTops(ArrayList<Validator> allValidators) {
        ArrayList<Validator> result = new ArrayList<>();
        for(Validator v:allValidators) {
            if(v.status == Validator.BONDED) {
                result.add(v);
            }
        }
        return result;

    }

    public static ArrayList<Validator> getIrisOthers(ArrayList<Validator> allValidators) {
        ArrayList<Validator> result = new ArrayList<>();
        for(Validator v:allValidators) {
            if(v.status != Validator.BONDED) {
                result.add(v);
            }
        }
        return result;
    }

    public static Validator selectValidatorByAddr(ArrayList<Validator> validators, String opAddr) {
        for (Validator v:validators) {
            if (v.operator_address.equals(opAddr)) {
                return v;
            }
        }
        return null;
    }


    public static int getCMCId(BaseChain chain) {
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            return BaseConstant.CMC_ATOM;

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            return BaseConstant.CMC_IRIS;

        } else if (chain.equals(BaseChain.BNB_MAIN)) {
            return BaseConstant.CMC_BNB;

        } else if (chain.equals(BaseChain.KAVA_MAIN)) {
            return BaseConstant.CMC_KAVA;
        }
        return BaseConstant.CMC_ATOM;
    }

    public static String getCGCId(BaseChain chain) {
        if (chain.equals(BaseChain.COSMOS_MAIN)) {
            return BaseConstant.CGC_ATOM;

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            return BaseConstant.CGC_IRIS;

        } else if (chain.equals(BaseChain.BNB_MAIN)) {
            return BaseConstant.CGC_BNB;

        } else if (chain.equals(BaseChain.KAVA_MAIN)) {
            return BaseConstant.CGC_KAVA;
        }
        return BaseConstant.CGC_ATOM;
    }

    public static int getMaxMemoSize(BaseChain chain) {
        if (chain.equals(BaseChain.COSMOS_MAIN) || chain.equals(BaseChain.KAVA_MAIN)) {
            return BaseConstant.MEMO_ATOM;

        } else if (chain.equals(BaseChain.IRIS_MAIN)) {
            return BaseConstant.MEMO_IRIS;

        } else if (chain.equals(BaseChain.BNB_MAIN)) {
            return BaseConstant.MEMO_BNB;
        }
        return BaseConstant.MEMO_IRIS;
    }

    public static int getCharSize(String memo) {
        int result = 1000;
        try {
            result = memo.trim().getBytes("UTF-8").length;
        } catch (Exception e) { }

        return result;
    }

    public static BnbToken getBnbToken(ArrayList<BnbToken> all, Balance balance) {
        if (all == null || balance == null) return null;
        for (BnbToken token:all) {
            if (token.symbol.equals(balance.symbol)) {
                return token;
            }
        }
        return null;
    }

    public static BnbToken getBnbMainToken(ArrayList<BnbToken> all) {
        if (all == null) return null;
        for (BnbToken token:all) {
            if (token.original_symbol.equals(COSMOS_BNB)) {
                return token;
            }
        }
        return null;
    }

    public static IrisToken getIrisToken(ArrayList<IrisToken> all, Balance balance) {
        if (all == null || balance == null) return null;
        for (IrisToken token:all) {
            if(balance.symbol.split("-")[0].equals(token.base_token.id)) {
                return token;
            }
        }
        return null;
    }

    public static IrisToken getIrisMainToken(ArrayList<IrisToken> all) {
        if (all == null) return null;
        for (IrisToken token:all) {
            if (token.base_token.id.equals(COSMOS_IRIS)) {
                return token;
            }
        }
        return null;
    }

    public static IovToken getIovToken(ArrayList<IovToken> all, Balance balance) {
        if (all == null || balance == null) return null;
        for (IovToken token:all) {
            if(balance.symbol.equals(token.tokenTicker)) {
                return token;
            }
        }
        return null;
    }

    public static IovToken getIovMainToken(ArrayList<IovToken> all) {
        if (all == null) return null;
        for (IovToken token:all) {
            if (token.tokenTicker.equals(COSMOS_IOV)) {
                return token;
            }
        }
        return null;
    }


    public static boolean isBnbBaseMarketToken(String symbol) {
        switch (symbol) {
            case "USDT.B-B7C":
                return true;
            case "ETH.B-261":
                return true;
            case "BTC.B-918":
                return true;


            case "USDSB-1AC":
                return true;
            case "THKDB-888":
                return true;
            case "TUSDB-888":
                return true;
            case "BTCB-1DE":
                return true;

        }
        return false;
    }

    public static String getBnbTicSymbol(String symbol) {
        if (isBnbBaseMarketToken(symbol)) {
            return COSMOS_BNB + "_" + symbol;

        } else {
            return symbol + "_"+COSMOS_BNB;
        }
    }


    public static String getIrisProposalType(Context c, String type) {
        String result = c.getString(R.string.str_iris_proposal_type_BasicProposal);
        if (type.equals(IRIS_PROPOAL_TYPE_BasicProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_BasicProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_ParameterProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_ParameterProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_PlainTextProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_PlainTextProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_TokenAdditionProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_TokenAdditionProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_SoftwareUpgradeProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_SoftwareUpgradeProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_SystemHaltProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_SystemHaltProposal);
        } else if (type.equals(IRIS_PROPOAL_TYPE_CommunityTaxUsageProposal)) {
            result = c.getString(R.string.str_iris_proposal_type_CommunityTaxUsageProposal);
        }
        return result;
    }


    public static String getIrisMonikerName(ArrayList<Validator> validators, String address) {
        String opAddress = WKey.convertDpAddressToDpOpAddress(address, BaseChain.IRIS_MAIN);
        String result = address;
        for (Validator v:validators) {
            if (v.operator_address.equals(opAddress)) {
                result = v.description.moniker;
            }
        }
        return result;
    }

    public static int getIrisVoterType(ArrayList<IrisVote> votes, String option) {
        int result = 0;
        for (IrisVote v:votes) {
            if (v.option.equals(option)) {
                result = result + 1;
            }
        }
        return result;
    }

    public static BigDecimal getIrisVoteRate(IrisProposal.TallyResult tally, String option) {
        try {
            BigDecimal yesAmount =  new BigDecimal(tally.yes);
            BigDecimal noAmount =  new BigDecimal(tally.no);
            BigDecimal vetoAmount =  new BigDecimal(tally.no_with_veto);
            BigDecimal abstainAmount =  new BigDecimal(tally.abstain);
            BigDecimal all = yesAmount.add(noAmount).add(vetoAmount).add(abstainAmount);

            if (option.equals("YES")) {
                return yesAmount.movePointRight(2).divide(all, 2, RoundingMode.DOWN);

            } else if (option.equals("No")) {
                return noAmount.movePointRight(2).divide(all, 2, RoundingMode.DOWN);

            } else if (option.equals("NoWithVeto")) {
                return vetoAmount.movePointRight(2).divide(all, 2, RoundingMode.DOWN);

            } else if (option.equals("Abstain")) {
                return all.movePointRight(2).divide(all, 2, RoundingMode.DOWN);

            }

        } catch (Exception e) {

        } finally {
            return BigDecimal.ZERO;
        }
    }

    public static IrisVote getMyVote(ArrayList<IrisVote> votes, String address) {
        for (IrisVote v:votes) {
            if (v.voter.equals(address)) {
                return v;
            }
        }
        return null;

    }

}
