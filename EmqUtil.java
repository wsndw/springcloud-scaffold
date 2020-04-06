package cn.cq.mqtt.util;


import net.sf.json.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;



/***
 * mqtt相关方法
 * @author cq
 */
public class EmqUtil {
    /**
     * mqtt发布方法
     *
     * @param mqtt mqtt相关参数，用户名、密码、连接地址、发布主题、客户端id
     * @throws MqttException
     */
    public static void publish(Mqtt mqtt) throws MqttException {
        //创建MQTT客户端连接
        MqttClient client = new MqttClient(mqtt.getHost(), mqtt.getClienId(), new MemoryPersistence());
        //设置连接参数
        MqttConnectOptions options = new MqttConnectOptions();
        //是否保存离线消息
        //false保存离线消息，下次上线可以接收离线时接收到的消息；
        //true不保存离线消息，下次上线不接收离线时接收到的消息；
        options.setCleanSession(true);
        //设置用户名密码
        options.setUserName(mqtt.getUserName());
        options.setPassword(mqtt.getPassWord().toCharArray());
        // 设置连接超时时间10s
        options.setConnectionTimeout(10);
        // 设置会话心跳时间20s
        options.setKeepAliveInterval(20);
        //读取ssl加密连接需要的ca证书、客户端证书、客户端秘钥
        try {
            options.setSocketFactory(
                    SslUtil.getSocketFactory(mqtt.getCaFilePath(), mqtt.getClientCrtFilePath(),
                            mqtt.getClientKeyFilePath(), ""));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            System.out.println("创建TLS连接工厂失败");
            e1.printStackTrace();
        }
        //客户端相关回调方法
        client.setCallback(new MqttCallback() {
            //消息到达后回调
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("接收到信息");
                System.out.println(message);
            }
            //消息到达后回调
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //消息传递成功token.getMessage()会返回null
                //消息正在传送中token.getMessage()会返回正在传递的消息
                try {
                    if (token.getMessage()==null){
                        System.out.println("消息发送成功");
                    }else{
                        System.out.println("消息"+token.getMessage()+"正在传递");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //连接丢失后回调
            @Override
            public void connectionLost(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("连接断开");
            }
        });
        //建立连接
        client.connect(options);
        //获取主题
        MqttTopic topic = client.getTopic(mqtt.getTopic());
        //创建发送的消息对象
        MqttMessage message = new MqttMessage();
        //设置qos质量等级
        message.setQos(2);
        //setRetained设置保留消息
        //false不保留消息，发布一个主题后，只有当前有订阅者存在的情况下才接收的到消息
        //true保留消息，发布一个主题后，在发送给当前订阅者后，还会存到服务器，如果有
        // 新的订阅者上线也会把该消息发给新的订阅者
        message.setRetained(false);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", mqtt.getMessage());
        message.setPayload(jsonObject.toString().getBytes());
        try {
            topic.publish(message);
        } catch (Exception e) {
            System.out.println("发布失败");
            e.printStackTrace();
        }
        System.out.println("发布成功");
    }

    /**
     * mqtt订阅上下线信息
     *
     * @param mqtt mqtt相关参数，用户名、密码、连接地址、订阅主题、客户端id
     * @throws MqttException
     */
    public static void subsreibeOnline(Mqtt mqtt) throws MqttException {
        //创建mqtt客户端
        MqttClient client = new MqttClient(mqtt.getHost(), mqtt.getClienId(), new MemoryPersistence());
        //设置连接参数
        MqttConnectOptions options = new MqttConnectOptions();
        //是否保存离线消息
        //false保存离线消息，下次上线可以接收离线时接收到的消息；
        //true不保存离线消息，下次上线不接收离线时接收到的消息；
        options.setCleanSession(false);
        //连接使用的用户名密码
        options.setUserName(mqtt.getUserName());
        options.setPassword(mqtt.getPassWord().toCharArray());
        //连接超时时间10s
        options.setConnectionTimeout(10);
        //心跳间隔20s
        options.setKeepAliveInterval(20);
        //自动重连
        options.setAutomaticReconnect(true);
        //读取ssl加密连接需要的ca证书、客户端证书、客户端秘钥
        try {
            options.setSocketFactory(
                    SslUtil.getSocketFactory(mqtt.getCaFilePath(), mqtt.getClientCrtFilePath(),
                            mqtt.getClientKeyFilePath(), ""));
        } catch (Exception e) {
            System.out.println("获取证书失败");
            e.printStackTrace();
        }
        //客户端相关回调方法
        client.setCallback(new MqttCallbackExtended() {
            //连接丢失后触发，可以断线重连
            @Override
            public void connectionLost(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("连接丢失");
                System.out.println("断线重连被触发");
            }

            //消息到达后触发，回显消息或插入数据库
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                /**
                 * 消费消息的回调接口，需要确保该接口不抛异常，该接口运行返回即代表消息消费成功。
                 * 消费消息需要保证在规定时间内完成，如果消费耗时超过服务端约定的超时时间，
                 * 对于可靠传输的模式，服务端可能会重试推送。
                 */
                System.out.println("接收到上下线状态信息");
                System.out.println(s);

                //使用线程的方法对数据库读写，避免因回调函数时间过长触发mqtt服务器抛弃连接
                /*CompletableFuture.supplyAsync(() -> {
                    System.out.println("online begin");
                    try {
                        //截取订阅接收到的主题字符串后面的两个部分，一个是客户端id，一个是在线状态
                        String str1=s.substring(0, s.indexOf("clients/"));
                        String str2=s.substring(str1.length()+8, s.length());
                        String clientId=str2.substring(0, str2.indexOf("/"));
                        String state=str2.substring(clientId.length()+1, str2.length());
                        HardWareState hardWareState=new HardWareState();
                        //不对自己本身的上下线状态进行入库
                        if (!clientId.equals("online")&&!clientId.equals("publish")
                                &&!clientId.equals("subscribe")&&!clientId.equals("pubId")
                                &&!clientId.equals("subId")){
                            hardWareState.setId(Long.parseLong(clientId));
                            hardWareState.setState(state);
                            hardWareStateMapper.updateonline(hardWareState);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("online end");
                    return true;
                });*/
            }

            //消息到达后回调
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //消息传递成功token.getMessage()会返回null
                //消息正在传送中token.getMessage()会返回正在传递的消息
                try {
                    if (token.getMessage()==null){
                        System.out.println("消息发送成功");
                    }else{
                        System.out.println("消息"+token.getMessage()+"正在传递");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //连接建立成功后回调，可以在这里进行订阅
            @Override
            public void connectComplete(boolean reconnect, String serverUri) {
                try {
                    System.out.println("连接已建立");
                    client.subscribe(mqtt.getTopic(), 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        client.connect(options);
    }

    /**
     * mqtt订阅
     *
     * @param mqtt mqtt相关参数，用户名、密码、连接地址、订阅主题、客户端id
     * @throws MqttException
     */
    public static void subsreibe(Mqtt mqtt) throws MqttException {
        //创建mqtt客户端
        MqttClient client = new MqttClient(mqtt.getHost(), mqtt.getClienId(), new MemoryPersistence());
        //设置连接参数
        MqttConnectOptions options = new MqttConnectOptions();
        //是否保存离线消息
        //false保存离线消息，下次上线可以接收离线时接收到的消息；
        //true不保存离线消息，下次上线不接收离线时接收到的消息；
        options.setCleanSession(false);
        //设置用户名密码
        options.setUserName(mqtt.getUserName());
        options.setPassword(mqtt.getPassWord().toCharArray());
        //连接超时时间
        options.setConnectionTimeout(10);
        //心跳间隔
        options.setKeepAliveInterval(20);
        //读取ssl加密连接需要的ca证书、客户端证书、客户端秘钥
        try {
            options.setSocketFactory(
                    SslUtil.getSocketFactory(mqtt.getCaFilePath(), mqtt.getClientCrtFilePath(),
                            mqtt.getClientKeyFilePath(), ""));
        } catch (Exception e) {
            System.out.println("读取证书失败");
            e.printStackTrace();
        }
        //客户端相关回调方法
        client.setCallback(new MqttCallbackExtended() {
            //连接丢失后触发，可以断线重连
            @Override
            public void connectionLost(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("连接断开");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                /**
                 * 消费消息的回调接口，需要确保该接口不抛异常，该接口运行返回即代表消息消费成功。
                 * 消费消息需要保证在规定时间内完成，如果消费耗时超过服务端约定的超时时间，对于可靠传输的模式，服务端可能会重试推送，业务需要做好幂等去重处理。超时时间约定参考限制
                 */
                System.out.println("接收到信息");
                System.out.println(mqttMessage);
                //使用线程的方法对数据库读写，避免因回调函数时间过长触发mqtt服务器抛弃连接
                /*String string1=mqttMessage.toString();
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("insert begin");
                    hardWare1= JSON.parseObject(string1, new TypeReference<HardWare>() {});
                    try {
                        hardWareMapper.insert(hardWare1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("insert end");
                    return true;
                });*/
            }

            //消息到达后回调
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //消息传递成功token.getMessage()会返回null
                //消息正在传送中token.getMessage()会返回正在传递的消息
                try {
                    if (token.getMessage()==null){
                        System.out.println("消息发送成功");
                    }else{
                        System.out.println("消息"+token.getMessage()+"正在传递");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //连接建立后回调
            @Override
            public void connectComplete(boolean reconnect, String serverUri) {
                System.out.println("连接建立成功");
            }
        });
        client.connect(options);
        client.subscribe(mqtt.getTopic(), 2);
    }
}
