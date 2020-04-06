package cn.cq.mqtt.emq;

import cn.cq.mqtt.util.EmqUtil;
import cn.cq.mqtt.util.Mqtt;
import org.eclipse.paho.client.mqttv3.*;

/***
 * emq下订阅示例
 * @author cq
 *
 */

public class EmqTlsSub {
    /**
     * 定义emq连接参数，注意使用ssl加密连接
     */
    public static String host = "ssl://172.16.42.45:8883";
    /**
     * 订阅对应主题
     */
    public static String topic = "time";
    /**
     * 客户端ID，保证唯一
     */
    private static String clientId = "1";
    /**
     * emq连接账号密码
     */
    private static String userName = "admin";
    private static String passWord = "password";

    public static void main(String[] args) throws MqttException {
        //模拟平台调用，传入mqtt相关参数
        Mqtt mqtt = new Mqtt();
        mqtt.setHost(host);
        mqtt.setTopic(topic);
        mqtt.setClienId(clientId);
        mqtt.setUserName(userName);
        mqtt.setPassWord(passWord);
        //调用EmqUtil中的对应方法
        EmqUtil.subsreibe(mqtt);
    }
}
