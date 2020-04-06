package cn.cq.mqtt.emq;

import cn.cq.mqtt.util.EmqUtil;
import cn.cq.mqtt.util.Mqtt;
import org.eclipse.paho.client.mqttv3.*;

/***
 * emq下发布
 * @author cq
 *
 */

public class EmqTlsPub {
    /**
     * 地址端口号
     */
	public static String host = "ssl://172.16.42.45:8883";
    /**
     * 发布主题
     */
	public static String topic = "time";
    /**
     * 客户端ID
     */
	public static String clientId= "publish";
    /**
     * mqtt服务器用户名密码
     */
	public static String userName = "admin";
	public static String passWord = "password";

	public static void main(String[] args) throws MqttException {
		Mqtt mqtt=new Mqtt();
		mqtt.setHost(host);
		mqtt.setTopic(topic);
		mqtt.setClienId(clientId);
		mqtt.setUserName(userName);
		mqtt.setPassWord(passWord);
		mqtt.setMessage("发送");
		EmqUtil.publish(mqtt);
	}
}
