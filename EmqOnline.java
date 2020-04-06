package cn.cq.mqtt.emq;

import cn.cq.mqtt.util.EmqUtil;
import cn.cq.mqtt.util.Mqtt;
import org.eclipse.paho.client.mqttv3.MqttException;


/***
 * emq下设备上下线状态订示例
 * @author cq
 */

public class EmqOnline {
    /**
     * 定义emq连接参数，注意使用ssl加密连接
     */
    public static String host = "ssl://172.16.42.45:8883";
    /**
     * 订阅客户端上下线$SYS系统主题
     */
    public static String topic = "$SYS/brokers/+/clients/#";
    /**
     * 客户端ID，保证唯一
     */
    public static String clientId = "online123";
    /**
     * emq连接账号密码
     */
    public static String userName = "admin";
    public static String passWord = "password";

    public static void main(String[] args) throws MqttException {
        //模拟平台调用，传入mqtt相关参数
        Mqtt mqtt = new Mqtt();
        mqtt.setHost(host);
        mqtt.setTopic(topic);
        mqtt.setClienId(clientId);
        mqtt.setUserName(userName);
        mqtt.setPassWord(passWord);
        //调用EmqUtil中的对应方法
        EmqUtil.subsreibeOnline(mqtt);
    }
}
