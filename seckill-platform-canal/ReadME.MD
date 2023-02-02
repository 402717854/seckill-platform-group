mq相关参数说明
# tcp, kafka, RocketMQ
canal.serverMode = RocketMQ
参数名	参数说明	默认值
canal.mq.servers	kafka为bootstrap.servers rocketMQ中为nameserver列表	127.0.0.1:6667
canal.mq.retries	发送失败重试次数	0
canal.mq.batchSize	kafka为ProducerConfig.BATCH_SIZE_CONFIG rocketMQ无意义	16384
canal.mq.maxRequestSize	kafka为ProducerConfig.MAX_REQUEST_SIZE_CONFIG rocketMQ无意义	1048576
canal.mq.lingerMs	kafka为ProducerConfig.LINGER_MS_CONFIG , 如果是flatMessage格式建议将该值调大, 如: 200 rocketMQ无意义	1
canal.mq.bufferMemory	kafka为ProducerConfig.BUFFER_MEMORY_CONFIG rocketMQ无意义	33554432
canal.mq.producerGroup	kafka无意义 rocketMQ为ProducerGroup名	Canal-Producer
canal.mq.canalBatchSize	获取canal数据的批次大小	50
canal.mq.canalGetTimeout	获取canal数据的超时时间	100
canal.mq.flatMessage	是否为json格式 如果设置为false,对应MQ收到的消息为protobuf格式 需要通过CanalMessageDeserializer进行解码	true
canal.mq.transaction	kafka消息投递是否使用事务, 主要针对flatMessage的异步发送和动态多topic消息投递进行事务控制来保持和canal binlog position的一致性, flatMessage模式下建议开启(需要kafka版本支持)。如果设置为false, flatMessage消息将会采用逐条同步的方式投递, 可能会产生消息丢失或者重复投递 rocketMQ无意义	false
—	—	—
canal.mq.topic	mq里的topic名 	无
canal.mq.dynamicTopic	mq里的动态topic规则, 1.1.3版本支持	无
canal.mq.partition	单队列模式的分区下标，	1
canal.mq.partitionsNum	散列模式的分区数	无
canal.mq.partitionHash	散列规则定义 库名.表名 : 唯一主键，比如mytest.person: id 1.1.3版本支持新语法，见下文	无


客户端实现MQ就OK了

######TCP模式下canal宕机之后重启会导致客户端接收不到变更信息，导致数据丢失，需客户端重启
######mq模式下客户端、canal、mq服务各自宕机不会出现数据丢失现象


TCP模式下订阅表需代码级别设置
CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.66.9", 11111),
"example", "canal", "canal");
//打开连接
connector.connect();
//订阅数据库表,全部表
//            connector.subscribe(".*\\..*");
connector.subscribe("seckill\\_auth\\..*,seckill\\_goods\\..*,seckill\\_order\\..*,seckill\\_sso\\..*,seckill\\_sys\\..*");
//回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
connector.rollback();

mq模式只需配置服务端instance.properties
# table regex白名单
canal.instance.filter.regex=seckill\\_auth\\..*,seckill\\_order\\..*,seckill\\_goods\\..*,seckill\\_sso\\..*,seckill\\_sys\\..*
# table black regex黑名单
canal.instance.filter.black.regex=mysql\\.slave_.*

show master logs;
show binlog events in 'LAPTOP-M8BKTIE5-bin.000066';
show master status;

canal集群依赖zk


####Canal 报错 Could not find first log file name in binary log index file

show master logs;
show binlog events in 'LAPTOP-M8BKTIE5-bin.000066';
show master status;

canal.instance.master.address=mall-mysql:3306                                                                                                                                      
canal.instance.master.journal.name=mysql-binlog.000233                                                                                                                             
canal.instance.master.position=652645                                                                                                                                              
canal.instance.master.timestamp=                                                                                                                                                   
canal.instance.master.gtid=

如果canal启动时候从日志看到报这个错误：can't find start position for example。有如下解决方法：
●单机
删除meta.dat文件，重启canal，问题解决。
●集群
进入canal对应的zookeeper集群下，删除节点/otter/canal/destinations/实例/1001/cursor，重启canal即可恢复（不懂命令可以到zookeeper官网或者百度查找操作命令）。



