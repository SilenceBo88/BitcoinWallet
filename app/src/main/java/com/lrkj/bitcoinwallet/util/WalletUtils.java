package com.lrkj.bitcoinwallet.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.TypefaceSpan;
import android.util.Log;

import com.lrkj.bitcoinwallet.Constants;

import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;

public class WalletUtils {

  private static final String TAG = "WalletUtils";

  private static class MonospaceSpan extends TypefaceSpan {
    MonospaceSpan() {
      super("monospace");
    }

    @Override
    public boolean equals(@Nullable final Object o) {
      return o == this || o != null && o.getClass() == getClass();
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

  /**
   * 格式化Hash
   * @param address
   * @param groupSize
   * @param lineSize
   * @return
   */
  @NonNull
  public static Spanned formatHash(final String address, final int groupSize, final int lineSize) {
    return formatHash(address, groupSize, lineSize, Constants.CHAR_THIN_SPACE);
  }

  /**
   * 格式化Hash
   * @param address
   * @param groupSize
   * @param lineSize
   * @return
   */
  @NonNull
  private static Spanned formatHash(final String address, final int groupSize, final int lineSize, final char groupSeparator) {
    Log.d(TAG, "formatHash: ");
    final SpannableStringBuilder builder = new SpannableStringBuilder();
    final int len = address.length();
    for (int i = 0; i < len; i += groupSize) {
      final int end = i + groupSize;
      final String part = address.substring(i, end < len ? end : len);

      builder.append(part);
      builder.setSpan(new MonospaceSpan(), builder.length() - part.length(), builder.length(),
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      if (end < len) {
        final boolean endOfLine = lineSize > 0 && end % lineSize == 0;
        builder.append(endOfLine ? '\n' : groupSeparator);
      }
    }

    return SpannedString.valueOf(builder);
  }


  /**
   * 获取发送交易的目的地址
   * @param tx
   * @param wallet
   * @return
   */
  @NonNull
  public static String getToAddressOfSent(final Transaction tx, final Wallet wallet) {
    Log.d(TAG, "getToAddressOfSent: ");
    for (final TransactionOutput output : tx.getOutputs()) {
      try {
        if (!output.isMine(wallet)) {
          final Script script = output.getScriptPubKey();
          return script.getToAddress(Constants.NETWORK_PARAMETERS, true).toString();
        }
      } catch (final ScriptException x) {
        // swallow
      }
    }
    return "";
  }

  /**
   * 获取接收交易的发送地址
   * @param tx
   * @param wallet
   * @return
   */
  @NonNull
  public static String getWalletAddressOfReceived(final Transaction tx, final Wallet wallet) {
    Log.d(TAG, "getWalletAddressOfReceived: ");
    for (final TransactionOutput output : tx.getOutputs()) {
      try {
        if (output.isMine(wallet)) {
          final Script script = output.getScriptPubKey();
          return script.getToAddress(Constants.NETWORK_PARAMETERS, true).toString();
        }
      } catch (final ScriptException x) {
        // swallow
      }
    }

    return "";
  }
}