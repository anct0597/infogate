package vn.infogate.ispider.extension.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vn.infogate.ispider.common.objectmapper.ObjectMapperFactory;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.Task;
import vn.infogate.ispider.core.scheduler.DuplicateRemovedScheduler;
import vn.infogate.ispider.core.scheduler.MonitoredScheduler;
import vn.infogate.ispider.core.scheduler.component.DuplicateRemover;
import vn.infogate.ispider.core.utils.JsonUtils;

/**
 * Use Redis as url scheduler for distributed crawlers.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class RedisScheduler extends DuplicateRemovedScheduler implements MonitoredScheduler, DuplicateRemover {

    protected JedisPool pool;

    private static final String QUEUE_PREFIX = "queue_";

    private static final String SET_PREFIX = "set_";

    private static final String ITEM_PREFIX = "item_";

    private final ObjectMapper mapper = ObjectMapperFactory.getInstance();

    public RedisScheduler(String host) {
        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisScheduler(JedisPool pool) {
        this.pool = pool;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(getSetKey(task));
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.sadd(getSetKey(task), request.getUrl()) == 0;
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        try (Jedis jedis = pool.getResource()) {
            jedis.rpush(getQueueKey(task), request.getUrl());
            if (checkForAdditionalInfo(request)) {
                String field = DigestUtils.sha1Hex(request.getUrl());
                String value = JsonUtils.toJson(mapper, request);
                jedis.hset((ITEM_PREFIX + task.getUUID()), field, value);
            }
        }
    }

    private boolean checkForAdditionalInfo(Request request) {
        if (request == null) {
            return false;
        }

        if (!request.getHeaders().isEmpty() || !request.getCookies().isEmpty()) {
            return true;
        }

        if (StringUtils.isNotBlank(request.getCharset()) || StringUtils.isNotBlank(request.getMethod())) {
            return true;
        }

        if (request.isBinaryContent() || request.getRequestBody() != null) {
            return true;
        }

        if (request.getExtras() != null && !request.getExtras().isEmpty()) {
            return true;
        }
        return request.getPriority() != 0L;
    }

    @Override
    public synchronized Request poll(Task task) {
        try (Jedis jedis = pool.getResource()) {
            String url = jedis.lpop(getQueueKey(task));
            if (url == null) return null;

            String key = ITEM_PREFIX + task.getUUID();
            String field = DigestUtils.sha1Hex(url);
            byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
            if (bytes != null) {
                return mapper.convertValue(new String(bytes), Request.class);
            }
            return new Request(url);
        }
    }

    protected String getSetKey(Task task) {
        return SET_PREFIX + task.getUUID();
    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getItemKey(Task task) {
        return ITEM_PREFIX + task.getUUID();
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        try (Jedis jedis = pool.getResource()) {
            Long size = jedis.llen(getQueueKey(task));
            return size.intValue();
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        try (Jedis jedis = pool.getResource()) {
            Long size = jedis.scard(getSetKey(task));
            return size.intValue();
        }
    }
}
