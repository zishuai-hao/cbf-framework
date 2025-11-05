package com.company.cbf.starter.data.service.forward;

import com.company.cbf.starter.data.config.ForwardMqttProperties;
import com.company.cbf.starter.data.entity.MqttData;
import com.company.cbf.starter.data.entity.MqttPubProtocol;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * AsyncBufferPushAdapter 功能验证测试类
 */
@Disabled
@ExtendWith(MockitoExtension.class)
class AsyncBufferPushAdapterTest {

    @Mock
    private AsyncPushService asyncPushService;
    @Mock
    private ForwardMqttProperties config;
    @Mock
    private Vertx vertx; // 模拟 Vert.x 环境

    @InjectMocks
    private AsyncBufferPushAdapter adapter; // 待测试的类

    @Captor
    private ArgumentCaptor<MqttPubProtocol> protocolCaptor;
    @Captor
    private ArgumentCaptor<Handler<Long>> timerHandlerCaptor;
    @Captor
    private ArgumentCaptor<Handler<Void>> runOnContextHandlerCaptor;


    // --- 辅助方法 ---

    private MqttData createMqttData(String deviceId, String type, int count) {
        MqttData data = new MqttData();
        data.setDeviceId(deviceId);
        data.setDeviceType(type);
        // 创建 N 个数据点，模拟一次 push 包含 N 个数据
        data.setValue(Collections.singletonList(
               List.of(System.currentTimeMillis() + "", String.valueOf(count))
        ));
        return data;
    }
    
    // 模拟批量数据
    private MqttData createMqttDataBatch(String deviceId, String type, int dataPointCount) {
        MqttData data = new MqttData();
        data.setDeviceId(deviceId);
        data.setDeviceType(type);
        
        List<List<String>> values = new java.util.ArrayList<>();
        for (int i = 0; i < dataPointCount; i++) {
            values.add(List.of(System.currentTimeMillis() + "_" + i, String.valueOf(i)));
        }
        data.setValue(values);
        return data;
    }

    // --- 初始化设置 ---

    @BeforeEach
    void setUp() {
        // 模拟配置
        when(config.isEnable()).thenReturn(true);
        when(config.getSendIntervalMs()).thenReturn(1000L); // 间隔设置为 1000ms
        when(config.getDataTag()).thenReturn("TEST_TAG");
        
        // 模拟 Vert.x 的 runOnContext 行为，直接执行 Handler
        doAnswer(invocation -> {
            Handler<Void> handler = invocation.getArgument(0);
            handler.handle(null);
            return null;
        }).when(vertx).runOnContext(any());
        
        // 初始化被测试类，并启动定时器捕获
        adapter.init();
        
        // 捕获定时器 Handler
        verify(vertx).setPeriodic(anyLong(), timerHandlerCaptor.capture());
    }

    // --- 测试用例 ---

    /**
     * 验证数据量达到阈值时是否立即发送
     */
    @Test
    void testPush_SizeThreshold_ShouldFlushImmediately() {
        // 1. 模拟数据，总计 5 条
        MqttData d1 = createMqttData("D001", "T1", 1); // 1条
        MqttData d2 = createMqttDataBatch("D001", "T1", 2); // 2条
        MqttData d3 = createMqttDataBatch("D001", "T1", 2); // 2条，总计 5 条，达到阈值

        // 2. 推送数据
        adapter.push(d1);
        adapter.push(d2);
        adapter.push(d3);

        // 3. 验证 AsyncPushService 是否被调用了一次
        verify(asyncPushService, times(1)).push(protocolCaptor.capture());

        // 4. 验证发送的数据内容
        MqttPubProtocol protocol = protocolCaptor.getValue();
        assertEquals("TEST_TAG", protocol.getDataTag());
        MqttData sentData = protocol.getMqttDataList().get(0);
        assertEquals("D001", sentData.getDeviceId());
        assertEquals("T1", sentData.getDeviceType());
        // 验证数据条数是否为 5 (1 + 2 + 2)
        assertEquals(5, sentData.getValue().size());
        
        // 5. 验证第二次推送数据时，缓存已被清空 (不立即发送)
        adapter.push(createMqttData("D001", "T1", 1));
        verify(asyncPushService, times(1)).push(any()); // 依然是 1 次
    }
    
