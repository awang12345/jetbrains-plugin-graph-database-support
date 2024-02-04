package com.vesoft.jetbrains.plugin.graphdb.database.nebula.client;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.NebulaSession;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.AuthResult;
import com.vesoft.nebula.client.graph.net.SessionState;
import com.vesoft.nebula.client.graph.net.SyncConnection;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionPool implements Serializable {

    private static final long serialVersionUID = 6051248334277617891L;

    private final ScheduledExecutorService healthCheckSchedule =
            Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService sessionQueueMaintainSchedule =
            Executors.newScheduledThreadPool(1);

    public CopyOnWriteArrayList<NebulaSession> sessionList = new CopyOnWriteArrayList<>();
    public AtomicInteger idleSessionSize = new AtomicInteger(0);
    public AtomicBoolean hasInit = new AtomicBoolean(false);
    public AtomicBoolean isClosed = new AtomicBoolean(false);

    private final AtomicInteger pos = new AtomicInteger(0);

    private final SessionPoolConfig sessionPoolConfig;
    private final int minSessionSize;
    private final int maxSessionSize;
    private final int cleanTime;
    private final int healthCheckTime;
    private final int retryTimes;
    private final int intervalTime;
    private final boolean reconnect;
    private final String spaceName;
    private final String useSpace;
    public static final String NULL_SPACE = "__NULL_SPACE__";

    private Map<Long, String> sessionIdSpaceMap = new ConcurrentHashMap<>();

    public SessionPool(SessionPoolConfig poolConfig) {
        this.sessionPoolConfig = poolConfig;
        this.minSessionSize = poolConfig.getMinSessionSize();
        this.maxSessionSize = poolConfig.getMaxSessionSize();
        this.cleanTime = poolConfig.getCleanTime();
        this.retryTimes = poolConfig.getRetryTimes();
        this.intervalTime = poolConfig.getIntervalTime();
        this.reconnect = poolConfig.isReconnect();
        this.healthCheckTime = poolConfig.getHealthCheckTime();
        this.spaceName = StringUtils.defaultIfBlank(poolConfig.getSpaceName(), NULL_SPACE);
        this.useSpace = "USE `" + spaceName + "`;";
        init();
    }


    /**
     * return an idle session
     */
    private synchronized NebulaSession getSession() throws ClientServerIncompatibleException,
            AuthFailedException, IOErrorException, BindSpaceFailedException {
        int retry = sessionPoolConfig.getRetryConnectTimes();
        while (retry-- >= 0) {
            // if there are idle sessions, get session from queue
            if (idleSessionSize.get() > 0) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdleAndSetUsed()) {
                        idleSessionSize.decrementAndGet();
                        return nebulaSession;
                    }
                }
            }
            // if session size is less than max size, get session from pool
            if (sessionList.size() < maxSessionSize) {
                return createSessionObject(SessionState.USED);
            }
            // there's no available session, wait for SessionPoolConfig.getWaitTime and re-get
            try {
                Thread.sleep(sessionPoolConfig.getWaitTime());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // if session size is equal to max size and no idle session here, throw exception
        throw new RuntimeException("no extra session available for :" + getAddress());
    }


    /**
     * init the SessionPool
     * this function is moved into SessionPool's constructor, no need to call it manually.
     */
    @Deprecated
    public boolean init() {
        if (hasInit.get()) {
            return true;
        }

        while (sessionList.size() < minSessionSize) {
            try {
                createSessionObject(SessionState.IDLE);
                idleSessionSize.incrementAndGet();
            } catch (Exception e) {
                throw new RuntimeException("create session failed.", e);
            }
        }
        healthCheckSchedule.scheduleAtFixedRate(this::checkSession, 0, healthCheckTime,
                TimeUnit.SECONDS);
        sessionQueueMaintainSchedule.scheduleAtFixedRate(this::updateSessionQueue, 0, cleanTime,
                TimeUnit.SECONDS);
        hasInit.compareAndSet(false, true);
        return true;
    }


    /**
     * Execute the nGql sentence.
     *
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     * @return The ResultSet
     */
    public ResultSet execute(String stmt) throws IOErrorException,
            ClientServerIncompatibleException, AuthFailedException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = null;
        ResultSet resultSet = null;
        int tryTimes = 0;
        while (tryTimes++ <= retryTimes) {
            try {
                nebulaSession = getSession();
                resultSet = nebulaSession.execute(stmt);
                if (resultSet.isSucceeded()
                        || resultSet.getErrorCode() == ErrorCode.E_SEMANTIC_ERROR.getValue()
                        || resultSet.getErrorCode() == ErrorCode.E_SYNTAX_ERROR.getValue()
                        || resultSet.getErrorCode() == ErrorCode.E_EXECUTION_ERROR.getValue()) {
                    releaseSession(nebulaSession);
                    return resultSet;
                }
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException interruptedException) {
                    // ignore
                }
            } catch (ClientServerIncompatibleException e) {
                // will never get here.
            } catch (AuthFailedException | BindSpaceFailedException e) {
                throw e;
            } catch (IOErrorException e) {
                if (nebulaSession != null) {
                    nebulaSession.release();
                    sessionList.remove(nebulaSession);
                }
                if (tryTimes < retryTimes) {
                    try {
                        Thread.sleep(intervalTime);
                    } catch (InterruptedException interruptedException) {
                        // ignore
                    }
                } else {
                    throw e;
                }
            } finally {
                if (resultSet != null) {
                    if (StringUtils.isBlank(resultSet.getSpaceName())) {
                        //这里防止sql执行异常session清空space
                        String lastSpaceName = sessionIdSpaceMap.get(nebulaSession.getSessionID());
                        if (StringUtils.isNotBlank(lastSpaceName)) {
                            nebulaSession.execute("USE " + lastSpaceName);
                        }
                    } else {
                        sessionIdSpaceMap.put(nebulaSession.getSessionID(), resultSet.getSpaceName());
                    }
                }
            }
        }
        if (nebulaSession != null) {
            nebulaSession.release();
            sessionList.remove(nebulaSession);
        }
        return resultSet;
    }


    /**
     * Execute the nGql sentence with parameter
     *
     * @param stmt         The nGql sentence.
     * @param parameterMap The nGql parameter map
     * @return The ResultSet
     */
    @Deprecated
    public ResultSet execute(String stmt, Map<String, Object> parameterMap)
            throws ClientServerIncompatibleException, AuthFailedException,
            IOErrorException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = getSession();
        ResultSet resultSet;
        try {
            resultSet = nebulaSession.executeWithParameter(stmt, parameterMap);

            // re-execute for session error
            if (isSessionError(resultSet)) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                resultSet = nebulaSession.executeWithParameter(stmt, parameterMap);
            }
        } catch (IOErrorException e) {
            useSpace(nebulaSession, null);
            throw e;
        }

        useSpace(nebulaSession, resultSet);
        return resultSet;
    }


    /**
     * close the session pool
     */
    public void close() {
        if (isClosed.get()) {
            return;
        }

        if (isClosed.compareAndSet(false, true)) {
            for (NebulaSession nebulaSession : sessionList) {
                nebulaSession.release();
            }
            sessionList.clear();
            if (!healthCheckSchedule.isShutdown()) {
                healthCheckSchedule.shutdown();
            }
            if (!sessionQueueMaintainSchedule.isShutdown()) {
                sessionQueueMaintainSchedule.shutdown();
            }
        }
    }


    /**
     * if the SessionPool has been initialized
     */
    public boolean isActive() {
        return hasInit.get();
    }

    /**
     * if the SessionPool is closed
     */
    public boolean isClosed() {
        return isClosed.get();
    }

    /**
     * get the number of all Session
     */
    public int getSessionNums() {
        return sessionList.size();
    }

    /**
     * get the number of idle Session
     */
    public int getIdleSessionNums() {
        return idleSessionSize.get();
    }


    /**
     * release the NebulaSession when finished the execution.
     */
    private void releaseSession(NebulaSession nebulaSession) {
        nebulaSession.isUsedAndSetIdle();
        idleSessionSize.incrementAndGet();
    }


    /**
     * check if session is valid, if session is invalid, remove it.
     */
    private void checkSession() {
        for (NebulaSession nebulaSession : sessionList) {
            if (nebulaSession.isIdleAndSetUsed()) {
                try {
                    idleSessionSize.decrementAndGet();
                    nebulaSession.execute("YIELD 1");
                    nebulaSession.isUsedAndSetIdle();
                    idleSessionSize.incrementAndGet();
                } catch (IOErrorException e) {
                    nebulaSession.release();
                    sessionList.remove(nebulaSession);
                }
            }
        }
    }

    /**
     * update the session queue according to minSessionSize
     */
    private void updateSessionQueue() {
        // remove the idle sessions
        if (idleSessionSize.get() > minSessionSize) {
            synchronized (this) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdle()) {
                        nebulaSession.release();
                        sessionList.remove(nebulaSession);
                        if (idleSessionSize.decrementAndGet() <= minSessionSize) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * create a {@link NebulaSession} with specified state
     *
     * @param state {@link SessionState}
     * @return NebulaSession
     */
    private NebulaSession createSessionObject(SessionState state)
            throws ClientServerIncompatibleException, AuthFailedException,
            IOErrorException, BindSpaceFailedException {
        SyncConnection connection = new SyncConnection();
        int tryConnect = sessionPoolConfig.getGraphAddressList().size();
        // reconnect with all available address
        while (tryConnect-- > 0) {
            try {
                if (sessionPoolConfig.isEnableSsl()) {
                    connection.open(getAddress(), sessionPoolConfig.getTimeout(),
                            sessionPoolConfig.getSslParam(),
                            sessionPoolConfig.isUseHttp2(),
                            sessionPoolConfig.getCustomHeaders());
                } else {
                    connection.open(getAddress(), sessionPoolConfig.getTimeout(),
                            sessionPoolConfig.isUseHttp2(),
                            sessionPoolConfig.getCustomHeaders());
                }
                break;
            } catch (Exception e) {
                if (tryConnect == 0 || !reconnect) {
                    throw e;
                } else {
                }
            }
        }

        AuthResult authResult;
        try {
            authResult = connection.authenticate(sessionPoolConfig.getUsername(),
                    sessionPoolConfig.getPassword());
        } catch (AuthFailedException e) {
            close();
            throw e;
        }

        NebulaSession nebulaSession = new NebulaSession(connection, authResult.getSessionId(),
                authResult.getTimezoneOffset(), state);
        if (StringUtils.isNotBlank(spaceName) && !NULL_SPACE.equals(spaceName)) {
            ResultSet result = nebulaSession.execute(useSpace);
            if (!result.isSucceeded()) {
                nebulaSession.release();
                throw new BindSpaceFailedException(result.getErrorMessage());
            }
        }
        sessionList.add(nebulaSession);
        return nebulaSession;
    }


    public HostAddress getAddress() {
        List<HostAddress> addresses = sessionPoolConfig.getGraphAddressList();
        int newPos = (pos.getAndIncrement()) % addresses.size();
        return addresses.get(newPos);
    }

    /**
     * execute the "USE SPACE_NAME" when session's space changed.
     *
     * @param nebulaSession NebulaSession
     * @param resultSet     execute response
     */
    private void useSpace(NebulaSession nebulaSession, ResultSet resultSet)
            throws IOErrorException {
        if (resultSet == null) {
            nebulaSession.release();
            sessionList.remove(nebulaSession);
            return;
        }
        // space has been drop, close the SessionPool
        if (resultSet.getSpaceName().trim().isEmpty()) {
            close();
            return;
        }
        // re-bind the configured spaceName, if bind failed, then remove this session.
        if (!spaceName.equals(resultSet.getSpaceName())) {
            ResultSet switchSpaceResult = nebulaSession.execute(useSpace);
            if (!switchSpaceResult.isSucceeded()) {
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                return;
            }
        }
        releaseSession(nebulaSession);
    }

    /**
     * execute the "USE SPACE_NAME" when session's space changed for Json interface
     *
     * @param nebulaSession NebulaSession
     * @param result        execute response
     */
    private void useSpaceForJson(NebulaSession nebulaSession, String result)
            throws IOErrorException {
        String responseSpaceName =
                (String) JSON.parseObject(result).getJSONArray("results")
                        .getJSONObject(0).get("spaceName");
        if (!spaceName.equals(responseSpaceName)) {
            nebulaSession.execute(useSpace);
        }
        releaseSession(nebulaSession);
    }


    private boolean isSessionError(ResultSet resultSet) {
        return resultSet != null
                && (resultSet.getErrorCode() == ErrorCode.E_SESSION_INVALID.getValue()
                || resultSet.getErrorCode() == ErrorCode.E_SESSION_NOT_FOUND.getValue()
                || resultSet.getErrorCode() == ErrorCode.E_SESSION_TIMEOUT.getValue());
    }


    private void checkSessionPool() {
        if (!hasInit.get()) {
            throw new RuntimeException("The SessionPool has not been initialized, "
                    + "please call init() first.");
        }
        if (isClosed.get()) {
            throw new RuntimeException("The SessionPool has been closed.");
        }
    }


    private void stmtCheck(String stmt) {
        if (stmt == null || stmt.trim().isEmpty()) {
            throw new IllegalArgumentException("statement is null.");
        }

//        if (stmt.trim().toLowerCase().startsWith("use") && stmt.trim().split(" ").length == 2) {
//            throw new IllegalArgumentException("`USE SPACE` alone is forbidden.");
//        }
    }

    public String getSpaceName() {
        return NULL_SPACE.equals(spaceName) ? null : spaceName;
    }
}