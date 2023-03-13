package com.harvest.core.service.utils;

import com.google.common.collect.Lists;
import com.harvest.core.log.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Alodi
 * @Date: 2023/3/13 3:27 PM
 * @Description: TODO
 **/
public class BizLogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizLogUtils.class);

    private final static ThreadLocal<List<OperationLog>> LOG_HOLDER = new ThreadLocal<>();

    public static <T extends OperationLog> void log(T log) {
        if (Objects.isNull(log)) {
            return;
        }
        if (LOG_HOLDER.get() == null) {
            LOG_HOLDER.set(Lists.newArrayList());
        }
        LOG_HOLDER.get().add(log);
    }

    public static List<OperationLog> get() {
        return LOG_HOLDER.get();
    }

    public static <T extends OperationLog> void clear() {
        LOG_HOLDER.remove();
    }

}