    /**
     * 验证定时器发送功能
     */
    @Test
    void testScheduledSend_TimeThreshold_ShouldFlush() {
        // 1. 模拟数据，总计 3 条 (未达阈值 5)
        MqttData d1 = createMqttDataBatch("D002", "T2", 3);
        adapter.push(d1);

        // 2. 验证未立即发送
        verify(asyncPushService, never()).push(any());

        // 3. 模拟定时器触发
        Handler<Long> timerHandler = timerHandlerCaptor.getValue();
        
        // 4. 执行定时器 Handler (模拟时间到达)
        timerHandler.handle(1L); // 触发定时器

        // 5. 验证 AsyncPushService 是否被调用了一次
        verify(asyncPushService, times(1)).push(protocolCaptor.capture());
        
        // 6. 验证发送的数据条数
        MqttPubProtocol protocol = protocolCaptor.getValue();
        assertEquals(3, protocol.getMqttDataList().get(0).getValue().size());
        
        // 7. 再次触发定时器，验证没有数据时不再发送
        timerHandler.handle(2L);
        verify(asyncPushService, times(1)).push(any()); // 依然是 1 次
    }

    /**
     * 验证强制发送功能
     */
    @Test
    void testFlush_ShouldSendRemainingData() {
        // 1. 模拟数据，总计 4 条 (未达阈值 5)
        MqttData d1 = createMqttDataBatch("D003", "T3", 4);
        adapter.push(d1);

        // 2. 验证未立即发送
        verify(asyncPushService, never()).push(any());

        // 3. 强制发送
        adapter.flush("D003");

        // 4. 验证发送
        verify(asyncPushService, times(1)).push(protocolCaptor.capture());
        MqttPubProtocol protocol = protocolCaptor.getValue();
        assertEquals(4, protocol.getMqttDataList().get(0).getValue().size());
    }

    /**
     * 验证发送失败时，数据是否被重新放回缓存
     */
    @Test
    void testSendAndClearBuffer_Failure_ShouldRestoreCache() throws Exception {
        // 1. 模拟数据，总计 5 条 (达到阈值)
        MqttData d1 = createMqttDataBatch("D004", "T4", 5);
        
        // 2. 模拟 AsyncPushService 抛出异常
        doThrow(new RuntimeException("Simulated Network Error"))
                .doNothing() // 第二次调用时成功
                .when(asyncPushService).push(any());

        // 3. 推送数据，第一次触发发送（失败）
        adapter.push(d1); 
        
        // 4. 验证 AsyncPushService 被调用了 1 次 (失败)
        verify(asyncPushService, times(1)).push(any());
        
        // 5. 模拟定时器触发（此时数据应已被放回缓存）
        Handler<Long> timerHandler = timerHandlerCaptor.getValue();
        timerHandler.handle(1L);

        // 6. 验证 AsyncPushService 被调用了 2 次 (第二次发送成功)
        verify(asyncPushService, times(2)).push(protocolCaptor.capture());
        
        // 7. 验证第二次发送的数据条数仍然是 5 条 (数据被恢复)
        MqttPubProtocol protocol = protocolCaptor.getValue();
        assertEquals(5, protocol.getMqttDataList().get(0).getValue().size());
    }
    
    /**
     * 验证多线程并发推送的安全性
     * 10 个线程，每个线程推送 1 条数据，总计 10 条，阈值是 5。
     * 最终应该只调用两次 push，并且第一次是 5 条，第二次也是 5 条。
     */
    @Test
    void testPush_ConcurrentSafety() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        Executors.newFixedThreadPool(10).execute(() -> {
            for (int i = 0; i < threadCount; i++) {
                adapter.push(createMqttData("CONCUR-01", "TC", 1));
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "所有线程应在规定时间内完成");
        
        // 第一次发送：前 5 条达到阈值，通过 runOnContext 立即发送
        verify(asyncPushService, times(1)).push(protocolCaptor.capture());
        
        // 第二次发送：剩余 5 条 (未达阈值)，等待定时器发送
        Handler<Long> timerHandler = timerHandlerCaptor.getValue();
        timerHandler.handle(1L);
        
        // 验证总共发送了 2 次
        verify(asyncPushService, times(2)).push(protocolCaptor.capture());

        // 验证两次发送的数据总量是 10 条
        List<MqttPubProtocol> allProtocols = protocolCaptor.getAllValues();
        int totalSent = allProtocols.stream()
                .mapToInt(p -> p.getMqttDataList().get(0).getValue().size())
                .sum();
        
        assertEquals(10, totalSent, "并发场景下，所有数据都应被发送");
    }
}