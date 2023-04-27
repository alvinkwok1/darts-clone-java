/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.dartsclone.details;

/**
 *
 * @author manabe
 */
public class Keyset {
    // key数组
    private final byte[][] _keys;
    // value数组
    private final int[] _values;
    public Keyset(byte[][] keys, int[] values) {
        _keys = keys;
        _values = values;
    }

    // 返回集合中key的数量
    int numKeys() {
        return _keys.length;
    }
    // 根据下标获取key对应的key
    byte[] getKey(int id) {
        return _keys[id];
    }

    // 根据key下标和key的byte下标获取对应的byte值
    byte getKeyByte(int keyId, int byteId) {
        if (byteId >= _keys[keyId].length) {
            return 0;
        }
        return _keys[keyId][byteId];
    }

    // 判断集合中值是否为空
    boolean hasValues() {
        return _values != null;
    }

    // 根据值的下标获取对应值，如果没有值的情况返回对应的id
    int getValue(int id) {
        if (hasValues()) {
            return _values[id];
        }
        return id;
    }
}
