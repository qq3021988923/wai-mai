package com.yang.service;

import com.yang.utils.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RabbitMQ生产者
 * 负责发送消息到队列
 */
@Component
@RequiredArgsConstructor
@Slf4j //发消息的工具：谁要发消息，调用它就行
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private WebSocketServer webSocketServer;



    /**
     * 发送学生操作通知消息
     * 场景：新增/修改学生后，异步通知WebSocket客户端
     *
     * @param studentId 学生ID
     * @param operation 操作类型（add/update）
     */
    public void sendStudentNotify(Integer studentId, String operation) {
        log.info("【RabbitMQ】发送学生通知消息：studentId={}, operation={}", studentId, operation);
        
        // 构造消息内容（可以用JSON，这里简单用字符串拼接）
        String message = studentId + ":" + operation;

        // 发送消息到交换机
        // 参数1: 交换机名称
        // 参数2: 路由键（决定消息去哪个队列）
        // 参数3: 消息内容
        rabbitTemplate.convertAndSend(
                "student.exchange",   // 交换机
                "student.add",       // 路由键 → 匹配 student.notify.queue 队列
                message              // 消息内容
        );

        log.info("【RabbitMQ】消息发送成功！消息内容：{}", message);
    }

    /**
     * 发送延迟消息
     * 场景：30秒后检查某个操作是否完成（演示延迟队列用法）
     *
     * @param studentId 学生ID
     */
    public void sendDelayMessage(Integer studentId) {
        log.info("【RabbitMQ】发送延迟消息（30秒后触发）：studentId={}", studentId);

        webSocketServer.sendToAll("我是延迟方法30秒后触发再触发一次\n" +
         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 发送到延迟队列（TTL=30秒）
        rabbitTemplate.convertAndSend(
                "student.exchange",
                "student.delay",      // 路由键 匹配 → 进入 student.delay.queue 延迟队列
                "timeout:" + studentId
        );

        log.info("【RabbitMQ】延迟消息已发送，30秒后将自动触发超时处理");
    }
}
