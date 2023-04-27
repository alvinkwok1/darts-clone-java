/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.dartsclone.details;

/**
 * 说白了这个东西是一个ArrayList的简单实现
 * Memory management of resizable array.
 * @author 
 */
class AutoIntPool {
    // 实际存储池
    private int[] _buf;
    // 池大小
    private int _size;
    // 池容量
    private int _capacity;
    // 获取缓存本身
    int[] getBuffer() {
        return _buf;
    }
    // 获取缓存中的第i个节点
    int get(int id) {
        return _buf[id];
    }

    // 将第i个节点设置为value
    void set(int id, int value) {
        _buf[id] = value;
    }

    // 判断是否为空
    boolean empty() {
        return (_size == 0);
    }

    // 返回池的大小
    int size() {
        return _size;
    }

    // 清空池
    void clear() {
        resize(0);
        _buf = null;
        _size = 0;
        _capacity = 0;
    }

    /**
     * 添加元素
     * @param value 元素值
     */
    void add(int value) {
        // 如果当前容量和存储元素一致，则进行容量扩充
        if (_size == _capacity) {

            resizeBuf(_size + 1);
        }
        _buf[_size++] = value;
    }
    
    void deleteLast() {
        --_size;
    }

    /**
     * 重置容量
     * @param size 目标容量
     */
    void resize(int size) {
        if (size > _capacity) {
            resizeBuf(size);
        }
        _size = size;
    }

    /**
     * 重置容量并指定为特定值
     * @param size 新容量
     * @param value 新增部分的值
     */
    void resize(int size, int value) {
        if (size > _capacity) {
            resizeBuf(size);
        }
        while (_size < size) {
            _buf[_size++] = value;
        }
    }
    
    void reserve(int size) {
        if (size > _capacity) {
            resizeBuf(size);
        }
    }

    /**
     * 池的扩充函数
     * @param size 目标容量
     */
    private void resizeBuf(int size) {
        // 存储目标容量
        int capacity;
        // 如果预期的大小大于当前容量的两倍
        // 扩充后的容量就是预期大小
        if (size >= _capacity * 2) {
            capacity = size;
        } else {
            // 否则按2的倍数相乘，找到离2的倍数最近的一个数作为目标容量
            capacity = 1;
            while (capacity < size) {
                capacity <<= 1;
            }
        }
        // 创建新数组
        int[] buf = new int[capacity];
        // 进行数组扩充
        if (_size > 0) {
            System.arraycopy(_buf, 0, buf, 0, _size);
        }
        _buf = buf;
        _capacity = capacity;
    }
    

}
