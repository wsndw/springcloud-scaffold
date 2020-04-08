package cn.cq.dao;

import lombok.Data;

/***
 * SkyWalking告警消息JSON数据
 * 基于List<org.apache.skywalking.oap.server
 * .core.alarm.AlarmMessage进行序列化
 * @author CQ
 */
@Data
public class SkyWalking {
    private Integer scopeId;
    private String scope;
    private String name;
    private Integer id0;
    private Integer id1;
    private String ruleName;
    private String alarmMessage;
    private Long startTime;
}
