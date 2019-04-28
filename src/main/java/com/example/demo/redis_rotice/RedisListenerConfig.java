package com.example.demo.redis_rotice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @Author: czw
 * @Date: 2019/4/28 09:53
 */
@Configuration
public class RedisListenerConfig {


    /**
     *开启redis key过期提醒
     * 修改redis相关事件配置。找到redis配置文件redis.conf，查看“notify-keyspace-events”的配置项，如果没有，添加“notify-keyspace-events Ex”，如果有值，添加Ex，相关参数说明如下：
     * K：keyspace事件，事件以__keyspace@<db>__为前缀进行发布；
     * E：keyevent事件，事件以__keyevent@<db>__为前缀进行发布；
     * g：一般性的，非特定类型的命令，比如del，expire，rename等；
     * $：字符串特定命令；
     * l：列表特定命令；
     * s：集合特定命令；
     * h：哈希特定命令；
     * z：有序集合特定命令；
     * x：过期事件，当某个键过期并删除时会产生该事件；
     * e：驱逐事件，当某个键因maxmemore策略而被删除时，产生该事件；
     * A：g$lshzxe的别名，因此”AKE”意味着所有事件。
     */

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
