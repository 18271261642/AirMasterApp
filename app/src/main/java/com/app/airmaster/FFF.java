package com.app.airmaster;

import com.blala.blalable.Utils;

public class FFF {

    public static void main(String[] args) {
        String str = "30303030303030303030000000000000";
        byte[] array = Utils.hexStringToByte(str);

        System.out.println("-------array="+new String(array));

        System.out.println('I'+'T');
    }

}
