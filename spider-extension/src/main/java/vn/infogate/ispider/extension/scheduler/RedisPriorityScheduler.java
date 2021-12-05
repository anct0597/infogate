package vn.infogate.ispider.extension.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Task;
import vn.infogate.ispider.core.utils.JsonUtils;

import java.util.Set;

public class RedisPriorityScheduler extends RedisScheduler {

    private static final String ZSET_PREFIX = "zset_";
    private static final String QUEUE_PREFIX = "queue_";
    private static final String NO_PRIORITY_SUFFIX = "_zore";
    private static final String PLUS_PRIORITY_SUFFIX = "_plus";
    private static final String MINUS_PRIORITY_SUFFIX = "_minus";

    private final ObjectMapper mapper = ObjectMapperFactory.getInstance();

    public RedisPriorityScheduler(String host) {
        super(host);
    }

    public RedisPriorityScheduler(JedisPool pool) {
        super(pool);
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        try (Jedis jedis = pool.getResource()) {
            if (request.getPriority() > 0) {
                jedis.zadd(getZsetPlusPriorityKey(task), request.getPriority(), request.getUrl());
            } else if (request.getPriority() < 0) {
                jedis.zadd(getZsetMinusPriorityKey(task), request.getPriority(), request.getUrl());
            } else {
                jedis.lpush(getQueueNoPriorityKey(task), request.getUrl());
            }

            setExtrasInItem(jedis, request, task);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        try (Jedis jedis = pool.getResource()) {
            String url = getRequest(jedis, task);
            if (StringUtils.isBlank(url)) {
                return null;
            }
            return getExtrasInItem(jedis, url, task);
        }
    }

    private String getRequest(Jedis jedis, Task task) {
        String url;
        Set<String> urls = jedis.zrevrange(getZsetPlusPriorityKey(task), 0, 0);
        if (urls.isEmpty()) {
            url = jedis.lpop(getQueueNoPriorityKey(task));
            if (StringUtils.isBlank(url)) {
                urls = jedis.zrevrange(getZsetMinusPriorityKey(task), 0, 0);
                if (!urls.isEmpty()) {
                    url = urls.toArray(new String[0])[0];
                    jedis.zrem(getZsetMinusPriorityKey(task), url);
                }
            }
        } else {
            url = urls.toArray(new String[0])[0];
            jedis.zrem(getZsetPlusPriorityKey(task), url);
        }
        return url;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(getSetKey(task));
        }
    }

    private String getZsetPlusPriorityKey(Task task) {
        return ZSET_PREFIX + task.getUUID() + PLUS_PRIORITY_SUFFIX;
    }

    private String getQueueNoPriorityKey(Task task) {
        return QUEUE_PREFIX + task.getUUID() + NO_PRIORITY_SUFFIX;
    }

    private String getZsetMinusPriorityKey(Task task) {
        return ZSET_PREFIX + task.getUUID() + MINUS_PRIORITY_SUFFIX;
    }

    private void setExtrasInItem(Jedis jedis, Request request, Task task) {
        if (request.getExtras() != null) {
            String field = DigestUtils.sha1Hex(request.getUrl());
            String value = JsonUtils.toJson(mapper, field);
            jedis.hset(getItemKey(task), field, value);
        }
    }

    private Request getExtrasInItem(Jedis jedis, String url, Task task) {
        String key = getItemKey(task);
        String field = DigestUtils.sha1Hex(url);
        byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
        if (bytes != null) {
            return mapper.convertValue(new String(bytes), Request.class);
        }
        return new Request(url);
    }
}
