package com.szy.font;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;

public class Font16 {
	private final static String TAG = "test";
	private Context context;

	public Font16(Context context) {
		this.context = context;
	}

	private final static String ENCODE = "GB2312";
	private final static String ZK16 = "HZK16";

	private int[][] arr;
	int all_16_32 = 16;
	int all_2_4 = 2;
	int all_32_128 = 32;

	public int[][] drawString(String str) {
		byte[] data = null;
		int[] code = null;
		int byteCount;
		int lCount;

		arr = new int[all_16_32][all_16_32];
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < 0x80) {
				continue;
			}
			code = getByteCode(str.substring(i, i + 1));

			Log.d(TAG, String.valueOf(code[0]) + "      " + String.valueOf(code[1]));

			data = read(code[0], code[1]);

			byteCount = 0;
			for (int line = 0; line < all_16_32; line++) {
				lCount = 0;
				for (int k = 0; k < all_2_4; k++) {
					for (int j = 0; j < 8; j++) {
						if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
							arr[line][lCount] = 1;
							System.out.print("@");
						} else {
							System.out.print(" ");
							arr[line][lCount] = 0;
						}
						lCount++;
					}
					byteCount++;
				}
				System.out.println();
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < all_16_32; i++) {
			for (int j = 0; j < all_16_32; j++) {
				sb.append(arr[i][j]);
			}
			sb.append("\n");
		}

		Log.v(TAG, "+++++++++++++++++++");
		Log.d(TAG, sb.toString());
		Log.v(TAG, "+++++++++++++++++++");

		return arr;
	}

	protected byte[] read(int areaCode, int posCode) {
		byte[] data = null;
		try {
			int area = areaCode - 0xa0;
			int pos = posCode - 0xa0;

			InputStream in = context.getResources().getAssets().open(ZK16);
			long offset = all_32_128 * ((area - 1) * 94 + pos - 1);
			in.skip(offset);
			data = new byte[all_32_128];
			in.read(data, 0, all_32_128);
			in.close();
		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
		}

		Log.v(TAG, "read data length === " + data.length);

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < all_32_128; i++) {
			Log.d(TAG, data[i] + "");
			for (int j = 0; j < 8; j++) {
				int a = (data[i] >> (7 - j)) & 0x1;
				sb.append(a);
			}
			if ( i % 2 != 0) {
				sb.append("\n");
			}
		}

		Log.d(TAG, "==============");
		Log.d(TAG, sb.toString());
		Log.d(TAG, "==============");

		return data;
	}

	protected int[] getByteCode(String str) {
		int[] byteCode = new int[2];
		try {
			byte[] data = str.getBytes(ENCODE);
			byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
			byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return byteCode;
	}

}
