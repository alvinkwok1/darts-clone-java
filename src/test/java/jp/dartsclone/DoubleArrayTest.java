/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.dartsclone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author manabe
 */
public class DoubleArrayTest {
    // 有效的key数量-65536
    private static final int NUM_VALID_KEYS = 1 << 16;
    // 无效的key的数量- 131072
    private static final int NUM_INVALID_KEYS = 1 << 17;
    // 最大结果集数量
    private static final int MAX_NUM_RESULTS = 6;
    
    public DoubleArrayTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testMain() {
        // 数据准备
        SortedSet<byte[]> validKeys = generateValidKeys(NUM_VALID_KEYS);
        Set<byte[]> invalidKeys = generateInvalidKeys(NUM_INVALID_KEYS, validKeys);
        // 执行测试
        testDarts(validKeys, invalidKeys);
    }
    
    private void testDarts(SortedSet<byte[]> validKeys, Set<byte[]> invalidKeys) {
        byte[][] byteKeys = validKeys.toArray(new byte[0][0]);
        byte[][] byteInvalidKeys = invalidKeys.toArray(new byte[0][0]);
                
        int[] values = new int[byteKeys.length];
        // 为每一个key分配一个ID
        int keyId = 0;
        for (int i = 0; i < values.length; ++i) {
            values[i] = keyId;
            ++keyId;
        }
        // 初始化tire
        DoubleArray dict = new DoubleArray();
        // 传入所有的有效key进行构建
        dict.build(byteKeys, null);
        testDict(dict, byteKeys, values, byteInvalidKeys);
        
        dict.build(byteKeys, values);
        testDict(dict, byteKeys, values, byteInvalidKeys);

        Random random = new Random();
        random.setSeed(0);
        for (int i = 0; i < values.length; ++i) {
            values[i] = random.nextInt(10);
        }
        dict.build(byteKeys, values);
        testDict(dict, byteKeys, values, byteInvalidKeys);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            dict.save(out);
        } catch (IOException e) {
            fail();
        }
        DoubleArray dictCopy = new DoubleArray();
        try {
            dictCopy.open(new ByteArrayInputStream(out.toByteArray()));
        } catch (IOException e) {
            fail();
        }
        assertEquals(dict.size(), dictCopy.size());
        testDict(dictCopy, byteKeys, values, byteInvalidKeys);
        
        testCommonPrefixSearch(dict, byteKeys, values);
    }
    
    private void testDict(DoubleArray dict,
    byte[][] keys, int[] values, byte[][] invalidKeys) {
        for (int i = 0; i < keys.length; ++i) {
            assertEquals(values[i],
                    dict.exactMatchSearch(keys[i]));
        }
        for (byte[] invalidKey : invalidKeys) {
            assertEquals(dict.exactMatchSearch(invalidKey), -1);
        }
    }

    private void testCommonPrefixSearch(DoubleArray dict,
            byte[][] keys, int[] values) {
        for (int i = 0; i < keys.length; ++i) {
            List<Pair<Integer, Integer>> results = dict.commonPrefixSearch(
                    keys[i], 0, MAX_NUM_RESULTS);
            
            assertTrue(results.size() >= 1);
            assertTrue(results.size() < 10);
            
            assertEquals(keys[i].length, results.get(results.size() - 1).first.intValue());
            assertEquals(values[i], results.get(results.size() - 1).second.intValue());
        }
    }
    
    private SortedSet<byte[]> generateValidKeys(int numKeys) {
        // 创建一个排序集合，并实现排序逻辑
        // 逐字进行比较，如果不等且a-b>0，则要发生交换，即a与b位置进行交换，得到的结果是一个升序结果
        SortedSet<byte[]> validKeys = new TreeSet<>((left, right) -> {
            for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
                int a = (left[i] & 0xff);
                int b = (right[j] & 0xff);
                if (a != b) {
                    return a - b;
                }
            }
            return left.length - right.length;
        });
        // 创建一个随机数并制定随机种子
        Random random = new Random();
        random.setSeed(1);
        // 用来存储key
        StringBuilder keyBuilder = new StringBuilder();
        while (validKeys.size() < numKeys) {
            keyBuilder.setLength(0);
            int length = random.nextInt(8) + 1;
            // 生成一个随机key
            for (int i = 0; i < length; ++i) {
                keyBuilder.append((char)('A' + random.nextInt(26)));
            }
            // 使用utf8编码存储到key集合中
            validKeys.add(keyBuilder.toString().getBytes(StandardCharsets.UTF_8));
        }
        return validKeys;
    }
    
    private Set<byte[]> generateInvalidKeys(int numKeys, Set<byte[]> validKeys) {
        Set<byte[]> invalidKeys = new HashSet<>();
        Random random = new Random();
        StringBuilder keyBuilder = new StringBuilder();
        while (invalidKeys.size() < numKeys) {
            keyBuilder.setLength(0);
            int length = random.nextInt(8) + 1;
            // 创建无效key
            for (int i = 0; i < length; ++i) {
                keyBuilder.append((char)('A' + random.nextInt(26)));
            }
            // 无效key应该是不包含在有效key里面的
            byte[] generatedKey;
            generatedKey = keyBuilder.toString().getBytes(StandardCharsets.UTF_8);
            if (!validKeys.contains(generatedKey)) {
                invalidKeys.add(generatedKey);
            }
        }
        return invalidKeys;
    }
}
